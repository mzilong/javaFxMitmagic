package sample.utils;

import javafx.concurrent.Task;

import java.util.concurrent.*;

/**
 * 线程池工具类
 * @author mzl
 * @Description ThreadPoolUtils
 * @Data  2020/9/2 10:51
 */
public class ThreadPoolUtils {
    /**
     * 开启一个线程池来运行
     * @param runnable 可运行任务
     */
    public static ScheduledThreadPoolExecutor runDelayTime(Runnable runnable){
        return runDelayTime(runnable,0);
    }

    /**
     * 开启一个延迟多少毫秒的线程池来运行
     * @param runnable 可运行任务
     * @param delay 延迟多少毫秒
     */
    public static ScheduledThreadPoolExecutor runDelayTime(Runnable runnable,long delay){
        return runDelayTime(runnable,delay,TimeUnit.MILLISECONDS);
    }

    /**
     * 开启一个延迟多少时间的线程池来运行
     * @param runnable 可运行任务
     * @param delay 延迟多少时间
     * @param timeUnit 时间单位
     */
    public static ScheduledThreadPoolExecutor runDelayTime(Runnable runnable,long delay,TimeUnit timeUnit){
        ScheduledThreadPoolExecutor stpExecutor = new ScheduledThreadPoolExecutor(1);
        stpExecutor.schedule(runnable,delay, timeUnit);
        return stpExecutor;
    }

    /**
     * 开启ExecutorService服务来运行
     * @param runnable 可运行任务
     */
    public static ExecutorService runExecutorService(Runnable runnable){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(runnable);
        return executorService;
    }

    /**
     * 异步执行任务
     *
     * @param task 任务
     */
    public static void runAsync(Task<?> task) {
        CompletableFuture.runAsync(task);
    }
}
