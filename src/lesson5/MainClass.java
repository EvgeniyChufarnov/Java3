package lesson5;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

public class MainClass {
    public static final int CARS_COUNT = 4;
    public static final String START_RACE_MESSAGE = "ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка началась!!!";
    public static final String FINISH_RACE_MESSAGE = "ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка закончилась!!!";
    public static CyclicBarrier getReadyBarrier;
    public static CountDownLatch raceFinishLatch;

    public static void main(String[] args) {
        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Подготовка!!!");
        Race race = new Race(new Road(60), new Tunnel(CARS_COUNT/2), new Road(40));
        Car[] cars = new Car[CARS_COUNT];
        getReadyBarrier = new CyclicBarrier(cars.length, MainClass::showStartRaceMassage);
        raceFinishLatch = new CountDownLatch(cars.length);

        for (int i = 0; i < cars.length; i++) {
            cars[i] = new Car(race, 20 + (int) (Math.random() * 10), getReadyBarrier, raceFinishLatch);
        }
        for (int i = 0; i < cars.length; i++) {
            new Thread(cars[i]).start();
        }

        waitForFinish();
        showFinishRaceMassage();
    }

    private static void waitForFinish() {
        try {
            raceFinishLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void showStartRaceMassage() {
        System.out.println(START_RACE_MESSAGE);
    }

    private static void showFinishRaceMassage() {
        System.out.println(FINISH_RACE_MESSAGE);
    }
}


