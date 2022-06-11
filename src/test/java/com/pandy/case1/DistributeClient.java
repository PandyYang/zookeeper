package com.pandy.case1;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: Pandy
 * @create: 2022/6/11
 **/
public class DistributeClient {

    // 注意 逗号左右不能有空格 配置host映射
    private String connectionString = "master:2181,node1:2181,node2:2181";

    private int sessionTimeout = 2000;
    private ZooKeeper zkClient;

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        DistributeClient client = new DistributeClient();
        // 获取zk链接
        client.getConnect();
        // 监听Servers子节点的变化
        client.getServerList();
        // 业务逻辑

        client.business();
    }

    private void business() throws InterruptedException {
        Thread.sleep(Long.MAX_VALUE);
    }

    private void getServerList() throws InterruptedException, KeeperException {
        List<String> children = zkClient.getChildren("/servers", true);

        ArrayList<String> servers = new ArrayList<>();

        for (String child : children) {
            byte[] data = zkClient.getData("/servers/" + child, false, null);
            servers.add(new String(data));
        }

        System.out.println(servers);
    }

    private void getConnect() throws IOException {
        zkClient = new ZooKeeper(connectionString, sessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                try {
                    getServerList();
                } catch (InterruptedException | KeeperException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
