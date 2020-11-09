import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.sql.Time;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @Classname ZkWatcher
 * @Description TODO 测试 Zookeeper 的连接状态监听器
 * @Date 2020/11/9 14:40
 * @Author Danrbo
 */
public class ZkConnectionWatcher implements Watcher {
    private final static String ZOOKEEPER_ADDR = "192.168.0.109:2182";
    private final static int SESSION_TIMEOUT = 5000;
    private final static CountDownLatch COUNT_DOWN_LATCH = new CountDownLatch(1);


    public static void main(String[] args) throws IOException, InterruptedException {
        ZooKeeper zooKeeper = new ZooKeeper(ZOOKEEPER_ADDR, SESSION_TIMEOUT, new ZkConnectionWatcher());
        COUNT_DOWN_LATCH.await();// 模拟正常连接
        zooKeeper.close();
    }


    @Override
    public void process(WatchedEvent event) {
        Event.KeeperState state = event.getState();
        if (state == Event.KeeperState.SyncConnected) {
            // 正常
            System.out.println("正常连接");
        } else if (state == Event.KeeperState.Disconnected) {
            // 可以用Windows断开虚拟机网卡的方式模拟
            // 当会话断开会出现，断开连接不代表不能重连，在会话超时时间内重连可以恢复正常
            System.out.println("断开连接");
        } else if (state == Event.KeeperState.Expired) {
            // 没有在会话超时时间内重新连接，而是当会话超时被移除的时候重连会走进这里
            System.out.println("连接过期");
        } else if (state == Event.KeeperState.AuthFailed) {
            // 在操作的时候权限不够会出现
            System.out.println("授权失败");
        }
        COUNT_DOWN_LATCH.countDown();
    }
}
