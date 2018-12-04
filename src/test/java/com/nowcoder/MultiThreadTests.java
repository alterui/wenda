package com.nowcoder;

import org.junit.rules.Timeout;
import sun.awt.SunHints;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ALTERUI on 2018/11/27 15:08
 */
class MyThread extends Thread {
    int tid ;

    public MyThread(int tid) {
        this.tid = tid;
    }
    @Override
    public void run() {
        try {
            for (int i = 0; i < 10; i++) {
                Thread.sleep(100);
                System.out.println(String.format("%d:%d", tid, i));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}


class MyThreadRunnable implements Runnable {
    private int tid;

    public int getTid() {
        return tid;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }

    public MyThreadRunnable(int tid) {
        this.tid = tid;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < 10; i++) {
                Thread.sleep(100);
                System.out.println(String.format("%d:%d",tid, i));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

/**
 * 消息队列：生产者与消费者
 */
class Customer implements Runnable {
    private BlockingQueue<String> q;

    public Customer(BlockingQueue<String> q) {
        this.q = q;
    }
    @Override
    public void run() {
        try {
            while (true) {

                System.out.println(Thread.currentThread().getName()+":"+q.take());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Producer implements Runnable {

    private BlockingQueue<String> q;

    public Producer(BlockingQueue<String> q) {
        this.q = q;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < 100; i++) {
                Thread.sleep(100);
                q.put(String.valueOf(i));


            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
public class MultiThreadTests  {

    public static void threadTest() {
       /* for (int i = 0; i < 10; i++) {

            new MyThreadRunnable(i).run();
        }
*/
        for (int j = 0; j < 10; j++) {
            final int finalJ = j;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        for (int i = 0; i < 10; i++) {
                            Thread.sleep(10);
                            System.out.println(String.format("T2 %d:%d", finalJ,i));
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

    }


    private static Object obj = new Object();

    public static void testSynchronized1() {
        synchronized (obj) {
            try {
                for (int i = 0; i < 10; i++) {
                    Thread.sleep(1000);
                    System.out.println(String.format("T3 %d", i));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void  testSynchronized2() {
        synchronized (obj) {
            try {
                for (int i = 0; i < 10; i++) {
                    Thread.sleep(1000);
                    System.out.println(String.format("T4 %d", i));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public static void testSynchronized() {
        testSynchronized1();
        testSynchronized2();
    }

    private static int userId;
    private static ThreadLocal<Integer> threadLocalIds = new ThreadLocal<>();



    public static void threadLocalTest() {

        for (int i = 0; i < 10; i++) {
            final int finalI = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        threadLocalIds.set(finalI);
                        Thread.sleep(100);
                        System.out.println("ThreadLocalIds:"+threadLocalIds.get());

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }).start();
        }

    }


    public static void threadLocalTest2() {

        for (int i = 0; i < 10; i++) {
            final int finalI = i;
            new Thread(new Runnable() {

                @Override
                public void run() {

                    try {

                            userId = finalI;
                            Thread.sleep(1000);
                            System.out.println("userId:" + userId);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }).start();
        }


    }


    public static void executorTest() {
        //ExecutorService service = Executors.newSingleThreadExecutor();
        ExecutorService service = Executors.newFixedThreadPool(2);
        service.submit(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    try {
                        Thread.sleep(1000);
                        System.out.println("Executor1:" + i);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        service.submit(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    try {
                        Thread.sleep(1000);
                        System.out.println("Executor1:" + i);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        service.shutdown();

        while (!service.isTerminated()) {
            try {
                Thread.sleep(1000);
                System.out.println(service.isTerminated());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private static int count = 0;
    private static AtomicInteger atomicInteger = new AtomicInteger(0);
    public static void WithoutAtomicTest() {

        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < 10; j++) {
                        try {
                            Thread.sleep(100);
                            System.out.println(count++);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }).start();
        }

    }


    public static void WithAtomicTest() {

        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < 10; j++) {
                        try {
                            Thread.sleep(100);
                            System.out.println(atomicInteger.incrementAndGet());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }).start();
        }

    }

    public static void futureTest() {
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<Integer> future = service.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Thread.sleep(2000);
                return 1;
            }
        });

        service.shutdown();

        try {
            //System.out.println(future.get());
            System.out.println(future.get(10000,TimeUnit.MICROSECONDS));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void synchronizedTest() {
        BlockingQueue<String> q = new ArrayBlockingQueue<>(10);
        new Thread(new Producer(q)).start();
        new Thread(new Customer(q),"Customer1").start();
        new Thread(new Customer(q),"Customer2").start();
    }
    public static void main(String[] args) {
      // threadLocalTest();
        //executorTest();
        //WithAtomicTest();
        futureTest();
    }

}
