package runnable_threads;

public class Main {
    public static void main (String[] args){
        System.out.println("Hello from the main thread");

        Thread anotherThread = new AnotherThread();
        anotherThread.setName("== another thread");
        anotherThread.start();

        new Thread(){
            @Override
            public void run() {
                System.out.println("--Hello from the anonymous thread");
            }
        }.start();


        Thread myRunnableThread = new Thread(new MyRunnable(){
            @Override
            public void run(){
                System.out.println("----hello from anonymous implementation of my Runnable");
                try {
                    anotherThread.join(2000);
                    System.out.println("----AnotherTread terminated, so im running again");

                }catch (InterruptedException e){
                    System.out.println("---- I was interrupted ");
                }
            }
        });
        myRunnableThread.start();
        anotherThread.interrupt();

        System.out.println("Hello again from the main thread");


    }
}
