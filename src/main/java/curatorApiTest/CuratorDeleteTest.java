package curatorApiTest;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryOneTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @Classname CuratorCreateTest
 * @Description TODO 使用 Curator 创建结点
 * @Date 2020/11/10 19:40
 * @Author Danrbo
 */
public class CuratorDeleteTest {
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
                namespace("delete").
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
     * 删除节点
     */
    @Test
    public void deleteTest1() throws Exception {
        client.delete().forPath("/node1");
    }

    /**
     * 带版本号的删除节点
     */
    @Test
    public void deleteTest2() throws Exception {
        client.delete().withVersion(0).forPath("/node2");
    }

    /**
     * 递归删除
     */
    @Test
    public void deleteTest3() throws Exception {
        client.delete().deletingChildrenIfNeeded().forPath("/node3/node33");
    }


    /**
     * 异步删除
     */
    @Test
    public void deleteTest4() throws Exception {
        client.delete().inBackground((curatorFramework, curatorEvent) -> {
            System.out.println(curatorEvent.getStat());
            System.out.println(curatorEvent.getType());
        }).forPath("/node4");
    }
}

