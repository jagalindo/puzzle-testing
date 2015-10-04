/**
 * Mandatory
 * Implements a mandatory class of a FODA feature model
 * @author Roberto E. Lopez-Herrejon
 */
package at.jku.sea.mvsc;

import java.util.*;

/**
 * Mapping of mandatory class to propositional logic
 * Let P be parent feature, C be child feature
 *  P <=> C, in CNF is equivalent
 *  (!P v C)  ^ (P v !C)  
 */
public class Mandatory implements FODANode {

	private List<List<Integer>> listVariables = new LinkedList<List<Integer>>();
	private int parentIndex, childIndex; 
	private SPL spl;
	
	/**
	 * Constructor of a mandatory feature
	 * @param parent Contains a reference to the parent feature
	 * @param child  Contains a reference to the child feature
	 * @param spl  Contains a reference of the SPL where the two features belong to
	 */
	public Mandatory(Feature parent, Feature child, SPL spl) {
		// Obtains the indices of the variables
		parentIndex = spl.getVariableFromFeature(parent);
		childIndex = spl.getVariableFromFeature(child);
		this.spl = spl;
	}
	
	/**
	 * Constructor based on names that validates receiving booleans as parents
	 * @param parentName String value of the parent in the mandatory constraint
	 * @param childName String value fo the child in the mandatory constraint
	 * @param spl SPL reference to the product line the feature belongs to
	 */
	public Mandatory (String parentName, String childName, SPL spl) {
		// If the parent name is TRUE then it is a special case
		// setting variable to true (as in the root)
		if (parentName.equals(LogicTransformation.TRUE)) { 
			parentIndex = SPL.TRUE_INDEX;
		}
		else {
			parentIndex = spl.getVariableFromFeature(spl.findFeature(parentName));
		}
		
		// If
		childIndex = spl.getVariableFromFeature(spl.findFeature(childName));
		this.spl = spl;
	}
	
	
	/**
	 * Converts the node to a CNF propositional formula
	 */
	public List<List<Integer>> convertPropositionalFormula() {
		
		// In case the parent index is a true feature the only class return is the term itself
		if (parentIndex == SPL.TRUE_INDEX) {
			List<Integer> firstClause = new LinkedList<Integer>();
			firstClause.add(childIndex);
			listVariables.add(firstClause);
			return listVariables;
		}
		
		// First clause (!P v C)
		List<Integer> firstClause = new LinkedList<Integer>();
		firstClause.add(LogicTransformation.NOT(parentIndex));
		firstClause.add(childIndex);
		
		// Second clause (P v !C) 
		List<Integer> secondClause = new LinkedList<Integer>();
		secondClause.add(parentIndex);
		secondClause.add(LogicTransformation.NOT(childIndex));
		
		// Adding the two classes as a CNF formula
		listVariables.add(firstClause);
		listVariables.add(secondClause);
		
		return listVariables;
	}
	
	/**
	 * Returns the names of the excluding features
	 */
	public String toStringNodes() { 
		return (spl.mapVariableToFeature.get(parentIndex)).getName() + " mandatory " + (spl.mapVariableToFeature.get(childIndex)).getName(); 
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
