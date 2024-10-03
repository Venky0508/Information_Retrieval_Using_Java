//Lab 2
//author: Srivenkatesh Nair (sn6711)

import java.util.ArrayList;

public class Main {
    

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        //Building the positional index
        PositionalIndex pi = new PositionalIndex("Lab1_Data");
        System.out.print("Part 1 => Positional Indexes: \n");
        System.out.println(pi);

        //Implementation of the intersect method
        System.out.print("\nPart 2 => Testing the intersect Method: \n");
        ArrayList<DocId> l1 = pi.docLists.get(pi.termList.indexOf("this"));
        System.out.println("this : "+l1);
        ArrayList<DocId> l2 = pi.docLists.get(pi.termList.indexOf("is"));
         System.out.println("is : "+l2);
        ArrayList<Object> result = pi.intersect("this", "is");
        ArrayList<Integer> mergedList = (ArrayList<Integer>) result.get(0);
        ArrayList<ArrayList<Integer>> postingsList = (ArrayList<ArrayList<Integer>>) result.get(1);
        if(mergedList.size() != 0) {
            for(int i = 0; i < mergedList.size(); i ++)
                System.out.println("Document " + mergedList.get(i).intValue()+" : " + postingsList.get(i));
        }
        else{
            System.out.println("No adjacency found!");
        }

        //Implementation of the phraseQuery method
        System.out.print("\nPart 3 => Testing the phraseQuery Method: \n");
        String[] query = {"that", "is", "what", "it", "sets"};
        System.out.println("Search Query : ");
        ArrayList<DocId> answer = pi.phraseQuery(query);
        int total = query.length;
        System.out.print("\nResult: \n");
        if (answer.size() > 0){
            for (int index = 0; index < answer.size(); index++){
                int wordInd = index % total;
                String word = query[wordInd];
                System.out.println(word + " : " + answer.get(index));
            }
        }
        else{
            System.out.println("No match found!");
        }

        //Testing phrase queries of different lengths
        System.out.print("\nPart 4 => Testing Phrase Queries: \n");

        String[] query1 = {"this", "is"};
        System.out.println("\nSearch Query 1 : ");
        ArrayList<DocId> answer1 = pi.phraseQuery(query1);
        int total1 = query1.length;
        System.out.print("\nResult: \n");
        if (answer1.size() > 0){
            for (int index = 0; index < answer1.size(); index++){
                int wordInd = index % total1;
                String word = query1[wordInd];
                System.out.println(word + " : " + answer1.get(index));
            }
        }
        else{
            System.out.println("No match found!");
        }
        
        String[] query2 = {"middle", "of", "nowhere"};
        System.out.println("\nSearch Query 2 : ");
        ArrayList<DocId> answer2 = pi.phraseQuery(query2);
        int total2 = query2.length;
        System.out.print("\nResult: \n");
        if (answer2.size() > 0){
            for (int index = 0; index < answer2.size(); index++){
                int wordInd = index % total2;
                String word = query2[wordInd];
                System.out.println(word + " : " + answer2.get(index));
            }
        }
        else{
            System.out.println("No match found!");
        }
        
        String[] query3 = {"signs", "of", "a", "pulse"};
        System.out.println("\nSearch Query 3 : ");
        ArrayList<DocId> answer3 = pi.phraseQuery(query3);
        int total3 = query3.length;
        System.out.print("\nResult: \n");
        if (answer3.size() > 0){
            for (int index = 0; index < answer3.size(); index++){
                int wordInd = index % total3;
                String word = query3[wordInd];
                System.out.println(word + " : " + answer3.get(index));
            }
        }
        else{
            System.out.println("No match found!");
        }

        String[] query4 = {"sounds", "like", "a", "cool", "movie"};
        System.out.println("\nSearch Query 4 : ");
        ArrayList<DocId> answer4 = pi.phraseQuery(query4);
        int total4 = query4.length;
        System.out.print("\nResult: \n");
        if (answer4.size() > 0){
            for (int index = 0; index < answer4.size(); index++){
                int wordInd = index % total4;
                String word = query4[wordInd];
                System.out.println(word + " : " + answer4.get(index));
            }
        }
        else{
            System.out.println("No match found!");
        }
    }
}
