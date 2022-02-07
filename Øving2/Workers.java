import java.util.concurrent.locks.Condition;

public class Workers{
    private int numberOfThreads;
    private Thread[] threads;
    private Condition lock;

    public Workers(int numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
        this.threads = new Thread[numberOfThreads];
    }

    public void start() {
        for(int i = 0; i < numberOfThreads; i++) {
            threads[i] = new Thread();
            threads[i].start();
        }
    }

    public void post(Runnable runnable) {
        while()
    }

    public void join() {
        for(Thread i : threads){
            try {
                i.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
