
import java.util.*;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

class MyThread extends Thread {

    public final int INIT = 0;
    public final int ACCEPT = 1;
    public final int DECLINE = 2;


    public CyclicBarrier barrier;
    public int myId;
    public int maxIdFound;
    public int parent;
    public Connection parentConnection;
    public ArrayList<Connection> connections;
    public int responseCounter = 0;
    public int messagesSent = 0;
    public HashMap<Connection, Message> recievedMessages;
    public boolean updatedParent = true;

    public int diam = 5;

    public MyThread(int my_id, ArrayList<Connection> my_neighbors, CyclicBarrier my_barrier) {
        //initialize our class variables
        myId = my_id;
        maxIdFound = my_id;
        parent = -1;
        parentConnection = null;
        parentConnection = null;
        connections = my_neighbors;
        recievedMessages = new HashMap<>();
        barrier = my_barrier;

        //start from within constructor so main thread never has to call it
        start();
    }

    public void run() {

        //run only once
        initialize();

        try {
            barrier.await();
            if (barrier.isBroken())
                barrier.reset();

            for (int i = 0; i < diam;) {

                processMessages(i);
                sendMessages(new Message(myId, maxIdFound, INIT), i);
                barrier.await();
                i++;
                if (barrier.isBroken())
                    barrier.reset();
            }
        }catch(InterruptedException | BrokenBarrierException e){
            e.printStackTrace();
        }

        System.out.println("MAX IS " + maxIdFound);


    }

    public void initialize(){
        sendMessages(new Message(myId, myId, INIT));
    }

    public void receiveMessages(){
        for(Connection connection : connections){
            Message message = connection.getMessage(myId);
            if(message != null){
                System.out.println(myId + "'s message " + message.senderid + " " +  message.type );
                recievedMessages.put(connection, message);
            }
        }
    }

    public void processMessages(int i){
        receiveMessages();
        if(myId == 2 && i ==1)
            System.out.println("yo " + recievedMessages.size());
        while(responseCounter < messagesSent || !recievedMessages.isEmpty()){
            //System.out.println(myId + " " + i + " " + responseCounter + " " + messagesSent );
            HashSet<Connection> connectionsToRemove = new HashSet<>();
            for (Connection connection: recievedMessages.keySet())
            {
                Message message = recievedMessages.get(connection);
                if(myId == 2 && message.senderid ==1)
                    System.out.println("type " + message.type);
                System.out.println();
                if (message.type == INIT) {
                    if (maxIdFound < message.maxIdFound){
                        maxIdFound = message.maxIdFound;
                        setParent(message.senderid, connection);
                    }
                    else {
                        sendResponse(new Message(myId, maxIdFound, DECLINE), connection);
                    }

                }
                else if (message.type == ACCEPT){
                    responseCounter++;
                }
                else if (message.type == DECLINE){
                    responseCounter++;
                }
                else{
                }
                connectionsToRemove.add(connection);
            }
            for(Connection connection : connectionsToRemove)
                recievedMessages.remove(connection);
            connectionsToRemove.clear();
            receiveMessages();
        }
        responseCounter = 0;
        messagesSent = 0;
        sendResponse(new Message(myId, maxIdFound, ACCEPT), parentConnection);

    }
    public void sendMessages(Message message, int round){
        System.out.println(myId + " " + updatedParent + " " + round);
        if(updatedParent == false)
            return;
        if(myId == 1)
        System.out.println("parenet of 1" + parent);
        for(Connection connection : connections){
            if(!connection.isParentConnection(myId)){
                if(myId ==1 && round ==1)
                System.out.println(myId + "sending");
                connection.sendMessage(myId, message);
                messagesSent++;
            }
        }
        updatedParent = false;
    }
    public void sendMessages(Message message){
        if(updatedParent == false)
            return;
        for(Connection connection : connections){
            if(!connection.isParentConnection(myId)){
                System.out.println(myId + "sending");
                connection.sendMessage(myId, message);
                messagesSent++;
            }
        }
        updatedParent = false;
    }

    public void sendResponse(Message message, Connection connection){
        if(connection != null)
            connection.sendMessage(myId, message);
    }

    public void setParent(int parentId, Connection connection){
        if(parentId != -1)
            sendResponse(new Message(myId, maxIdFound, DECLINE), parentConnection);
        parent = parentId;
        parentConnection = connection;
        for(Connection conn : connections){
            if(conn.isParentConnection(myId))
                conn.removeParent();
        }
        connection.hasParent(myId);
        updatedParent = true;
    }

}
