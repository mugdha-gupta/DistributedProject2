public class Message {

    int senderid;
    int maxIdFound;
    int type;

    public Message(int id, int maxId, int type) {
        this.senderid = id;
        this.maxIdFound = maxId;
        this.type = type;
    }
}
