/**
* Excludes
* Implements a require constraint of a FODA feature model
* @author Roberto E. Lopez-Herrejon
*/

package at.jku.sea.mvsc;

import java.util.LinkedList;
import java.util.List;

/**
* Mapping of excludes class to propositional logic
* Let F1 and F2 be to features
*  F1 => !F2 , in CNF is equivalent
*  (!F1 v !F2)  
*/
public class Excludes implements FODANode {
	private List<List<Integer>> listVariables = new LinkedList<List<Integer>>();
	private int f1Index, f2Index; 
	private SPL spl;
	
	/**
	 * Constructor of a mandatory feature
	 * @param parent Contains a reference to the parent feature
	 * @param child  Contains a reference to the child feature
	 * @param spl  Contains a reference of the SPL where the two features belong to
	 */
	public Excludes(Feature f1, Feature f2, SPL spl) {
		// Obtains the indices of the variables
		f1Index = spl.getVariableFromFeature(f1);
		f2Index = spl.getVariableFromFeature(f2);
		this.spl = spl;
	}
	
	/**
	 * Constructor based on names 
	 * @param parentName String value of the parent in the mandatory constraint
	 * @param childName String value fo the child in the mandatory constraint
	 * @param spl SPL reference to the product line the feature belongs to
	 */
	public Excludes (String f1, String f2, SPL spl) {
		f1Index = spl.getVariableFromFeature(spl.findFeature(f1));
		f2Index = spl.getVariableFromFeature(spl.findFeature(f2));
		this.spl = spl;
	}
	
	
	/**
	 * Converts the node to a CNF propositional formula
	 */
	public List<List<Integer>> convertPropositionalFormula() {
		
		// First clause (!f1 v !f2)
		List<Integer> firstClause = new LinkedList<Integer>();
		firstClause.add(LogicTransformation.NOT(f1Index));
		firstClause.add(LogicTransformation.NOT(f2Index));
		
		// Adding the two classes as a CNF formula
		listVariables.add(firstClause);
		
		return listVariables;
	}
	
	
	/**
	 * Returns the names of the excluding features
	 */
	public String toStringNodes() { 
		return (spl.mapVariableToFeature.get(f1Index)).getName() + " excludes " + (spl.mapVariableToFeature.get(f2Index)).getName(); 
	}
	
	/**
	 * Returns the formula translated to feature names.
	 * Returns only one because this constraint adds only one clause
	 */
	public String toStringFormula() {
		return spl.translateConstraint(listVariables.get(0));
	}
	
	
}
