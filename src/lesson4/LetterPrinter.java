package lesson4;

public class LetterPrinter implements Runnable {
    private static volatile String currentLetter = null;

    private final String letter;
    private final String nextLetter;
    private final Object monitor;
    private final int numberOfRepeats;

    public LetterPrinter(String letter, String nextLetter, Object monitor, int numberOfRepeats) {
        this.letter = letter;
        this.nextLetter = nextLetter;
        this.monitor = monitor;
        this.numberOfRepeats = numberOfRepeats;

        if (currentLetter == null) {
            currentLetter = letter;
        }
    }

    @Override
    public void run() {
        synchronized (monitor) {
            printLetter();
        }
    }

    private void printLetter() {
        try {
            for (int i = 0; i < numberOfRepeats; i++) {
                while (!currentLetter.equals(letter)) {
                    monitor.wait();
                }

                System.out.print(letter);
                currentLetter = nextLetter;
                monitor.notifyAll();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
