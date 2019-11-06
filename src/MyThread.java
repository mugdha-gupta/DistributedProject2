import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

class MyThread extends Thread {


    public int myId;
    public int maxIdFound;
    int parent;
    ArrayList<Connection> connections;
    int responseCounter = 0;

    public MyThread(int my_id, ArrayList<Connection> my_neighbors) {
        //initialize our class variables
        myId = my_id;
        maxIdFound = my_id;
        parent = -1;
        connections = my_neighbors;

        //start from within constructor so main thread never has to call it
        start();
    }

    public void run()  {
        initialize();
        receiveMessages();
        processMessages();

    }

    public void initialize(){
        System.out.println("initializing " + this.id);

    }
    public void receiveMessages(){
        System.out.println("receiving " + this.id);

    }
    public void processMessages(){
        System.out.println("processing "+ this.id);

    }

}
