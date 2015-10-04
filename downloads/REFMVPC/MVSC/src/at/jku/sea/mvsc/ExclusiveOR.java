/**
 * ExclusiveOr
 * Implements an inclusive or (at least one) group relation of a FODA model
 * @author Roberto E. Lopez-Herrejon
 */
package at.jku.sea.mvsc;

import java.util.LinkedList;
import java.util.List;

/**
 * Mapping of InclusiveOr class to propositional logic
 * Let P be parent feature, C1, C2, C3 be child feature
 *  (F1 <=> (!F2 ^ ..^!Fn ^ P))^ 
 * (F2 <=> (!F1 ^!F3 ...^!Fn ^ P))^ ...
 * (Fn <=> (!F1 ^ ...^ !Fn-1 ^ P))
*  in CNF is equivalent
 *  !P v C1 v C2 v C3
 *  (!C1 v P)   (!C1 v !C2) (!C1 v !C3)
 *  (!C2 v P)  (!C2 v !C3)
 *  (!C3 v P) ...
 */
public class ExclusiveOR implements FODANode {
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
	public ExclusiveOR (Feature parent, List<Feature> children, SPL spl) {
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
	public ExclusiveOR (String parentName, List<String> childrenNames, SPL spl) {
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
		// Indices of the variables
		int nextPosition, lastPosition;
		
		// First clause (!P v C1 v C2 v C3)
		List<Integer> firstClause = new LinkedList<Integer>();
		firstClause.add(LogicTransformation.NOT(parentIndex));
		for(int childFeature : childrenFeatures) {
			firstClause.add(childFeature);	
		}
		listVariables.add(firstClause);
		
		// The last position to iterate over the list of feature variables
		lastPosition = childrenFeatures.size();
		
		// Remaining clauses (!Child_i v P) ^ (!Child_i v !Child_i+j)
		for(int childFeature : childrenFeatures) {
			List<Integer> newClause = new LinkedList<Integer>();
			newClause.add(LogicTransformation.NOT(childFeature));
			newClause.add(parentIndex);
			listVariables.add(newClause);
			
			// Adds the remaining combinations of child terms

			/*
			List<Integer> remainingChildren = childrenFeatures.subList(childrenFeatures.indexOf(childFeature), 
																		childrenFeatures.size()-1);
			*/
			nextPosition = childrenFeatures.indexOf(childFeature)+1;
			// @debug
			// System.out.println("sublist " + childrenFeatures.indexOf(childFeature) + " next " + nextPosition + " last " + lastPosition);
			
			// If there are no more clauses to add
			if (nextPosition > lastPosition) continue;
			
			List<Integer> remainingChildren = childrenFeatures.subList(nextPosition, lastPosition);
			// @debug
			// System.out.println("indices from " + nextPosition + " to " + lastPosition + " name " + childFeature);
			
			for (int childRemainingFeature : remainingChildren ) {
				List<Integer> newChildrenClause = new LinkedList<Integer>();
				newChildrenClause.add(LogicTransformation.NOT(childFeature));
				newChildrenClause.add(LogicTransformation.NOT(childRemainingFeature));
				listVariables.add(newChildrenClause);			
			}
			
		} // of all childrenFeatures
		
		
		return listVariables;
	}
	
	
	/**
	 * Returns the names of the excluding features.
	 */
	public String toStringNodes() { 
		StringBuffer string = new StringBuffer();
		string.append((spl.mapVariableToFeature.get(parentIndex)).getName() + " exclusive-or ");
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