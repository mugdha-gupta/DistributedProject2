import java.time.LocalDateTime;
import java.util.Random;

//Class to kee track of the message information
//this instance is sent over the connections between threads
//for communication
public class Message {

    int senderid;
    int maxIdFound;
    int type;
    int delay;
    LocalDateTime readyTime;

    public Message(int id, int maxId, int type) {
        this.senderid = id;
        this.maxIdFound = maxId;
        this.type = type;
        Random rand = new Random();
        delay = rand.nextInt(10)+1;
        readyTime = null;
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