import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.HashMap;

public class Floodmax {

    public static void main(String[] args) {
        HashMap<Integer, ArrayList<Integer>> neighbors;
        File file = new File("C:\\Users\\mugdh\\Downloads\\Distributed2\\Distributed2\\src\\input.dat");
        try {
            neighbors = processInputFile(file);
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

        HashMap<Integer, ArrayList<Integer>> neighborMap = new HashMap<>();
        int[] threadIDs = new int[numThreads];
        for (int i = 0; i < numThreads; i++) {
            threadIDs[i] = sc.nextInt();
            neighborMap.put(threadIDs[i], new ArrayList<Integer>());
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
        return neighborMap;
    }

}


