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
public class CuratorSetTest {
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
                namespace("set").
                build();
        client.start();
        System.out.println(client.getState());
    }

    @Test
    @After
    public void close() {
        client.close();
    }

    /**
     * 修改节点数据
     */
    @Test
    public void setTest1() throws Exception {
        client.setData().forPath("/node1", "node11".getBytes());
    }

    /**
     * 带版本号的修改节点数据
     */
    @Test
    public void setTest2() throws Exception {
        client.setData().withVersion(0).forPath("/node2", "node22".getBytes());
    }

    /**
     * 异步的带版本号的修改数据
     */
    @Test
    public void setTest3() throws Exception {
        client.setData().withVersion(0).inBackground((curatorFramework, curatorEvent) -> {
            System.out.println(curatorEvent.getType());
            System.out.println(curatorEvent.getStat());
        }).forPath("/node3", "node33".getBytes());
    }
}

