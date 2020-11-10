package curatorApiTest;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.retry.RetryOneTime;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

/**
 * @Classname CuratorCreateTest
 * @Description TODO 使用 Curator 创建结点
 * @Date 2020/11/10 19:40
 * @Author Danrbo
 */
public class CuratorCreateTest {
    private final static String ADDR_LIST = "192.168.10.109:2182,192.168.10.109:2183,192.168.10.109:2184";
    private final static int SESSION_TIMEOUT = 5000;
    private final static int SLEEP_TIME = 1000;
    private CuratorFramework client;


    @Test
    @Before
    public void connect() {
        client = CuratorFrameworkFactory.builder().
                connectString(ADDR_LIST).
                sessionTimeoutMs(SESSION_TIMEOUT).
                retryPolicy(new RetryOneTime(SLEEP_TIME)).
                namespace("create").
                build();
        client.start();
        System.out.println(client.getState());
    }

    @Test
    @After
    public void close(){
        client.close();
    }

    /**
     * 创建一个节点,权限为 crwda。
     */
    @Test
    public void createTest1() throws Exception {
        client.create().withMode(CreateMode.PERSISTENT).
                withACL(ZooDefs.Ids.CREATOR_ALL_ACL).
                forPath("/node1","node1".getBytes());
    }

    /**
     * 自定义权限列表
     */
    @Test
    public void createTest2() throws Exception {
        ArrayList<ACL> acls = new ArrayList<>();
        Id id = new Id("ip","192.168.10.130");
        acls.add(new ACL(ZooDefs.Perms.ALL,id));
        client.create().withMode(CreateMode.PERSISTENT).
                withACL(acls).
                forPath("/node2","node2".getBytes());
    }

    /**
     * 递归创建结点，例如我们要创建 /node3/node33 这个节点，/node3 这个父节点
     * 我们之前没有创建，在这里可以使用 creatingParentsIfNeeded() 来递归创建节点。
     */
    @Test
    public void createTest3() throws Exception {
        client.create().creatingParentsIfNeeded().
                withMode(CreateMode.PERSISTENT).
                withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE).
                forPath("/node3/node33","node33".getBytes());
    }

    /**
     * 异步创建节点，创建完毕后会执行回调函数。
     */
    @Test
    public void createTest4() throws Exception {
        client.create().withMode(CreateMode.PERSISTENT).
                withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE).
                inBackground((curatorFramework, curatorEvent) -> {
                    System.out.println(curatorEvent.getStat());
                    System.out.println(curatorEvent.getType());
                }).
                forPath("/node4","node4".getBytes());
    }
}
