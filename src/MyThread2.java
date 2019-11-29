import java.util.*;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

class MyThread2 extends Thread {

    public final int INIT = 0;
    public final int ACCEPT = 1;
    public final int DECLINE = 2;
    public final int COMPLETE = 3;
    public final int NOTMYPARENT = 3;
    public final int LEADER = 3;


    public CyclicBarrier barrier;
    public int myId;
    public int maxIdFound;
    public int parent;
    public Connection parentConnection;
    private ArrayList<Integer> children;
    public ArrayList<Connection> connections;
    public int responseCounter = 0;
    public int acceptCounter = 0;
    public int rejectCounter = 0;
    public int completeCounter = 0;
    public int messagesSent = 0;
    public HashMap<Connection, Message> recievedMessages;
    public boolean updatedParent = true;
    public boolean leaderFound = false;
    public boolean completed = false;


    public MyThread2(int my_id, ArrayList<Connection> my_neighbors, CyclicBarrier my_barrier) {
        //initialize our class variables
        myId = my_id;
        maxIdFound = my_id;
        parent = -1;
        parentConnection = null;
        connections = my_neighbors;
        recievedMessages = new HashMap<>();
        barrier = my_barrier;
        children = new ArrayList<>();
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

            //while (!completed) {
                processMessages();
//                barrier.await();
//                if (barrier.isBroken())
//                    barrier.reset();
            //}
            System.out.println(myId + "completed " + completed );




        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }

        System.out.println("Thread: " + myId + "\tLeader found: " + maxIdFound);
        if(parent == -1){
            for(Connection connection : connections){
                System.out.println("There were " + connection.getNumberMessages() + " messages sent." + myId);
                break;
            }
        }


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
    // and propagate the new maxIdFound to our children
    //if it received an INIT message but the maxIdFound of the message is not greater
    // simply send a decline message back to the sender
    //if it received an ACCEPT or DENY message, increment the response counter.
    //we use response counter to make sure all our children have responded to our message as received

    public void processMessages() {
        receiveMessages();
        //System.out.println(myId + " " + recievedMessages.toString());
        while (!recievedMessages.isEmpty() && !completed) {
            HashSet<Connection> connectionsToRemove = new HashSet<>();
            for (Connection connection : recievedMessages.keySet()) {
                Message message = recievedMessages.get(connection);
                System.out.println(myId + " from: " + message.senderid + " type: " + message.type + "  cur max: " + maxIdFound + " message max: " + message.maxIdFound);
                if (message.type == INIT) {
                    if (maxIdFound < message.maxIdFound) {
                        maxIdFound = message.maxIdFound;
                        setParent(message.senderid, connection);
                        children.clear();
                        sendMessages(new Message(myId, maxIdFound, INIT));
                        sendResponse(new Message(myId, maxIdFound, ACCEPT), connection);
                        responseCounter = 0;
                        acceptCounter = 0;
                        rejectCounter = 0;
                        completeCounter = 0;
                    } else {
                        if (connection != null)
                            sendResponse(new Message(myId, maxIdFound, DECLINE), connection);
                    }

                } else if (message.type == ACCEPT) {
                    if (maxIdFound == message.maxIdFound) {
                        responseCounter++;
                        acceptCounter++;
                        children.add(message.senderid);
                        System.out.println(myId + "'s new child is " + message.senderid);
                    }

                    responseCounter++;
                } else if (message.type == DECLINE) {
                    if (maxIdFound == message.maxIdFound) {
                        rejectCounter++;
                        System.out.println(myId + " got rejected by " + message.senderid);
                        if (children.contains(message.senderid)) {
                            acceptCounter--;
                            children.remove(message.senderid);
                            System.out.println(myId + " lost their child " + message.senderid);
                        }
                        else{
                            responseCounter++;
                        }
                        if (rejectCounter == connections.size()){
                            sendResponse(new Message(myId, maxIdFound, COMPLETE), parentConnection);
                            completed = true;
                        }

                    }
                } else if (message.type == COMPLETE) {
                    completeCounter++;
                    System.out.println(myId + "'s child " + message.senderid + " completed max: " + maxIdFound);
                    if (completeCounter == children.size()) {
                        sendResponse(new Message(myId, maxIdFound, COMPLETE), parentConnection);
                        System.out.println(myId + "i'm a completed internal node");
                        completed = true;
                    }

                }

                connectionsToRemove.add(connection);
            }
            for (Connection connection : connectionsToRemove)
                recievedMessages.remove(connection);
            connectionsToRemove.clear();
            receiveMessages();
        }
//        responseCounter = 0;
//        messagesSent = 0;
//        if (parentConnection != null)
//            sendResponse(new Message(myId, maxIdFound, ACCEPT), parentConnection);


        //ackNack();
        System.out.println(myId + recievedMessages.toString());
    }

    public void ackNack(){
        if (parent != -1 && completeCounter == connections.size()-1){
            System.out.println(myId +"-->"+ parent +" MAX="+ maxIdFound);
            //sendResponse(new Message(myId, maxIdFound, ACCEPT), parentConnection);
            leaderFound = true;
        }
        else if (parent == -1 && completeCounter == connections.size()){
            leaderFound = true;
            System.out.println(myId +"I'm the leader!!");
            //sendMessages(new Message(myId, maxIdFound, LEADER));
        }
    }

    //find every edge connected to this node and send the given message down the connection
    public void sendMessages(Message message) {
        for (Connection connection : connections) {
            if (!connection.isParentConnection(myId)) {
                connection.sendMessage(myId, message);
                messagesSent++;
            }
        }
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

        sendResponse(new Message(myId, maxIdFound, NOTMYPARENT), connection);

        parent = parentId;
        parentConnection = connection;
        for (Connection conn : connections) {
            if (conn.isParentConnection(myId))
                conn.removeParent();
        }
        connection.hasParent(myId);
        updatedParent = true;

        System.out.println(myId + "'s parent is " + parent + " max: " + maxIdFound);
    }

}
