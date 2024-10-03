// Lab 5
// author: Srivenkatesh Nair

import java.util.*;

/**
 * Document clustering
 *
 */
public class Clustering {
	public int numClusters;  // Number of clusters
    public ArrayList<Doc> documents;  // List of documents
    public ArrayList<ArrayList<Doc>> clusters;  // List of clusters

    // Constructor to initialize the number of clusters
    public Clustering(int numC) {
        this.numClusters = numC;
        this.documents = new ArrayList<Doc>();
        this.clusters = new ArrayList<ArrayList<Doc>>(numC);
        for (int i = 0; i < numC; i++) {
            clusters.add(new ArrayList<Doc>());
        }
    }

    // Method to preprocess documents and create document vectors
    public void preprocess(String[] docs) {
        // Create a list of unique terms (vocabulary)
        Set<String> vocabSet = new HashSet<String>();
        for (String doc : docs) {
            String[] terms = doc.split(" ");
            vocabSet.addAll(Arrays.asList(terms));
        }
        ArrayList<String> vocabList = new ArrayList<String>(vocabSet);
		Collections.sort(vocabList);
		// System.out.println(vocabList);

        // Create Doc objects for each document
        for (String doc : docs) {
            this.documents.add(new Doc(doc, vocabList));
        }
	
    }

    // Method to perform k-means clustering
    public void cluster() {
        // Choose initial centroids (first and ninth documents)
        Doc centroid1 = new Doc(documents.get(0));
        Doc centroid2 = new Doc(documents.get(8));
        ArrayList<Doc> initialCentroids = new ArrayList<Doc>();
		initialCentroids.add(centroid1);
        initialCentroids.add(centroid2);

		ArrayList<Double> temp = new ArrayList<Double>();
		ArrayList<ArrayList<Double>> newCentroids = new ArrayList<ArrayList<Double>>();
		newCentroids.add(temp);
		newCentroids.add(temp);

        

        boolean changed = true;
		boolean flag = true;

        while (changed == true) {
            // Clear previous clusters
            for (ArrayList<Doc> cluster : clusters) {
                cluster.clear();
            }

            // Assign each document to the nearest centroid
            for (Doc doc : documents) {
                double minDistance = Double.MAX_VALUE;
                int closestCentroidIndex = -1;
				if (flag == true){
					for (int i = 0; i < initialCentroids.size(); i++) {
						double distance = distanceTo(initialCentroids.get(i).termFreq, doc.termFreq);
						if (distance < minDistance) {
							minDistance = distance;
							closestCentroidIndex = i;
						}
					}
					clusters.get(closestCentroidIndex).add(doc);
				}
				else{
					for (int i = 0; i < newCentroids.size(); i++) {
						double distance = distanceTo(newCentroids.get(i), doc.termFreq);
						if (distance < minDistance) {
							minDistance = distance;
							closestCentroidIndex = i;
						}
					}
					clusters.get(closestCentroidIndex).add(doc);
				}

            }

            // Recompute centroids
            changed = false;
			if (flag == true){
				for (int i = 0; i < initialCentroids.size(); i++) {
					if (clusters.get(i).isEmpty()) {
						continue;  // Skip centroid update if the cluster is empty
					}

					ArrayList<Double> newCentroid = computeCentroid(clusters.get(i));
					if (isEqual(newCentroid,initialCentroids.get(i).termFreq) == false) {
						newCentroids.set(i, newCentroid);
						changed = true;
					}
				}
				flag = false;
			}
			else{
				for (int i = 0; i < newCentroids.size(); i++) {
					if (clusters.get(i).isEmpty()) {
						continue;  // Skip centroid update if the cluster is empty
					}

					ArrayList<Double> newCentroid = computeCentroid(clusters.get(i));
					if (isEqual(newCentroid,newCentroids.get(i)) == false) {
						newCentroids.set(i, newCentroid);
						changed = true;
					}
				}
			}
        }

        // Output the results
        for (int i = 0; i < clusters.size(); i++) {
            System.out.println("Cluster: " + i);
            for (Doc doc : clusters.get(i)) {
                System.out.print(documents.indexOf(doc)+"  ");
            }
			System.out.println("\n");
        }
	
    }

	// Method to calculate the Euclidean distance between this document and another document
    public double distanceTo(ArrayList<Double> one, ArrayList<Double> two) {
        double sum = 0.0;
        for (int i = 0; i < one.size(); i++) {
            double v1 = one.get(i);
            double v2 = two.get(i);
            sum += Math.pow(v1 - v2, 2);
        }
        return Math.sqrt(sum);
    }

	//Method to compute the new centroid by taking the average value 
	public ArrayList<Double> computeCentroid(ArrayList<Doc> docs) {
        if (docs.isEmpty()){
		 return null;
		}
        ArrayList<Double> centroidFreq = new ArrayList<>(Collections.nCopies(docs.get(0).termIndex.size(), 0.0));
		// Summing up to get term frequencies for each term in the cluster
        for (Doc doc : docs) {
            for (int i = 0; i < doc.termFreq.size(); i++) {
                centroidFreq.set(i, centroidFreq.get(i) + doc.termFreq.get(i));
            }
        }
        // Divide the sum by the number of documents to get the average
        for (int i = 0; i < centroidFreq.size(); i++) {
            centroidFreq.set(i, centroidFreq.get(i).doubleValue() / docs.size());
        }
        return centroidFreq;
    }

	//Method to check whether we are getting the same centroid as the previous iteration
	public boolean isEqual(ArrayList<Double> first, ArrayList<Double> second){
		for (int i = 0; i < first.size(); i++){
			double x = first.get(i).doubleValue();
			double y = second.get(i).doubleValue();
			if (x != y){
				return false;
			}
		}
		return true;
	}


    public static void main(String[] args) {
        String[] docs = {
            "hot chocolate cocoa beans",  // doc 0
            "cocoa ghana africa",         // doc 1
            "beans harvest ghana",        // doc 2
            "cocoa butter",               // doc 3
            "butter truffles",            // doc 4
            "sweet chocolate can",        // doc 5
            "brazil sweet sugar can",     // doc 6
            "suger can brazil",           // doc 7
            "sweet cake icing",           // doc 8
            "cake black forest"           // doc 9
        };

        Clustering c = new Clustering(2);
        c.preprocess(docs);
        c.cluster();

        /*
         * Expected result:
         * Cluster: 0
         * 0    1    2    3    4    
         * Cluster: 1
         * 5    6    7    8    9    
         */
    }
}



/**
 * 
 * Document class for the vector representation of a document
 */
class Doc {
    public ArrayList<Double> termFreq;  // termFreq: Stores the frequency of each term
    public ArrayList<String> termIndex;  // termIndex: Stores the list of terms (vocabulary)

    // Constructor to create a document vector from text
    public Doc(String text, ArrayList<String> allTerms) {
        this.termIndex = allTerms;
        this.termFreq = new ArrayList<Double>(Collections.nCopies(termIndex.size(), 0.0)); // Initialize with 0s

        String[] terms = text.split(" ");
		
        for (String term : terms) {
            int index = termIndex.indexOf(term);
            if (index != -1) {
                termFreq.set(index, termFreq.get(index) + 1);
            }
        }
    }

    // Copy constructor
    public Doc(Doc doc) {
        this.termFreq = new ArrayList<>(doc.termFreq);
        this.termIndex = doc.termIndex;
    }

    @Override
    public String toString() {
        return termFreq.toString();
    }
}



