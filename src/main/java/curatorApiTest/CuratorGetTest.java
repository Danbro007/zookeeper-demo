package curatorApiTest;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.retry.RetryOneTime;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * @Classname CuratorCreateTest
 * @Description TODO 使用 Curator 读取节点
 * @Date 2020/11/10 19:40
 * @Author Danrbo
 */
public class CuratorGetTest {
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
                namespace("get").
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
     * 读取节点
     */
    @Test
    public void getTest1() throws Exception {
        String s = new String(client.getData().forPath("/node1"));
        System.out.println(s);
    }

    /**
     * 读取节点的属性
     */
    @Test
    public void getTest2() throws Exception {
        // 读取的节点属性存放在这里
        Stat stat = new Stat();
        String s = new String(client.getData().storingStatIn(stat).forPath("/node1"));
        System.out.println(stat.getVersion());// 节点数据的版本号
        System.out.println(s);
    }

    /**
     * 异步读取节点
     */
    @Test
    public void getTest3() throws Exception {
        client.getData().inBackground((curatorFramework, curatorEvent) -> {
            System.out.println(curatorEvent.getStat());
            System.out.println(curatorEvent.getType());
        }).forPath("/node1");
    }

    /**
     * 读取子节点
     */
    @Test
    public void getChildrenTest() throws Exception {
        List<String> stringList = client.getChildren().forPath("/node1");
        stringList.forEach(System.out::println);
    }

    /**
     * 判断节点是否存在
     */
    @Test
    public void getNodeIsExistTest1() throws Exception {
        Stat stat = client.checkExists().forPath("/node1");
        System.out.println(stat.getVersion());
    }

    /**
     * 异步判断节点是否存在
     */
    @Test
    public void getNodeIsExistTest2() throws Exception {
        client.checkExists().inBackground((curatorFramework, curatorEvent) -> {
            System.out.println(curatorEvent.getType());
            System.out.println(curatorEvent.getStat());
        }).forPath("/node1");
    }

}

