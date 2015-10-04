/**
 * This class implements the policy of selecting a fixing feature from all the features of the feature model.
 * 
 */
package at.jku.sea.mvsc.fixing.simulation;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import at.jku.sea.mvsc.Feature;
import at.jku.sea.mvsc.LogicTransformation;
import at.jku.sea.mvsc.SPL;
import at.jku.sea.mvsc.constraints.SCUtils;
import at.jku.sea.sat.implementation.picosat.Picosat4J;

public class SelectFromAll extends SelectionPolicy {

	/**
	 * Selects the feature with largest PWC value that is not selected in valueAssignments and not selected in assumingFeatures
	 * The return value is the ID of the feature, and thus it must always be positive value.
	 * Note: the pwcList is sorted in descending order
	 */
	public int selectFeature(List<PairwiseCommonalityPair> pwcList, int[] valueAssignments, List<Integer> assummingFeatures, SPL spl) {
		int feature = 0;
		
		for (PairwiseCommonalityPair pwcPair : pwcList) {
			feature = pwcPair.featureID;
			
			// checks if the feature is selectable or not
			if (isFeatureSelected(feature, valueAssignments) && !isFeatureAssumed(feature, assummingFeatures)) {
				// if selectable, we need now to check if its PWC value is not 0, meaning it cannot be a fixing solution
				if (pwcPair.numProducts>0) {
					return feature;  
				} else {
					return SelectionPolicy.INCOMPATIBLE_FEATURE;
				}
			} // of selectable feature
			
		} // for all feature pairs
		
		return feature;
		
	} // of selectFeature
	
	/**
	 * Keeps the original PWC pairs because it is looking for all features
	 * @param commonalityValues List of PWC read from the statistics file.
	 * @param commonFeatures  List of features common to all members of the SPL
	 */
	protected List<PairwiseCommonalityPair> pruneFeatures(List<PairwiseCommonalityPair> commonalityValues, SPL spl) {
		return commonalityValues;
	} // of prune of SPL-wide features
	
	
} // of SelectFromAll policy class
