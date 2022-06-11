package com.pandy.case2;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author: Pandy
 * @create: 2022/6/11
 *
 * 分布式锁的实现
 **/
public class DistributeLock {

    // 注意 逗号左右不能有空格 配置host映射
    private String connectionString = "master:2181,node1:2181,node2:2181";

    private int sessionTimeout = 2000;
    private ZooKeeper zkClient;

    private String waitPath = "";

    private CountDownLatch connectLatch = new CountDownLatch(1);

    private CountDownLatch waitLatch = new CountDownLatch(1);
    private String currentMode;

    public DistributeLock() throws IOException, InterruptedException, KeeperException {

        // 获取连接
        zkClient = new ZooKeeper(connectionString, sessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                // 如果连接上zk 释放
                if (event.getState() == Event.KeeperState.SyncConnected) {
                    connectLatch.countDown();
                }
                // waitLatch需要释放
                if (event.getType() == Event.EventType.NodeDeleted && event.getPath().equals(waitPath)) {
                    waitLatch.countDown();
                }
            }
        });
        // 等待zk正常链接，往下走程序
        connectLatch.await();
        // 判断根节点 locks是否存在
        Stat stat = zkClient.exists("/locks", false);
        if (stat == null) {
            // 创建根节点
            zkClient.create("/locks", "locks".getBytes(StandardCharsets.UTF_8), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
    }

    /**
     * 加锁操作
     */
    public void zkLock() throws InterruptedException, KeeperException {
        // 创建对应的临时带序号节点
        currentMode = zkClient.create("/locks/" + "seq-", null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        // 判断创建的节点是否是最小序号节点，如果是获取到锁，如果不是，监听前一个序号节点
        List<String> children = zkClient.getChildren("/locks", false);

        if (children.size() == 1) {
            // 只有一个节点 肯定是最小节点 直接获取锁
            return;
        } else {
            // 如果有多个节点 则需要进行判断
            Collections.sort(children);

            // 获取对应的节点名称 seq-000000
            String thisNode = currentMode.substring("/locks/".length());

            // 获取在集合中的位置
            int index = children.indexOf(thisNode);
            if (index == -1) {
                System.out.println("数据异常");
            } else if (index == 0) {
                // 只有一个节点 直接获取锁
                return;
            } else {
                waitPath = "/locks/" + children.get(index - 1);

                // 在 waitPath 上注册监听器, 当 waitPath 被删除时, zookeeper 会回调监听器的 process 方法
                // 监听前一个节点的变化 watch 为 true 调用process
                zkClient.getData(waitPath, true, null);
                waitLatch.await();

                return;
            }

        }

    }

    /**
     * 解锁操作
     */
    public void unZkLock() throws InterruptedException, KeeperException {

        // 删除节点
        zkClient.delete(currentMode, -1);
    }
}
