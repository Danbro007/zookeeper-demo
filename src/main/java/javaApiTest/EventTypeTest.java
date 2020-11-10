package javaApiTest;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @Classname javaApiTest.EventTypeTest
 * @Description TODO
 * @Date 2020/11/9 14:51
 * @Author Danrbo
 */
public class EventTypeTest {
    private final static String ZOOKEEPER_ADDR = "192.168.0.109:2182";
    private final static int SESSION_TIMEOUT = 5000;
    private final static CountDownLatch COUNT_DOWN_LATCH = new CountDownLatch(1);
    public ZooKeeper zooKeeper;

    @Before
    @Test
    public void before() throws IOException, InterruptedException {
        zooKeeper = new ZooKeeper(ZOOKEEPER_ADDR, SESSION_TIMEOUT, new MyWatcher());
    }

    @Test
    @After
    public void after() throws InterruptedException {
        TimeUnit.SECONDS.sleep(50);
        zooKeeper.close();
    }

    static class MyWatcher implements Watcher {
        @Override
        public void process(WatchedEvent event) {
            COUNT_DOWN_LATCH.countDown();
            System.out.println("监听到事件类型：" + event.getType());
        }
    }

    /**
     * 只能使用一次的监听器，监听 /watcher1 结点的事件
     */
    @Test
    public void exist1() throws KeeperException, InterruptedException {
        zooKeeper.exists("/watcher1", new MyWatcher());
    }

    /**
     * 自定义监听器
     */
    @Test
    public void exist2() throws KeeperException, InterruptedException {
        zooKeeper.exists("/watcher1", event -> {
            System.out.println("自定义监听器监听到：" + event.getType());
        });
    }

    /**
     * 演示循环使用监听器
     */
    @Test
    public void exist3() throws KeeperException, InterruptedException {
        zooKeeper.exists("/watcher1", new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("多次使用的自定义监听器：" + event.getType());
                try {
                    zooKeeper.exists("/watcher1", this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 演示注册多个监听器
     */
    @Test
    public void exist4() throws KeeperException, InterruptedException {
        zooKeeper.exists("/watcher1", e -> {
            System.out.println("监听器1：" + e.getType());
        });

        zooKeeper.exists("/watcher1", e -> {
            System.out.println("监听器2：" + e.getType());
        });
    }

}
