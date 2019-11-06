import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.HashMap;

public class Floodmax {

    public static void main(String[] args) {
        HashMap<Integer, ArrayList<Connection>> neighbors;
        HashMap<Integer, ArrayList<Connection>> links;

        File file = new File("C:\\Users\\Nymisha\\IdeaProjects\\DistributedProject2\\src\\input.dat");
        try {
            links = processInputFile(file);

        } catch (IOException ex) {
            System.err.println(ex);
        }


    }

    static HashMap<Integer, ArrayList<Connection>> processInputFile(File file) throws FileNotFoundException {
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

        HashMap<Integer, ArrayList<Integer>> neighborMap = new HashMap<>();
        HashMap<Integer, ArrayList<Connection>> connections = new HashMap<>();
        int[] threadIDs = new int[numThreads];
        for (int i = 0; i < numThreads; i++) {
            threadIDs[i] = sc.nextInt();
            neighborMap.put(threadIDs[i], new ArrayList<>());
            connections.put(threadIDs[i], new ArrayList<>());
        }

        for (int i = 0; i < numThreads; i++) {
            for (int j = 0; j < numThreads; j++) {
                int connection = sc.nextInt();
                if (connection == 1) {
                    ArrayList<Integer> friends = neighborMap.get(threadIDs[i]);
                    ArrayList<Connection> connectionArrayList = connections.get(threadIDs[i]);
                    friends.add(threadIDs[j]);
                    connectionArrayList.add(new Connection(threadIDs[i], threadIDs[j]));
                    neighborMap.put(threadIDs[i], friends);
                    connections.put(threadIDs[i], connectionArrayList);
                }
            }
        }

        Thread[] threads = new Thread[numThreads];
        for(int i = 0; i < numThreads; i++){
            threads[i] = new MyThread(threadIDs[i], connections.get(threadIDs[i]));
        }
        System.out.println(neighborMap.toString());
        return connections;
    }
}


