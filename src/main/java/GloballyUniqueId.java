import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @Classname GloballyUniqueId
 * @Description TODO 使用 Zookeeper 来实现分布式唯一 ID
 * @Date 2020/11/9 16:32
 * @Author Danrbo
 */
public class GloballyUniqueId implements Watcher {
    private final static String ZOOKEEPER_ADDR = "192.168.0.109:2182";
    private final static int SESSION_TIMEOUT = 5000;
    private final static CountDownLatch COUNT_DOWN_LATCH = new CountDownLatch(1);
    public ZooKeeper zooKeeper;

    public GloballyUniqueId() throws IOException {
        this.zooKeeper = new ZooKeeper(ZOOKEEPER_ADDR, SESSION_TIMEOUT, this);
    }


    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        GloballyUniqueId globallyUniqueId = new GloballyUniqueId();
        COUNT_DOWN_LATCH.await();
        for (int i = 0; i < 4; i++) {
            String uniqueID = globallyUniqueId.getUniqueID();
            System.out.println(uniqueID);

        }
    }

    public String getUniqueID() throws KeeperException, InterruptedException {
        String path = zooKeeper.create("/uniqueid", new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        return path.substring(9);
    }


    @Override
    public void process(WatchedEvent event) {
        Event.KeeperState state = event.getState();
        if (state == Event.KeeperState.SyncConnected) {
            System.out.println("正常连接");
        } else if (state == Event.KeeperState.Disconnected) {

            System.out.println("断开连接");
        } else if (state == Event.KeeperState.Expired) {
            System.out.println("连接过期");
        } else if (state == Event.KeeperState.AuthFailed) {
            System.out.println("授权失败");
        }
        COUNT_DOWN_LATCH.countDown();

    }
}
