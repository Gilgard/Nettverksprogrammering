import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Workers{
    private Lock jobQueueLock = new ReentrantLock();
    private Queue<Job> jobQueue = new ConcurrentLinkedQueue<>();
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
                findWorkerForJob(jobQueue.poll().runnable);
                jobAvailable.signalAll();
            }
        });
    }

    private void findWorkerForJob(Runnable runnable) {
        workerListLock.lock();

        Thread jobThread = new Thread(runnable);
        while(!workers.offer(jobThread)) {
            try {
                availableWorker.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Creates separate thread to follow the thread state, and remove it from active jobs once completed.
        Thread waitThread = new Thread(() -> {
            jobThread.start();

            try {
                jobThread.join();

                workerListLock.lock();
                workers.remove(jobThread);
                availableWorker.signalAll();
                workerListLock.unlock();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        waitThread.start();
        workerListLock.unlock();
    }

    public void post(Runnable runnable){
        Job job = new Job(runnable, 0);
        addJobToQueue(job);
    }

    public void postTimeOut(Runnable runnable, long waitTime) {
        Job job = new Job(runnable, System.currentTimeMillis() + waitTime);
        addJobToQueue(job);
    }

    private void addJobToQueue(Job job) {
        jobQueueLock.lock();
        jobQueue.add(job);
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
    
    private void joinAvailableWorkers() {
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

class Job {
    Runnable runnable;
    long startTime;

    public Job(Runnable runnable, long startTime) {
        this.runnable = runnable;
        this.startTime = startTime;
    }
}

