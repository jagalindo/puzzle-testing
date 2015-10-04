/**
 * PairwiseCommonality
 * This class contains the information of pair-wise commonality of a SPL
 * The objects are serialized
 */
package at.jku.sea.mvsc.fixing.simulation;

import java.io.Serializable;
import java.util.Map;

public class PairwiseCommonality implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3L;
	// Data
	String featureModel;			// name of the feature model
	double numberOfProducts;		// available for all the product line
	int numberOfFeatures;			// number of features of the feature model
	long elapsedTime;				// time elapsed to compute the number of all products of the SPL
	
	// Mappings between features ids and their names
	protected Map<Integer, String> mapIdToFeature;
	protected Map<String, Integer> mapFeatureToId;

	// Map with the measurements value
	protected Map<IndexMatrix, PairwiseCommonalityMeasurement> measurementMap; 
	
	public PairwiseCommonality(String featureModelFileName, Map<Integer, String> mapIdToFeature, Map<String, Integer> mapFeatureToId,
			Map<IndexMatrix, PairwiseCommonalityMeasurement> measurementMap) {
		this.featureModel = featureModelFileName;
		this.mapIdToFeature = mapIdToFeature;
		this.mapFeatureToId = mapFeatureToId;
		this.measurementMap = measurementMap;
	}
	
	public void setNumberOfProducts(double number) {
		this.numberOfProducts = number;
	}
	
	public void setNumberOfFeatures(int number) {
		this.numberOfFeatures = number;
	}
	
	public void setElapsedTime(long time) {
		this.elapsedTime = time;
	}
	
	// Get and set methods
	public Map<Integer, String> getMapIdToFeature () { return this.mapIdToFeature; }
	public Map<String, Integer> getMapFeatureToId () { return this.mapFeatureToId; }
	public Map<IndexMatrix, PairwiseCommonalityMeasurement> getMeasurementMap() { return measurementMap; }
	public int getNumberOfFeatures() { return numberOfFeatures; }
	public double getNumberOfProducts() { return numberOfProducts; }
	
	
	
	// *****************************************************************
	// Statistics computed from pairwise values
	public long getTotalComputationElapsedTime() {
		long result = 0;
		// Gets the list of PWC measurements and accumulates the time of each of them
		for (PairwiseCommonalityMeasurement pwcMeasurement : measurementMap.values()) {
			result+=pwcMeasurement.getComputationElapsedTime();
		}
		return result;
	} // of getTotalComputationElapsedTime
	
	
} // PairwiseCommonality class

