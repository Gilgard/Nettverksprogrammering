import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Workers{
    private Lock jobQueueLock = new ReentrantLock();
    private Queue<Runnable> jobQueue = new ConcurrentLinkedQueue<>();
    private Condition jobAvailable = jobQueueLock.newCondition();

    private Lock workerListLock = new ReentrantLock();
    private Queue<Thread> workers;
    private Condition availableWorker = workerListLock.newCondition();

    private boolean isRunning = false;

    public Workers(int numberOfThreads) {
        this.workers = new ArrayBlockingQueue<>(numberOfThreads);
    }

    public void start() {
        isRunning = true;
        work().start();
    }

    private Thread work() {
        return new Thread(() -> {

            while(isRunning) {
                jobQueueLock.lock();

                //====================================================
                //Todo: gjør om runnable til jobb
                //====================================================
                
                while (jobQueue.isEmpty() || jobQueue.peek().startTime > System.currentTimeMillis()) {
                    try {
                        if(!jobQueue.isEmpty()) {
                            long waitTime = jobQueue.peek().startTime - System.currentTimeMillis();
                            // Waiting for job-start time
                            jobAvailable.await(waitTime, TimeUnit.MILLISECONDS);
                        } else {
                            // Waiting for new jobs to arrive in availableJobsList
                            jobAvailable.await();
                        }

                        if(!isRunning) {
                            //Consumer-thread will now exit to respect join()
                            return;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                // Adding new job to active jobs-list.
                workerListLock.lock();

                Thread jobThread = new Thread(jobQueue.poll());
                while(!workers.offer(jobThread)) {
                    try {
                        availableWorker.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void post(Runnable runnable) {
        jobQueueLock.lock();
        jobQueue.add(runnable);
        jobAvailable.signalAll();
        jobQueueLock.unlock();
    }

    public void join() {
        joinAvailableJobs();
        joinAvailableWorkers();
    }

    private void joinAvailableJobs() {
        jobQueueLock.lock();
        while (!jobQueue.isEmpty()) {
            try {
                jobAvailable.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        isRunning = false;
        jobAvailable.signalAll();
        jobQueueLock.unlock();
    }
    
    public void joinAvailableWorkers() {
        workerListLock.lock();
        while(!workers.isEmpty()) {
            try {
                availableWorker.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        availableWorker.signalAll();
        workerListLock.unlock();
    }
}
