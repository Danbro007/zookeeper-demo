import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @Classname ZkSet
 * @Description TODO
 * @Date 2020/11/9 12:42
 * @Author Danrbo
 */
public class ZkSet {
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
     * 同步更新
     */
    @Test
    public void zkSetTest1() throws KeeperException, InterruptedException {
        zooKeeper.setData("/set/node1", "node11".getBytes(), 0);
    }

    /**
     * 异步更新
     */
    @Test
    public void zkSetTest2() throws KeeperException, InterruptedException {
        zooKeeper.setData("/set/node2", "node2".getBytes(), 0, new AsyncCallback.StatCallback() {
            @Override
            public void processResult(int rc, String path, Object ctx, Stat stat) {
                // 讲道理，要判空
                System.out.println(rc + " " + path + " " + stat.getVersion() + " " + ctx);
            }
        }, "I am context");
    }
}
