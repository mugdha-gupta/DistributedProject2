
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
    public Connection parentConnection;
    public ArrayList<Connection> connections;
    public int responseCounter = 0;
    public int messagesSent = 0;
    public HashMap<Connection, Message> recievedMessages;
    public boolean updatedParent = true;

    public int diam = 0;

    public MyThread(int my_id, ArrayList<Connection> my_neighbors, CyclicBarrier my_barrier, int my_diam) {
        //initialize our class variables
        myId = my_id;
        maxIdFound = my_id;
        parent = -1;
        parentConnection = null;
        parentConnection = null;
        connections = my_neighbors;
        recievedMessages = new HashMap<>();
        barrier = my_barrier;
        diam = my_diam;
        //start from within constructor so main thread never has to call it
        start();
    }

    // Initialize the algorithm by having all initial messages sent first
    // Then process the messages for diam rounds
    // After diam rounds, all threads will have the correct leader id as maxIdFound
    public void run() {

        sendMessages(new Message(myId, myId, INIT));

        try {
            barrier.await();
            if (barrier.isBroken())
                barrier.reset();

            for (int i = 0; i < diam; ) {

                processMessages(i);
                sendMessages(new Message(myId, maxIdFound, INIT), i);
                barrier.await();
                i++;
                if (barrier.isBroken())
                    barrier.reset();
            }
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }

        System.out.println("Thread: " + myId + "\tLeader found: " + maxIdFound);


    }


    //check all the connections to see fi any messages have been sent to this thread
    //if there is a message, add it to the list of messages we need to process
    public void receiveMessages() {
        for (Connection connection : connections) {
            Message message = connection.getMessage(myId);
            if (message != null) {
                recievedMessages.put(connection, message);
            }
        }
    }

    //while there are messages left to process, respond accordingly
    //if it received an INIT message, check if the new maxIdFound is greater than our current one
    //and if it is greater, set this sender as our new parent
    //and once the final parent has been decided, send an ACCEPT message to the sender (now parent)
    // and propogate the new maxIdFound to our children
    //if it received an INIT message but the maxIdFound of the message is not greater
    // simply send a decline message back to the sender
    //if it received an ACCEPT or DENY message, increment the response counter.
    //we use response counter to make sure all our children have responded to our message as received

    public void processMessages(int i) {
        receiveMessages();
        while (responseCounter < messagesSent || !recievedMessages.isEmpty()) {
            HashSet<Connection> connectionsToRemove = new HashSet<>();
            for (Connection connection : recievedMessages.keySet()) {
                Message message = recievedMessages.get(connection);
                if (message.type == INIT) {
                    if (maxIdFound < message.maxIdFound) {
                        maxIdFound = message.maxIdFound;
                        setParent(message.senderid, connection);
                    } else {
                        if (connection != null)
                            sendResponse(new Message(myId, maxIdFound, DECLINE), connection);
                    }

                } else if (message.type == ACCEPT) {
                    responseCounter++;
                } else if (message.type == DECLINE) {
                    responseCounter++;
                }
                connectionsToRemove.add(connection);
            }
            for (Connection connection : connectionsToRemove)
                recievedMessages.remove(connection);
            connectionsToRemove.clear();
            receiveMessages();
        }
        responseCounter = 0;
        messagesSent = 0;
        if (parentConnection != null)
            sendResponse(new Message(myId, maxIdFound, ACCEPT), parentConnection);

    }

    //find every edge connected to this node and send the given message down the connection
    public void sendMessages(Message message, int round) {
        if (updatedParent == false)
            return;
        for (Connection connection : connections) {
            if (!connection.isParentConnection(myId)) {
                connection.sendMessage(myId, message);
                messagesSent++;
            }
        }
        updatedParent = false;
    }

    public void sendMessages(Message message) {
        if (updatedParent == false)
            return;
        for (Connection connection : connections) {
            if (!connection.isParentConnection(myId)) {
                connection.sendMessage(myId, message);
                messagesSent++;
            }
        }
        updatedParent = false;
    }

    //send the given message to the specific node down the specified connection
    public void sendResponse(Message message, Connection connection) {
        if (connection != null) {
            connection.sendMessage(myId, message);
        }
    }

    //keep track of the parent's edge so that we don't send back the same message to them
    //and if there already was a previous parent, remove that connection
    //and set the new parent connection
    public void setParent(int parentId, Connection connection) {
        if (parent != -1) {
            if (parentConnection != null)
                sendResponse(new Message(myId, maxIdFound, DECLINE), parentConnection);
        }
        parent = parentId;
        parentConnection = connection;
        for (Connection conn : connections) {
            if (conn.isParentConnection(myId))
                conn.removeParent();
        }
        connection.hasParent(myId);
        updatedParent = true;
    }

}
