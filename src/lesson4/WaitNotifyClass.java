package lesson4;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WaitNotifyClass {
    private static final Object MONITOR = new Object();
    private static final int NUMBER_OF_REPEATS = 5;
    private static final int NUMBER_OF_THREADS_IN_POOL = 3;

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS_IN_POOL);
        executorService.execute(new LetterPrinter("A", "B", MONITOR, NUMBER_OF_REPEATS));
        executorService.execute(new LetterPrinter("B", "C", MONITOR, NUMBER_OF_REPEATS));
        executorService.execute(new LetterPrinter("C", "A", MONITOR, NUMBER_OF_REPEATS));
        executorService.shutdown();
    }
}