package curatorApiTest;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryOneTime;

/**
 * @Classname CuratorConnection
 * @Description TODO 与 Zookeeper 集群建立连接
 * @Date 2020/11/10 19:41
 * @Author Danrbo
 */
public class CuratorConnection {

    private final static String ADDR_LIST = "192.168.10.109:2182,192.168.10.109:2183,192.168.10.109:2184";
    private final static int SESSION_TIMEOUT = 5000;
    private final static int SLEEP_TIME = 1000;

    public static void main(String[] args) {
        CuratorFramework client = CuratorFrameworkFactory.builder().
                connectString(ADDR_LIST).// 集群的IP地址列表
                sessionTimeoutMs(SESSION_TIMEOUT).// session 超时时间
                retryPolicy(new RetryOneTime(SLEEP_TIME)).// 重试策略：当session 超时大于 1000 ms 则会执行一次重试，就只有一次
                namespace("create").// 名称空间
                build();
        client.start();
        System.out.println(client.getState());
        client.close();
    }
}
