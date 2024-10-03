import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class PositionalIndex {
   String[] myDocs;
   ArrayList<String> termList; // dictionary
   ArrayList<ArrayList<DocId>> docLists;
   ArrayList<ArrayList<String>> doc;

   //Constructor of the class
   public PositionalIndex(String folderName) {
      // myDocs = docs;
      termList = new ArrayList<String>();
      docLists = new ArrayList<ArrayList<DocId>>(); // postings list
      doc = new ArrayList<ArrayList<String>>();
      ArrayList<DocId> docList; // postings for a single term
      
      File folder = new File(folderName);
      File[] listOfFiles = folder.listFiles();
      myDocs = new String[listOfFiles.length - 1]; // Here, I did length - 1 because I was getting an additional file named '.DS_Store' after unzipping within the Lab1_Data directory.
      int num = 0;
      for(int i = 1; i < listOfFiles.length; i++) {
         myDocs[num] = listOfFiles[i].getName();
         num++;
      }
      Arrays.sort(myDocs);

      for(int i = 0; i < myDocs.length; i++) {
         ArrayList<String> words = new ArrayList<String>();
         String[] tokens = parse(folderName + "/" + myDocs[i]);
         for(String word: tokens){
            words.add(word);
         }
         doc.add(words);
         String token;
         
         for(int j = 0; j < tokens.length; j++) {
            token = tokens[j];
            
            if(!termList.contains(token)) { // is this term in the dictionary?
               termList.add(token);
               docList = new ArrayList<DocId>();
               DocId doid = new DocId(i, j); // document ID and position passed in
               docList.add(doid); // add to postings for this term
               docLists.add(docList); // add row to postings list
            }
            else { // term is in dictionary, need to make updates
               int index = termList.indexOf(token);
               docList = docLists.get(index);
               int k = 0; 
               boolean match = false; // did we already see this document?
               // search the postings for a document id
               // if match, insert a new position for this document
               for(DocId doid : docList) {
                  if(doid.docId == i) { // we've seen term in this document before
                     doid.insertPosition(j); // add a position to the position list
                     docList.set(k, doid); // update position list
                     match = true;
                     break;
                  }
                  k++;
               }
               
               // if no match, add new document Id to the list, along with position
               if(!match) {
                  DocId doid = new DocId(i, j);
                  docList.add(doid);
               }
            }
         }
      }
   }

   //This method parses the data and returns all the tokens within a arraylist of strings
   public String[] parse(String fileName) {
      String[] tokens = null; // return these tokens at the end
      try {
         @SuppressWarnings("resource")
         BufferedReader reader = new BufferedReader(new FileReader(fileName));
         String allLines = new String(); // store all lines in file in this String
         String line = null;
         
         line = reader.readLine();
         while(line != null) {
            allLines += line.toLowerCase(); // case folding
            line = reader.readLine();
         }
         
         tokens = allLines.split("[ .,?!:;$#%*+/()\"\\-&]+"); 
      }
      catch(IOException ioe) {
         ioe.printStackTrace();
      }
      
      return tokens;
   }
   
   public String toString() {
      String matrixString = new String();
      ArrayList<DocId> docList;
      
      for(int i = 0; i < termList.size(); i++) {
         matrixString += String.format("%-15s", termList.get(i));
         docList = docLists.get(i);
         
         for(int j = 0; j < docList.size(); j++) {
            matrixString += docList.get(j) + "\t"; // DocId has a toString method
         }
         
         matrixString += "\n";
      }
      
      return matrixString;
   }
   
   //The intersect method finds the whether the two strings come one after another within any documents in the collection of documents
   @SuppressWarnings("removal")
   public ArrayList<Object> intersect(String q1, String q2) {
      ArrayList<Integer> mergedList = new ArrayList<Integer>();
      ArrayList<ArrayList<Integer>> postingsList = new ArrayList<ArrayList<Integer>>();
      ArrayList<DocId> l1 = docLists.get(termList.indexOf(q1)); // first term's doc list
      // System.out.println(q1+ " : "+l1);
      ArrayList<DocId> l2 = docLists.get(termList.indexOf(q2)); // second term's doc list
      // System.out.println(q2+ " : "+l2);
      int id1 = 0, id2 = 0; // doc list pointers
      
      while(id1 < l1.size() && id2 < l2.size()) {
         // if both terms appear in the same document
         if(l1.get(id1).docId == l2.get(id2).docId) {
            // get the position information for both terms

            ArrayList<Integer> pp1 = l1.get(id1).positionList;
            ArrayList<Integer> pp2 = l2.get(id2).positionList;
            int pid1 = 0, pid2 = 0; // position list pointers
            ArrayList<Integer> subList = new ArrayList<Integer>();
            while (pid1 < pp1.size() && pid2 < pp2.size()){
               int p1 = pp1.get(pid1).intValue();
               int p2 = pp2.get(pid2).intValue();
               if (p2 - p1 == 1){
                  subList.add(pp2.get(pid2));
                  pid1++;
                  pid2++;
               }
               if (p1 > p2){
                  pid2++;
               }
               if (p2 > p1){
                  pid1++;
               }
               
            }
            
            if (subList.size() > 0){
               mergedList.add(new Integer(l1.get(id1).docId));
               postingsList.add(subList);
            }
            // determine if the two terms have an adjacency in the current document
            // if it does, stop comparing the position lists and add the document ID
            // to the mergedList
            
            id1++;
            id2++;
         }
         else if(l1.get(id1).docId < l2.get(id2).docId)
            id1++;
         else
            id2++;
      }
      ArrayList<Object> ans = new ArrayList<Object>();
      ans.add(mergedList);
      ans.add(postingsList);
      return ans;
   }

   //This method is similar to the intersect method. It finds the intersect between two objects. 
   //Each object consists of two arraylists one stores document ids and the other stores the corresponding arraylist of postings
   @SuppressWarnings({"unchecked" })
   public ArrayList<Object> merge(ArrayList<Object> q1, ArrayList<Object> q2) {
      ArrayList<Integer> mergedList = new ArrayList<Integer>();
      ArrayList<ArrayList<Integer>> postingsList = new ArrayList<ArrayList<Integer>>();
      ArrayList<Integer> docList1 = (ArrayList<Integer>) q1.get(0);
      ArrayList<ArrayList<Integer>> postingList1 = (ArrayList<ArrayList<Integer>>) q1.get(1);
      ArrayList<Integer> docList2 = (ArrayList<Integer>) q2.get(0);
      ArrayList<ArrayList<Integer>> postingList2 = (ArrayList<ArrayList<Integer>>) q2.get(1);
      int id1 = 0, id2 = 0; 

      while (id1<docList1.size() && id2<docList2.size()){
         if (docList1.get(id1).intValue() == docList2.get(id2).intValue()){
            ArrayList<Integer> pp1 = postingList1.get(id1);
            ArrayList<Integer> pp2 = postingList2.get(id2);
            int pid1 = 0, pid2 = 0;
            ArrayList<Integer> subList = new ArrayList<Integer>();
            while (pid1<pp1.size() && pid2<pp2.size()){
               int p1 = pp1.get(pid1).intValue();
               int p2 = pp2.get(pid2).intValue();
               if (p2 - p1 == 1){
                  subList.add(pp2.get(pid2));
                  pid1++;
                  pid2++;
               }
               if (p1 > p2){
                  pid2++;
               }
               if (p2 > p1){
                  pid1++;
               }
            }
            if (subList.size() > 0){
               mergedList.add(docList1.get(id1));
               postingsList.add(subList);
            }
            // determine if the two terms have an adjacency in the current document
            // if it does, stop comparing the position lists and add the document ID
            // to the mergedList
            
            id1++;
            id2++;
         }
         else if(docList1.get(id1).intValue() < docList2.get(id2).intValue())
            id1++;
         else
            id2++;
      }
      ArrayList<Object> ans = new ArrayList<Object>();
      ans.add(mergedList);
      ans.add(postingsList);
      return ans;
   }

   //Implementation of the phraseQuery method
   @SuppressWarnings("unchecked")
   public ArrayList<DocId> phraseQuery(String[] query){
      //Printing the document id and postings information for each token in the query
      for (int q = 0; q < query.length; q++){
         String word = query[q];
         ArrayList<DocId> l1 = docLists.get(termList.indexOf(word));
         System.out.println(word+ " : "+l1);
      }

      ArrayList<DocId> result = new ArrayList<DocId>();
      ArrayList<Integer> docIds = new ArrayList<Integer>();
      ArrayList<ArrayList<Integer>> postings = new ArrayList<ArrayList<Integer>>();

      //Checking whether the query contains more than one keyword
      if (query.length > 1){
      String first = query[0];
      ArrayList<Object> temp = new ArrayList<Object>();
      ArrayList<ArrayList<Object>> ans = new ArrayList<ArrayList<Object>>();
      String next = new String();
      
      //Finding the intersect between each biword combination in a consecutive order
      for(int index = 1; index < query.length; index++){
         next = query[index];
         temp = this.intersect(first, next);
         ans.add(temp);
         first = query[index];
      }
      
      //Finding the intersect between all the objects stored in ans. Merging all the results to get the final object that stores the result
      if (ans.size() > 1){
      ArrayList<Object> finalAns = ans.get(0);
      ArrayList<Object> inter = new ArrayList<Object>();
      ArrayList<Integer> check = new ArrayList<Integer>();
      int id = 1;
      while(id < ans.size()){
            inter = ans.get(id);
            finalAns = this.merge(finalAns,inter);
            check =(ArrayList<Integer>) finalAns.get(0);
            if (check.size() == 0){
               return result;
            }
            id++;
            }

      //Creating a arraylist of DocId objects 
      docIds =(ArrayList<Integer>) finalAns.get(0);
      postings = (ArrayList<ArrayList<Integer>>) finalAns.get(1);
      for(int ids = 0; ids < docIds.size(); ids++){
         int docNo = docIds.get(ids).intValue();
         ArrayList<Integer> posList = postings.get(ids);
         for (int loc = 0; loc < posList.size(); loc++){
            int pos = posList.get(loc).intValue();
            int total = query.length - 1;
            for (int start = pos - total; start < pos + 1; start++){
               DocId doid = new DocId(docNo, start);
               result.add(doid);
            }
         }
      }
      }

      else{
         ArrayList<Object> finalAns = ans.get(0); 
         docIds =(ArrayList<Integer>) finalAns.get(0);
         postings = (ArrayList<ArrayList<Integer>>) finalAns.get(1);
         for(int ids = 0; ids < docIds.size(); ids++){
            int docNo = docIds.get(ids).intValue();
            ArrayList<Integer> posList = postings.get(ids);
            for (int loc = 0; loc < posList.size(); loc++){
               int pos = posList.get(loc).intValue();
               int total = query.length - 1;
               for (int start = pos - total; start < pos + 1; start++){
                  DocId doid = new DocId(docNo, start);
                  result.add(doid);
               }
            }
         }
      }

      }
      
      return result;
      
   }
}

class DocId {
   int docId;
   ArrayList<Integer> positionList;
   
   @SuppressWarnings("removal")
   public DocId(int did, int position) {
      docId = did;
      positionList = new ArrayList<Integer>();
      positionList.add(new Integer(position));
   }
   
   @SuppressWarnings("removal")
   public void insertPosition(int position) {
      positionList.add(new Integer(position));
   }
   
   public String toString() {
      String docIdString = "" + docId + ":<";
      for(Integer pos : positionList)
         docIdString += pos + ",";
      
      // remove extraneous final comma
      docIdString = docIdString.substring(0, docIdString.length() - 1) + ">";
      return docIdString;
   }
}
