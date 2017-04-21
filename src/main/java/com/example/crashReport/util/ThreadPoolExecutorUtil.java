package com.example.crashReport.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by xupeizuo on 2017/2/21.
 */

public final class ThreadPoolExecutorUtil {

    /**
     * 1、new Thread的弊端
     a. 每次new Thread新建对象性能差。
     b. 线程缺乏统一管理，可能无限制新建线程，相互之间竞争，及可能占用过多系统资源导致死机或oom。
     c. 缺乏更多功能，如定时执行、定期执行、线程中断。
     相比new Thread，Java提供的四种线程池的好处在于：
     a. 重用存在的线程，减少对象创建、消亡的开销，性能佳。
     b. 可有效控制最大并发线程数，提高系统资源的使用率，同时避免过多资源竞争，避免堵塞。
     c. 提供定时执行、定期执行、单线程、并发数控制等功能。
     */

    private static final int fixPoolSize=10;
    private static ExecutorService cachedThreadPool;
    private static ExecutorService fixedThreadPool;
    private static ScheduledExecutorService scheduledThreadPool;
    private static ExecutorService singleThreadExecutor;

    /**
     * 可缓存线程池 如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程
     * @return
     */
    public static synchronized ExecutorService getCachedThreadPool(){
        if(cachedThreadPool ==null){
             cachedThreadPool = Executors.newCachedThreadPool();
        }
        return cachedThreadPool;
    }

    /**
     * 定长线程池 可控制线程最大并发数，超出的线程会在队列中等待
     * @return
     */
    public static synchronized ExecutorService getFixedThreadPool(){
        if(fixedThreadPool ==null){
             fixedThreadPool = Executors.newFixedThreadPool(fixPoolSize);
        }
        return fixedThreadPool;
    }

    /**
     * 定长线程池，支持定时及周期性任务执行
     * @return
     */
    public static synchronized ScheduledExecutorService getScheduledThreadPool(){
        if(scheduledThreadPool == null){
            scheduledThreadPool = Executors.newScheduledThreadPool(fixPoolSize);
        }
        return scheduledThreadPool;
    }

    /**
     * 单线程化的线程池，它只会用唯一的工作线程来执行任务，保证所有任务按照指定顺序(FIFO, LIFO, 优先级)执行
     * @return
     */
    public static synchronized ExecutorService getSingleThreadExecutor(){
        if(singleThreadExecutor ==null ){
            singleThreadExecutor = Executors.newSingleThreadExecutor();
        }
        return singleThreadExecutor;
    }

}
