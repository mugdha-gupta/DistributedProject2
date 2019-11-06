import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Connection {
    private HashMap<Integer, Queue<Message>> input = new HashMap<>();
    private HashMap<Integer, Queue<Message>> output = new HashMap<>();
    private int idHasParentConnection = -1;

    public Connection(int processId1, int processId2){
        Queue<Message> queue1 = new LinkedList<>();
        input.put(processId1, queue1);
        output.put(processId2, queue1);

        Queue<Message> queue2 = new LinkedList<>();
        input.put(processId2, queue2);
        output.put(processId1, queue2);

    }

    public void sendMessage(int myId, Message message){
        Random rand = new Random();
        int transmissionTime =   rand.nextInt(10)+1;
        try {
            TimeUnit.MILLISECONDS.sleep(transmissionTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        output.get(myId).add(message);
    }

    public Message getMessage(int myId){
        Message message = input.get(myId).poll();
        return message;
    }

    public boolean isParentConnection(int myId){
        if(idHasParentConnection == myId)
            return true;
        else
            return false;
    }

    public void hasParent(int myId){
        idHasParentConnection = myId;
    }

    public void removeParent(){
        idHasParentConnection = -1;
    }
}
