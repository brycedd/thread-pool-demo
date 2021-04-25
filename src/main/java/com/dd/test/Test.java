package com.dd.test;

import com.dd.concurrent.ThreadPoolExecutor;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author Bryce
 * @date 2021/4/12
 */
public class Test {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ThreadPoolExecutor threadPoolExecutor =
                new ThreadPoolExecutor(0, 1, new LinkedBlockingDeque<>());

        /*for (int i = 0; i<10; i++) {
            threadPoolExecutor.submit(() -> {
                System.out.println("xxx");
            });
        }*/

        for (int i = 0; i<10; i++) {
            FutureTask<Object> submit = threadPoolExecutor.submit(() -> "返回值----》");
            System.out.println(submit.get());
        }
        System.out.println(threadPoolExecutor.getPoolSize());

    }
}
