package com.qjx.demo.code.chain;

import static org.awaitility.Awaitility.await;
import static org.awaitility.Awaitility.with;
import static org.awaitility.pollinterval.FibonacciPollInterval.fibonacci;

import com.qjx.demo.code.utils.CommonUtils;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import org.awaitility.Duration;
import org.awaitility.core.ConditionTimeoutException;
import org.junit.Test;

interface CounterService extends Runnable {

    int getCount();
}

class CounterServiceImpl implements CounterService {

    private volatile int count = 0;

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public void run() {
        new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                CommonUtils.sleep(1000);
                count += 1;
            }
        }).start();
    }
}

/**
 * Awaitility 是一个流行的 Java 库，用于简化在测试中等待异步操作完成的过程。它提供了一种优雅的方式来确保在执行断言之前，异步操作已经完成。
 * with() 和 await() 方法在 Awaitility 中的使用场景和功能有所不同：
 * with() 方法:
 * 这个方法通常用于配置 Awaitility 的行为，例如设置超时时间、轮询间隔、轮询延迟等。
 * with() 方法返回一个 ConfiguredAssertion 对象，允许你链式调用来进一步配置等待逻辑。
 * 它不是一个等待条件的方法，而是一个配置方法，帮助你定制 Awaitility 的默认行为。
 * await() 方法:
 * await() 方法用于启动实际的等待过程，它等待一个给定的条件变为真。
 * 在调用 await() 后，你可以指定一个 lambda 表达式作为条件检查，这个表达式将周期性地执行，直到条件满足或达到超时。
 * await() 方法可以跟在 with() 方法之后，以应用之前配置的设置。
 *
 * @author qinjiaxing on 2024/7/23
 * @author <others>
 */
public class AwaitilityTest {

    /**
     * 默认等待时间
     * await().until(Callable conditionEvaluator)最多等待10s直到conditionEvaluator满足条件，否则ConditionTimeoutException。
     */
    @Test
    public void testAsynchronousNormal() {
        CounterServiceImpl service = new CounterServiceImpl();
        service.run();
        // 可以通过 setDefaultTimeout 提前设置超时时间，比如这里设置1s，其实应该执行5s的，所以会提前报错的
        // Awaitility.setDefaultTimeout(Duration.ONE_SECOND);
        await().until(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return service.getCount() == 5;
            }
        });
    }

    /**
     * 最多等待
     * await().atMost() 设置最多等待时间，如果在这时间内条件还不满足，将抛出ConditionTimeoutException。
     */
    @Test(expected = ConditionTimeoutException.class)
    public void testAsynchronousAtMost() {
        final CounterService service = new CounterServiceImpl();
        service.run();
        // 这里设置了 3s，其实是会报错的
        await().atMost(3, TimeUnit.SECONDS).until(() -> service.getCount() == 5);
    }

    /**
     * 最少等待
     * await().atLeast() 设置至少等待时间；多个条件时候用and()连接。
     */
    @Test
    public void testAsynchronousAtLeast() {
        final CounterService service = new CounterServiceImpl();
        service.run();
        // 这里指定，至少1s，至多3s，条件如果不满足的话，会报错 ConditionTimeoutException 异常
        await().atLeast(1, TimeUnit.SECONDS).atMost(3, TimeUnit.SECONDS).until(() -> service.getCount() == 2);
    }

    /**
     * 轮询
     * with().pollInterval(ONE_HUNDRED_MILLISECONDS).and().with().pollDelay(50, MILLISECONDS) that is conditions are checked after 50ms then 50ms+100ms。
     */
    @Test
    public void testAsynchronousPoll() {
        final CounterService service = new CounterServiceImpl();
        service.run();
        // 轮询查询,pollInterval每隔多少时间段轮询, pollDelay每次轮询间隔时间
        // 轮询间隔为100毫秒，轮询延迟为50毫秒
        with().pollInterval(Duration.ONE_HUNDRED_MILLISECONDS)
                .and()
                .with()
                .pollDelay(50, TimeUnit.MILLISECONDS)
                .await("count is greater 3 ")
                .until(() -> service.getCount() == 4);
    }

    /**
     * Fibonacci轮询
     * with().pollInterval(fibonacci(SECONDS))非线性轮询，按照fibonacci数轮询。
     */
    @Test
    public void testAsynchronousFibonacciPoll() {
        final CounterService service = new CounterServiceImpl();
        service.run();
        with().pollInterval(fibonacci(TimeUnit.SECONDS)).await("count is greater 3")
                .until(() -> service.getCount() == 4);
    }

    /**
     * Fibonacci轮询
     * with().pollInterval(fibonacci(SECONDS))非线性轮询，按照fibonacci数轮询。
     */
    @Test
    public void testAsynchronousTime() {
        final CounterService service = new CounterServiceImpl();
        service.run();
        with().timeout(4, TimeUnit.SECONDS)
                .until(() -> service.getCount() == 3);
    }


}
