/**
 * Requires
 * Implements a require constraint of a FODA feature model
 * @author Roberto E. Lopez-Herrejon
 */

package at.jku.sea.mvsc;

import java.util.LinkedList;
import java.util.List;

/**
 * Mapping of requires class to propositional logic
 * Let P be parent feature, C be child feature
 *  C => P, in CNF is equivalent
 *  (!C v P)  
 */
public class Requires implements FODANode {
	private List<List<Integer>> listVariables = new LinkedList<List<Integer>>();
	private int parentIndex, childIndex; 
	private SPL spl;
	
	/**
	 * Constructor of a mandatory feature
	 * @param parent Contains a reference to the parent feature
	 * @param child  Contains a reference to the child feature
	 * @param spl  Contains a reference of the SPL where the two features belong to
	 */
	public Requires(Feature parent, Feature child, SPL spl) {
		// Obtains the indices of the variables
		parentIndex = spl.getVariableFromFeature(parent);
		childIndex = spl.getVariableFromFeature(child);
		this.spl = spl;
	}
	
	/**
	 * Constructor based on names 
	 * @param parentName String value of the parent in the mandatory constraint
	 * @param childName String value fo the child in the mandatory constraint
	 * @param spl SPL reference to the product line the feature belongs to
	 */
	public Requires (String parentName, String childName, SPL spl) {
		parentIndex = spl.getVariableFromFeature(spl.findFeature(parentName));
		childIndex = spl.getVariableFromFeature(spl.findFeature(childName));
		this.spl = spl;
	}
	
	
	
	/**
	 * Converts the node to a CNF propositional formula
	 */
	public List<List<Integer>> convertPropositionalFormula() {
		
		// First clause (!C v P)
		List<Integer> firstClause = new LinkedList<Integer>();
		firstClause.add(LogicTransformation.NOT(childIndex));
		firstClause.add(parentIndex);
		
		// Adding the two classes as a CNF formula
		listVariables.add(firstClause);
		
		return listVariables;
	}
	
	
	/**
	 * Returns the names of the excluding features
	 */
	public String toStringNodes() { 
		return (spl.mapVariableToFeature.get(parentIndex)).getName() + " requires " + (spl.mapVariableToFeature.get(childIndex)).getName(); 
	}
	
	/**
	 * Returns the formula translated to feature names.
	 * Returns only one because this constraint adds only one clause.
	 */
	public String toStringFormula() {
		return spl.translateConstraint(listVariables.get(0));
	}
	
}
