import java.util.Random;

public class Message {

    int id;
    int maxIdFound;
    String type;
    int transmissionTime;

    public Message(int id, int maxId, String type) {
        this.id = id;
        this.maxIdFound = maxId;
        this.type = type;

        Random rand = new Random();
        this.transmissionTime =   rand.nextInt(10)+1;
    }
}
