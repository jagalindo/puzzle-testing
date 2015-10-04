/**
 * This class contains the information for fixing sets of a single feature
 */
package at.jku.sea.mvsc.fixing.simulation;

import java.io.Serializable;
import java.util.Set;

public class FeatureFixingSet implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;
	
	// Data
	int feature;					// feature id of the requiring feature
	Set<Integer> fixingSet;			// list of features in the fixing set
	long elapsedTime;				// time in miliseconds taken to compute the fixing set
	boolean isFixing;				// true if the fixing sets solves the inconsistencies or false if there is no fixing set
	int numberCommonFeatures;		// number of common features with feature F, notice that maximum is num. features in SPL - 1
	
	
	public FeatureFixingSet(int feature, Set<Integer> fixingSet, long elapsedTime, boolean isFixing, int numberCommonFeatures) {
		this.feature = feature;
		this.fixingSet= fixingSet;
		this.elapsedTime = elapsedTime;
		this.isFixing = isFixing;
		this.numberCommonFeatures =  numberCommonFeatures;
	}
	
	public int getFeature() { return feature;}
	public Set<Integer> getFixingSet() { return fixingSet; }
	public long getElapsedTime() { return elapsedTime; }
	public boolean getIsFixing() { return isFixing; }
	public double getNumberCommonFeatures() { return numberCommonFeatures; }
	
}// of FeatureFixingSet
