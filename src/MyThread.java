
import java.util.*;
import java.util.concurrent.CyclicBarrier;

class MyThread extends Thread {

    public final int INIT = 0;
    public final int ACCEPT = 1;
    public final int DECLINE = 2;


    public CyclicBarrier barrier;
    public int myId;
    public int maxIdFound;
    public int parent;
    public ArrayList<Connection> connections;
    public int responseCounter = 0;
    public HashMap<Connection, Message> recievedMessages;

    public MyThread(int my_id, ArrayList<Connection> my_neighbors, CyclicBarrier my_barrier) {
        //initialize our class variables
        myId = my_id;
        maxIdFound = my_id;
        parent = -1;
        connections = my_neighbors;
        recievedMessages = new HashMap<>();
        barrier = my_barrier;

        //start from within constructor so main thread never has to call it
        start();
    }

    public void run()  {
        initialize();
        receiveMessages();
        processMessages();

    }

    public void initialize(){
        sendMessages(new Message(myId, myId, INIT));

    }
    public void receiveMessages(){
        recievedMessages.clear();
        for(Connection connection : connections){
            Message message = connection.getMessage(myId);
            if(message != null)
                recievedMessages.put(connection, connection.getMessage(myId));
        }
    }

    public void processMessages(){
        for (Connection connection: recievedMessages.keySet())
        {
            Message message = recievedMessages.get(connection);
            if (message.type == INIT) {
                if (maxIdFound < message.maxIdFound){
                    parent = message.senderid;
                    setParent(parent);
                    sendMessages(new Message(myId, parent, INIT));
                    responseCounter = 0;
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

            }
            else{
                System.out.println("invalid type of message");
            }

        }

    }

    public void sendMessages(Message message){
        for(Connection connection : connections){
            if(!connection.isParentConnection(myId))
                connection.sendMessage(myId, message);
        }
    }

    public void sendResponse(Message message, int parent){
        System.out.println("send response to potential parent here");

    }

    public void setParent(int parentId){
        
    }

}
