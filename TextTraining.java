import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

//Questions: does the Hashtag point to position 0 or 1 && do u add the # info to observations


public class TextTraining {
    public Map<String, Map<String, Double>> observations = new HashMap<String, Map<String, Double>>();
    public Map<String, Map<String, Double>> transitions = new HashMap<String, Map<String, Double>>();
    public Map<String, Map<String, Double>> observations_count = new HashMap<String, Map<String, Double>>();
    public Map<String, Map<String, Double>> transtions_count = new HashMap<String, Map<String, Double>>();

    public void trainThis(String wordFile, String PosFile) throws Exception {

        // our hypothetical tag
        String initialTag = "#";

        BufferedReader words = new BufferedReader(new FileReader(wordFile));
        BufferedReader POS = new BufferedReader(new FileReader(PosFile));

        String line;
        // while we still have sentences to read
        while ((line = words.readLine()) != null) {
            String[] wordPieces = line.toLowerCase().split(" ");
            String[] POSpieces = POS.readLine().split(" ");

            // Loop through all of the words.
            for (int i = 0; i < wordPieces.length - 1; i++) {
                String word = wordPieces[i];
                String tag = POSpieces[i];
                String nextTag = POSpieces[i + 1]; //this will throw an error @ last index


                if (i == 0) { //if it is the first
                    if (!transitions.containsKey(initialTag)) {  //if observation doesn't have unique key
                        transitions.put(initialTag, new HashMap<>());
                        transitions.get(initialTag).put(tag, 1.0);
                    } else {
                        if (!transitions.get(initialTag).containsKey(tag)) {  //if observation doesn't have unique key
                            transitions.get(initialTag).put(tag, 1.0);
                        } else {
                            transitions.get(initialTag).put(tag, transitions.get(initialTag).get(tag) + 1);
                        }
                    }
                }

                // Do we have this POS yet?
                if (!transitions.containsKey(tag)) {
                    transitions.put(tag, new HashMap<>());
                    transitions.get(tag).put(nextTag, 1.0);

                    observations.put(tag, new HashMap<>());
                    observations.get(tag).put(word, 1.0);

                } else {
                    // We have the POS, but maybe not the next one
                    if (!transitions.get(tag).containsKey(nextTag)) {
                        transitions.get(tag).put(nextTag, 1.0);
                    } else {
                        transitions.get(tag).put(nextTag, transitions.get(tag).get(nextTag) + 1.0);
                    }

                    // We have the POS, but we haven't seen this word as this POS yet.
                    if (!observations.get(tag).containsKey(word)) {
                        observations.get(tag).put(word, 1.0);
                    } else {
                        observations.get(tag).put(word, observations.get(tag).get(word) + 1.0);
                    }
                }
            }
            //Fencepost case where there is no transition
            String lastWord = wordPieces[wordPieces.length - 1];
            String lastTag = POSpieces[wordPieces.length - 1];

            // Have we seen this POS before?
            if (!observations.containsKey(lastTag)) {
                observations.put(lastTag, new HashMap<>());
                observations.get(lastTag).put(lastWord, 1.0);
            } else {

                // Is this word logged as this POS yet?
                if (!observations.get(lastTag).containsKey(lastWord)) {  //if observation doess't have unique key
                    observations.get(lastTag).put(lastWord, 1.0);
                } else {
                    observations.get(lastTag).put(lastWord, observations.get(lastTag).get(lastWord) + 1.0);
                }
            }

        }

        words.close();
        POS.close();

        // For testing purposes
        observations_count = observations;
        transtions_count = transitions;


        // Turn all of our counts into log values.
        divideProbs(observations);
        divideProbs(transitions);





    }


    //calculate the probabilities in observations and probabilities
    public void divideProbs (Map<String,Map<String, Double>> someMap) {

        // loop through all of the outside map's keys.
        for(String tags: someMap.keySet()) {
            int sum = 0;
            // loop through all of the inner map's keys.
            for (String words: someMap.get(tags).keySet()) {
                sum += someMap.get(tags).get(words);
            }
            // While we're still inside of the outside key, divide all of the inside values.
            for (String words : someMap.get(tags).keySet()) {
                someMap.get(tags).put(words, Math.log(someMap.get(tags).get(words) / sum));
            }

        }
    }

}

