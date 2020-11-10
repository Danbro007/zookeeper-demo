package javaApiTest;

import lombok.Data;
import lombok.SneakyThrows;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @Classname javaApiTest.ZkConfigCenter
 * @Description TODO 使用 Zookeeper 来实现配置中心的配置实现更新
 * @Date 2020/11/9 15:32
 * @Author Danrbo
 */
@Data
public class ZkConfigCenter implements Watcher {
    private String databaseUrl;
    private String username;
    private String password;
    private final static String ZOOKEEPER_ADDR = "192.168.0.109:2182";
    private final static int SESSION_TIMEOUT = 5000;
    private final static CountDownLatch COUNT_DOWN_LATCH = new CountDownLatch(1);
    public ZooKeeper zooKeeper;

    public ZkConfigCenter() throws IOException, KeeperException, InterruptedException {
        this.zooKeeper = new ZooKeeper(ZOOKEEPER_ADDR, SESSION_TIMEOUT, this);
        COUNT_DOWN_LATCH.await();
        // 到 Zookeeper 中读取配置
        refreshConfig();
    }

    public void refreshConfig() throws KeeperException, InterruptedException {
        this.databaseUrl = new String(zooKeeper.getData("/config/url", true, null));
        this.username = new String(zooKeeper.getData("/config/username", true, null));
        this.password = new String(zooKeeper.getData("/config/password", true, null));
    }

    /**
     * 启动配置中心，每隔10秒打印一次配置
     */
    public static void main(String[] args) throws InterruptedException, IOException, KeeperException {
        ZkConfigCenter zkConfigCenter = new ZkConfigCenter();
        for (int i = 0; i < 4; i++) {
            System.out.println("--------------------------");
            System.out.println(zkConfigCenter.databaseUrl);
            System.out.println(zkConfigCenter.username);
            System.out.println(zkConfigCenter.password);
            TimeUnit.SECONDS.sleep(10);
        }
    }

    @SneakyThrows
    @Override
    public void process(WatchedEvent event) {
        if (event.getType() == Event.EventType.NodeDataChanged) {
            // 刷新当前配置中的配置
            refreshConfig();

        } else if (event.getState() == Event.KeeperState.SyncConnected) {
            System.out.println("连接成功");
            COUNT_DOWN_LATCH.countDown();
        }
    }
}

