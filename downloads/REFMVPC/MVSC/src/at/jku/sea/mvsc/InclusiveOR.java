/**
 * InclusiveOr
 * Implements an inclusive or (at least one) group relation of a FODA model
 * @author Roberto E. Lopez-Herrejon
 */
package at.jku.sea.mvsc;

import java.util.LinkedList;
import java.util.List;

/**
 * Mapping of InclusiveOr class to propositional logic
 * Let P be parent feature, C1, C2, C3 be child feature
 *  P <=> C1 v C2 V C3, in CNF is equivalent
 *  C1 v C2 v C3 v !P
 *  !C1 v P
 *  !C2 v P
 *  !C3 v P
 */
public class InclusiveOR implements FODANode {
	private List<List<Integer>> listVariables = new LinkedList<List<Integer>>();
	private int parentIndex; 
	private List<Integer> childrenFeatures = new LinkedList<Integer>();
	private SPL spl;
	
	/**
	 * Constructor of a mandatory feature
	 * @param parent Contains a reference to the parent feature
	 * @param children  Contains a reference to the children features
	 * @param spl  Contains a reference of the SPL where the two features belong to
	 */
	public InclusiveOR (Feature parent, List<Feature> children, SPL spl) {
		// Obtains the indices of the variables
		parentIndex = spl.getVariableFromFeature(parent);
		for (Feature child : children) {
			childrenFeatures.add(spl.getVariableFromFeature(child));
		}
		this.spl = spl;
	}
	
	
	/**
	 * Constructor based on names 
	 * @param parentName String value of the parent in the mandatory constraint
	 * @param childNames List<String>value of the children in the mandatory constraint
	 * @param spl SPL reference to the product line the feature belongs to
	 */
	public InclusiveOR (String parentName, List<String> childrenNames, SPL spl) {
		parentIndex = spl.getVariableFromFeature(spl.findFeature(parentName));
		
		for (String name : childrenNames) {
			// @debug
			// System.out.println("child name " + name);
			// System.out.println("feature " + spl.findFeature(name).name);
			childrenFeatures.add(spl.getVariableFromFeature(spl.findFeature(name)));
		}
		this.spl = spl;
	}
	
	
	/**
	 * Converts the node to a CNF propositional formula
	 */
	public List<List<Integer>> convertPropositionalFormula() {
		
		// First clause (!P v C1 v C2 v C3)
		List<Integer> firstClause = new LinkedList<Integer>();
		firstClause.add(LogicTransformation.NOT(parentIndex));
		for(int childFeature : childrenFeatures) {
			firstClause.add(childFeature);	
		}
		listVariables.add(firstClause);
		
		// Remaining clauses !Child_i v P
		for(int childFeature : childrenFeatures) {
			List<Integer> newClause = new LinkedList<Integer>();
			newClause.add(LogicTransformation.NOT(childFeature));
			newClause.add(parentIndex);
			listVariables.add(newClause);
		}
		
		
		return listVariables;
	}
	
	/**
	 * Returns the names of the excluding features.
	 */
	public String toStringNodes() { 
		StringBuffer string = new StringBuffer();
		string.append((spl.mapVariableToFeature.get(parentIndex)).getName() + " inclusive-or ");
		for(int child : childrenFeatures) string.append(spl.mapVariableToFeature.get(child) + " ");
		return string.toString();
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
