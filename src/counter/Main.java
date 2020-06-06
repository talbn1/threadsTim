package counter;

public class Main {
    public static void main (String[] args){

        Countdown countdown = new Countdown();

        CountdownThread t1 = new CountdownThread(countdown);
        t1.setName("Thread 1");
        CountdownThread t2 = new CountdownThread(countdown);
        t2.setName("Thread 2");
        CountdownThread t3 = new CountdownThread(countdown);
        t3.setName("Thread 3");



        t1.start();
        t2.start();
        t3.start();
    }
}

class Countdown{

//option one, synchronized in function deceleration
    public void doCountdown(){
        String color;

        switch(Thread.currentThread().getName()) {
            case "Thread 1":
                color = ThreadColor.ANSI_CYAN;
                break;
            case "Thread 2":
                color = ThreadColor.ANSI_PURPLE;
                break;
            default:
                color = ThreadColor.ANSI_GREEN;
                break;
        }

        //option two, synchronized in block deceleration
        //When we synchronize code we should only synchronized the part that need to be synchronize (absolute minimum)
        //In this case, only for needed to be synchronized
        synchronized (this){
            for (int i = 10; i > 0; i--) {
                System.out.println(color+Thread.currentThread().getName() + ": i=" +i);
            }
        }



    }
}

class CountdownThread extends Thread{

    private Countdown threadCountdown;
    public CountdownThread(Countdown countdown){
        threadCountdown = countdown;
    }

    public void run(){
        threadCountdown.doCountdown();
    }
}
