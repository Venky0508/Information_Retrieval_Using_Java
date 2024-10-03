import java.util.*;
import java.io.*;

public class Parser {
   String[] myDocs;
   ArrayList<String> stopList;
   ArrayList<ArrayList<String>> docLists;

   
   public Parser(String folderName) {
      docLists = new ArrayList<ArrayList<String>>();
      stopList = new ArrayList<String>();
      //Reading the stopwords into an arraylist of strings
      try (BufferedReader stopReader = new BufferedReader(new FileReader("stopwords.txt"))) {
         String line = null;
         line = stopReader.readLine();
         line  = line.trim();
         while(line != null) {
            stopList.add(line);
            line = stopReader.readLine();
         }
      } catch (IOException e) {
         e.printStackTrace();
      }
      File folder = new File(folderName);
      File[] listOfFiles = folder.listFiles();
      myDocs = new String[listOfFiles.length - 1]; // Here, I did length - 1 because I was getting an additional file named '.DS_Store' after unzipping within the Lab1_Data directory.
      int j = 0;
      for(int i = 1; i < listOfFiles.length; i++) {
         myDocs[j] = listOfFiles[i].getName();
         j++;
      }
      Arrays.sort(myDocs);
      
      // System.out.println("Sorted document list");
      // for(int i = 0; i < myDocs.length; i++)
      //    System.out.println(myDocs[i]);
      
      Collections.sort(stopList); 
      ArrayList<String> subList = new ArrayList<String>();
      //Created Stemmer object
      Stemmer st = new Stemmer();
      
      for(int id = 0; id < myDocs.length; id++){
      String[] tokens = parse(folderName + "/" + myDocs[id]); // parse the first file
      
      for(String token:tokens) {
         if(searchStopword(token) == -1) {
         //Performing stemming and then adding the token to the document arraylist
         st.add(token.toCharArray(), token.length());
         st.stem();
         String ans = st.toString();
         subList.add(ans);
         }
      }
      docLists.add(subList);
      subList = new ArrayList<String>();
   }
   }
   
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
   
   public int searchStopword(String key) {
      int lo = 0;
      int hi = stopList.size() - 1;
      
      while(lo <= hi) {
         // key is in a[lo..hi] or not
         int mid = lo + (hi - lo) / 2;
         int result = key.compareTo(stopList.get(mid));
         
         if(result < 0) // key alphabetically less than current middle stop list term
            hi = mid - 1;
         else if(result > 0 ) // key alphabetically greater than current middle stop list term
            lo = mid + 1;
         else
            return mid; // found stopword match
      }
      
      return -1; // no stopword match
   }
   
}
