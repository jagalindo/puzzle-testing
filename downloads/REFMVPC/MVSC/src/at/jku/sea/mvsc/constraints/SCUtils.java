/**
 * Contains utility functions for computing SC constraints.
 * @author Roberto E. Lopez-Herrejon SEA-JKU
 * @since March 11, 2010
 */

package at.jku.sea.mvsc.constraints;

import java.util.Collection;
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
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.util.EcoreUtil;
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
import org.eclipse.uml2.uml.PackageableElement;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.StateMachine;
import org.eclipse.uml2.uml.Region;
import org.eclipse.uml2.uml.Transition;

import at.jku.sea.mvsc.Feature;
import at.jku.sea.mvsc.LogicTransformation;
import at.jku.sea.mvsc.SPL;


import at.jku.sea.sat.implementation.picosat.Picosat4J;

import at.jku.sea.mvsc.constraints.IRuleInstance;
// import at.jku.sea.mvsc.constraints.SCStatechartActionInOwnerClassMethod.RuleInstance;


public class SCUtils {

	// ***********************************************************************
	// ***********************************************************************
	
	// ******************* General constants
	public static final String MESSAGE_UNDEFINED = "Undefined";
	public static final String MESSAGE_CONFLICT = "Conflict";
	
	
	// ***********************************************************************
	// ***********************************************************************
	
	// ******************* Data structure manipulation
	
	/**
	 * Generic method for filtering the elements of the diagrams
	 * @param <T> UML type of the filtered elements.
	 * @param list List of PackageableElements from which to filter those of type T.
	 * @param type EClassifier used to identify the objects in the list. e.g. UMLPackage.Literals.ASSOCIATION
	 * @return a List<T> with the elements that match the T UML type.
	 */
	public static <T> List<T> filterList(EList<PackageableElement> list, EClassifier type) {
		// Set<T> matchedList = new HashSet<T>();
		List<T> matchedList = new LinkedList<T>();
		Collection<Object> listObjects = EcoreUtil.getObjectsByType(list, type);
		for (Object obj : listObjects) { matchedList.add((T)obj); 	}
		return matchedList;
	}
	
	/**
	 * Searches a class in a list of Class objects.
	 * @param className The string name of the class.
	 * @param classes The list of Class objects.
	 * @return Null if the class is not in the list, otherwise the reference to the class
	 */
	public static Class findClass (String className, List<Class> classes) {
		
		for (Class searchClass : classes) {
			if (searchClass.getName().equals(className)) return searchClass; 
		}
		return null;
	}
	
	
	// Perhaps it can be useful to other constraints
	// Note: Not use in this class
	/** 
	 * Method that checks the name of a message inside a features class
	 * @param messageName The name of the message to find.
	 * @param receivingClass The name of the class that is the target.
	 * @param classes The list of classes where to look for.
	 * @return
	 */
	/*
	public boolean inFeature(String messageName, String receivingClass, List<Class> classes)  {
		
		Class featureClass = SCUtils.findClass (receivingClass, classes);
		
		// If the class does not contain the class looked
		if (featureClass == null) return false;
		
		// Obtains the class with the given name
		for (Operation operation : featureClass.getOperations()) {
			
			// If the name of the message and the operation are the same return
			if (operation.getName().equals(messageName)) return true; 
		}
		
		// Traverses the operations 
		
		return false;
		
	} // of inFeature
	*/

	
	// ***********************************************************************
	// ***********************************************************************
	
	
	// ******************* Requiring Rules
	// ******************* Propositional Logic Manipulation
	
	/* Requiring rule logic Impl is F => ( G v H v I)
	 * The process to add this constraints is described as follows
	 * Impl -- F => G v H v I
	 * We need to check satisfiability of
	 *  ! (Plf => Impl)    -- if satisfiable then there is at least one configuration that causes problems
	 *  Simplifying
	 *  ! ( !Plf v Impl)   -- de Morgan
	 *  Plf ^ !Impl
	 *  
	 *  Thus negating Impl, that is !Impl
	 *  ! (F => G v H v I)
	 *  ! ( !F v G v H v I ) - de Morgan
	 *  F ^ !G ^ !H ^!I  , adding the term feature
	 *  
	 *  Finally, the expression that goes into the SAT solver is
	 *  Plf ^ F ^ !G ^ !H ^ !I
	 *  
	 *  Algorithm
	 *  for each feature f
	 *    create an empty list of CNF clauses LCNF (list of Integers) 
	 *    create an empty list L of integers to represent it
	 *    L.add feature f translated to integer spl.getVariableFromFeature(f)
	 *    for each feature g in the conflict set of f
	 *      L.add negation of translation to integer of g
	 *    LNCF.add (L) adds the created CNF clause to the list of clauses
	 *    
	 */
	
	
	/**
	 * Performs the safe composition of a requiring rule
	 */
	public static <T> void checkSafeComposition(RequiringRule rule, List<IRuleInstance<T>> listRuleInstances, SPL spl) {
		
		// @debug
		// System.out.println("Requiring rule CNF") ;
			
		// **** Calling the SAT solver for detecting inconsistencies
		
		// Auxiliary variables
		long startTime, endTime;
		boolean answerValue;
		long elapsedTime;
		List<Boolean> answerValues;
		
		// Sets up the SAT solver instance
		Picosat4J picosat4J = Picosat4J.getInstance();
		
		// For all instance rules
		for (IRuleInstance<T> ruleInstance : listRuleInstances) {
			
			// if the rule instance does not require checking safe composition then skip
			if (!ruleInstance.needsSATEvaluation()) continue;
			
			// if there are no features in the requiring set then it is an error that must be signaled 
			if (ruleInstance.getTargetFeatures().size()==0) {
				ruleInstance.setError(true);
				ruleInstance.setErrorMessage(MESSAGE_UNDEFINED);
				
				// System.out.println("Error: message " + ruleInstance.transitionName + " undefined in SPL"); // refactored
				// System.out.println("Error: message " + ruleInstance.getId()+ " undefined in SPL");
				
				continue;
			}
			
			// computes CNFs
			computeCNFRequiringRule(ruleInstance, spl);
			
			//@debug
			// System.out.println(" executing sat " + ruleInstance.getId());
			
			// Initializes the SAT solver
			initializeSAT(picosat4J, spl.getDomainConstraints(), ruleInstance.getCNF());  
			
			// Calls the SAT solver and gets the time elapsed
			startTime = System.nanoTime(); // System.currentTimeMillis();  
			answerValue = picosat4J.isSatisifable();
			endTime = System.nanoTime(); // System.currentTimeMillis();
			elapsedTime = endTime - startTime;
			
			// @debug
			// System.out.println("Elapsed time : " + startTime + " - " + endTime + " = " + elapsedTime);
			
			// Sets the evaluation time
			ruleInstance.setEvaluationTime(elapsedTime);
			
			// Sets the evaluation result
			answerValues = new LinkedList<Boolean>();
			answerValues.add(answerValue);
			ruleInstance.setEvaluationValues(answerValues);
			
		} // of each rule instances
		
	} // check safe composition of requiring rules
	
	
	
	/**
	 * Computes the CNF propositional formulas for the requiring rule
	 * @param ruleInstance
	 * @param spl
	 */
	public static <T> void computeCNFRequiringRule(IRuleInstance<T> ruleInstance, SPL spl) {
		List<List<Integer>> cnf = new LinkedList<List<Integer>>();
		List<Integer> singleCNF;
		
		// CNF expression has the form  F ^ !G  ^ !H  ...
		//   where F is the source feature and G and H are the features that define them
		singleCNF = new LinkedList<Integer>();
		singleCNF.add(spl.getVariableFromFeature(ruleInstance.getSourceFeature()));
		cnf.add(singleCNF);
		
		// Add the defining features
		for (Feature feature : ruleInstance.getTargetFeatures()) {
			singleCNF = new LinkedList<Integer>();
			singleCNF.add(LogicTransformation.NOT(spl.getVariableFromFeature(feature)));
			cnf.add(singleCNF);
		}
		
		// Registers the CNF implementation back in the rule instance
		ruleInstance.setCNF(cnf);
		
	} // of computeCNF
	
	
	
	/**
	 * Translates into a string the propositional formula for the Impl part of an instance of a requiring rule 
	 * @param cnf
	 * @param spl
	 * @return
	 */
	public static String translatesRequiringImpl(Feature sourceFeature, Set<Feature> requiringFeatures) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(sourceFeature.getName() + " => ");
		int size = requiringFeatures.size();
		int counter = 0;
		for (Feature feature : requiringFeatures) {
			buffer.append(feature.getName());
			if (counter < size-1 ) buffer.append(" v ");
			counter++;
		}
		return buffer.toString();
	}
	
	
	// ***********************************************************************
	// ***********************************************************************

	// ******************* Conflicting Rules
	/* --- This is the correct for this rule Impl is F => !( G v H v I)
	 * The process to add this constraints is described as follows
	 * Impl -- F => ! (G v H v I)
	 * We need to check satisfiability of
	 *  ! (Plf => Impl)    -- if satisfiable then there is one configuration that causes problems
	 *  Simplifying
	 *  ! ( !Plf v Impl)   -- de Morgan
	 *  Plf ^ !Impl
	 *  
	 *  Thus negating Impl, that is !Impl
	 *  ! (F => !(G v H v I))
	 *  ! ( !F v !(G v H  v I) ) -- de Morgan
	 *  !!F ^ !!(G v H  v I) ) - double negation
	 *  F ^ (G v H  v I)  , distributivity
	 *  (F^G) v (F^H) v (F^I)  -- so it becomes a multiple call to the SAT solver
	 *  
	 *  Finally, the expression that goes into the SAT solver is
	 *  Plf ^ (F^G)
	 *  Plf ^ (F^H)
	 *  Plf ^ (F^I)
	 *  
	 *  Algorithm
	 *  create an empty list of CNF clauses LCNF (list of Integers) 
	 *  for each feature f  
	 *    for each feature g in the conflict set of f
	 *      LNCF.add(new L ( translate(f), translate(g)))
	 *      
	 *  spl.setConflictingCNF(LNCF at index of SCAssociationEndName constraint)
	 *      		   
	 *  For this case it is not necessary to add the commutative in the conflicting set, but the other form it does
	 */
	
	/**
	 * Performs the safe composition of a requiring rule
	 */
	public static <T> void checkSafeComposition(ConflictingRule rule, List<IRuleInstance<T>> listRuleInstances, SPL spl) {
		
		// @debug
		// System.out.println("Conflicting rule CNF") ;
			
		// **** Calling the SAT solver for detecting inconsistencies
		
		// Auxiliary variables
		long startTime, endTime;
		boolean answerValue;
		long elapsedTime = 0 ;
		List<Boolean> answerValues;
		
		// Sets up the SAT solver instance
		Picosat4J picosat4J = Picosat4J.getInstance();
		
		// For all instance rules
		for (IRuleInstance<T> ruleInstance : listRuleInstances) {
			
			// if the rule instance does not require checking safe composition then skip
			if (!ruleInstance.needsSATEvaluation()) continue;
			
			// if there are no features in the requiring set then it is an error that must be signaled 
			if (ruleInstance.getTargetFeatures().size() > 0) {
				ruleInstance.setError(true);
				ruleInstance.setErrorMessage(MESSAGE_CONFLICT);
			}
			
			//@debug
			// System.out.println(" executing sat " + ruleInstance.getId());
			
			// Sets the evaluation result
			answerValues = new LinkedList<Boolean>();
			
			// Calls the SAT solver for each conflicting feature and records the consistency result
			for (Feature targetFeature : ruleInstance.getTargetFeatures()) {
			
				// Initializes the SAT solver
				initializeSAT(picosat4J, spl.getDomainConstraints(), computeCNFConflictingRule(ruleInstance.getSourceFeature(), targetFeature, spl));  
				
				// Calls the SAT solver and gets the time elapsed
				startTime = System.nanoTime(); // System.currentTimeMillis();
				answerValue = picosat4J.isSatisifable();
				endTime = System.nanoTime();
				elapsedTime += (endTime - startTime);
				
				// adds the consistency answer to the values list
				answerValues.add(answerValue);
			}
			
			// Sets the evaluation time of all the instance evaluations
			ruleInstance.setEvaluationTime(elapsedTime);
			
			// Sets the evaluation values
			ruleInstance.setEvaluationValues(answerValues);
			
		} // of each rule instances
		
	} // check safe composition of requiring rules

	
	
	
	/**
	 * Computes the CNF propositional formulas for the conflicting rule.
	 * From the IMP: F => !(G v H v I) it returns a list of CNFs with a single term like
	 *   F
	 *   H
	 *   Which is the pair of F and one of the conflicting features, it simply creates a single CNF for each of them
	 * @param sourceFeature F
	 * @param targetFeature H
	 * @param spl
	 */
	public static List<List<Integer>> computeCNFConflictingRule(Feature sourceFeature, Feature targetFeature, SPL spl) {
		List<List<Integer>> cnf = new LinkedList<List<Integer>>();
		
		// adds sourceFeature
		List<Integer> singleCNF = new LinkedList<Integer>();
		singleCNF.add(spl.getVariableFromFeature(sourceFeature));
		cnf.add(singleCNF);
		
		// adds targetFeature
		singleCNF = new LinkedList<Integer>();
		singleCNF.add(spl.getVariableFromFeature(targetFeature));
		cnf.add(singleCNF);
	
		
		// Registers the CNF implementation back in the rule instance
		 return cnf;
		
	} // of computeCNF
	
	
	// ******************* Propositional Logic Manipulation
	
	
	/**
	 * Translates into a string the propositional formula for the Impl part of an instance of a conflicting rule 
	 * @param cnf
	 * @param spl
	 * @return
	 */
	public static String translatesConflictingImpl(Feature sourceFeature, Set<Feature> conflictingFeatures) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(sourceFeature.getName() + " => ");
		int size = conflictingFeatures.size();
		if (size > 0) buffer.append("!(");
		int counter = 0;
		for (Feature feature : conflictingFeatures) {
			buffer.append(feature.getName());
			if (counter < size-1 ) buffer.append(" v ");
			counter++;
		}
		if (size > 0) buffer.append(")");
		return buffer.toString();
	}
	
	
	
	
	// ***********************************************************************
	// ***********************************************************************

	// ******************* SAT Solver routines ************************
	
	/**
	 * Initializes the SAT solver with the SPL domain constraints and the rule added CNFs
	 * @param picosat4J The reference to the SAT solver.
	 * @param domainConstraints The list of domain constraints of the SPL
	 * @param ruleInstanceCNF The constraints of the rule instance
	 */
	public static void initializeSAT(Picosat4J picosat4J, List<List<Integer>> domainConstraints, List<List<Integer>> ruleInstanceCNF) {
		
		initializeDomainConstraintsSAT(picosat4J, domainConstraints);
		
		//@refactored, to initialization of only domain constraints
		/*
		// First adds the domain constraints
		for (List<Integer> cnf : domainConstraints) {
			Integer[] arrayCNF = cnf.toArray(new Integer[0]);
			int[] arrayCNFInt = new int[arrayCNF.length];
			for (int i=0; i< arrayCNF.length; i++) arrayCNFInt[i] = arrayCNF[i].intValue();
			picosat4J.addClause(arrayCNFInt);
		}
		*/
		
		// Adds the constraints of the rule instance
		for (List<Integer> cnf : ruleInstanceCNF) {
			Integer[] arrayCNF = cnf.toArray(new Integer[0]);
			int[] arrayCNFInt = new int[arrayCNF.length];
			for (int i=0; i< arrayCNF.length; i++) arrayCNFInt[i] = arrayCNF[i].intValue();
			picosat4J.addClause(arrayCNFInt);
		}
		
	} // of initialize SAT
	/**
	 * Initializes the SAT solver with the SPL domain constraints
	 * @param picosat4J The reference to the SAT solver.
	 * @param domainConstraints the list of domain constraints of the SPL
	 */
	public static void initializeDomainConstraintsSAT(Picosat4J picosat4J, List<List<Integer>> domainConstraints) {
		// Clears any existing clauses
		picosat4J.clearClauses();
		
		// First adds the domain constraints
		for (List<Integer> cnf : domainConstraints) {
			Integer[] arrayCNF = cnf.toArray(new Integer[0]);
			int[] arrayCNFInt = new int[arrayCNF.length];
			for (int i=0; i< arrayCNF.length; i++) arrayCNFInt[i] = arrayCNF[i].intValue();
			picosat4J.addClause(arrayCNFInt);
		}
	} // initializes the SAT with only 
	
} // of SCUtils
