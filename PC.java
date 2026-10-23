import java.util.LinkedList;

public class PC {
    private final LinkedList<Integer> buffer = new LinkedList<>();
    private final int capacity = 2;
    private int nextValue;

    public void produce() {
        while (true) {
            synchronized (buffer) {
                while (buffer.size() == capacity) {
                    try {
                        buffer.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }

                int value = nextValue++;
                buffer.add(value);
                System.out.println("Producer produced-" + value);
                buffer.notify();
            }

            pause();
        }
    }

    public void consume() {
        while (true) {
            synchronized (buffer) {
                while (buffer.isEmpty()) {
                    try {
                        buffer.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }

                int value = buffer.removeFirst();
                System.out.println("Consumer consumed-" + value);
                buffer.notify();
            }

            pause();
        }
    }

    private void pause() {
        try {
            Thread.sleep(1_000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        PC pc = new PC();
        Thread producer = new Thread(pc::produce, "Producer");
        Thread consumer = new Thread(pc::consume, "Consumer");

        producer.start();
        consumer.start();

        producer.join();
        consumer.join();
    }
}
