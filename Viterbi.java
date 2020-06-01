import org.bytedeco.javacv.FrameFilter;

import java.io.*;
import java.util.*;

public class Viterbi {


    public void viterbiTagging(String fileName,  TextTraining trained) throws Exception {
        String initialTag = "#";
        Double u = -14.0;


        BufferedReader input = new BufferedReader(new FileReader(fileName));
        BufferedWriter output = new BufferedWriter(new FileWriter("tagged.txt"));


        String line;
        // While we still have sentences to read
        while ((line = input.readLine()) != null) {
            String[] pieces = line.toLowerCase().split(" ");
            // Helper method to do all of the for loop checks
            ArrayList<String> path = viterbiHelper(trained, initialTag, pieces, u);

            // Loop through the path and write it out to the console.
            for (String s : path) {
                output.write(s + " ");
            }

            output.write("\n");

        }
        output.close();
        input.close();

    }

    public void compare(String fileName ) throws Exception {
        int correct = 0;
        int incorrect = 0;
        BufferedReader real = new BufferedReader(new FileReader(fileName));
        BufferedReader viterbi = new BufferedReader(new FileReader("tagged.txt"));
        String line;
        while ((line = real.readLine()) != null) {
            // get an array of all the words.
            String[] realWords = line.split(" ");
            String[] viterviWords = viterbi.readLine().split(" ");

            for (int i = 0; i < realWords.length; i++) {
                String realWord = realWords[i];
                String viterbiWord = viterviWords[i];
                // We made a correct tag.
                if(realWord.equals(viterbiWord) ) {
                    correct ++;
                }
                // We made an incorrect tag.
                else {
                    incorrect ++;
                }
            }
        }

        real.close();
        viterbi.close();
        System.out.println("Number of Correct: " + correct);
        System.out.println("Number of Incorrect: " + incorrect);

        // Divide correct by total, multiply by 100 to get percent
        double percent =(double)(correct)/(correct+incorrect) * 100;
        System.out.println("Percent Correct: " + percent);
    }

    public void consoleTagging(TextTraining trainer){
        Scanner in = new Scanner(System.in);
        // Get an array of all of the words that the user just entered.
        while (true) {
            System.out.println("Enter a sentence. To quit, enter");

            String words =in.nextLine();
            if(words.isEmpty()){
                break;
            }
            String[] pieces = words.toLowerCase().split(" ");
            String initialTag = "#";
            double u = -14;
                // Helper again to loop through all of the words and create our path.
                ArrayList<String> path = viterbiHelper(trainer, initialTag, pieces, u);

                System.out.println(path);

                System.out.println("\n");
        }
    }


    public ArrayList<String> viterbiHelper(TextTraining t, String initialTag, String[] ps, double unseen){
        // Back pointers
        ArrayList<Map<String, String>> bp = new ArrayList<>();

        Set<String> currStates = new HashSet<>();
        currStates.add(initialTag);

        Map<String, Double> currScores = new HashMap<>();
        currScores.put(initialTag, 0.0);

        // Loop through all of the words.
        for (int i = 0; i < ps.length; i++) {
            Set<String> nextStates = new HashSet<>();
            Map<String, Double> nextScores = new HashMap<>();
            // Create a back pointers map for each word.
            bp.add(new HashMap<>());

            // For every current state
            for (String currState : currStates) {
                // Is this currState one that we've seen before?
                if (t.transitions.containsKey(currState)) {
                    for (String nextState : t.transitions.get(currState).keySet()) {
                        nextStates.add(nextState);
                        Double d;
                        // If we haven't seen this word, assign the unseen value to it.
                        if (!t.observations.get(nextState).containsKey(ps[i])) {
                            d = unseen;
                        }
                        // Otherwise, we can make an observation score on this word.
                        else {
                            d = t.observations.get(nextState).get(ps[i]);
                        }

                        // Calculate the next scores for each current state based on the current score, transition score,
                        // and observation score
                        Double nextScore = currScores.get(currState) +
                                t.transitions.get(currState).get(nextState) +
                                d;

                        // If we have not seen this next state before or we have found a better score, put that in nextScores
                        // and update back pointers.
                        if (!nextScores.containsKey(nextState) || nextScore > nextScores.get(nextState)) {
                            nextScores.put(nextState, nextScore);
                            bp.get(i).put(nextState, currState);

                        }
                    }
                }
            }
            // Increment currStates and currScores.
            currStates = nextStates;
            currScores = nextScores;


        }

        // Create our path by looping backwards through backpointers.
        ArrayList<String> path = new ArrayList<>();
        String next = null;
        double score = -500;

        for(String s: currScores.keySet()) {
            // Fencepost case, find the most likely tag for the last word.
            if(currScores.get(s) > score) {
                next = s;
                score = currScores.get(s);
            }

        }

        // Loop through back pointers backwards, given the most likely tag, find what likely came before it.
        for (int i = bp.size() - 1; i >= 0; i--) {
            path.add(0, next);
            next = bp.get(i).get(next);
        }
        return path;
    }

}