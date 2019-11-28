//Nymisha Jahagirdar and Mugdha Gupta
//CS 6380 Project 2

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

public class Floodmax {

    public static void main(String[] args) {
        HashMap<Integer, ArrayList<Integer>> neighborhood;
        HashMap<Integer, ArrayList<Connection>> connections;
        int diameter;
        Thread[] threads;

        //input processing
        if(args.length < 1){
            System.out.println("No path for the input file given.");
            return;
        }

        File file = new File(args[0]);
        neighborhood = getNeighborhood(file); //get the neighbor map
        diameter = findDiameter(neighborhood); //get the diameter from the neigbor map
        connections = createConnections(neighborhood); //get connections map from neighbor map
        initializeThreads(connections, diameter); //initialize all threads

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

    //fidn the diameter using the neigborhood
    static int findDiameter(HashMap<Integer, ArrayList<Integer>> neighborhood){
        int diameter =0;
        for(int id : neighborhood.keySet()){ //for each process
            int longestPath = getLongest(id, neighborhood); //find longest path to any node
            if(longestPath > diameter)
                diameter =longestPath; //if the longest path is bigger than current diameter, set diameter to lengest path
        }
        return diameter;
    }

    //brute force solution to find the length of path to furthest vertex
    static int getLongest(int id, HashMap<Integer, ArrayList<Integer>> neighborhood){
        HashSet<Integer> visited = new HashSet<>(); //set of visited nodes starts at empty
        visited.add(id); //add current id to the visited node
        int dist = 0;
        while (visited.size() < neighborhood.keySet().size()){ //while we haven't visited all nodes
            HashSet<Integer> toAdd = new HashSet<>();
            for(int visitedId : visited){
                ArrayList<Integer> neighbors = neighborhood.get(visitedId); //get all nodes reachable from visited nodes
                for(int neighbor : neighbors){
                    if(!visited.contains(neighbor)){ //if that node is not already in visited add it
                        toAdd.add(neighbor);
                    }
                }
            }
            for(int ad : toAdd) //using to add to avoid concurrent modification error
                visited.add(ad);
            toAdd.clear();
            dist++; //we travelled one length
        }
        return dist;
    }

    //initialize all the threads
    static void initializeThreads(HashMap<Integer, ArrayList<Connection>> connections, int diameter){
        int numThreads = connections.keySet().size();
        Thread[] threads = new Thread[numThreads];
        CyclicBarrier barrier = new CyclicBarrier(numThreads); //create cyclic barrier to syncronize rounds
        int i = 0;
        for(int id : connections.keySet()){ //for each process
            threads[i] = new MyThread(id, connections.get(id), barrier, diameter); //create a thread, this will also start the thread
            i++;
        }
        return;
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

