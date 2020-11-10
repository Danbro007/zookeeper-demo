package javaApiTest;

import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @Classname TestZookeeper
 * @Description TODO 测试连接到 Zookeeper 集群
 * @Date 2020/11/9 11:37
 * @Author Danrbo
 */
public class ZookeeperHadoopConnection {
    private final static String ZOOKEEPER_ADDR = "192.168.10.109:2182,192.168.10.109:2183,192.168.10.109:2184";
    private final static int SESSION_TIMEOUT = 5000;
    private final static CountDownLatch COUNT_DOWN_LATCH = new CountDownLatch(1);

    public static void main(String[] args) throws IOException, InterruptedException {
        ZooKeeper zooKeeper = new ZooKeeper(ZOOKEEPER_ADDR, SESSION_TIMEOUT, event -> {
            if (event.getState() == Watcher.Event.KeeperState.SyncConnected) {
                System.out.println("连接成功");
                COUNT_DOWN_LATCH.countDown();
            }
        });
        COUNT_DOWN_LATCH.await();
        System.out.println(zooKeeper.getSessionId());
        zooKeeper.close();
    }
}
