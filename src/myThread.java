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

    public CyclicBarrier barrier;
    public CyclicBarrier done;
    public CountDownLatch leaderFoundLatch;

    public PipedInputStream inputStream;
    public PipedOutputStream outputStream;

    public int myId;
    public int maxIdFound;
    public int parent;
    ArrayList<Integer> children;

    public MyThread(CyclicBarrier my_b, CyclicBarrier my_done, CountDownLatch my_latch, PipedInputStream my_instream, PipedOutputStream my_outstream, int my_id) {
        //initialize our class variables
        barrier = my_b;
        done = my_done;
        leaderFoundLatch = my_latch;
        inputStream = my_instream;
        outputStream = my_outstream;
        myId = my_id;
        maxIdFound = my_id;

        //start from within constructor so main thread never has to call it
        start();
    }

    public void run()  {

    }

}