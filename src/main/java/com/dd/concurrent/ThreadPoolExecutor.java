package com.dd.concurrent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Bryce
 * @date 2021/4/12
 */
public class ThreadPoolExecutor extends AbstractExecutorService {

    private volatile int corePoolSize;
    private volatile int maximumPoolSize;
    private volatile long keepAliveTime;
    private volatile boolean allowCoreThreadTimeOut; //是否需要超时

    private final AtomicInteger ctl = new AtomicInteger(0); //worker实时数量

    private BlockingQueue<Runnable> workQueue;
    private ReentrantLock mainLock;

    public ThreadPoolExecutor(int corePoolSize, int maximumPoolSize, BlockingQueue<Runnable> workQueue) {
        this.corePoolSize = corePoolSize;
        this.maximumPoolSize = maximumPoolSize;
        this.workQueue = workQueue;
    }

    public ThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, boolean allowCoreThreadTimeOut, BlockingQueue<Runnable> workQueue) {
        this.corePoolSize = corePoolSize;
        this.maximumPoolSize = maximumPoolSize;
        this.keepAliveTime = keepAliveTime;
        if(keepAliveTime > 0) {
            allowCoreThreadTimeOut = true;
        }
        this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
        this.workQueue = workQueue;
    }

    public int getPoolSize() {
        return ctl.get();
    }

    @Override
    public void execute(Runnable command) {
        if(command == null) {
            throw new NullPointerException();
        }

        int c = ctl.get();
        if(c < corePoolSize) {
            addWorker(command,true);
        }else if(workQueue.offer(command)) { //队列未满，还能继续添加进去
            //        workQueue.add() todo 区别
            addWorker(null,false);
        }else {
            reject(command);
        }
    }

    private void reject(Runnable command) {
        RejectedExecutionHandler rejectedExecutionHandler = new RejectedExecutionHandler();
        rejectedExecutionHandler.rejectedExecution(command);

    }

    /**
     * 完善此方法
     */
    @Override
    public void shutDown() {

    }

    static class RejectedExecutionHandler {
        public void rejectedExecution(Runnable command) {
            throw new RejectedExecutionException(String.format("队列已满，这个task:  不再处理！",command.toString()));
        }
    }

    private void addWorker(Runnable task,Boolean core) {

        if(core) {
            ctl.incrementAndGet();
        }

        Worker worker = new Worker(task);
        worker.thread.start();
    }


    class Worker extends ReentrantLock implements Runnable{
        private Runnable firstTask;
        private Thread thread;

        public Worker(Runnable firstTask) {
            this.firstTask = firstTask;
            thread = new Thread(this);
        }

        @Override
        public void run() {
            runWorker(this);
        }


        private void runWorker(Worker w) {
            try {
                w.lock();
                Runnable task = w.firstTask;
                if(task != null || (task = getTask()) != null) {
                    task.run();
                }
            } finally {
                processWorkerExit(w);
                w.unlock();
            }

        }

        private void processWorkerExit(Worker worker) {
            addWorker(null,false);
        }

        private Runnable getTask() {
            try {
                if(workQueue.isEmpty()) {
                    return null;
                }
                Runnable r = allowCoreThreadTimeOut
                        ? workQueue.poll(keepAliveTime, TimeUnit.NANOSECONDS)
                        : workQueue.take();
                if(r != null) {
                    return r;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


}
