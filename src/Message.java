//Class to kee track of the message information
//this instance is sent over the connections between threads
//for communication
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
