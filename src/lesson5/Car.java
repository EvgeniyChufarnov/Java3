package lesson5;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;

public class Car implements Runnable {
    private static int CARS_COUNT;
    private static String WINNER_MESSAGE_PREFIX = "Победитель: ";
    public static AtomicBoolean isSomeoneFinished;
    static {
        CARS_COUNT = 0;
    }
    private Race race;
    private int speed;
    private String name;
    private CyclicBarrier getReadyBarrier;
    private CountDownLatch raceFinishLatch;
    public String getName() {
        return name;
    }
    public int getSpeed() {
        return speed;
    }
    public Car(Race race, int speed, CyclicBarrier getReadyBarrier, CountDownLatch raceFinishLatch) {
        this.race = race;
        this.speed = speed;
        CARS_COUNT++;
        this.name = "Участник #" + CARS_COUNT;
        this.getReadyBarrier = getReadyBarrier;
        this.raceFinishLatch = raceFinishLatch;
        isSomeoneFinished = new AtomicBoolean(false);
    }
    @Override
    public void run() {
        try {
            System.out.println(this.name + " готовится");
            Thread.sleep(500 + (int)(Math.random() * 800));
            System.out.println(this.name + " готов");
            getReadyBarrier.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < race.getStages().size(); i++) {
            race.getStages().get(i).go(this);
        }
        raceFinishLatch.countDown();
        if (isSomeoneFinished.compareAndSet(false, true)) {
            showWinnerMessage();
        }
    }

    private void showWinnerMessage() {
        System.out.println(WINNER_MESSAGE_PREFIX + name);
    }
}