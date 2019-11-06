import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

public class Floodmax {

    static int maxCount, x;

    public static void main(String[] args) {
        HashMap<Integer, ArrayList<Integer>> neighbors;

        File file = new File("C:\\Users\\mugdh\\gitviews\\DistributedProject2\\src\\input.dat");
        try {
            processInputFile(file);


        } catch (IOException ex) {
            System.err.println(ex);
        }



    }

    static HashMap<Integer, ArrayList<Integer>> processInputFile(File file) throws FileNotFoundException {
        if (!file.exists()) {
            System.out.println("The input file does not exist.");
            return null;
        }

        Scanner sc = new Scanner(file);
        int numThreads = 0;

        if (sc.hasNext())
            numThreads = sc.nextInt(); //get num threads

        if (numThreads < 1) {
            System.out.println("User Error: Number of processes given is zero.");
            return null;
        }

        CyclicBarrier barrier = new CyclicBarrier(numThreads);
        HashMap<Integer, ArrayList<Integer>> neighborMap = new HashMap<>();
        int[] threadIDs = new int[numThreads];
        for (int i = 0; i < numThreads; i++) {
            threadIDs[i] = sc.nextInt();
            neighborMap.put(threadIDs[i], new ArrayList<>());
        }

        for (int i = 0; i < numThreads; i++) {
            for (int j = 0; j < numThreads; j++) {
                int connection = sc.nextInt();
                if (connection == 1) {
                    ArrayList<Integer> friends = neighborMap.get(threadIDs[i]);
                    friends.add(threadIDs[j]);
                    neighborMap.put(threadIDs[i], friends);
                }
            }
        }
        createConnections(neighborMap, threadIDs, numThreads, barrier);

        return neighborMap;
    }

    static int findDiameter(HashMap<Integer, ArrayList<Integer>> neighborhood){
        int diameter =0;
        for(int id : neighborhood.keySet()){
            int longestPath = getLongest(id, neighborhood);
            if(longestPath > diameter)
                diameter =longestPath;
        }
        return diameter;
    }

    static int getLongest(int id, HashMap<Integer, ArrayList<Integer>> neighborhood){
        HashSet<Integer> visited = new HashSet<>();
        visited.add(id);
        int dist = 0;
        while (visited.size() < neighborhood.keySet().size()){
            HashSet<Integer> toAdd = new HashSet<>();
            for(int visitedId : visited){
                ArrayList<Integer> neighbors = neighborhood.get(visitedId);
                for(int neighbor : neighbors){
                    if(!visited.contains(neighbor)){
                        toAdd.add(neighbor);
                    }
                }
            }
            for(int ad : toAdd)
                visited.add(ad);
            toAdd.clear();
            dist++;
        }
        return dist;
    }

    static void initializeThreads(int numThreads,
                                  int[] threadIDs,
                                  HashMap<Integer,
                                          ArrayList<Connection>> connections, CyclicBarrier barrier, int diam){
        Thread[] threads = new Thread[numThreads];
        for(int i = 0; i < numThreads; i++){
            threads[i] = new MyThread(threadIDs[i], connections.get(threadIDs[i]), barrier, diam);
        }
    }

    static void createConnections(HashMap<Integer, ArrayList<Integer>> neighborhood, int[] threadIDs, int numThreads, CyclicBarrier barrier) {
        HashMap<Integer, ArrayList<Connection>> connections = new HashMap<>();
        AtomicInteger counter = new AtomicInteger();

        for (int id : threadIDs) {
            ArrayList<Integer> neighbors = neighborhood.get(id);
            for (int neighbor : neighbors) {
                if (id < neighbor) {
                    Connection connection = new Connection(id, neighbor,  counter);
                    ArrayList<Connection> myConnections;
                    if (connections.containsKey(id))
                        myConnections = connections.get(id);
                    else
                        myConnections = new ArrayList<>();
                    ArrayList<Connection> neighborConnections;
                    if (connections.containsKey(neighbor))
                        neighborConnections = connections.get(neighbor);
                    else
                        neighborConnections = new ArrayList<>();
                    myConnections.add(connection);
                    neighborConnections.add(connection);
                    connections.put(id, myConnections);
                    connections.put(neighbor, neighborConnections);
                }
            }
        }
        int diam = findDiameter(neighborhood);
        initializeThreads(numThreads, threadIDs, connections, barrier, diam);
    }

}


