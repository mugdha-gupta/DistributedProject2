import java.util.Random;

public class Message {

    int senderid;
    int maxIdFound;
    int type;
    int transmissionTime;

    final int INIT = 0;
    final int ACCEPT = 1;
    final int DECLINE = 2;

    public Message(int id, int maxId, int type) {
        this.senderid = id;
        this.maxIdFound = maxId;
        this.type = type;

        Random rand = new Random();
        this.transmissionTime =   rand.nextInt(10)+1;
    }
}
