/**
 * SCConstraintDefinition.java
 * Abstract class that defines the skeleton of Safe Composition Constraints
 */
package at.jku.sea.mvsc.constraints;

import java.util.*;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.PackageableElement;

import at.jku.sea.mvsc.constraints.IRuleInstance;

import at.jku.sea.mvsc.*;

public abstract class SCConstraintDefinition {
	
	/** Constants for indexing collected information from constraint evaluation */
	public final static int  NUMBER_CONSTRAINT_RULES = 8;  // 1 plus because in the paper we start with 1
	public final static int  ASSOCIATION_ENDNAME_UNIQUE = 3; 
	public final static int  ASSOCIATION_ATMOST_ONE_COMPOSITION_OR_AGGREGATION = 4; 
	
	
	// Refactored to SCUtils
	/**
	 * Generic method for filtering the elements of the diagrams
	 * @param <T> UML type of the filtered elements.
	 * @param list List of PackageableElements from which to filter those of type T.
	 * @param type EClassifier used to identify the objects in the list. e.g. UMLPackage.Literals.ASSOCIATION
	 * @return a List<T> with the elements that match the T UML type.
	 */
	public <T> List<T> filterList(EList<PackageableElement> list, EClassifier type) {
		// Set<T> matchedList = new HashSet<T>();
		List<T> matchedList = new LinkedList<T>();
		Collection<Object> listObjects = EcoreUtil.getObjectsByType(list, type);
		for (Object obj : listObjects) { matchedList.add((T)obj); 	}
		return matchedList;
	}
	
	
	
	public abstract Map<Feature, SortedSet<Feature>> apply (SPL spl);

	// public abstract Map<Feature, List<IRuleInstance>> applyRule(SPL spl);
	
}
