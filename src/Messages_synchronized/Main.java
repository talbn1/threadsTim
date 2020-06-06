package Messages_synchronized;

import java.util.Random;

public class Main {

    public static void main (String[] args){

        Message message = new Message();

        new Thread(new Writer(message)).start();
        new Thread(new Reader(message)).start();

    }
}

class Message {

    private String message;
    private boolean empty = true;

    public synchronized String read(){
        //if we will leave while as true, we will have a deadlock because in snyc block only one thread can run, and it is locked
        //wait will wait for interruptions
        while(empty){

            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        empty = true;
        notifyAll();
        return message;
    }
    public synchronized void write(String message){
        while(!empty){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        empty = false;
        this.message = message;
        notifyAll();
    }
}

class Writer implements Runnable{

    private Message message;

    public Writer(Message message) {
        this.message = message;
    }

    @Override
    public void run() {

        String messages[] = {"This is message 1","This is message 2","This is message 3","This is message 4","This is message 5","This is message 6"};
        Random random = new Random();

        for (int i = 0; i < messages.length ; i++) {
            message.write(messages[i]);

            try {
                Thread.sleep(random.nextInt(2000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        message.write("Finished");


    }


}


class Reader implements  Runnable{

    private Message message;

    public Reader(Message message){
        this.message = message;
    }

    public void run() {
        Random random = new Random();
        for (String latestMessages = message.read(); !latestMessages.equals("Finished") ; latestMessages = message.read()) {
            System.out.println(latestMessages);
            try {
                Thread.sleep(random.nextInt(2000));
            }catch (InterruptedException e){

            }
        }
    }
}
