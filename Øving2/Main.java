public class Main{
    public static void main(String[] args) {
        Workers worker_threads = new Workers(4);
        Workers event_loop = new Workers(1);

        Runnable a = new Runnable() {
            @Override
            public void run() {
                System.out.println("Hei fra task a");
            }
        };

        Runnable b = new Runnable() {
            @Override
            public void run() {
                System.out.println("Hei fra task b");
            }
        };

        Runnable c = new Runnable() {
            @Override
            public void run() {
                System.out.println("Hei fra task c");
            }
        };

        Runnable d = new Runnable() {
            @Override
            public void run() {
                System.out.println("Hei fra task d");
            }
        };

        worker_threads.start();
        event_loop.start();

        worker_threads.post(a);
        worker_threads.post(b);
        event_loop.post(c);
        event_loop.post(d);
        
        worker_threads.join();
        event_loop.join();
    }
}