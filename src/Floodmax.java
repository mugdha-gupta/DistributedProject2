//Nymisha Jahagirdar and Mugdha Gupta
//CS 6380 Project 2

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

public class Floodmax {

    public static void main(String[] args) {
        HashMap<Integer, ArrayList<Integer>> neighborhood;
        HashMap<Integer, ArrayList<Connection>> connections;

//        //input processing
//        if(args.length < 1){
//            System.out.println("No path for the input file given.");
//            return;
//        }

        File file = new File("C:\\Users\\Nymisha\\IdeaProjects\\DistributedProject2\\src\\input.dat");
        neighborhood = getNeighborhood(file); //get the neighbor map
        connections = createConnections(neighborhood); //get connections map from neighbor map
        initializeThreads(connections); //initialize all threads

    }

    //parse input file and return graph in HashMap form with all neighbors
    static HashMap<Integer, ArrayList<Integer>> getNeighborhood(File file)  {
        //file doesn't exist
        if (!file.exists()) {
            System.out.println("The input file does not exist.");
            return null;
        }

        Scanner sc = null;
        try {
            sc = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        int numThreads = 0;

        //numthreads first thing read
        if (sc.hasNext())
            numThreads = sc.nextInt(); //get num threads

        if (numThreads < 1) {
            System.out.println("User Error: Number of processes given is zero.");
            return null;
        }

        HashMap<Integer, ArrayList<Integer>> neighborMap = new HashMap<>();

        //initialize the array lists in the neighbor map
        for (int i = 0; i < numThreads; i++)
            neighborMap.put(sc.nextInt(), new ArrayList<>());

        //for each process id
        for(int processId : neighborMap.keySet()){
            for(int possibleNeighborId : neighborMap.keySet()){ //we will look at each possible neighbor
                if(sc.nextInt() == 1){ //if connection exists here get neighbors and add possible neighbor to it
                    ArrayList<Integer> currNeighbors = neighborMap.get(processId);
                    currNeighbors.add(possibleNeighborId);
                    neighborMap.put(processId, currNeighbors);
                }
            }
        }

        //return the neighbor map
        return neighborMap;
    }

    //initialize all the threads
    static void initializeThreads(HashMap<Integer, ArrayList<Connection>> connections){
        int numThreads = connections.keySet().size();
        Thread[] threads = new Thread[numThreads];
        CyclicBarrier barrier = new CyclicBarrier(numThreads+1); //create cyclic barrier to synchronize rounds
        CountDownLatch latch = new CountDownLatch(numThreads);
        int i = 0;
        for(int id : connections.keySet()){ //for each process
            threads[i] = new MyThread3(id, connections.get(id), barrier, latch); //create a thread, this will also start the thread
            i++;
        }
        while (latch.getCount() > 0) {
            barrierAwait(barrier);
        }
        barrierAwait(barrier);

        return;
    }

    private static void barrierAwait(CyclicBarrier barrier){
        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }

    //create the connection map
    static HashMap<Integer, ArrayList<Connection>> createConnections(HashMap<Integer, ArrayList<Integer>> neighborhood) {
        HashMap<Integer, ArrayList<Connection>> connections = new HashMap<>();
        AtomicInteger counter = new AtomicInteger(); //atomic integer to count num of messages

        for (int id : neighborhood.keySet()) { //for each process
            ArrayList<Integer> neighbors = neighborhood.get(id); //get all the neighbors
            for (int neighbor : neighbors) {
                if (id < neighbor) { //if id is less than neighbor, this connection was already created
                    Connection connection = new Connection(id, neighbor,  counter);
                    ArrayList<Connection> myConnections;
                    //get list of current process connection if exists, else create it
                    if (connections.containsKey(id))
                        myConnections = connections.get(id);
                    else
                        myConnections = new ArrayList<>();
                    ArrayList<Connection> neighborConnections;
                    //get list of neighbor process connection if exists, else create it
                    if (connections.containsKey(neighbor))
                        neighborConnections = connections.get(neighbor);
                    else
                        neighborConnections = new ArrayList<>();

                    //add the connection to the current id's and the neighbor's list of connections
                    myConnections.add(connection);
                    neighborConnections.add(connection);

                    //add them back to the connections map
                    connections.put(id, myConnections);
                    connections.put(neighbor, neighborConnections);
                }
            }
        }
        return connections;
    }

}
