package com.dd.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * @author Bryce
 * @date 2021/4/12
 */
public interface ExecutorService extends Executor{

    void shutDown();
    <T>FutureTask<T> submit(Runnable runnable);
    <T>FutureTask<T> submit(Callable callable);
}
