import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Connection {
    private HashMap<Integer, Queue<Message>> input = new HashMap<>();
    private HashMap<Integer, Queue<Message>> output = new HashMap<>();
    private int idHasParentConnection = -1;
    private AtomicInteger counter;

    //initialize connection information
    public Connection(int processId1, int processId2, AtomicInteger counter){
        Queue<Message> queue1 = new LinkedList<>();
        input.put(processId1, queue1);
        output.put(processId2, queue1);

        Queue<Message> queue2 = new LinkedList<>();
        input.put(processId2, queue2);
        output.put(processId1, queue2);

        this.counter = counter;
    }

    //function to send the message over the connection, given the source id and the message
    //we send message
    //and increment our counter to keep track of the total number of messages sent
    public void sendMessage(int myId, Message message){
        message.sendMessage();
        output.get(myId).add(message); //sends messages
        //nummessages ++
        if(message.type != 3)
            counter.getAndIncrement();
    }

    //we return any ready messages
    public Message getMessage(int myId){
        Message message = input.get(myId).peek();
        if(message != null && message.isReady())
            return input.get(myId).poll();
        else
            return null;
    }

    //used to flag a connection as a parent edge so that we don't send duplicate messages
    public boolean isParentConnection(int myId){
        if(idHasParentConnection == myId)
            return true;
        else
            return false;
    }

    //manipulation of the parent of the connection
    public void hasParent(int myId){
        idHasParentConnection = myId;
    }

    //no longer is a parent connection for this id
    public void removeParent(){
        idHasParentConnection = -1;
    }

    public int getNumberMessages(){
        return counter.get();
    }
}
