package pandy.zk;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author: Pandy
 * @create: 2022/6/11
 **/
public class ZkClient {

    // 注意 逗号左右不能有空格 配置host映射
    private String connectionStirng = "master:2181,node1:2181,node2:2181";

    private int sessionTimeout = 2000;
    private ZooKeeper zkClient;
    private List<String> children;

    /**
     * 初始化链接zk
     * @throws IOException
     */
    @Before
    public void init() throws IOException {
        zkClient = new ZooKeeper(connectionStirng, sessionTimeout, new Watcher() {

            /**
             * 每次节点改变在此处进行监听回调
             * @param event
             */
            @Override
            public void process(WatchedEvent event) {

                System.out.println("------------------------------------");
                try {
                    children = zkClient.getChildren("/", true);

                    for (String child : children) {
                        System.out.println("child = " + child);
                    }
                } catch (KeeperException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 创建节点
     * 设置权限
     * 设置节点值
     * @throws InterruptedException
     * @throws KeeperException
     */
    @Test
    public void create() throws InterruptedException, KeeperException {
        String nodeCreated = zkClient.create("/pandy", "dida".getBytes(StandardCharsets.UTF_8), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    /**
     * 获取根目录下的所有节点的集合
     * 注意此方法不能复用
     * @throws InterruptedException
     * @throws KeeperException
     */
    @Test
    public void getChildren() throws InterruptedException, KeeperException {
        children = zkClient.getChildren("/", true);

        children.forEach(System.out::println);

        Thread.sleep(Long.MAX_VALUE);
    }

    @Test
    public void exist() throws InterruptedException, KeeperException {
        Stat stat = zkClient.exists("/pandy", false);

        System.out.println(stat == null ? "未获取到" : "该节点存在");

        Stat stat2 = zkClient.exists("/pandy999", false);

        System.out.println(stat2 == null ? "未获取到" : "该节点存在");
    }


}
