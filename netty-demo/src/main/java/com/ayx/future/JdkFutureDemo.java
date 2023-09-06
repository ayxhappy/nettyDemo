package com.ayx.future;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

@Slf4j
public class JdkFutureDemo {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService threadPool = Executors.newFixedThreadPool(2);

        Future<Integer> future = threadPool.submit(() -> {
            log.info("计算结果中");
            TimeUnit.SECONDS.sleep(1);
            return 50;
        });
        log.info("等待结果中");
        log.info("计算结果为：{}",future.get());
    }
}
