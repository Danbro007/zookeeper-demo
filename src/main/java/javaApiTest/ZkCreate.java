package javaApiTest;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.junit.Test;
import org.junit.After;
import org.junit.Before;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @Classname javaApiTest.ZkCreate
 * @Description TODO
 * <p>
 * Zookeeper 结点创建测试
 * @Date 2020/11/9 11:51
 * @Author Danrbo
 */
public class ZkCreate {
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
     * 权限是以枚举的方式
     * OPEN_ACL_UNSAFE 就是 world:anyone:crdwa
     */
    @Test
    public void createTest1() throws Exception {
        String s = zooKeeper.create("/create/node1", "node1".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        System.out.println(s);
    }

    /**
     * 自定义的权限方式
     */
    @Test
    public void createTest2() throws Exception {
        ArrayList<ACL> acls = new ArrayList<>();
        Id id = new Id("world", "anyone");
        acls.add(new ACL(ZooDefs.Perms.WRITE, id));
        acls.add(new ACL(ZooDefs.Perms.READ, id));
        zooKeeper.create("/create/node2", "node2".getBytes(), acls, CreateMode.PERSISTENT);
    }

    /**
     * 以 IP 模式授予权限
     */
    @Test
    public void createTest3() throws Exception {
        ArrayList<ACL> acls = new ArrayList<>();
        Id id = new Id("ip", "192.168.133.133");
        acls.add(new ACL(ZooDefs.Perms.ALL, id));
        zooKeeper.create("/create/node3", "node3".getBytes(), acls, CreateMode.PERSISTENT);
    }

    /**
     * auth 模式
     */
    @Test
    public void createTest4() throws Exception {
        // 添加授权用户
        zooKeeper.addAuthInfo("digest", "danbro:12345".getBytes());
        zooKeeper.create("/node4", "node4".getBytes(),
                ZooDefs.Ids.CREATOR_ALL_ACL, CreateMode.PERSISTENT);
    }

    /**
     * auth 模式自定义权限
     */
    @Test
    public void createTest5() throws Exception {
        zooKeeper.addAuthInfo("digest", "danbro:12345".getBytes());
        List<ACL> acls = new ArrayList<>();
        Id id = new Id("auth", "itcast");
        acls.add(new ACL(ZooDefs.Perms.READ, id));
        zooKeeper.create("/create/node5", "node5".getBytes(),
                acls, CreateMode.PERSISTENT);
    }

    /**
     * digest 模式
     */
    @Test
    public void createTest6() throws Exception {
        List<ACL> acls = new ArrayList<>();
        Id id = new Id("digest", "danbro:qUFSHxJjItUW/93UHFXFVGlvryY=");
        acls.add(new ACL(ZooDefs.Perms.READ, id));
        zooKeeper.create("/create/node6", "node6".getBytes(),
                acls, CreateMode.PERSISTENT);
    }

    /**
     * 异步创建，创建完毕会执行回调函数。
     */
    @Test
    public void createTest7() throws Exception {
        zooKeeper.create("/create/node7", "node7".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT, new AsyncCallback.StringCallback() {
            /**
             * @param rc 状态，0 则为成功，以下的所有示例都是如此
             * @param path 路径
             * @param ctx 上下文参数
             * @param name 路径
             */
            @Override
            public void processResult(int rc, String path, Object ctx, String name) {
                System.out.println(rc + " " + path + " " + name + " " + ctx);
            }
        }, "I am context");
        TimeUnit.SECONDS.sleep(5);
        System.out.println("结束");
    }


}
