//My implementation of Lab1
//author:Srivenkatesh Nair

import java.util.*;

public class Main {
    public static void main(String[] args) {
      //Reading data into a parser class object
      String path = "Lab1_Data";
      Parser p = new Parser(path);
      System.out.println("No of Documents: " + p.docLists.size() + "\n");
      for (int i = 0; i <p.docLists.size(); i++)
      {
        System.out.println("Document "+ i + " : " + p.docLists.get(i)+"\n");
      }
      //Creating an inverted index object which stores a dictionary of terms and their corresponding document ids in an arraylist
      InvertedIndex inverted = new InvertedIndex(p.docLists);
      System.out.println("Inverted Index: \n"+inverted);
      
      //Queries to be searched 
      String query1 = new String("wisdom");
      String query2 = new String("plot");
      String[] query3 = {"strange","thing"};
      String[] query4 = {"film","review"};
      String[] query5 = {"a","good","start"};
      String[] query6 = {"american","thrilling","happy","chase"};
      
      //Each term in the queries and their occurrences in the documents
      ArrayList<Integer> ans1 = inverted.oneWordSearch("strange");
      ArrayList<Integer> ans2 = inverted.oneWordSearch("thing");
      ArrayList<Integer> ans3 = inverted.oneWordSearch("film");
      ArrayList<Integer> ans4 = inverted.oneWordSearch("review");
      ArrayList<Integer> ans5 = inverted.oneWordSearch("a");
      ArrayList<Integer> ans6 = inverted.oneWordSearch("good");
      ArrayList<Integer> ans7 = inverted.oneWordSearch("start");
      ArrayList<Integer> ans8 = inverted.oneWordSearch("american");
      ArrayList<Integer> ans9 = inverted.oneWordSearch("thrilling");
      ArrayList<Integer> ans10 = inverted.oneWordSearch("chase");
      ArrayList<Integer> ans11 = inverted.oneWordSearch("wisdom");
      ArrayList<Integer> ans12 = inverted.oneWordSearch("plot");
      ArrayList<Integer> ans13 = inverted.oneWordSearch("happy");
      System.out.println("Terms and their document ids:");
      System.out.println("wisdom: "+ans11+"\nplot: "+ans12+"\nstrange: "
      +ans1+"\nthing: "+ans2+"\nfilm: "+ans3+"\nreview: "+ans4+"\na: "+
      ans5+"\ngood: "+ans6+"\nstart: "+ans7+"\namerican: "+ans8+"\nthrilling: "+
      ans9+"\nhappy: "+ans13+"\nchase: "+ans10+ "\n");

      //Implementing each search strategy
      ArrayList<Integer> result1 = inverted.oneWordSearch(query1);
      ArrayList<Integer> result2 = inverted.oneWordSearch(query2);
      ArrayList<Integer> result3 = inverted.andSearch(query3);
      ArrayList<Integer> result4 = inverted.andSearch(query4);
      ArrayList<Integer> result5 = inverted.orSearch(query3);
      ArrayList<Integer> result6 = inverted.orSearch(query4);
      ArrayList<Integer> result7 = inverted.andSearch(query5);
      ArrayList<Integer> result8 = inverted.andSearch(query6);

      
      //Outputs
      //One word query search
        if(result1.size() != 0) {
        System.out.println("One Word Search -");
        System.out.print("Document Number: " + result1 + " Document names: ");
            for(Integer doc7: result1){
                System.out.print(p.myDocs[doc7.intValue()]+" ");
            }
        System.out.println("Words:"+query1+"\n");
        }
        else{
            System.out.println("One Word Search -");
            System.out.println("Words:"+query1+"\nNo match!");
        }


        if(result2.size() != 0) {
        System.out.println("One Word Search -");
        System.out.print("Document Number: " + result2 + " Document names: ");
            for(Integer doc8: result2){
                System.out.print(p.myDocs[doc8.intValue()]+" ");
            }
        System.out.println("Words:"+query2+"\n");
        }
        else{
            System.out.println("One Word Search -");
            System.out.println("Words:"+query2+"\n");
        }

        //Two word query search using AND operator
        if(result3.size() != 0) {
        System.out.println("Two Word (AND) Search -");
        System.out.print("Document Number: " + result3 + " Document names: ");
            for(Integer doc1: result3){
                System.out.print(p.myDocs[doc1.intValue()]+" ");
            }
        System.out.print("Words:");
            for(String word1: query3){
                System.out.print(word1+ " ");
            }
        System.out.println("\n");
        }
        else{
            System.out.println("Two Word (AND) Search -");
            System.out.print("Words:");
            for(String word1: query3){
                System.out.print(word1+ " ");
            }
            System.out.println("\nNo match!");
        }


        if(result4.size() != 0) {
        System.out.println("Two Word (AND) Search -");
        System.out.print("Document Number: " + result4 + " Document names: ");
            for(Integer doc2: result4){
                System.out.print(p.myDocs[doc2.intValue()]+" ");
            }
        System.out.print("Words:");
            for(String word2: query4){
                System.out.print(word2+ " ");
            }
        System.out.println("\n");
        }
        else{
            System.out.println("Two Word (AND) Search -");
            System.out.print("Words:");
            for(String word2: query4){
                System.out.print(word2+ " ");
            }
            System.out.println("\nNo match!");
        }
        
        //Two word query search using OR operator
        if(result5.size() != 0) {
        System.out.println("Two Word (OR) Search -");
        System.out.print("Document Number: " + result5 + " Document names: ");
            for(Integer doc3: result5){
                System.out.print(p.myDocs[doc3.intValue()]+" ");
            }
        System.out.print("Words:");
            for(String word3: query3){
                System.out.print(word3+ " ");
            }
        System.out.println("\n");
        }
        else{
            System.out.println("Two Word (OR) Search -");
            System.out.print("Words:");
            for(String word3: query3){
                System.out.print(word3+ " ");
            }
            System.out.println("\nNo match!");
        }


        if(result6.size() != 0) {
        System.out.println("Two Word (OR) Search -");
        System.out.print("Document Number: " + result6 + " Document names: ");
            for(Integer doc4: result6){
                System.out.print(p.myDocs[doc4.intValue()]+" ");
            }
        System.out.print("Words:");
            for(String word4: query4){
                System.out.print(word4+ " ");
            }
        System.out.println("\n");
        }
        else{
            System.out.println("Two Word (OR) Search -");
            System.out.print("Words:");
            for(String word4: query4){
                System.out.print(word4+ " ");
            }
            System.out.println("\nNo match!");
        }

        //Multi-word query search using AND operator
        if(result7.size() != 0) {
        System.out.println("Multi Word (AND) Search -");
        System.out.print("Document Number: " + result7 + " Document names: ");
            for(Integer doc5: result7){
                System.out.print(p.myDocs[doc5.intValue()]+" ");
            }
        System.out.print("Words:");
            for(String word5: query5){
                System.out.print(word5+ " ");
            }
        System.out.println("\n");
        }
        else{
            System.out.println("Multi Word (AND) Search -");
            System.out.print("Words:");
            for(String word5: query5){
                System.out.print(word5+ " ");
            }
            System.out.println("\nNo match!");
        }


        if(result8.size() != 0) {
        System.out.println("Multi Word (AND) Search -");
        System.out.print("Document Number: " + result8 + " Document names: ");
            for(Integer doc6: result8){
                System.out.print(p.myDocs[doc6.intValue()]+" ");
            }
        System.out.print("Words:");
            for(String word6: query6){
                System.out.print(word6+ " ");
            }
        System.out.println("\n");
        }
        else{
            System.out.println("Multi Word (AND) Search -");
            System.out.print("Words:");
            for(String word6: query6){
                System.out.print(word6+ " ");
            }
            System.out.println("\nNo match!");
        }

    }
}
