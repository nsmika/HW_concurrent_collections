import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
    private static final int TEXT_COUNT = 10_000;
    private static final int TEXT_LENGTH = 100_000;
    private static final int QUEUE_CAPACITY = 100;

    private static final BlockingQueue<String> queueA = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
    private static final BlockingQueue<String> queueB = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
    private static final BlockingQueue<String> queueC = new ArrayBlockingQueue<>(QUEUE_CAPACITY);

    public static void main(String[] args) throws InterruptedException {
        Thread textGenerator = new Thread(() -> {
            try {
                for (int i = 0; i < TEXT_COUNT; i++) {
                    String text = generateText("abc", TEXT_LENGTH);
                    queueA.put(text);
                    queueB.put(text);
                    queueC.put(text);
                }
                queueA.put("ready");
                queueB.put("ready");
                queueC.put("ready");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread maxFinderA = new Thread(() -> {
            findMaxCharacter(queueA, 'a');
        });

        Thread maxFinderB = new Thread(() -> {
            findMaxCharacter(queueB, 'b');
        });

        Thread maxFinderC = new Thread(() -> {
            findMaxCharacter(queueC, 'c');
        });

        textGenerator.start();
        maxFinderA.start();
        maxFinderB.start();
        maxFinderC.start();


        textGenerator.join();
        maxFinderA.join();
        maxFinderB.join();
        maxFinderC.join();

    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static void findMaxCharacter(BlockingQueue<String> queue, char targetChar) {
        String maxText = null;
        int maxCount = 0;

        try {
            while (true) {
                String text = queue.take();
                if (text.equals("ready")) {
                    break;
                }
                int count = countChar(text, targetChar);
                if (count > maxCount) {
                    maxCount = count;
                    maxText = text;
                }
            }
            System.out.println("Текст с максимальным количеством букв " + targetChar + " (" + maxCount + "):\n" + maxText + "\n");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static int countChar(String text, char targetChar) {
        int count = 0;
        for (char c : text.toCharArray()) {
            if (c == targetChar) {
                count++;
            }
        }
        return count;
    }
}