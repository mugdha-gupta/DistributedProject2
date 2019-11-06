
import java.util.*;
import java.util.concurrent.BrokenBarrierException;
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
        receiveMessages();
        processMessages();
        initialize();
        try {
            barrier.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
        if(barrier.isBroken())
            barrier.reset();

    }

    public void initialize(){
        sendMessages(new Message(myId, myId, INIT));

    }
    public void receiveMessages(){
        recievedMessages.clear();
        for(Connection connection : connections){
            Message message = connection.getMessage(myId);
            if(message != null)
                recievedMessages.put(connection, message);
        }
    }

    public void processMessages(){
        for (Connection connection: recievedMessages.keySet())
        {
            Message message = recievedMessages.get(connection);
            if (message.type == INIT) {
//                System.out.println("init " + myId + " " + message.senderid );
                if (maxIdFound < message.maxIdFound){
                    setParent(message.senderid, connection);
                    sendMessages(new Message(myId, parent, INIT));
                    responseCounter = 0;
                }
                else {
                    sendResponse(new Message(myId, maxIdFound, DECLINE), message.senderid);

                }

            }
            else if (message.type == ACCEPT){
                System.out.println("accept " + myId + " " + message.senderid );

                responseCounter++;

            }
            else if (message.type == DECLINE){
                System.out.println("decline " + myId + " " + message.senderid );

                responseCounter++;

            }
            else{
                System.out.println("invalid type of message");
            }

        }

    }

    public void sendMessages(Message message){
        for(Connection connection : connections){
            if(!connection.isParentConnection(myId)){
                System.out.println("sending " + myId );
                connection.sendMessage(myId, message);
            }

        }
    }

    public void sendResponse(Message message, int parent){
        System.out.println("send response to potential parent here");

    }

    public void setParent(int parentId, Connection connection){
        parent = parentId;
        for(Connection conn : connections){
            if(conn.isParentConnection(myId))
                conn.removeParent();
        }
        connection.hasParent(myId);
    }

}
