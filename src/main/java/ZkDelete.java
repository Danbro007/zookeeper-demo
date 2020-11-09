import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @Classname ZkDelete
 * @Description TODO
 * @Date 2020/11/9 12:51
 * @Author Danrbo
 */
public class ZkDelete {
    private final static String ZOOKEEPER_ADDR = "192.168.0.109:2182";
    private final static int SESSION_TIMEOUT = 5000;
    private final static CountDownLatch COUNT_DOWN_LATCH = new CountDownLatch(1);
    public ZooKeeper zooKeeper;

    /**
     * 连接 Zookeeper
     */
    @Before
    @Test
    public void before() throws IOException {
        zooKeeper = new ZooKeeper(ZOOKEEPER_ADDR, SESSION_TIMEOUT, event -> {
            if (event.getState() == Watcher.Event.KeeperState.SyncConnected) {
                System.out.println("连接成功");
                COUNT_DOWN_LATCH.countDown();
            }
        });
    }

    /**
     * 释放资源
     */
    @After
    @Test
    public void after() throws InterruptedException {
        zooKeeper.close();
    }

    /**
     * 同步删除
     */
    @Test
    public void deleteTest1() throws KeeperException, InterruptedException {
        zooKeeper.delete("/delete/node1", 0);
    }


    /**
     * 异步删除
     */
    @Test
    public void deleteTest2() {
        zooKeeper.delete("/delete/node2", 0, (rc, path, ctx) ->
                System.out.println(rc + " " + path + " " + ctx), "I am context");
    }
}
