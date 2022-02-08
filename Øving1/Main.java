import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.stream.Collectors;

class PrimeNumbersWithThreads{
    public static void findPrimes(int numThreads, int start, int end) {
        PrimeThread.primeList = new ArrayBlockingQueue<>(end); // Guaranteed to be enough
        PrimeThread[] pThreads = new PrimeThread[numThreads];
        int step = (end - start) / numThreads + 1;
        for(int i = 0; i<numThreads; i++){
            pThreads[i] = new PrimeThread(i * step + start, Math.min(end, (i + 1) * step - 1) + start);
            pThreads[i].start();
        }

        try {
            for (int i = 0; i < numThreads; i++) {
                pThreads[i].join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Total prime count: " + PrimeThread.primeList.size()); // Output total amount of primes from the Array List
        List<Integer> sortedPrimes = PrimeThread.primeList.stream().sorted().collect(Collectors.toList());
        System.out.println(sortedPrimes);

    }
}

class PrimeThread extends Thread {
    static ArrayBlockingQueue<Integer> primeList;
    int start, end;

    public PrimeThread(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public synchronized void run() {

        for(int i = start; i <= end; i++){
            int counter = 0;
            for(int num = i - 1; num > 1; num--){

                //condition to check if the number is prime
                if(i % num == 0){

                    //increment counter
                    counter = counter + 1;
                }
            }

            if(counter == 0){
                primeList.add(i);
            }
        }
    }
}

class Main {
    public static void main(String[] args) {
        PrimeNumbersWithThreads.findPrimes(4,0,1000);
    }
}