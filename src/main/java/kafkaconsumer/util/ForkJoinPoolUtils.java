package kafkaconsumer.util;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;

@Slf4j
public class ForkJoinPoolUtils {

    public static  <T> T execute(Callable<T> input) {
        final ForkJoinPool customThreadPool = new ForkJoinPool(ForkJoinPool.getCommonPoolParallelism() * 2 + 1);
        T result = null;
        try {
            result = customThreadPool.submit(input).get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error during stream execution.", e);
        } finally {
            customThreadPool.shutdown();
        }
        return result;
    }

}
