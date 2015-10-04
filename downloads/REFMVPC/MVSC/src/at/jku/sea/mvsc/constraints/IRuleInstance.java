/**
 * Defines a common interface for all the Safe Composition rule instances
 * @author Roberto E. Lopez-Herrejon. JKU Linz.
 * 
 */
package at.jku.sea.mvsc.constraints;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Set;

// import org.eclipse.uml2.uml.AggregationKind;
// import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Message;
import org.eclipse.uml2.uml.UMLPackage;
// import org.eclipse.uml2.uml.ReceiveOperationEvent;
import org.eclipse.uml2.uml.Collaboration;
import org.eclipse.uml2.uml.Interaction;
import org.eclipse.uml2.uml.Behavior;
import org.eclipse.uml2.uml.Lifeline;
import org.eclipse.uml2.uml.MessageSort;
// import org.eclipse.uml2.uml.InteractionFragment;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.MessageEnd;
import org.eclipse.uml2.uml.MessageOccurrenceSpecification;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.StateMachine;
import org.eclipse.uml2.uml.Region;
import org.eclipse.uml2.uml.Transition;

import at.jku.sea.mvsc.Feature;
import at.jku.sea.mvsc.LogicTransformation;
import at.jku.sea.mvsc.SPL;

public interface IRuleInstance <T>{
	
	/**
	 * An identifier for the rule instance
	 * @return
	 */
	String getId(); 

	/**
	 * Gets the source feature in the Impl formula F => ...
	 * @return
	 */
	Feature getSourceFeature();
	
	/**
	 * Gets the list of features on the RHS of Impl formula
	 * @return The list of requiring or conflicting features
	 */
	Set<Feature> getTargetFeatures();
	
	
	/**
	 * Returns whether or not an evaluation using the SAT solver is required.
	 * SAT solver evaluation is required when: 
	 *  + requiring feature: when the requirement is not met by the same feature or no providing feature is available (error)
	 *  + conflicting feature: when there is at least one feature that conflicts with
	 * @return
	 */
	boolean needsSATEvaluation();
	
	/**
	 * Returns the time it takes to evaluate with the SAT solver the SPLf => IMPf expression
	 * Requiring rules: the time it takes for the one call made
	 * Conflicting rules: the time it takes for all the calls made
	 * @return
	 */
	long getEvaluationTime();
	
	
	/**
	 * Returns the boolean value of the SAT evaluation
	 * Requiring rules: have one value
	 * Conflicting rules: have one value for each evaluation
	 * @return
	 */
	List<Boolean> getEvaluationValues();
	

	/**
	 * Gets the CNF representation of this rule.
	 * Requiring rules: each CNF corresponds to a single individual CNF class used for a call
	 * Conflicting rules: pending???
	 * @return
	 */
	List<List<Integer>> getCNF();
	
	
	/**
	 * Returns whether or not the rule instance contains an error. E.g. cannot be fulfilled 
	 * @return
	 */
	boolean hasError();
	
	
	/**
	 * Gets the error message if any.
	 * @return
	 */
	String getErrorMesssage();

	
	/**
	 * Gets the object that holds the Data of each corresponding instance
	 * @return
	 */
	T getRuleInstanceDataObject();
	
	/**
	 * Gets or computes the string value that goes in index position in the viewer table
	 * @param index  Zero-based position of the column information
	 * @return
	 */
	String getViewerColumnValue(int index);
	
	// **********************************************************************************
	// *** Set methods
	
   /**
    * Sets the value of the CNF clauses
    * @param CNF
    */
	void setCNF(List<List<Integer>> CNF);
	
	
	/**
	 * Sets the evaluation values result of the SAT solver calls
	 * @param evaluationValues
	 */
	void setEvaluationValues(List<Boolean> evaluationValues);
	
	
	/**
	 * Sets the evaluation time that took the SAT solver
	 * @param evaluationTime
	 */
	void setEvaluationTime(long evaluationTime); 

	
	/**
	 * Sets the value of the error field in the rule instance
	 * @param error IF there is
	 */
	void setError(boolean error);
	
	
	/**
	 * Sets the error message resulting of the evaluation of the rule instance.
	 * @param message
	 */
	void setErrorMessage(String message);
}
