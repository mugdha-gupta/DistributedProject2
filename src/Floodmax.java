import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CyclicBarrier;

public class Floodmax {

    static int maxCount, x;

    public static void main(String[] args) {
        HashMap<Integer, ArrayList<Integer>> neighbors;
        int diam = 0;

        File file = new File("C:\\Users\\Nymisha\\IdeaProjects\\DistributedProject2\\src\\input.dat");
        try {
            neighbors = processInputFile(file);
//            for (int start : neighbors.keySet()) {
//
//                int newDiam = dfs(neighbors.get(start));
//                if (newDiam > diam)
//                    diam = newDiam;
//            }



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

        CyclicBarrier barrier = new CyclicBarrier(numThreads+1);
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
        System.out.println(neighborMap.toString());
        createConnections(neighborMap, threadIDs, numThreads);

        maxCount = Integer.MIN_VALUE;

        int start = 0;
        for (int key : neighborMap.keySet())
        {
            start = key;
            break;
        }

        dfs(start, numThreads, neighborMap.get(start));

        dfs(x, numThreads, neighborMap.get(x));
        System.out.println("diam " + maxCount);
        return neighborMap;
    }

    static int dfs(ArrayList<Integer> neighbors){

        return 1;
    }

    static void initializeThreads(int numThreads, int[] threadIDs, HashMap<Integer, ArrayList<Connection>> connections){
        Thread[] threads = new Thread[numThreads];
        for(int i = 0; i < numThreads; i++){
            threads[i] = new MyThread(threadIDs[i], connections.get(threadIDs[i]));
        }
    }

    static void createConnections(HashMap<Integer, ArrayList<Integer>> neighborhood, int[] threadIDs, int numThreads) {
        HashMap<Integer, ArrayList<Connection>> connections = new HashMap<>();
        for (int id : threadIDs) {
            ArrayList<Integer> neighbors = neighborhood.get(id);
            for (int neighbor : neighbors) {
                if (id < neighbor) {
                    Connection connection = new Connection(id, neighbor);
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
        initializeThreads(numThreads, threadIDs, connections);
    }
    static void dfs(int node, int n, ArrayList<Integer> neighbors)
    {
        boolean[] visited = new boolean[n + 1];
        int count = 0;

        // Mark all the vertices as not visited
        Arrays.fill(visited, false);

        // Increment count by 1 for visited node
        dfsUtil(node, count + 1, visited, neighbors);

    }
    static void dfsUtil(int node, int count, boolean visited[], ArrayList<Integer> neighbors)
    {
        visited[node] = true;
        count++;

        for(int neighbor: neighbors)
        {
            if(!visited[neighbor]){
                if (count >= maxCount) {
                    maxCount = count;
                    x = neighbor;
                }
                dfsUtil(neighbor, count, visited, neighbors);
            }
        }
    }
}


