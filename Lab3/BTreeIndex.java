//Lab 3
//author: Srivenkatesh Nair (sn6711)

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


public class BTreeIndex {
    TreeNode rootN; //root node of the tree
    ArrayList<TermEntry> tokenMap; //stores the sorted list of objects of the class TermEntry
    ArrayList<String> termList; // dictionary
    ArrayList<ArrayList<Integer>> docIds; // used for each term's postings

    //Constructor
    @SuppressWarnings("removal")
    public BTreeIndex(String[] docs){
        //I have hard coded this list of terms which are not terms that belong to english language and have no context.
        String[] incorrectToken = {"'", "'90s", "'r'", "'s", "10", "1960's", "1990s", "1997's", 
                                    "2", "20", "20th", "3", "4", "7", "8", "9", "y2k"};
        
        //Parsing the documents
        ArrayList<ArrayList<String>> docLists = new ArrayList<ArrayList<String>>();
        termList = new ArrayList<String>();
        docIds = new ArrayList<ArrayList<Integer>>();
        for(int id = 0; id < docs.length; id++){
            String[] tokens = parse("Lab1_Data/" + docs[id]);
            ArrayList<String> subList = new ArrayList<String>();
            for(String word: tokens){
                subList.add(word);
             }
            docLists.add(subList);
        }

        ArrayList<Integer> docList;
        for(int i = 0; i < docLists.size(); i++) {
            ArrayList<String> tokens = docLists.get(i); // perform basic tokenization
            
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
        
        //Creating the arraylist of objects of the class TermEntry
        tokenMap = new ArrayList<TermEntry>();
        for(int j=0; j < termList.size(); j++){
            String word = termList.get(j);
            boolean contains = Arrays.asList(incorrectToken).contains(word);
            if (!contains){
            ArrayList<Integer> posting = docIds.get(j);
            TermEntry node = new TermEntry(word, posting);
            tokenMap.add(node);
            }
        }
        
        // Sorting logic
        Collections.sort(tokenMap, new Comparator<TermEntry>() {
            @Override
            public int compare(TermEntry te1, TermEntry te2) {
                return te1.getTerm().compareTo(te2.getTerm());
            }
        });

        //Finding the root node
        int start = 0;
        int end = tokenMap.size() - 1;
        int mid = (start + end) / 2;
        rootN = new TreeNode(tokenMap.get(mid));
        
        // left side of array passed to left subtree
        buildTree(rootN, tokenMap, start, mid - 1);
        // right side of array passed to right subtree
        buildTree(rootN, tokenMap, mid + 1, end);
    }

    //This method builds the binary tree. Used the similar code as the one given by the professor 
    public void buildTree(TreeNode node, ArrayList<TermEntry> tokenMap, int start, int end ){
        if(start <= end) {
            int mid = (start + end) / 2;
            if(tokenMap.get(mid).getTerm().compareTo(node.data.getTerm()) < 0) { // left subtree
               node.left = new TreeNode(tokenMap.get(mid));
               buildTree(node.left, tokenMap, start, mid - 1);
               buildTree(node.left, tokenMap, mid + 1, end);
            }
            else { // right subtree
               node.right = new TreeNode(tokenMap.get(mid));
               buildTree(node.right, tokenMap, start, mid - 1);
               buildTree(node.right, tokenMap, mid + 1, end);
            }
         }
    }

    //This method inserts a new node into the existing binary tree
    public void add(TreeNode node, TreeNode iNode) {
        if(node == null) {
            node = iNode;
            return;
        }
        if(iNode.data.getTerm().compareTo(node.data.getTerm()) < 0) {
            if(node.left == null) {
                node.left = iNode;
            } 
            else{
                add(node.left, iNode);
            }
        }
        else if(iNode.data.getTerm().compareTo(node.data.getTerm()) > 0) {
            if(node.right == null) {
                node.right = iNode;
            } 
            else{
                add(node.right, iNode);
            }
        }
    }

    //This method searches a node in the existing binary tree with the term similar as key
    public TreeNode search(TreeNode n, String key) {
        if(n == null || n.data.getTerm().equals(key)) {
            return n;
        }

        if(key.compareTo(n.data.getTerm()) < 0) {
            return search(n.left, key);
        } 
        else{
            return search(n.right, key);
        }
    }

    //This method visualizes the binary tree and writes it to a file 'tree.txt'
    public void visualizeTree(TreeNode root){
        ArrayList<ArrayList<TermEntry>> ans = new ArrayList<ArrayList<TermEntry>>();
        for (int k = 0; k < 5; k++){
            ArrayList<TermEntry> temp = new ArrayList<TermEntry>();
            ans.add(temp);
        }
        //Calls the visualize() method to recursively store the tree in the arraylist 'ans'
        visualize(root, 0, ans);

        String fileName = "tree.txt";
        File file = new File(fileName);

        try {
            // Check if the file already exists
            if (!file.exists()) {
                // Create a new file
                file.createNewFile();
                System.out.println("File created: " + fileName);
            } else {
                System.out.println("File already exists: " + fileName);
            }

        // Write to the file
        FileWriter fw = new FileWriter(file.getAbsoluteFile()); 
        BufferedWriter bw = new BufferedWriter(fw);
        for (int x=0; x < 5; x++){
            if(x != 0){
                bw.write("Level "+ x + " :\n");
            }
            else{
                bw.write("Level "+ x + " (Root Node) :\n");
            }
            ArrayList<TermEntry> nodes = ans.get(x);
            int flag1 = 0;
            int flag2 = 0;
            for (int y=0; y < nodes.size(); y++){
                if(y < nodes.size()/2 && flag1 == 0 && x != 0){
                    bw.write("-------Left Subtree------\n");
                    flag1 = 1;
                }
                if(y == nodes.size()/2 && flag2 == 0 && x != 0){
                    bw.write("-------Right Subtree------\n");
                    flag2 = 1;
                }
                TermEntry node = nodes.get(y);
                bw.write("Term: "+ node.getTerm() + "  Postings: "+ node.getPostings()+"\n");

            }
            bw.write("\n");
        }
        bw.close();
        System.out.println("Content written to file: " + fileName);
    }
    catch (IOException e) {
        e.printStackTrace();
    }
    }

    //This method recursively stores the binary tree in the arraylist 'result'
    public void visualize(TreeNode rootNode, int level, ArrayList<ArrayList<TermEntry>> result){
        if(level < 5){
            ArrayList<TermEntry> nodes = result.get(level);
            nodes.add(rootNode.data);
            result.set(level, nodes);
            visualize(rootNode.left, level+1, result);
            visualize(rootNode.right, level+1, result);
        }
    }
    
    //Used the andMerge() method from Lab 1 to perform AND operation on conjunctive queries
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
    
    //This method parses the data files and performs tokenization
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

    public static void main(String[] args) {
        
        String folderName = "Lab1_Data"; 
        File folder = new File(folderName);
        File[] listOfFiles = folder.listFiles();
        String[] myDocs = new String[listOfFiles.length - 1];
        int j = 0;
        for(int i = 1; i < listOfFiles.length; i++) {
         myDocs[j] = listOfFiles[i].getName();
         j++;
        }
        Arrays.sort(myDocs);

        //Creating the binary tree index
        BTreeIndex tree = new BTreeIndex(myDocs);
        TreeNode root = tree.rootN;

        //Queries to be searched 
        String query1 = new String("wisdom");
        String query2 = new String("plot");
        String[] query3 = {"strange","thing"};
        String[] query4 = {"film","review"};
        String[] query5 = {"a","good","start"};
        String[] query6 = {"american","thrilling","chase"};

        //Query 1
        System.out.println("Search Query 1:" + query1);
        TreeNode ans1 = tree.search(root, query1);
        System.out.println("Result:");
        if(ans1 != null) {
            System.out.println("Term: "+ans1.data.getTerm()+ "  Document IDs: "+ans1.data.getPostings());
        }
        else{
            System.out.println("No Documents!");
        }
        System.out.println(" ");

        //Query 2
        System.out.println("Search Query 2:" + query2);
        TreeNode ans2 = tree.search(root, query2);
        System.out.println("Result:");
        if(ans2 != null) {
            System.out.println("Term: "+ans2.data.getTerm()+ "  Document IDs: "+ans2.data.getPostings());
        }
        else{
            System.out.println("No Documents!");
        }
        System.out.println(" ");

        //Query 3
        System.out.print("Search Query 3: ");
        for(int index1=0; index1 < query3.length; index1++){
            System.out.print(query3[index1] + " ");
        }
        TreeNode prev1 = tree.search(root, query3[0]);
        System.out.println("\nTerm: "+prev1.data.getTerm()+ "  Document IDs: "+prev1.data.getPostings());
        ArrayList<Integer> ans3 = prev1.data.getPostings();
        TreeNode next1;
        for(int pointer1=1; pointer1 < query3.length; pointer1++){
            next1 = tree.search(root, query3[pointer1]);
            System.out.println("Term: "+next1.data.getTerm()+ "  Document IDs: "+next1.data.getPostings());
            ArrayList<Integer> postings = next1.data.getPostings();
            ans3 = tree.andMerge(ans3, postings);
        }
        System.out.println("Result:");
        if(ans3.size() != 0){
            System.out.println("Document IDs: "+ ans3);
        }
        else{
            System.out.println("No Documents!");
        }
        System.out.println(" ");
        
        //Query 4
        System.out.print("Search Query 4: ");
        for(int index2=0; index2 < query4.length; index2++){
            System.out.print(query4[index2] + " ");
        }
        TreeNode prev2 = tree.search(root, query4[0]);
        System.out.println("\nTerm: "+prev2.data.getTerm()+ "  Document IDs: "+prev2.data.getPostings());
        ArrayList<Integer> ans4 = prev2.data.getPostings();
        TreeNode next2;
        for(int pointer2=1; pointer2 < query4.length; pointer2++){
            next2 = tree.search(root, query4[pointer2]);
            System.out.println("Term: "+next2.data.getTerm()+ "  Document IDs: "+next2.data.getPostings());
            ArrayList<Integer> postings = next2.data.getPostings();
            ans4 = tree.andMerge(ans4, postings);
        }
        System.out.println("Result:");
        if(ans4.size() != 0){
            System.out.println("Document IDs: "+ ans4);
        }
        else{
            System.out.println("No Documents!");
        }
        System.out.println(" ");

        //Query 5
        System.out.print("Search Query 5: ");
        for(int index3=0; index3 < query5.length; index3++){
            System.out.print(query5[index3] + " ");
        }
        TreeNode prev3 = tree.search(root, query5[0]);
        System.out.println("\nTerm: "+prev3.data.getTerm()+ "  Document IDs: "+prev3.data.getPostings());
        ArrayList<Integer> ans5 = prev3.data.getPostings();
        TreeNode next3;
        for(int pointer3=1; pointer3 < query5.length; pointer3++){
            next3 = tree.search(root, query5[pointer3]);
            System.out.println("Term: "+next3.data.getTerm()+ "  Document IDs: "+next3.data.getPostings());
            ArrayList<Integer> postings = next3.data.getPostings();
            ans5 = tree.andMerge(ans5, postings);
        }
        System.out.println("Result:");
        if(ans5.size() != 0){
            System.out.println("Document IDs: "+ ans5);
        }
        else{
            System.out.println("No Documents!");
        }
        System.out.println(" ");

        //Query 4
        System.out.print("Search Query 6: ");
        for(int index4=0; index4 < query6.length; index4++){
            System.out.print(query6[index4] + " ");
        }
        TreeNode prev4 = tree.search(root, query6[0]);
        System.out.println("\nTerm: "+prev4.data.getTerm()+ "  Document IDs: "+prev4.data.getPostings());
        ArrayList<Integer> ans6 = prev4.data.getPostings();
        TreeNode next4;
        for(int pointer4=1; pointer4 < query6.length; pointer4++){
            next4 = tree.search(root, query6[pointer4]);
            System.out.println("Term: "+next4.data.getTerm()+ "  Document IDs: "+next4.data.getPostings());
            ArrayList<Integer> postings = next4.data.getPostings();
            ans6 = tree.andMerge(ans6, postings);
        }
        System.out.println("Result:");
        if(ans6.size() != 0){
            System.out.println("Document IDs: "+ ans6);
        }
        else{
            System.out.println("No Documents!");
        }
        System.out.println(" ");

        System.out.println("Visualization of the binary tree - ");
        tree.visualizeTree(root);   
    }  

}

//This class is used to create an object which stores the term and corresponding postings in the binary tree index
class TermEntry{
    String term;
    ArrayList<Integer> postings;

    public TermEntry(String word, ArrayList<Integer> docIds){
        term = word;
        postings = docIds;
    }

    public String getTerm() {
        return term;
    }

    public ArrayList<Integer> getPostings() {
        return postings;
    }
}

//This class is used to create nodes of the binary tree
class TreeNode {
    TreeNode left;
    TreeNode right;
    TermEntry data;
    
    public TreeNode(TermEntry value) {
       data = value;
    }
 }