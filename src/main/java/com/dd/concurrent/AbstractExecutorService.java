package com.dd.concurrent;


import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * @author Bryce
 * @date 2021/4/12
 */
public abstract class AbstractExecutorService implements ExecutorService {

    @Override
    public <T> FutureTask<T> submit(Runnable runnable) {

        FutureTask<T> futureTask = new FutureTask<T>(runnable,null);
        execute(futureTask);
        return futureTask;
    }

    @Override
    public <T> FutureTask<T> submit(Callable callable) {

        FutureTask<T> futureTask = new FutureTask<T>(callable);
        execute(futureTask);
        return futureTask;
    }
}
