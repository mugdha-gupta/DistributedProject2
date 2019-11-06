
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
        //run only once
        initialize();

        //now process
        processMessages();

    }

    public void initialize(){
        sendMessages(new Message(myId, myId, INIT));

    }
    public void receiveMessages(){
        for(Connection connection : connections){
            Message message = connection.getMessage(myId);
            if(message != null)
                recievedMessages.put(connection, message);
        }
    }

    public void processMessages(){
        receiveMessages();
        while(!recievedMessages.isEmpty() || responseCounter != connections.size()){
            HashSet<Connection> connectionsToRemove = new HashSet<>();
            for (Connection connection: recievedMessages.keySet())
            {
                Message message = recievedMessages.get(connection);
                if (message.type == INIT) {
                    if (maxIdFound < message.maxIdFound){
                        setParent(message.senderid, connection);
                        sendMessages(new Message(myId, parent, INIT));
                        responseCounter = 0;
                    }
                    else {
                        sendResponse(new Message(myId, maxIdFound, DECLINE), connection);
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
                connectionsToRemove.add(connection);
            }
            for(Connection connection : connectionsToRemove)
                recievedMessages.remove(connection);
            receiveMessages();
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

    public void sendResponse(Message message, Connection connection){
        System.out.println("send response to potential parent here");
        connection.sendMessage(myId, message);
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
