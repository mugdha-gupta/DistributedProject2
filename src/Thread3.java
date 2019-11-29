import java.util.*;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

class MyThread3 extends Thread {

    public final int INIT = 0;
    public final int ACCEPT = 1;
    public final int DECLINE = 2;

    // tells us when processes have sent an acknowledge message
    public CountDownLatch latch;

    // process variables
    public int myId;
    public int maxIdFound;
    public int parent;

    // to make sending parent message easier
    public Connection parentConnection;

    // keep track of what children i send message to
    private ArrayList<Integer> children;

    // to keep track of all connections i hvae
    public ArrayList<Connection> connections;

    // for termination with convergecast
    public int responseCounter = 0;
    public int messagesSent = 0;

    // our "inbox"
    public HashMap<Connection, Message> recievedMessages;

    // the people we've sent ack in past
    public Set<Integer> setAck;
    boolean isIncremented = false;

    CyclicBarrier barrier;



    public MyThread3(int my_id, ArrayList<Connection> my_neighbors, CyclicBarrier my_barrier, CountDownLatch my_latch) {
        //initialize our class variables
        myId = my_id;
        maxIdFound = my_id;
        parent = -1;
        parentConnection = null;
        connections = my_neighbors;
        recievedMessages = new HashMap<>();
        children = new ArrayList<>();
        latch = my_latch;
        setAck = new HashSet<>();
        barrier = my_barrier;
        //start from within constructor so main thread never has to call it
        start();
    }

    // Initialize the algorithm by having all initial messages sent first
    // Then process the messages for diam rounds
    // After diam rounds, all threads will have the correct leader id as maxIdFound
    public void run() {

        sendMessages(new Message(myId, myId, INIT));

        while (latch.getCount() > 0) {
            processMessages();
            try {
                barrier.await(); // At the end of the node's phases, it calls await on the cb which stops the node until the next round.
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        }

        System.out.println(myId + "completed ");


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

        while (!recievedMessages.isEmpty()) {
            HashSet<Connection> connectionsToRemove = new HashSet<>();

            for (Connection connection : recievedMessages.keySet()) {
                Message message = recievedMessages.get(connection);
                //System.out.println(myId + " from: " + message.senderid + " type: " + message.type + "  cur max: " + maxIdFound + " message max: " + message.maxIdFound);
                if (message.type == INIT) {
                    if (maxIdFound < message.maxIdFound) {
                        maxIdFound = message.maxIdFound;
                        setParent(message.senderid, connection);
                        children.clear();
                        sendMessages(new Message(myId, maxIdFound, INIT));
                        //sendResponse(new Message(myId, maxIdFound, ACCEPT), connection);
                        responseCounter = 0;
                    } else {
                        if (connection != null)
                            sendResponse(new Message(myId, maxIdFound, DECLINE), connection);
                    }

                } else if (message.type == ACCEPT) {
                    if (maxIdFound == message.maxIdFound) {
                        responseCounter++;
                        children.add(message.senderid);
                        System.out.println(myId + "'s new child is " + message.senderid);
                    }

                } else if (message.type == DECLINE) {
                    if (maxIdFound == message.maxIdFound) {
                        responseCounter++;
                    }
                }

                connectionsToRemove.add(connection);
            }
            for (Connection connection : connectionsToRemove)
                recievedMessages.remove(connection);
            connectionsToRemove.clear();
        }

        ackNack();
    }

    public void ackNack(){
        if (parent != -1 && responseCounter == connections.size()){

            if(setAck.add(parent)){
                System.out.println(myId +"-->"+ parent +" MAX="+ maxIdFound);
                sendResponse(new Message(myId, maxIdFound, ACCEPT), parentConnection);

            }
            if(!isIncremented){
                latch.countDown();
                isIncremented = true;
            }


        }
        else if (parent == -1 && responseCounter == connections.size()){

            System.out.println(myId +"I'm the leader!!");
            //sendMessages(new Message(myId, maxIdFound, LEADER));
            latch.countDown();
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


        parent = parentId;
        parentConnection = connection;
        for (Connection conn : connections) {
            if (conn.isParentConnection(myId))
                conn.removeParent();
        }
        connection.hasParent(myId);

        System.out.println(myId + "'s parent is " + parent + " max: " + maxIdFound);
    }

}