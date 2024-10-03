import java.util.*;

public class InvertedIndex {
   ArrayList<String> termList; // dictionary
   ArrayList<ArrayList<Integer>> docIds; // used for each term's postings
   
   @SuppressWarnings("removal")
   public InvertedIndex(ArrayList<ArrayList<String>> docs) {
      ArrayList<ArrayList<String>> myDocs = docs; // store document collection here
      termList = new ArrayList<String>();
      docIds = new ArrayList<ArrayList<Integer>>();
      ArrayList<Integer> docList; // singular postings for a given term
      
      for(int i = 0; i < myDocs.size(); i++) {
         ArrayList<String> tokens = myDocs.get(i); // perform basic tokenization
         
         for(String token : tokens) {
            if(!termList.contains(token)) { // new term
               termList.add(token); // add term to dictionary
               docList = new ArrayList<Integer>(); // postings for this term
               docList.add(new Integer(i)); // create initial posting for the term
               docIds.add(docList); // add postings list for this term
            }
            else { // an existing term; update postings list for that term
               int index = termList.indexOf(token); // find index from term list
               docList = docIds.get(index);
               
               if(!docList.contains(new Integer(i))) { // not already a posting
                  docList.add(new Integer(i)); // add posting to postings
                  docIds.set(index, docList); // update postings for this term
               }
            }
         }
      }
   }
   
   public String toString() {
      String matrixString = new String();
      ArrayList<Integer> docList;
      
      for(int i = 0; i < termList.size(); i++) {
         matrixString += String.format("%-15s", termList.get(i));
         docList = docIds.get(i);
         
         for(int j = 0; j < docList.size(); j++) {
            matrixString += docList.get(j) + "\t";
         }
         
         matrixString += "\n";
      }
      
      return matrixString;
   }
   
   public ArrayList<Integer> oneWordSearch(String query) {
      Stemmer st = new Stemmer();
      st.add(query.toCharArray(), query.length());
      st.stem();
      String ans = st.toString();
      int index = termList.indexOf(ans);
      
      if(index < 0) // no documents contain this keyword, return nothing
         return null;
      
      return docIds.get(index); // return postings for this term
   }
   
   public ArrayList<Integer> andSearch(String[] query) {
      Stemmer st = new Stemmer();
      st.add(query[0].toCharArray(), query[0].length());
      st.stem();
      String ans = st.toString();
      ArrayList<Integer> result = oneWordSearch(ans); // look for first keyword
      int termId = 1;
      
      while(termId < query.length) { // look for remaining keywords
         st.add(query[termId].toCharArray(), query[termId].length());
         st.stem();
         String newAns = st.toString();
         ArrayList<Integer> result1 = oneWordSearch(newAns); // look for current keyword
         result = andMerge(result, result1); // merge current list with intermediate list
         termId++;
      }
      
      return result;
   }

   public ArrayList<Integer> orSearch(String[] query) {
      Stemmer st = new Stemmer();
      st.add(query[0].toCharArray(), query[0].length());
      st.stem();
      String ans = st.toString();
      ArrayList<Integer> result = oneWordSearch(ans); // look for first keyword
      int termId = 1;
      
      while(termId < query.length) { // look for remaining keywords
         st.add(query[termId].toCharArray(), query[termId].length());
         st.stem();
         String newAns = st.toString();
         ArrayList<Integer> result1 = oneWordSearch(newAns); // look for current keyword
         result = orMerge(result, result1); // merge current list with intermediate list
         termId++;
      }
      
      return result;
   }

   //Updated Implementation of AND operator to merge; handling the case of Null Pointer Exception
   public ArrayList<Integer> andMerge(ArrayList<Integer> l1, ArrayList<Integer> l2) {
      ArrayList<Integer> mergedList = new ArrayList<Integer>();
      int id1 = 0, id2 = 0; // positions in the respective lists
      
      while(id1 < l1.size() && id2 < l2.size()) {
         if (id1 == l1.size()-1 && id2 == l2.size()-1){
            if(l1.get(id1).intValue() == l2.get(id2).intValue()) { // found a match
               mergedList.add(l1.get(id1));
            }
            break;
         }
         
         if(l1.get(id1).intValue() == l2.get(id2).intValue()) { // found a match
            mergedList.add(l1.get(id1));
            if(id1 != l1.size()-1){
               id1++;
            }
            if(id2 != l2.size()-1){
               id2++;
            }
            
         }
         else if(l1.get(id1) < l2.get(id2)) {
            if(id1 == l1.size()-1 && id2 !=l2.size()-1){
               id2++;
            }
            if(id1 != l1.size()-1){
               id1++;
            }
         }
         else{
            if(id2 == l2.size()-1 && id1 != l1.size()-1){
               id1++;
            }
            if(id2 != l2.size()-1){
               id2++;
            }
         }
         
      }
      
      return mergedList;
   }

   //Implementation of OR operator to merge 
   public ArrayList<Integer> orMerge(ArrayList<Integer> l1, ArrayList<Integer> l2) {
      ArrayList<Integer> mergedList = new ArrayList<Integer>();
      if (l1.size() > l2.size()){
         mergedList = l1;
         for (int id1 = 0; id1 < l2.size(); id1++){
            Integer docId = l2.get(id1);
            if (!mergedList.contains(docId)){
               mergedList.add(docId);
            }
         }
      }
      else{
         mergedList = l2;
         for (int id2 = 0; id2 < l1.size(); id2++){
            Integer docId = l1.get(id2);
            if (!mergedList.contains(docId)){
               mergedList.add(docId);
            }
         }
      }
      
      Collections.sort(mergedList);
      
      return mergedList;
   }
   
}