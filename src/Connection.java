import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class Connection {
    private HashMap<Integer, Queue<Message>> input = new HashMap<>();
    private HashMap<Integer, Queue<Message>> output = new HashMap<>();
    private HashSet<Integer> parentConnection = new HashSet<>();

    public Connection(int processId1, int processId2){
        Queue<Message> queue1 = new LinkedList<>();
        input.put(processId1, queue1);
        output.put(processId2, queue1);

        Queue<Message> queue2 = new LinkedList<>();
        input.put(processId2, queue2);
        output.put(processId1, queue2);

    }

    public void sendMessage(int myId, Message message){
        output.get(myId).add(message);
    }

    public Message getMessage(int myId){
        return input.get(myId).poll();
    }

    public boolean isParentConnection(int myId){
        if(parentConnection.contains(myId))
            return true;
        else
            return false;
    }

    public void hasParent(int myId){
        parentConnection.add(myId);
    }
}
