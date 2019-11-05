import java.util.Random;

public class Message {

    int id;
    int maxIdFound;
    int type;
    int transmissionTime;

    final int INIT = 0;
    final int ACCEPT = 1;
    final int DECLINE = 2;

    public Message(int id, int maxId, int type) {
        this.id = id;
        this.maxIdFound = maxId;
        this.type = type;

        Random rand = new Random();
        this.transmissionTime =   rand.nextInt(10)+1;
    }
}
