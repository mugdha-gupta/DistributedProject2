
import java.util.*;



class MyThread extends Thread {

    public final int INIT = 0;
    public final int ACCEPT = 1;
    public final int DECLINE = 2;


    public int myId;
    public int maxIdFound;
    int parent;
    ArrayList<Connection> connections;
    int responseCounter = 0;
    public HashMap<Connection, Message> recievedMessages;
    
    public MyThread(int my_id, ArrayList<Connection> my_neighbors) {
        //initialize our class variables
        myId = my_id;
        maxIdFound = my_id;
        parent = -1;
        connections = my_neighbors;
        recievedMessages = new HashMap<>();

        //start from within constructor so main thread never has to call it
        start();
    }

    public void run()  {
        initialize();
        receiveMessages();
        processMessages();

    }

    public void initialize(){
        System.out.println("initializing " + myId);
        sendMessages(new Message(myId, myId, INIT));

    }
    public void receiveMessages(){
        System.out.println("receiving " + myId);
        recievedMessages.clear();
        for(Connection connection : connections){
            Message message = connection.getMessage(myId);
            if(message != null)
                recievedMessages.put(connection, connection.getMessage(myId));
        }
    }

    public void processMessages(){
        System.out.println("processing "+ myId);
        for (Connection connection: recievedMessages.keySet())
        {
            Message message = recievedMessages.get(connection);
            if (message.type == INIT) {
                if (maxIdFound < message.maxIdFound){
                    parent = message.senderid;
                    connection.hasParent(myId);
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
        for(Connection connection : connections){
            if(!connection.isParentConnection(myId))
                connection.sendMessage(myId, message);
        }
    }

    public void sendResponse(Message message, int parent){
        System.out.println("send response to potential parent here");

    }

}
