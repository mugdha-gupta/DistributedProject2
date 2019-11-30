import java.time.LocalDateTime;
import java.util.Random;

//Class to kee track of the message information
//this instance is sent over the connections between threads
//for communication
public class Message {

    static final int INIT = 0;
    static final int ACCEPT = 1;
    static final int DECLINE = 2;
    static final int DUMMY = 3;
    static final int LEADER = 4;

    int senderid;
    int maxIdFound;
    int type;
    int delay;
    int round;
    LocalDateTime readyTime;

    public Message(int id, int maxId, int type, int round) {
        this.senderid = id;
        this.maxIdFound = maxId;
        this.type = type;
        Random rand = new Random();
        delay = rand.nextInt(10)+1;
        readyTime = null;
        this.round = round;
    }

    public void sendMessage(){
        readyTime = LocalDateTime.now().plusNanos(delay);
    }

    public boolean isReady(){
        if(readyTime == null)
            return false;
        if(readyTime.isBefore(LocalDateTime.now()) || readyTime.isEqual(LocalDateTime.now()))
            return true;
        else
            return false;
    }


}