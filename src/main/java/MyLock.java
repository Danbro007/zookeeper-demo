import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @Classname ZookeeperKey
 * @Description TODO 使用 Zookeeper 实现分布式锁
 * @Date 2020/11/9 19:42
 * @Author Danrbo
 */
public class MyLock {

    private final static String ZOOKEEPER_ADDR = "192.168.10.109:2182";
    private final static int SESSION_TIMEOUT = 50000;
    private final static CountDownLatch COUNT_DOWN_LATCH = new CountDownLatch(1);
    public ZooKeeper zooKeeper;
    private final String rootPath = "/Locks";
    private final String pathPrefix = "Locks";
    private String lockPath;
    private LockWatcher lockWatcher = new LockWatcher();

    public MyLock() throws InterruptedException, IOException {
        zooKeeper = new ZooKeeper(ZOOKEEPER_ADDR, SESSION_TIMEOUT, event -> {
            if (event.getType() == Watcher.Event.EventType.None) {
                if (event.getState() == Watcher.Event.KeeperState.SyncConnected) {
                    System.out.println("连接到服务端");
                    COUNT_DOWN_LATCH.countDown();
                }
            }
        });
        COUNT_DOWN_LATCH.await();
    }


    /**
     * 先创建锁，然后尝试获取锁。
     */
    public void acquireLock() throws KeeperException, InterruptedException {
        createLock();
        attemptLock();
    }

    /**
     * 尝试获取锁
     */
    private void attemptLock() throws KeeperException, InterruptedException {
        List<String> locks = zooKeeper.getChildren(rootPath, false);
        Collections.sort(locks);
        int i = locks.indexOf(lockPath.substring(rootPath.length() + 1));
        // 获取到锁
        if (i == 0) {
            System.out.println("获取到锁");
            return;
        }
        // 没有获取到锁，则对上一个锁进行监听，如果上一个锁被删除了则唤醒当前锁尝试获取锁。
        else {
            String lastLockPath = locks.get(i - 1);
            Stat stat = zooKeeper.exists(rootPath + "/" + lastLockPath, lockWatcher);
            if (stat == null) {
                attemptLock();
            } else {
                synchronized (lockWatcher) {
                    lockWatcher.wait();
                }
                attemptLock();
            }
        }
    }

    private void createLock() throws KeeperException, InterruptedException {
        Stat stat = zooKeeper.exists(rootPath, false);
        if (stat == null) {
            zooKeeper.create(rootPath, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
        // 存储有序节点
        lockPath = zooKeeper.create(rootPath + "/" + pathPrefix, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println("创建结点成功：" + lockPath);
    }

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        MyLock myLock = new MyLock();
        myLock.acquireLock();
    }

    public void releaseLock() throws InterruptedException, KeeperException {
        zooKeeper.delete(lockPath, -1);
        zooKeeper.close();
    }
}


/**
 * 唤醒当前线程的监听器
 */
class LockWatcher implements Watcher {
    @Override
    public void process(WatchedEvent event) {
        if (event.getType() == Watcher.Event.EventType.NodeDeleted) {
            synchronized (this) {
                notifyAll();
            }
        }
    }
}
