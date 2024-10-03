//Lab 4
//author: Srivenkatesh Nair

import java.io.*;
import java.util.*;


public class NBClassifier {
    ArrayList<String> trainDocs;
    int[] trainingLabels;
    int numClasses;
    int[] classCounts; // number of docs per class
    String[] classStrings; // concatenated string for all terms in a class
    int[] classTokenCounts; // total number of terms per class (includes duplicate terms)
    HashMap<String, Double>[] condProb; // one hash map for each class
    HashSet<String> vocabulary; // entire vocabuary

    //Constructor
    @SuppressWarnings("unchecked")
    public NBClassifier(String trainDataFolder){
        trainDocs = new ArrayList<String>();
        preprocess(trainDataFolder);
        //Used the same code here, as the one provided by the professor just made a few changes
        numClasses = 2;
        classCounts = new int[numClasses];
        classStrings = new String[numClasses];
        classTokenCounts = new int[numClasses];
        condProb = new HashMap[numClasses];
        vocabulary = new HashSet<String>();

        for(int i = 0; i < numClasses; i++) {
            classStrings[i] = "";
            condProb[i] = new HashMap<String, Double>();
         }
         
         for(int i = 0; i < trainingLabels.length; i++) {
            classCounts[trainingLabels[i]]++;
            classStrings[trainingLabels[i]] += (trainDocs.get(i) + " "); // add the document content to the class string
         }
         
         for(int i = 0; i < numClasses; i++) {
            String[] tokens = classStrings[i].split("[ .,?!:;$#%*+/()\"\\-&]+");

            classTokenCounts[i] = tokens.length;
             
            // collecting the token counts
            for(String token : tokens) {
               vocabulary.add(token);
               
               if(condProb[i].containsKey(token)) {
                  double count = condProb[i].get(token);
                  condProb[i].put(token, count + 1);
               }
               else
                  condProb[i].put(token, 1.0);
            }
         }
         
          // computing the class conditional probability using Laplace smoothing
         for(int i = 0; i < numClasses; i++) {
            Iterator<Map.Entry<String, Double>> iterator = condProb[i].entrySet().iterator();
            int vSize = vocabulary.size();
            
            while(iterator.hasNext())
            {
               Map.Entry<String, Double> entry = iterator.next();
               String token = entry.getKey();
               Double count = entry.getValue();
               count = (count + 1) / (classTokenCounts[i] + vSize);
               condProb[i].put(token, count);
            }
         }
    }

    // This function is basically used to initialize the integer array of class labels for the training documents.
    // I have done the rest of the initializations in the constructor
    public void preprocess(String trainDataFolder) {
        ArrayList<Integer> classLabels = new ArrayList<Integer>();
        String positive = trainDataFolder + "/pos";
        String negative = trainDataFolder + "/neg";
        File pos = new File(positive);
        File[] posReviews = pos.listFiles();
        File neg = new File(negative);
        File[] negReviews = neg.listFiles();
        for (int i = 0; i < posReviews.length; i++){
            try (BufferedReader reader = new BufferedReader(new FileReader(posReviews[i]))) {
            String allLines = new String(); // store all lines in file in this String
            String line = null;
             
            line = reader.readLine();
            while(line != null) {
                allLines += line.toLowerCase(); // case folding
                line = reader.readLine();
            }
            trainDocs.add(allLines);
            classLabels.add(0);
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
        }

        for (int j = 0; j < negReviews.length; j++){
            try (BufferedReader reader = new BufferedReader(new FileReader(negReviews[j]))) {
            String allLines = new String(); // store all lines in file in this String
            String line = null;
             
            line = reader.readLine();
            while(line != null) {
                allLines += line.toLowerCase(); // case folding
                line = reader.readLine();
            }
            trainDocs.add(allLines);
            classLabels.add(1);
            } 
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        int total = classLabels.size();
        trainingLabels = new int[total];
        for(int k = 0; k < total; k++){
            int label = classLabels.get(k).intValue();
            trainingLabels[k] = label;
        }
    }

    // This function is used to classify each test document and returns a class value on the basis of likelihood.
    public int classify(String testDoc){
        int label = 0;
        int vSize = vocabulary.size();
        double[] score = new double[numClasses]; // class likelihood for each class
      
        for(int i = 0; i < score.length; i++) {
            // use log to avoid precision problems
            score[i] = Math.log(classCounts[i] * 1.0 / trainDocs.size()); // prior probability of class
        }
      
        String[] tokens = testDoc.split("[ .,?!:;$#%*+/()\"\\-&]+");
      
        for(int i = 0; i < numClasses; i++) {
      
            for(String token: tokens) {
                // use log/addition to avoid precision problems
                if(condProb[i].containsKey(token))
                    score[i] += Math.log(condProb[i].get(token)); // term's class conditional probability
                else
                    score[i] += Math.log(1.0 / (classTokenCounts[i] + vSize)); // previously unknown term, compute its Laplace smoothed class conditional probability            
            }
        }
      
        double maxScore = score[0];
      
        // find the largest class likelihood and save its label to return as the class value
        for(int i = 0; i < score.length; i++) {
            System.out.println("Class " + i + " likelihood = " + score[i]);
            if(score[i] > maxScore) {
                label = i;
                maxScore = score[i];
            }
        }
        return label;
    }

    // This function classifies all the test documents and returns the classification accuracy for the test data.
    public double classifyAll(String testDataFolder){
        ArrayList<Integer> posLabels = new ArrayList<Integer>();
        ArrayList<Integer> negLabels = new ArrayList<Integer>();
        int label;
        String positive = testDataFolder + "/pos";
        String negative = testDataFolder + "/neg";
        File pos = new File(positive);
        File[] posReviews = pos.listFiles();
        File neg = new File(negative);
        File[] negReviews = neg.listFiles();
        System.out.println("------------------ Test Documents -----------------");
        label = 0;
        for (int i = 0; i < posReviews.length; i++){
            try (BufferedReader reader = new BufferedReader(new FileReader(posReviews[i]))) {
            String allLines = new String(); // store all lines in file in this String
            String line = null;
             
            line = reader.readLine();
            while(line != null) {
                allLines += line.toLowerCase(); // case folding
                line = reader.readLine();
            }
            System.out.println("Document Number: "+ (i + 1));
            label = classify(allLines);
            System.out.println(" ");
            posLabels.add(label);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        } 
        
        label = 1;
        for (int j = 0; j < negReviews.length; j++){
            try (BufferedReader reader = new BufferedReader(new FileReader(negReviews[j]))) {
            String allLines = new String(); // store all lines in file in this String
            String line = null;
             
            line = reader.readLine();
            while(line != null) {
                allLines += line.toLowerCase(); // case folding
                line = reader.readLine();
            }
            System.out.println("Document Number: "+ (posReviews.length + j + 1));
            label = classify(allLines);
            System.out.println(" ");
            negLabels.add(label);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        } 

        double counter = 0;
        for(int x = 0; x < posLabels.size(); x++){
            if(posLabels.get(x).intValue() != 0){
                counter ++;
            }
        }
        for(int y = 0; y < negLabels.size(); y++){
            if(negLabels.get(y).intValue() != 1){
                counter ++;
            }
        }
       
        double total = posLabels.size() + negLabels.size();
        System.out.println("Total number of documents: " + total);
        System.out.println("Number of Correct Classifications: "+ (total - counter));
        System.out.println("Number of Incorrect Classifications: "+ counter);
        System.out.println("Accuracy = "+ (total - counter) + "/" + total);
        double accuracy = (total - counter)/total;
        return accuracy;
    }

    //Main function
    public static void main(String[] args) {
        String trainData = "Lab4_Data/train";
        String testData = "Lab4_Data/test";
        NBClassifier classifier = new NBClassifier(trainData);
        double acc = classifier.classifyAll(testData);
        System.out.println("Classification Accuracy: " + acc);
    }

}
