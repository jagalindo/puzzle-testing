/**
 * Interface implemented by the classes that realize the different policies for 
 * selecting fixing features from a set
 */
package at.jku.sea.mvsc.fixing.simulation;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
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

public abstract class SelectionPolicy {
	
	public static final int INCOMPATIBLE_FEATURE= Integer.MAX_VALUE;
	public static final String EXCLUDES_SPL_MANDATORY = "Excludes SPL Mandatory";
	public static final String EXCLUDES_ROOT = "Excludes Root";
	public static final String SELECT_FROM_ALL = "Select from All";
	
	// File extension values
	public static final String EXTENSION_EXCLUDES_SPL_MANDATORY =".nm";
	public static final String EXTENSION_EXCLUDES_ROOT = ".nr";
	public static final String EXTENSION_SELECT_FROM_ALL = ".all";
	
	/**
	 * Method that selects a feature based on the pairwise commonality values for a given feature
	 * @param pwcList List of pwc values (FeatureID, NumOfProducts) for a given feature
	 * @param valueAssignments Contains the output of the SAT solver assignments
	 * @param assummingFeatures The list of features that have been currently assumed
	 * @return
	 */
	public void computeFixingSet (PairwiseCommonality pwc, SPL spl, String policy, String fileExtension, String targetFile) {
		// @debug
		System.out.println("\n\nComputing Satisfying Feature Sets " + policy);
		
		// Loads up the pairwise commonality file
		// PairwiseCommonality pwc = deserializationPairwiseCommonality(pwcFileName);
		Map<IndexMatrix, PairwiseCommonalityMeasurement> measurementMap =  pwc.measurementMap;
		
		// Using pico for generating solutions
		Picosat4J picosat4J = Picosat4J.getInstance();
		//picosat4J.printStatistics();
		
		// Sets up the SAT solver with the domain constraints PLf
		SCUtils.initializeDomainConstraintsSAT(picosat4J, spl.getDomainConstraints());
		
		// PairwiseCommonalityPairs hold both a feature ID and the corresponding number of products with that feature
		List<PairwiseCommonalityPair> commonalityValues = new LinkedList<PairwiseCommonalityPair>();
		PairwiseCommonalityMeasurement matrixEntry = null;
		IndexMatrix index = null;
		
		// fixingFeature and fixingSet hold the id;s of the candidate fixing feature and its collected set
		int fixingFeature = Integer.MIN_VALUE; // the defaulted value
		Set<Integer> fixingSet = new HashSet<Integer>();
		
		// List that contains the feature ids currently assummed for this evaluation.
		// Originally only the featureX, and subsquently the fixingFeatures will be added.
		List<Integer> assumingList = new LinkedList<Integer>();
	

		// Creates the data structure to serialized the computed fixing sets for the entire spl and the list individual feature sets
		SPLFixingSets splFixingSets = new SPLFixingSets(spl.getSPLName(), policy, pwc.getMapIdToFeature(), pwc.getMapFeatureToId(),
														pwc.numberOfProducts, pwc.numberOfFeatures);
		List<FeatureFixingSet> featuresFixingSets = new LinkedList<FeatureFixingSet>();
		
		// Calculates the time it takes to compute the fixing sets
		long startTime = System.currentTimeMillis();
		
		// For each feature in the SPL compute its satisfying set
		for(Feature featureX : spl.getFeatures()) {
			//@debug
			System.out.print("Feature " + featureX.getName() + " ->");
			
			// Resets the start time clock
			startTime = System.currentTimeMillis();
			
			// Resets the commonality values for each set
			commonalityValues.clear();
			
			// Resets the fixing set for the new featureX
			fixingSet.clear();
		
			// Resets PicoSAT and sets up the SAT solver with the domain constraints PLf
			SCUtils.initializeDomainConstraintsSAT(picosat4J, spl.getDomainConstraints());
			
			// Resets the list of assumptions for the SAT and adds feature X to it
			// IMP = Plf ^ F ^ !Freqi
			assumingList.clear();
			assumingList.add(spl.getVariableFromFeature(featureX));
			
			// Adds a clause instead of assuming a value
			picosat4J.addClause(spl.getVariableFromFeature(featureX));
			
			// for each feature Y in the SPL , notice that if X=Y the inner loop is effectively skipped
			for (Feature featureY : spl.getFeatures()) {
				
				// Skips the matrix diagonal
				if (featureX.equals(featureY)) continue; 
				
				// Creates an index to look for in the matrix.
				// NOTE: that the id in the file is different from the ID in the SPL, that's why the extra search in the pwc map.
				index = new IndexMatrix(pwc.mapFeatureToId.get(featureX.getName()), pwc.mapFeatureToId.get(featureY.getName()));
				
				// The matrix index should always be there, if not there is a serious bug
				// PATCH: for now just traverse the set until it finds the index coordinates or its inverse, tested with equals
				for (IndexMatrix keyIndex : measurementMap.keySet()) {
					if (keyIndex.equals(index)) { index = keyIndex; break;}
					
				} // check in the key sets
				
				matrixEntry = measurementMap.get(index);
				if (matrixEntry !=null) {
					commonalityValues.add(new PairwiseCommonalityPair(spl.getVariableFromFeature(featureY), matrixEntry.getNumberOfCommonProducts(), spl));
				} else {
					System.out.println(" BIG ERROR: Commonality Value Not Found"); System.exit(0);
				} 
				
			} // for each feature Y
		
			// At this point we have computed the list of commonalityValues for a feature X
			// Sorts the elements by number of commonality value
			Collections.sort(commonalityValues, new PairwiseCommonalityPairComparator());
			
			// Computes the number of common features, those that have a number of products greater than 0.
			int commonFeatures = countCommonFeatures(commonalityValues); 
		
			// Prunes the common features out of the commonality pairs
			List<PairwiseCommonalityPair> pairsPruned = pruneFeatures(commonalityValues, spl);		
		
		
			// While there are still faulty feature combinations
			while (picosat4J.isSatisifable()) {
			
				// Obtains the set of variable assignments in the configuration
				int[] assignments = picosat4J.next();
			
				/*
				//@debug
				System.out.print("\nFaulty configuration [");
				for (int f : assignments)  {
					if (f<0) System.out.print("NOT ");
					System.out.print(spl.getFeatureFromVariable(Math.abs(f)).getName() + " ");
				}
				System.out.println("]");
				*/
				
				// Obtains a fixing feature
				fixingFeature = selectFeature(pairsPruned, assignments, assumingList, spl);
			
				//@debug
				// System.out.println("Fixing feature " + spl.getFeatureFromVariable(fixingFeature).getName());
			
				
				// if a fixing feature was found then add it to the list of assumed variables and update the SAT
				if (existsFixingSet(fixingFeature)) {
					// Adds to the fixing set
					fixingSet.add(fixingFeature);
				
					// Adds the fixing feature to the set of assumptions but negated if not already assumed
					if (!assumingList.contains(LogicTransformation.NOT(fixingFeature))) {
						assumingList.add(LogicTransformation.NOT(fixingFeature));
						picosat4J.addClause(LogicTransformation.NOT(fixingFeature));
	
					}
				} 
				//else {
					//@debug
					// System.out.print("*");
				//}
				
			} //while there are faulty combinations 
		
			long endTime = System.currentTimeMillis();
			boolean isFixing = isFixingSetCorrect(spl.getVariableFromFeature(featureX), fixingSet, picosat4J, spl);
			System.out.print(" " + isFixing + " [" ); 
			for (int feature : fixingSet) System.out.print(spl.getFeatureFromVariable(feature).getName() + " ");
			System.out.println("]");
			
			/*
			System.out.print("Assumed ");
			for (int assumedFeature : assumingList)  System.out.print(spl.getFeatureFromVariable(Math.abs(assumedFeature)).getName() + " ");
			System.out.println("]");
			 */
			
			// **** Creates the fixing set for the feature X and adds it to the list of features
			// Notice that because the fixingSet is cleared in every iteration, for the serialization I create a new hash set with the collection.
			// boolean isFixing = isFixingSetCorrect(spl.getVariableFromFeature(featureX), fixingSet, picosat4J, spl);
			
			FeatureFixingSet featureFixingSet = new FeatureFixingSet((splFixingSets.getMapFeatureToId()).get(featureX.getName()), 
					translatesFixingSet(fixingSet,spl,splFixingSets), endTime-startTime, isFixing, commonFeatures);
			featuresFixingSets.add(featureFixingSet);
			
			//@debug, is it adding the feature correctly???
			System.out.print((splFixingSets.getMapIdToFeature()).get(featureFixingSet.feature)+ ",[");
			for (int feature : featureFixingSet.getFixingSet()) {
				System.out.print(splFixingSets.getMapIdToFeature().get(feature) + " ");
			}
			System.out.println("]," + featureFixingSet.getIsFixing() + "," + featureFixingSet.getElapsedTime() + "," + featureFixingSet.getNumberCommonFeatures()+  "\n");
			
			
		} // for each feature X	
		
		// *********************************************
		// Serializes the fixing sets of the SPL
		
		// Sets the list of feature fixing sets into the SPLFixing set before serializing it
		splFixingSets.setFeatureFixingSets(featuresFixingSets);
		
		// Computes the statistics of the product line
		splFixingSets.computeStatistics();
		
		//@debug
		 System.out.println(splFixingSets);
		
		try
	      {
	         // FileOutputStream fileOut = new FileOutputStream(targetFile.substring(0, targetFile.lastIndexOf(".")) + fileExtension);
			FileOutputStream fileOut = new FileOutputStream(targetFile + fileExtension);
	         ObjectOutputStream out = new ObjectOutputStream(fileOut);
	         out.writeObject(splFixingSets); 
	         out.close();
	         fileOut.close();
	      }catch(IOException i) {
	          i.printStackTrace();
	      }
		
	    //@debug
	    System.out.println("\nSerialization SPL " + spl.getSPLName() + " done!");
	   
	    
	} // of computeFixingSets
	
	
	/**
	 * Patch for now, it has to do with the double translation between PWC pairs and SPL that do not use the same name convention
	 * for their features. So a double mapping has to be made, connected by the names of them.
	 */
	public Set<Integer> translatesFixingSet(Set<Integer> fixingSet, SPL spl, SPLFixingSets splFixingSets) {
		Set<Integer> result = new HashSet<Integer>();
		
		// Reads the set of fixing features, using ID from SPL file
		for (int feature : fixingSet) {
			String name = spl.getFeatureFromVariable(feature).getName();
			int newFeatureId = splFixingSets.getMapFeatureToId().get(name);
			result.add(newFeatureId);
		}
		
		return result;
	} // of translates method
	
	/**
	 * Selects a feature for the fixing set. This will be implemented differently in each policy.
	 * @param pairs Standard set of PWC pairs.
	 * @param assignments Value assignments in a configuration.
	 * @param assumingList List of variables currently assumed.
	 * @param spl Reference to the containing SPL.
	 * @return
	 */
	protected abstract int selectFeature( List<PairwiseCommonalityPair> pairs, int[] assignments, List<Integer> assumingList, SPL spl) ;
	



	/**
	 * This methods removes a set of features from the PWC pairs. Used to implement different selection policies.
	 * @param commonalityValues Original list of PWC pairs from which elements are removed.
	 * @param commonFeatures The list of features to be removed from consideration
	 * @return
	 */
	protected abstract List<PairwiseCommonalityPair> pruneFeatures(List<PairwiseCommonalityPair> commonalityValues, SPL spl);
	
	/**
	 * Checks if there is a solution to the fixing set
	 * @param fixingFeature. If the value is less than 0, it means that no fixing feature was found to 
	 * complete the fixing set.
	 * @return
	 */
	protected boolean existsFixingSet(int fixingFeature) {
		return (fixingFeature != SelectionPolicy.INCOMPATIBLE_FEATURE);
	} 
	
	/**
	 * Checks to see if a computed fixing set is indeed correct. 
	 * Checks if the number of products created with F + its fixing set is the same as
	 * @param candidateFixingSet
	 * @return
	 */
	public boolean isFixingSetCorrect(int requiringFeature, Set<Integer> candidateFixingSet, Picosat4J picosat4J, SPL spl ) {
		
		// Sets up the SAT solver with the domain constraints PLf
		SCUtils.initializeDomainConstraintsSAT(picosat4J, spl.getDomainConstraints());
	
		// Adds F as assumption SPLF ^ F 
		picosat4J.addClause(requiringFeature);
		
		// Adds fixing features as negated assumptions  !Freqi  resulting in SPLF ^ F  ^ !FReqi
		for (int feature : candidateFixingSet) {
			picosat4J.addClause(LogicTransformation.NOT(feature));
		}
		
		// Recall that it is fixing, if the safe composition expression is not satisfiable
		return !picosat4J.isSatisifable();
	}
	
	/**
	 * Checks whether a given feature is the root of the tree or not.
	 * @param feature
	 * @param spl
	 * @return
	 */
	protected boolean isFeatureRoot(int feature, SPL spl) {
		return (spl.getVariableFromFeature(spl.getRoot()) == feature);
	}
	

	/** 
	 * Checks if the value of feature is negated or not, which means is selected within a configuration value
	 * @param feature Feature that is searched for in the value assignment
	 * @param valueAssignments List of values returned by a SAT call
	 * @return
	 */
	protected boolean isFeatureSelected(int feature,  int[] valueAssignments) {
		for (int value : valueAssignments) {
			// if the feature corresponds to one of the value assignments
			if (feature == Math.abs(value)) {
				
				// if the value in the assignment is negated, meaning it has not been selected for this configuration
				if (LogicTransformation.isNegated(value)) return false;  
			}
		}
		return true;
	}
	
	/**
	 * This check is necessary because a feature can be assumed by a different (earlier) configuration
	 * @param feature Feature to look for.
	 * @param assummingFeatures Checks if it is already assumed or not
	 * @return
	 */
	protected boolean isFeatureAssumed(int feature,List<Integer> assummingFeatures) {
		return (assummingFeatures.contains(feature) ||  assummingFeatures.contains(LogicTransformation.NOT(feature)));
	}

	
	/**
	 * Traverses the list counting how many have number of products greater than zero.
	 * @param commonalityValues List of PWC pairs.
	 * @return The number of 
	 */
	public int countCommonFeatures(List<PairwiseCommonalityPair> commonalityValues) {
		int count=0;
		for (PairwiseCommonalityPair pair : commonalityValues) {
			if (pair.numProducts >0) count++;
		}
		return count;
	}
	
	
} // of SelectionPolicy

