import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.*;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;



class MyThread extends Thread {

    public final int INIT = 0;
    public final int ACCEPT = 1;
    public final int DECLINE = 2;


    public int id;
    public int maxIdFound;
    public int parent;
    public ArrayList<Integer> neighbors;
    public Queue<Message> recievedMessages;
    public int responseCounter = 0;

    public MyThread(int my_id, ArrayList<Integer> my_neighbors) {

        id = my_id;
        maxIdFound = my_id;
        parent = -1;
        neighbors = my_neighbors;
        recievedMessages = new LinkedList<>();


        //start from within constructor so main thread never has to call it
        start();
    }

    public void run()  {
        initialize();
        receiveMessages();
        processMessages();

    }

    public void initialize(){
        System.out.println("initializing " + this.id);
        sendMessages(new Message(id, id, INIT));

    }
    public void receiveMessages(){
        System.out.println("receiving " + this.id);


    }
    public void processMessages(){
        System.out.println("processing "+ this.id);
        while (!recievedMessages.isEmpty())
        {
            Message message = recievedMessages.poll();
            if (message.type == INIT) {
                if (maxIdFound < message.maxIdFound){
                    parent = message.senderid;
                    sendMessages(new Message(id, parent, INIT));
                }
                else {
                    sendResponse(new Message(id, maxIdFound, DECLINE), message.senderid);

                }

            }
            else if (message.type == ACCEPT){
                responseCounter++;

            }
            else if (message.type == DECLINE){
                responseCounter++;
//                if (maxIdFound == message.maxIdFound) {
//                    responseCounter++;
//                }

            }
            else{
                System.out.println("invalid type of message");
            }

        }

    }

    public void sendMessages(Message message){
        System.out.println("send messages to neighbors here");

    }

    public void sendResponse(Message message, int parent){
        System.out.println("send response to potential parent here");

    }

}
