/**
 * This class contains the fixing sets of an entire product line.
 * The serialization is going to determine the policy to use 
 */
package at.jku.sea.mvsc.fixing.simulation;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class SPLFixingSets implements Serializable {
	/** Default serializationID */
	private static final long serialVersionUID = 1L;
	
	// Data
	String featureModel;			// name of the feature model
	String fixingPolicy;			// name of the policy to use (all, excludes root, non-mandatory, etc.)
	double numberOfProducts;			// number of products in the product line
	int numberOfFeatures;			// number of features in the product line
	double averageCommonRatio;		// average ratio, for feature F =  (FixingSet length) / common F.
	double averageComputationTime;  // average computation time for all fixing sets
	double averageFixingSetLength;	// average length of fixing set
	
	// Mappings between features ids and their names
	protected Map<Integer, String> mapIdToFeature;
	protected Map<String, Integer> mapFeatureToId;

	// Map with the measurements value
	protected List<FeatureFixingSet> featuresFixingSets; 
	
	public SPLFixingSets(String featureModelFileName, String fixingPolicy, Map<Integer, String> mapIdToFeature, Map<String, Integer> mapFeatureToId,
			double numberOfProducts, int numberOfFeatures) {
		this.featureModel = featureModelFileName;
		this.fixingPolicy = fixingPolicy;
		this.mapIdToFeature = mapIdToFeature;
		this.mapFeatureToId = mapFeatureToId;
		this.numberOfProducts = numberOfProducts;
		this.numberOfFeatures = numberOfFeatures;
	}
	
	
	/** Computes the statistics
	 * Average common ratio =  fixing set length / common size
	 * Average computation time 
	 * Average fixing set length
	 */
	public void computeStatistics() {
		// local var
		int fixingSetSize;
		
		// Resets statistics values
		averageCommonRatio = 0;
		averageComputationTime = 0;
		averageFixingSetLength = 0;
		
		// Accumulating the values
		for (FeatureFixingSet featureFixingSet :  featuresFixingSets) {
			fixingSetSize = featureFixingSet.getFixingSet().size();
			averageCommonRatio+=((double)fixingSetSize) / ((double)featureFixingSet.getNumberCommonFeatures());
			averageComputationTime+=featureFixingSet.getElapsedTime();
			averageFixingSetLength+=fixingSetSize;
		}
		
		// Computing the average
		averageCommonRatio = averageCommonRatio / numberOfFeatures;
		averageComputationTime = averageComputationTime / numberOfFeatures;
		averageFixingSetLength = averageFixingSetLength / numberOfFeatures;
		
	} // of compute statistics
	
	// **** getter and setters
	public void setFeatureFixingSets(List<FeatureFixingSet> featuresFixingSets) { this.featuresFixingSets = featuresFixingSets; }
	public Map<Integer, String> getMapIdToFeature () { return this.mapIdToFeature; }
	public Map<String, Integer> getMapFeatureToId () { return this.mapFeatureToId; }
	public List<FeatureFixingSet> getFeaturesFixingSets() { return featuresFixingSets; }
	public String getFeatureModel() { return featureModel; }
	public String getFixingPolicy() { return fixingPolicy; }
	public double getAverageCommonRatio() { return averageCommonRatio; }
	public double getAverageComputationTime() { return averageComputationTime; }
	public double getAverageFixingSetLength() { return averageFixingSetLength; }
 	public double getNumberOfProducts() { return numberOfProducts; }
	public int getNumberOfFeatures() { return numberOfFeatures; }
	
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("SPL " + featureModel + " policy " + fixingPolicy);
		
		for(FeatureFixingSet ffs : featuresFixingSets) {
			buffer.append("\n<" + mapIdToFeature.get(ffs.feature)+ ",[");
			for (int feature : ffs.getFixingSet()) {
				buffer.append(mapIdToFeature.get(feature) + " ");
			}
			
			buffer.append(" ],");
			buffer.append(ffs.isFixing + ",");
			buffer.append(ffs.elapsedTime + ",");
			buffer.append(ffs.numberCommonFeatures);
			buffer.append(">");
		}// for all features
	
		// Adds the SPL stats
		buffer.append("\n<Avg.FSLenght=" + averageFixingSetLength + ", Avg.CommonRatio=" + averageCommonRatio + ", Avg.ComputationTime=" + averageComputationTime);
		buffer.append(">");
		
		return buffer.toString();
	} // of toString
	
} // of SPLFixingSets
