/**
 * Optional
 * Implements a mandatory class of a FODA feature model
 * @author Roberto E. Lopez-Herrejon
 */

package at.jku.sea.mvsc;

import java.util.LinkedList;
import java.util.List;

/**
 * Mapping of optional class to propositional logic
 * Let P be parent feature, C be child feature
 *  C => P, in CNF is equivalent
 *  (!C v P)  
 */
public class Optional implements FODANode {
	private List<List<Integer>> listVariables = new LinkedList<List<Integer>>();
	private int parentIndex, childIndex; 
	private SPL spl;
	
	/**
	 * Constructor of a optional feature
	 * @param parent Contains a reference to the parent feature
	 * @param child  Contains a reference to the child feature
	 * @param spl  Contains a reference of the SPL where the two features belong to
	 */
	public Optional(Feature parent, Feature child, SPL spl) {
		// Obtains the indices of the variables
		parentIndex = spl.getVariableFromFeature(parent);
		childIndex = spl.getVariableFromFeature(child);
		this.spl = spl;
	}
	
	/**
	 * Constructor based on names of features
	 * @param parentName String with parent name
	 * @param childName String with child name
	 * @param spl Reference SPL
	 */
	public Optional (String parentName, String childName, SPL spl) {
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
		return (spl.mapVariableToFeature.get(parentIndex)).getName() + " optional " + (spl.mapVariableToFeature.get(childIndex)).getName(); 
	}
	
	/**
	 * Returns the formulas translated to feature names.
	 */
	public String toStringFormula() {
		StringBuffer string = new StringBuffer();

		for (List<Integer> clause : listVariables) 
			string.append(spl.translateConstraint(clause)+"\n");
		
		return string.toString();
		
	} // toStringFormula
	
}
