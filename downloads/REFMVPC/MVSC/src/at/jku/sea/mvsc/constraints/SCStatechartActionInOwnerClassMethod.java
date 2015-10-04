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

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.*;

import at.jku.sea.mvsc.Feature;
import at.jku.sea.mvsc.LogicTransformation;
import at.jku.sea.mvsc.SPL;


import at.jku.sea.sat.implementation.picosat.Picosat4J;

import at.jku.sea.mvsc.constraints.IRuleInstance;
import at.jku.sea.mvsc.gui.*;


public class SCStatechartActionInOwnerClassMethod extends SCConstraintDefinition implements RequiringRule {

	// Class variables
	private static final String nameRule = "Rule 6. State machine action must be defined as an operation in owner class";
	
	
	@Override
	public Map<Feature, SortedSet<Feature>> apply(SPL spl) {
		
		// Obtains the list of features of the SPL where this rule will be applied
		List<Feature> listFeatures = spl.getFeatures();
		
		
		// ***********************************************************************
		// ***********************************************************************
		
		// ******************* Gathers the information from the features	
		
		// **************** Creates necessary data structures
		StateMachine stateMachine;
		List<Class> classes;
		List<TransitionData> transitions;
		
		// Maps features to the classes that they define
		Map<Feature, List<Class>> mapFeatureToClasses= new HashMap<Feature, List<Class>>();
		
		// Maps features to the transition data that they contain
		Map<Feature, List<TransitionData>> mapFeatureToTransitionData = new HashMap<Feature, List<TransitionData>>();
		
		// Reads the messages of all features
		for (Feature feature : listFeatures) {
			
			// @debug 
			//System.out.println("\n\transition of feature " + feature.getName());
			
			// if the package diagram does not exists, add the entries with no objects and skip
			if (feature.getClassDiagram()==null) {
				transitions = new LinkedList<TransitionData>();
				mapFeatureToTransitionData.put(feature, transitions);
				
				classes = new LinkedList<Class>();
				 
				// Adds the collected classes to the feature
				mapFeatureToClasses.put(feature, classes);
			
				continue; 
			}
			
			// Gets the classes of a feature
			classes = SCUtils.filterList(feature.getClassDiagram().getPackagedElements(), UMLPackage.Literals.CLASS);
			
			// The list of associations will be sorted by name of association
			Collections.sort(classes, new ClassComparator()); 
		
			// maps a feature to its classes
			mapFeatureToClasses.put(feature, classes);
		
			// Resets the list of transitions for the feature
			transitions = new LinkedList<TransitionData>();
			
			// Gets the state machines out of the classes
			for (Class klass : classes) {
				for (Behavior behavior : klass.getOwnedBehaviors()) {
					
					// @debug
					//System.out.println("Behavior name " + behavior.getName());
					
					// if the owned behavior is a state machine record its information
					if (behavior instanceof StateMachine) {
						
						//@debug
						//System.out.println("It is state machine");
						
						stateMachine = (StateMachine) behavior;
						
						// Traverses all regions in the state machine to get their transitions
						for (Region region : stateMachine.getRegions()) {
							for (Transition transition : region.getTransitions()) {
								transitions.add(new TransitionData(transition.getName(), stateMachine.getName(),
												  				   klass, region.getName()));
								
								//@debug
								//System.out.println("transition " + transition.getName());
								
							} // for all transitions
						} // for all regions
					} // of state machine
				} // for all behaviors in the class
			} // for all classes
			
			// The list of associations will be sorted by name of association
			Collections.sort(transitions, new TransitionDataComparator()); 
			
			// maps the transition data to the features
			mapFeatureToTransitionData.put(feature, transitions);
			
		} // for all features
		
		// @debug
		/*
		System.out.println("Printing collected feature data");
		for (Feature feature : listFeatures) {
			transitions = mapFeatureToTransitionData.get(feature);
			System.out.println("Feature "  + feature.getName());
			for (TransitionData transitionData :  transitions) {
				System.out.println(transitionData);
			}
		} // of all features
		*/

		
		// ***********************************************************************
		// ***********************************************************************
		
		// ******************* Performs the Safe Composition Checking
		/* Algorithm to follow
		 1. For each feature f
		  2. for each transition t in f
		     3. if t is an operation of its containing class 
		         done --- create instance rule but do not evaluate
		     4. otherwise, if t is not an operation in containing class
		         create a new rule instance <transition_name, source feature, class_name, requiringFeatures>
		        5. for each compatible feature g
		          6. if transition is operation of a class in g
		            7. add the feature to requiringFeatures of rule instance  
		                  
		*/
		
		// Map of a feature to the features it is compatible with
		Map<Feature, SortedSet<Feature>> compatibleFeatures = spl.getCompatibleFeatures();
	
		
		// List with the rule instances detected
		List<IRuleInstance<TransitionData>> listRuleInstances = new LinkedList<IRuleInstance<TransitionData>>();
		
		//  List of the features that re required by an instance of a rule
		Set<Feature> requiringFeatures = new TreeSet<Feature>();
		
		//@debug
		//System.out.println("\n\n Detecting inconsistencies ");
		
		// 1. For each feature
		for (Feature f : listFeatures) {
			
			//@debug
			//System.out.println("\nFeature " +  f.getName());
			
			// Get message data
			transitions = mapFeatureToTransitionData.get(f);
			
			// Writes the header of the feature being checked
			spl.writeAnalysisResult(f.getName());
			
			// 2. for each transition in f
			for (TransitionData transitionData : transitions) {
				
				// 3. if t is an operation of its containing class done ! --- create instance rule but do not evaluate
				if (isTransitionInClass(transitionData.name, transitionData.klass)) {
					// create instance rule that is not evaluated evaluate
					listRuleInstances.add(new RuleInstance(transitionData.name, f, new TreeSet<Feature>(), false, transitionData));	
					
					//@debug
					//System.out.println("Transition " + transitionData.name + " already defined in class");
					
					continue;
				}
				
				// 4. otherwise, if t is not an operation in containing class
		        //     create a new rule instance <transition_name, source feature, class_name, requiringFeatures>
				// creates a blank copy for the required features of this message
				requiringFeatures = new TreeSet<Feature>();
				
				// 5. for each compatible feature g
				for (Feature g : compatibleFeatures.get(f)) {
					
					// 6. if transition is operation of a class in g		
					if (isTransitionInClass (transitionData.name, SCUtils.findClass (transitionData.klass.getName(), mapFeatureToClasses.get(g)))) {
						// add the feature to requiringFeatures of rule instance  
						requiringFeatures.add(g);
						
						//@debug
						//System.out.println("\t defined in feature " + g.getName());
					}					
				} // of compatible features	
				
				// create a new rule instance <message_name, source feature, source class, target class, requiringFeatures>
				listRuleInstances.add(new RuleInstance(transitionData.name, f, requiringFeatures, true, transitionData));	
				
			} // for each transition data		
		} // for each feature f
		
		

		
		// ******************* Computes the propositional logic formulas		
		
		// Checks safe composition on each of the rule instances
		SCUtils.checkSafeComposition(this, listRuleInstances, spl);
	
		// Displays the result in a table
		String[] columnNames = {"Transition", "QName", "SC", "Error", "Imp", "Consistent", "msec", "Remarks" };
		int[] columnWidths = { 100, 300, 100, 100, 100, 100, 100, 100};
		int[] columnAlignments = { SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.LEFT,SWT.LEFT, SWT.LEFT};
		
		RuleInstanceTableViewer<TransitionData> tableViewer =  
			new RuleInstanceTableViewer<TransitionData>("Rule 6 for product line " + spl.getSPLName(), columnNames, columnWidths, 
													 columnAlignments, new RuleLabelProvider(),
													 listRuleInstances);
		tableViewer.open();

		
		// @debug
		System.out.println("Rule 6 instances " + listRuleInstances.size());
		
		// TODO Auto-generated method stub
		return null;
		
	} // of apply
	

	
	// *****************************************************************************************
	// ********************* Auxiliary functions
	
	/** Checks if a transition is one of the operations of its containing class.
	 * Pending: it only checks against its name and not its entire signature.
	 */
	public boolean isTransitionInClass(String transitionName, Class transitionClass) {
		
		// checks if the transition class is not empty. E.g. a feature that does not define that class
		if (transitionClass == null) return false;
		
		// Checks the operations looking for a name
		for (Operation operation : transitionClass.getOperations()) {
			if (operation.getName().equals(transitionName)) return true;
		}
		return false;
	}
	
	
	// *****************************************************************************************
	// ********************* Auxiliary nested classes
	
	/** Private class for main transition data to compare */
	class TransitionData {
		String name; 							// name of the message
		String stateMachineName;
		Class klass;
		String regionName;
		
		public TransitionData(String name, String stateMachineName, Class klass, String regionName) {
			this.name = name;
			this.stateMachineName = stateMachineName;
			this.klass = klass;
			this.regionName = regionName;
		}
		
		/* Empty constructor for variable initialization  */
		public TransitionData() {
			this.name = new String();
			this.stateMachineName = new String();
			this.regionName = new String();
	
		}
		
		/**
		 * Overrides default toString
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			String result = new String();
			result = "< "+ name + " " + stateMachineName + " " + klass.getName() + " " + regionName + ">";
			return result;
		}

		/**
		 * Computes the comma separated string for cvs output 
		 * @return
		 */
		public String commaSeparated () {
			String result = new String();
			result = "< "+ name + "," + stateMachineName + "," + klass.getName() + "," + regionName + ">";
			return result;
		}
		
	} // of class MessageData
	
	
	/**
	 * Class that compares the message data 
	 * @author Roberto
	 *
	 */
	class TransitionDataComparator implements Comparator<TransitionData> {
		
		/** Constants for association comparison */
		public static final int MESSAGE_EQUAL = 0;
		public static final int MESSAGE_LESSTHAN = -1;
		public static final int MESSAGE_GREATERTHAN = 1;
		
		/**
		 * Compares the string values of the data using compareTo
		 * @param s1 First string.
		 * @param s2 Second string.
		 */
		public int compareStrings(String s1, String s2) {
			int result =	MESSAGE_EQUAL;
		// First compares by names
			if (s1!=null) {
				if (s2!=null) {
					// both not null so they can be compared
					result = s1.compareTo(s2);
					if (result!=MESSAGE_EQUAL) return result;
				} else
				{ // e1 is not null but e2 is null
					return MESSAGE_LESSTHAN;
				}
			}else {
				// e1.name is null
				return MESSAGE_GREATERTHAN;
			}
			
			return result;
			
		} // compareStrings
		
		/**
		 * Method from interface Comparator 
		 * Orders them according to the names, class names, and state machine names
		 */
		public int compare (TransitionData e1, TransitionData e2) {
			
			// First compare the names of the messages
			int result = compareStrings(e1.name, e2.name);
			if (result != MESSAGE_EQUAL) return result;
			
			// Second compare the collaboration name
			result = compareStrings(e1.klass.getName(), e2.klass.getName());
			if (result != MESSAGE_EQUAL) return result;
			
			// Third compare the behavior name
			return compareStrings(e1.stateMachineName, e2.stateMachineName);
			
		} // compare
		
	} // of AssociationDataComparator
	

	

	/**
	 * This class contains the information for instances of this rule
	 * @author Roberto
	 *
	 */
	class RuleInstance implements IRuleInstance <TransitionData>{
		String transitionName;
		Feature sourceFeature;
		Set<Feature> requiringFeatures;
		List<List<Integer>> CNF;
		boolean needsSAT;
		List<Boolean> evaluationValues;
		long evaluationTime;
		boolean hasError;
		String errorMessage;
		TransitionData transitionData;
		
		public RuleInstance(String transitionName, Feature sourceFeature, Set<Feature> requiringFeatures, 
							boolean needsSAT, TransitionData transitionData) {
			this.transitionName = transitionName;
			this.sourceFeature = sourceFeature;
			this.requiringFeatures = requiringFeatures;
			this.needsSAT = needsSAT;
			this.transitionData = transitionData;
			CNF = new LinkedList<List<Integer>>();
			evaluationValues = new LinkedList<Boolean>();
			evaluationTime = 0;
			hasError = false;
			errorMessage = new String();
		}
		
		// interface get methods
		public String getId() { return transitionName;  }
		public List<List<Integer>> getCNF() { return CNF; }
		public boolean needsSATEvaluation() { return needsSAT; }
		public Set<Feature> getTargetFeatures() { return requiringFeatures; }
		public List<Boolean> getEvaluationValues() { return evaluationValues; }
		public long getEvaluationTime() { return evaluationTime; }
		public boolean hasError() { return hasError; }
		public String getErrorMesssage() { return errorMessage; }
		public Feature getSourceFeature() {  return sourceFeature; }
		public TransitionData getRuleInstanceDataObject() { return transitionData; }
		
		// Used in the table label provider
		public String getViewerColumnValue(int index) {
			switch (index) {
			case 0 : // transition name
				return transitionName;
			case 1 : // qualified name
				return sourceFeature.getName() + "." + transitionData.stateMachineName + "." + transitionData.regionName;
			case 2 : // requires safe composition
				return Boolean.toString(needsSAT);
			case 3 : // has error
				return Boolean.toString(hasError);
			case 4 : // logical proposition
				if (!needsSAT || hasError) { // if does not need SAT or is an error return empty 
					return "";
				}
				return SCUtils.translatesRequiringImpl(sourceFeature, requiringFeatures);
			case 5 : // is consistent (if satisfiable means is inconsistent, that is why it is negated)
				if (!needsSAT || hasError) { // if does not need SAT or is an error return empty 
					return "";
				}
				return Boolean.toString(!evaluationValues.get(0)); // 
			case 6 : // eval time
				if (!needsSAT || hasError) { // if does not need SAT or is an error return empty 
					return "";
				}
				return Long.toString(evaluationTime);
			case 7 : // remarks
				return errorMessage;
			default :
				return "unkown " + index;
			}
		} // getViewrColumnValue
		
		// interface set methods
		public void setCNF(List<List<Integer>> CNF) { this.CNF = CNF; }
		public void setEvaluationValues(List<Boolean> evaluationValues) { this.evaluationValues = evaluationValues; }
		public void setEvaluationTime(long evaluationTime) { this.evaluationTime = evaluationTime; }
		public void setError(boolean error) { this.hasError = error; }
		public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
		
		// Get methods
		public String getTransitionName() { return transitionName; }
		
		/**
		 * Overrides the default method to display the information of the instances
		 */
		public String toString() {
			StringBuffer result = new StringBuffer();
			result.append("<" + transitionName + "," + sourceFeature.getName() + ", ");
			// result.append( sourceClass + " ," + targetClass + ", [");
			result.append( transitionData.klass.getName() + ", [");
			for (Feature feature : requiringFeatures) {
				result.append(feature.getName() + " ");
			}
			result.append("] >");
			return result.toString();
		} // of toString
		
		
	} // of RuleInstance		
	
	
	
	/**
	 * This class contains the information for instances of this rule
	 * @author Roberto
	 *
	 */
	class RuleLabelProvider extends LabelProvider implements ITableLabelProvider {
		
		public Image getColumnImage(Object element, int index) { return null; }
		
		public String getColumnText(Object element, int index) {
			IRuleInstance<TransitionData> ruleInstance = (IRuleInstance<TransitionData>) element;
			return ruleInstance.getViewerColumnValue(index);
		}
		
	} // of Label provider
	
	
} // of main class
