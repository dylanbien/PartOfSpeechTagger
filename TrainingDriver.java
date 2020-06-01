import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

public class TrainingDriver {

    public static void main(String[] args) throws Exception {

        System.out.println("Hard code testing on drill sentences");
        TextTraining hardCode = hardCoding();
        Viterbi hardCodeTesting = new Viterbi();
        hardCodeTesting.viterbiTagging("PS 5/texts/recitation-sentences.txt", hardCode);
        hardCodeOutput();

        System.out.println("");
        TextTraining simpleTraining = new TextTraining();
        simpleTraining.trainThis("PS 5/texts/simple-train-sentences.txt","PS 5/texts/simple-train-tags.txt");
        Viterbi b = new Viterbi();
        b.viterbiTagging("PS 5/texts/simple-test-sentences.txt", simpleTraining);
        System.out.println("Testing with Viterbi Decoding on Simple");
        b.compare("PS 5/texts/simple-test-tags.txt");
        System.out.println("\n");

        System.out.println("Testing with Viterbi Decoding on Brown");
        TextTraining brownTraining = new TextTraining();
        brownTraining.trainThis("PS 5/texts/brown-train-sentences.txt","PS 5/texts/brown-train-tags.txt");
        Viterbi c = new Viterbi();
        c.viterbiTagging("PS 5/texts/brown-test-sentences.txt", brownTraining);
        c.compare("PS 5/texts/brown-test-tags.txt");
        System.out.println("\n");

        c.consoleTagging(brownTraining);

    }

    public static TextTraining hardCoding () {
        TextTraining t = new TextTraining();

        t.transitions.put("#", new HashMap<>());
        t.transitions.get("#").put("N", Math.log(5/7.0));
        t.transitions.get("#").put("NP", Math.log(2/7.0));

        t.transitions.put("CNJ", new HashMap<>());
        t.transitions.get("CNJ").put("N", Math.log(1/3.0));
        t.transitions.get("CNJ").put("NP", Math.log(1/3.0));
        t.transitions.get("CNJ").put("V", Math.log(1/3.0));

        t.transitions.put("N", new HashMap<>());
        t.transitions.get("N").put("CNJ", Math.log(2/8.0));
        t.transitions.get("N").put("V", Math.log(6/8.0));

        t.transitions.put("NP", new HashMap<>());
        t.transitions.get("NP").put("V", Math.log(2/2.0));

        t.transitions.put("V", new HashMap<>());
        t.transitions.get("V").put("CNJ", Math.log(1/9.0));
        t.transitions.get("V").put("N", Math.log(6/9.0));
        t.transitions.get("V").put("V", Math.log(2/9.0));


        t.observations.put("CNJ", new HashMap<>());
        t.observations.get("CNJ").put("and", Math.log(3/3.0));

        t.observations.put("N", new HashMap<>());
        t.observations.get("N").put("cat", Math.log(5/12.0));
        t.observations.get("N").put("dog", Math.log(5/12.0));
        t.observations.get("N").put("watch", Math.log(2/12.0));

        t.observations.put("NP", new HashMap<>());
        t.observations.get("NP").put("chase", Math.log(5/5.0));

        t.observations.put("V", new HashMap<>());
        t.observations.get("V").put("chase", Math.log(2/9.0));
        t.observations.get("V").put("get", Math.log(1/9.0));
        t.observations.get("V").put("watch", Math.log(6/9.0));

        return t;
    }

    //loops through the actual words and prints them next to tagged texts
    public static void hardCodeOutput () throws Exception {
        BufferedReader output = new BufferedReader(new FileReader("tagged.txt"));

        String line;
        int i = 0;
        String[] Sentences = {"[cat chase dog]", "[cat watch chase]", "[chase get watch]"};
        while ((line = output.readLine()) != null) {
            String[] wordPieces = line.split(" ");
            System.out.print("Hard code sentence: " + Sentences[i] + " gives [ ");
            i++;
            for(String s: wordPieces) {
                System.out.print(s + " ");

            }
            System.out.println("]");
        }
        output.close();
    }
}