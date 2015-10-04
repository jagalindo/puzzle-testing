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

public class ExcludesSPLMandatory extends SelectionPolicy {


	/**
	 * Selects the feature with largest PWC value that is not selected in valueAssignments and not selected in assumingFeatures,
	 * AND that is NOT a SPL-wide mandatory feature
	 * The return value is the ID of the feature, and thus it must always be positive value.
	 * Note: the pwcList is sorted in descending order
	 */
	public int selectFeature(List<PairwiseCommonalityPair> pwcList, int[] valueAssignments, List<Integer> assummingFeatures, SPL spl) {
		int feature = SelectionPolicy.INCOMPATIBLE_FEATURE;;
		
		/* 
		 * Traverses the PWC pairs list until it finds a feature that:
		 * 1) Has been selected in the configuration, because it can fix this configuration if an element to it is added
		 * 2) Has not been assumed, that is, it is not part of the fixing set already
		 */
		
		//@debug
		/*
		System.out.print("\nConfiguration [");
		for (int f : valueAssignments)  {
			if (f<0) System.out.print("NOT ");
			System.out.print(spl.getFeatureFromVariable(Math.abs(f)).getName() + " ");
		}
		System.out.println("]");
		*/
		
		//@debug
		/*
		System.out.print("Assumed [");
		for (int f : assummingFeatures)  {
			if (f<0) System.out.print("NOT ");
			System.out.print(spl.getFeatureFromVariable(Math.abs(f)).getName() + " ");
		}
		System.out.println("]");
		*/
		
		for (PairwiseCommonalityPair pwcPair : pwcList) {
			feature = pwcPair.featureID;

			//@debug
			/*
			System.out.print("<" + spl.getFeatureFromVariable(feature).getName() + "," + isFeatureSelected(feature, valueAssignments)
					+ "," + !isFeatureAssumed(feature, assummingFeatures) + "," + pwcPair.numProducts + ">");
			*/
			
			// checks if the feature is selectable or not
			if (isFeatureSelected(feature, valueAssignments) && !isFeatureAssumed(feature, assummingFeatures)) {
				// if selectable, we need now to check if its PWC value is not 0, meaning it cannot be a fixing solution
				if (pwcPair.numProducts>0) {
					return feature;  
				}
			} // of selectable feature
			
		} // for all feature pairs
		System.out.println();
		
		return SelectionPolicy.INCOMPATIBLE_FEATURE;
				
	} // of selectFeature

	
	
	/**
	 * Removes from PWC pair values those for features that are SPL-wide common
	 * @param commonalityValues List of PWC read from the statistics file.
	 * @param commonFeatures  List of features common to all members of the SPL
	 */
	protected List<PairwiseCommonalityPair> pruneFeatures(List<PairwiseCommonalityPair> commonalityValues, SPL spl) {
		List<PairwiseCommonalityPair> result = new LinkedList<PairwiseCommonalityPair>();
		
		List<Integer> commonFeatures = spl.getSPLWideCommonFeatures();
		for (PairwiseCommonalityPair pair : commonalityValues) {
			if (!commonFeatures.contains(pair.featureID)) {
				result.add(pair);
			}
		}
		
		return result;
		
	} // of prune of SPL-wide features

}
