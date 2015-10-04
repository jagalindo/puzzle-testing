/**
 * Constains a consistency rule instance for simulation purposes
 */
package at.jku.sea.mvsc.fixing.simulation;
import java.util.LinkedList;
import java.util.List;

import at.jku.sea.mvsc.Feature;
import at.jku.sea.mvsc.constraints.RuleType;

public class SimConsistencyRuleInstance {

		Feature sourceFeature;
		// @pending element of type EObject to hold the element(s) that causes the requirements
		// @pending There should be a list of pairs (Feature, element)
		List<Feature> targetFeature = new LinkedList<Feature>();
		int[] faultyConfiguration; 
		// @pending adjust the details 
		RuleType ruleDefinitionReference; 
		
} // simulation of consistency rule instance
