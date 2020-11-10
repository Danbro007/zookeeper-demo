package javaApiTest;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @Classname javaApiTest.ZkGet
 * @Description TODO
 * @Date 2020/11/9 13:04
 * @Author Danrbo
 */
public class ZkGet {
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
     * 同步查看
     */
    @Test
    public void getData1() throws KeeperException, InterruptedException {
        Stat stat = new Stat();
        byte[] data = zooKeeper.getData("/get/node1", false, stat);
        System.out.println(new String(data));
        // 判空
        System.out.println(stat.getCtime());
    }


    /**
     * 异步查看
     */
    @Test
    public void getData2() throws InterruptedException {
        zooKeeper.getData("/get/node2", false, (rc, path, ctx, bytes, stat) -> {
            // 判空
            System.out.println(rc + " " + path
                    + " " + ctx + " " + new String(bytes) + " " +
                    stat.getCzxid());
        }, "I am context");
        TimeUnit.SECONDS.sleep(3);
    }

    /**
     * 同步
     */
    @Test
    public void getChildren1() throws Exception {
        List<String> getList = zooKeeper.getChildren("/get", false);
        getList.forEach(System.out::println);
    }

    /**
     * 异步
     */
    @Test
    public void getChildren2() throws Exception {
        zooKeeper.getChildren("/get", false, (rc, path, ctx, list) -> {
            list.forEach(System.out::println);
            System.out.println(rc + " " + path + " " + ctx);
        }, "I am children");
        TimeUnit.SECONDS.sleep(3);
    }

    @Test
    public void exists1() throws Exception {
        Stat exists = zooKeeper.exists("/get", false);
        // 判空
        System.out.println(exists.getVersion() + "成功");
    }
    @Test
    public void exists2() throws Exception {
        zooKeeper.exists("/get", false, (rc, path, ctx, stat) -> {
            // 判空
            System.out.println(rc + " " + path + " " + ctx + " " + stat.getVersion());
        }, "I am children");
        TimeUnit.SECONDS.sleep(1);
    }
}
