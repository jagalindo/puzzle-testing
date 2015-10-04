/**
 * This class is the main class of the simulation
 * Creates the required instances of requiring and conflicting rules
 * Runs the simulations and gathers the statistics
 */
package at.jku.sea.mvsc.fixing.simulation;

import es.us.isa.FAMA.models.featureModel.GenericFeatureModel;
import at.jku.sea.mvsc.SPL;

public class Simulation {

	/**
	 * Creates a consistency rule instance for simulation purposes
	 * @return
	 */
	public SimConsistencyRuleInstance createRequiringInstance() {
		SimConsistencyRuleInstance cri = new SimConsistencyRuleInstance();
		
		return cri;
	} // of creating requiring instances
	
	
	/**
	 * Handles the simulation for a single SPL
	 * From the product line description of for Safe composition, and the fm for analysis using FAMA
	 */
	public void splSimulation(SPL spl, GenericFeatureModel fm) {
		
		
	} // single SPL simulation 
	
} // simulation

