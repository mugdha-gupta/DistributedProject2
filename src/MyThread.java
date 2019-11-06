
import java.util.*;

class MyThread extends Thread {

    public final int INIT = 0;
    public final int ACCEPT = 1;
    public final int DECLINE = 2;


    public int myId;
    public int maxIdFound;
    public int parent;
    public ArrayList<Connection> connections;
    public int responseCounter = 0;
    public Queue<Message> receivedMessages;
    
    public MyThread(int my_id, ArrayList<Connection> my_neighbors) {
        //initialize our class variables
        myId = my_id;
        maxIdFound = my_id;
        parent = -1;
        connections = my_neighbors;
        receivedMessages = new LinkedList<>();

        //start from within constructor so main thread never has to call it
        start();
    }

    public void run()  {
        initialize();
        receiveMessages();
        processMessages();

    }

    public void initialize(){
        //System.out.println("initializing " + myId);
        sendMessages(new Message(myId, myId, INIT));

    }
    public void receiveMessages(){
        //System.out.println("receiving " + myId);


    }
    public void processMessages(){
        //System.out.println("processing "+ myId);
        while (!receivedMessages.isEmpty())
        {
            Message message = receivedMessages.poll();
            if (message.type == INIT) {
                if (maxIdFound < message.maxIdFound){
                    parent = message.senderid;
                    sendMessages(new Message(myId, parent, INIT));
                }
                else {
                    sendResponse(new Message(myId, maxIdFound, DECLINE), message.senderid);

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
