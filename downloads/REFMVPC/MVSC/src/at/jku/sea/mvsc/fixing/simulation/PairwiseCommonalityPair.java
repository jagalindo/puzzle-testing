/**
 * Class that holds the name of a feature and its PWC value
 * It is used to compare teh
 */
package at.jku.sea.mvsc.fixing.simulation;

import at.jku.sea.mvsc.SPL;

public class PairwiseCommonalityPair{
	protected double numProducts;
	protected int featureID;
	protected SPL spl;
	
	public PairwiseCommonalityPair(int featureID, double numProducts, SPL spl) {
		this.featureID = featureID;
		this.numProducts = numProducts;
		this.spl = spl;
	} 

	public String toString() {
		String result = "[ " + spl.getFeatureFromVariable(featureID).getName() + "," + numProducts + "] ";
		return result;
	}
	
} // class
