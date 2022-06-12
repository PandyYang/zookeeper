package com.pandy.ServerClient;

import org.apache.zookeeper.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author: Pandy
 * @create: 2022/6/11
 *
 * 注册zk分布式集群
 **/
public class DistributeServer {

    // 注意 逗号左右不能有空格 配置host映射
    private String connectionString = "master:2181,node1:2181,node2:2181";

    private int sessionTimeout = 2000;
    private ZooKeeper zkClient;

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        DistributeServer server = new DistributeServer();

        // 获取zk链接
        server.getConnect();
        // 注册服务器到zk集群
        server.registry(args[0]);

        // 启动业务逻辑
        server.business();
    }

    private void business() throws InterruptedException {
        Thread.sleep(Long.MAX_VALUE);
    }

    private void registry(String hostname) throws InterruptedException, KeeperException {
        String create = zkClient.create("/servers/" + hostname, hostname.getBytes(StandardCharsets.UTF_8), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);

        System.out.println(hostname + " is online");
    }

    private void getConnect() throws IOException {
        zkClient = new ZooKeeper(connectionString, sessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent event) {

            }
        });
    }


}
