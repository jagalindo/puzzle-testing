/**
 * This class contains the data that will be computed for each entry of the
 * pairwise commonality metric.
 * Data stored: number of products in common, time elapsed to compute the number of products.
 */
package at.jku.sea.mvsc.fixing.simulation;

import java.io.Serializable;

public class PairwiseCommonalityMeasurement implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4L;
	double numberOfCommonProducts;
	long computationElapsedTime;
	
	
	public PairwiseCommonalityMeasurement(double products, long time) {
		this.numberOfCommonProducts = products;
		this.computationElapsedTime = time;
	}
	public double getNumberOfCommonProducts() { return this.numberOfCommonProducts; }
	public long getComputationElapsedTime() { return this.computationElapsedTime; }
}
