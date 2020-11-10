package javaApiTest;

import org.apache.zookeeper.KeeperException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @Classname javaApiTest.LockTest
 * @Description TODO
 * @Date 2020/11/9 20:18
 * @Author Danrbo
 */
public class LockTest {
    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        for (int i = 0; i < 5; i++) {
            MyLock myLock = new MyLock();
            myLock.acquireLock();
            TimeUnit.MILLISECONDS.sleep(1000);
            myLock.releaseLock();
        }
    }
}
