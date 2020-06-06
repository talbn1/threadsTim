package producer_consumer;

import com.sun.corba.se.impl.resolver.SplitLocalResolverImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.System.exit;
import static java.lang.System.setOut;

public class Main {

    public static final String EOF = "EOF";

    public static void main (String args[]){

        //ArrayList is unsafe for thread, the good list is vector
        //Thread pool example ""ExecutorService""

        ExecutorService executorService = Executors.newFixedThreadPool(3);
        List<String> buffer = new ArrayList<String>();
        ReentrantLock bufferLock = new ReentrantLock();

        MyProducer producer = new MyProducer(buffer,ThreadColor.ANSI_YELLOW, bufferLock);
        MyConsumer consumer1 = new MyConsumer(buffer,ThreadColor.ANSI_PURPLE, bufferLock);
        MyConsumer consumer2 = new MyConsumer(buffer,ThreadColor.ANSI_CYAN, bufferLock);


        // this is how you start threads on ExecutorService
        executorService.execute(producer);
        executorService.execute(consumer1);
        executorService.execute(consumer2);

        Future<String> future = executorService.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                System.out.println(ThreadColor.ANSI_WHITE + "Im being printed for the callable class");
                return "this is callable result";
            }
        });

        try {
            System.out.println(future.get());
        }catch (ExecutionException e){
            System.out.println("Something went wrong");
        }catch (InterruptedException e){
            System.out.println("thread running the task was interrupted");
        }

        //it is possible that some threads wont terminated so we need to have this next command.
        executorService.shutdown();

        //
/*
        new Thread(producer).start();
        new Thread(consumer1).start();
        new Thread(consumer2).start();
*/




/*      ---------- synchronized example variables --------------
        MyProducer producer = new MyProducer(buffer,ThreadColor.ANSI_YELLOW);
        MyConsumer consumer1 = new MyConsumer(buffer,ThreadColor.ANSI_PURPLE);
        MyConsumer consumer2 = new MyConsumer(buffer,ThreadColor.ANSI_CYAN);

        new Thread(producer).start();
        new Thread(consumer1).start();
        new Thread(consumer2).start();

*/

    }
}

//synchronized - ...cons... 1 the synchronized block must be within the same method.
// 2. we do not know what tread will be lock and we so not have any information about it.
// 3. if multiple threads wait for lock, it is not first come first enter.



///------------ Implementation with ReentrantLock------------------///

class MyProducer implements Runnable{

    private List<String> buffer;
    private String color;

    private ReentrantLock bufferLock;


    public MyProducer(List<String> buffer, String color, ReentrantLock bufferLock) {
        this.buffer = buffer;
        this.color = color;
        this.bufferLock = bufferLock;
    }



    public void run() {
        Random random = new Random();
        String[] nums = {"1","2","3","4","5"};

        for (String num:nums){
            try {
                System.out.println(color + "Adding..." + num);
                bufferLock.lock();
                try {
                    buffer.add(num);
                }finally {
                    bufferLock.unlock();
                }
                Thread.sleep(random.nextInt(1000));
            } catch (InterruptedException e) {
                System.out.println("Producer was interrupted");
            }
        }
        System.out.println(color + "Adding EOF and exit");
        bufferLock.lock();
        try {
            buffer.add("EOF");
        }finally {
            bufferLock.unlock();
        }


    }
}

    class MyConsumer implements Runnable{

        private ReentrantLock bufferLock;

        private List<String> buffer;
        String color;

        public MyConsumer(List<String> buffer, String color, ReentrantLock bufferLock) {
            this.buffer = buffer;
            this.color = color;
            this.bufferLock = bufferLock;
        }
        @Override
        public void run() {
            int counter=0;

            while(true){
                if(bufferLock.tryLock()){
                    try {
                        if(buffer.isEmpty()){
                            continue;
                        }
                        System.out.println(color+"the counter " + counter);
                        if(buffer.get(0).equals("EOF")){
                            System.out.println(color+"exit");
                            exit(0);
                        }else {
                            System.out.println(color+"Removed "+buffer.remove(0));
                        }
                    }finally {
                        bufferLock.unlock();
                    }
                }else{
                    counter++;
                }
            }
        }
}



///------------ Implementation with synchronized ------------------///

/*class MyProducer implements Runnable{

    private List<String> buffer;
    private String color;

    public MyProducer(List<String> buffer, String color) {
        this.buffer = buffer;
        this.color = color;
    }



    public void run() {
        Random random = new Random();
        String[] nums = {"1","2","3","4","5"};

        for (String num:nums){
            try {
                System.out.println(color + "Adding..." + num);
                synchronized (buffer){
                    buffer.add(num);
                }


                Thread.sleep(random.nextInt(1000));
            } catch (InterruptedException e) {
                System.out.println("Producer was interrupted");
            }
        }

        System.out.println(color + "Adding EOF and exit");

        synchronized (buffer){
            buffer.add("EOF");
        }

    }
}

class MyConsumer implements Runnable{

    private List<String> buffer;
    String color;

    public MyConsumer(List<String> buffer, String color) {
        this.buffer = buffer;
        this.color = color;
    }
    @Override
    public void run() {
        while(true){
            synchronized (buffer){
                if(buffer.isEmpty()){
                    continue;
                }
                if(buffer.get(0).equals("EOF")){
                    System.out.println(color+"exit");
                    exit(0);
                }else {
                    System.out.println(color+"Removed "+buffer.remove(0));
                }
            }
        }
    }
}*/



