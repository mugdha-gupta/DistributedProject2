import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.HashMap;

public class Floodmax {
    public static void main(String[] args) {

        try{

            File file = new File("C://Users//Nymisha//IdeaProjects//Distributed2//src//input.dat");
            if (!file.exists()) {
                System.out.println("The input file does not exist.");
                return;
            }
            Scanner sc = new Scanner(file);
            int numThreads = 0;

            if (sc.hasNext())
                numThreads = sc.nextInt(); //get num threads

            if (numThreads < 1) {
                System.out.println("User Error: Number of processes given is zero.");
                return;
            }

            HashMap<Integer, ArrayList<Integer>> neighbors = new HashMap<>();

            int[] threadIDs = new int[numThreads];
            for (int i = 0; i < numThreads; i++) {
                threadIDs[i] = sc.nextInt();
                neighbors.put(threadIDs[i], new ArrayList<Integer>());

            }

            for (int i = 0; i < numThreads; i++) {
                for (int j = 0; j < numThreads; j++) {
                    int connection = sc.nextInt();
                    if (connection == 1) {
                        ArrayList<Integer> friends = neighbors.get(threadIDs[i]);
                        friends.add(threadIDs[j]);
                        neighbors.put(threadIDs[i], friends);

                    }
                }
            }
            System.out.println(neighbors.toString());


        } catch (IOException ex) {
            System.err.println(ex);
        }

    }
}


