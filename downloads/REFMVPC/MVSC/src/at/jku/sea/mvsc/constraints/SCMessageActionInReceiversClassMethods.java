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
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
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

import at.jku.sea.mvsc.Feature;
import at.jku.sea.mvsc.LogicTransformation;
import at.jku.sea.mvsc.SPL;
import at.jku.sea.mvsc.constraints.SCMessageDirectionInClassAssociation.MessageData;
import at.jku.sea.mvsc.constraints.SCMessageDirectionInClassAssociation.RuleLabelProvider;
import at.jku.sea.mvsc.gui.RuleInstanceTableViewer;
import at.jku.sea.sat.implementation.picosat.Picosat4J;

public class SCMessageActionInReceiversClassMethods extends SCConstraintDefinition implements RequiringRule {

	// Class variables
	private static final String nameRule = "Rule 5. Message action must be defined as an operation in receiver's class";
	
	@Override
	public Map<Feature, SortedSet<Feature>> apply(SPL spl) {
			
		// Obtains the list of features of the SPL where this rule will be applied
		List<Feature> listFeatures = spl.getFeatures();
		
		
		// ***********************************************************************
		// ***********************************************************************
		
		// ******************* Gathers the information from the features
		
		// **************** Creates necessary data structures
		// List<Message> messages;		
		 // UMLPackage.Literals.RECEIVE_OPERATION_EVENT__OPERATION
		// List<ReceiveOperationEvent> receiveOperations;
		
		List<Collaboration> collaborations;
		List<Class> classes;
		// List<Interaction> interactions;
		Interaction interaction;
		MessageEnd messageReceiveEnd; 
		// Message newMessage;
		List<String> targetTypeNames = new LinkedList<String>();
		
		// Maps features to their corresponding messages data 
		Map<Feature, List<MessageData>> mapFeatureToMessagesData = new HashMap<Feature, List<MessageData>>();
		List<MessageData> messageDataListF; 
		
		// Maps features to the classes that they define
		Map<Feature, List<Class>> mapFeatureToClasses= new HashMap<Feature, List<Class>>();
		
		// Reads the messages of all features
		for (Feature feature : listFeatures) {
			
			// @debug 
			// System.out.println("\n\nMessage of feature " + feature.getName());
			
			// Creates a new object for message data for the new feature
			messageDataListF = new LinkedList<MessageData>();
			
			
			// if the package diagram does not exists, add the entries with no objects and skip
			if (feature.getClassDiagram()==null) {
				// Adds the collected messages to the feature
				mapFeatureToMessagesData.put(feature, messageDataListF);
				
				classes = new LinkedList<Class>();
				 
				// Adds the collected classes to the feature
				mapFeatureToClasses.put(feature, classes);
			
				continue; 
			}
			
			// Gets the classes that 
			classes = filterList(feature.getClassDiagram().getPackagedElements(), UMLPackage.Literals.CLASS);
			
			// Filters the collaborations from which to get the interactions with their messages
			collaborations = filterList(feature.getClassDiagram().getPackagedElements(), UMLPackage.Literals.COLLABORATION);
			
			for (Collaboration collaboration : collaborations) {
				
				// @debug
				// System.out.println(collaboration.getName() + " " );
				
				// 1. Get the behaviors of the collaborations ( supertypes of Interactions)
				for (Behavior behavior: collaboration.getOwnedBehaviors()) {
					
					// @debug
					// System.out.println("behavior -> " +  behavior.getName());
					
					// 2. If an interaction is found extract the messages
					if (behavior instanceof Interaction) {
						interaction = (Interaction) behavior;
										
						// 3. traverse the messages and obtain their target classes
						for (Message message : interaction.getMessages()) {

							// if the message is a reply, slip it as most certainly it wont be in the caller side
							if (message.getMessageSort()== MessageSort.REPLY_LITERAL) continue;
							
							// @debug
							// System.out.println("message " + message.getName() + " sort " + message.getMessageSort());
							
							messageReceiveEnd = message.getReceiveEvent();
							
							// If the message received is not the occurence of a message specification then get next message
							if(!(messageReceiveEnd instanceof MessageOccurrenceSpecification) || 
									((MessageOccurrenceSpecification)messageReceiveEnd).getCovereds() == null) 
								continue;
							
							// Otherwise extract the names of the target classes where to look for the message name
							targetTypeNames = new LinkedList<String>();
							
							// Gets the life lines associated to the a message received end
							for (Lifeline lifeline : ((MessageOccurrenceSpecification)messageReceiveEnd).getCovereds()) {
								// System.out.println("life line " + lifeline.getName());
								// System.out.println( " receiving class " + lifeline.getRepresents().getType().getName());
								targetTypeNames.add(lifeline.getRepresents().getType().getName());
							} // for all lifelines
							
							// Adds a new element of MessageData and adds it to the corresponding list 
							messageDataListF.add(new MessageData(message.getName(), targetTypeNames, 
												 collaboration.getName(), behavior.getName()));				
						} // for all messages in an interaction
					} // if it is an interaction
				} // of all behavior
			} // all collaborations
			
			
			// The list of associations will be sorted by name of association
			Collections.sort(messageDataListF, new MessageDataComparator()); 
			
			
			// Adds the collected messages to the feature
			mapFeatureToMessagesData.put(feature, messageDataListF);
			
			// The list of associations will be sorted by name of association
			Collections.sort(classes, new ClassComparator()); 
			 
			// Adds the collected classes to the feature
			mapFeatureToClasses.put(feature, classes);
			
		} // of all features
		
		
		
		/*
		// @debug
		System.out.println("Printing collected feature data");
		for (Feature feature : listFeatures) {
			messageDataListF = mapFeatureToMessagesData.get(feature);
			System.out.println("Feature "  + feature.getName());
			for (MessageData messageData :  messageDataListF) {
				System.out.println(messageData);
			}
			for (Class klass : mapFeatureToClasses.get(feature)) {
				System.out.println(klass.getName());
				for (Operation operation : klass.getOperations()) {
					System.out.println("\t" + operation.getName());
				}
			}
			
		} // of all features
		*/

		
		// ***********************************************************************
		// ***********************************************************************
		
		// ******************* Performs the Safe Composition Checking
		
		/* Algorithm to follow
		 1. For each feature f
		  2. get message of f
		  3. for each message in f
		     4. get the classes it targets
		     5. if message is defined in classes in f - skip
			 6. otherwise research for in compatible features of g
		        for each compatible feature g
			   	   if message is defined in classes of g
		        create <message_name, f, class_name, <Feature that define them>* >
		*/
		
		// Map of a feature to the features it is compatible with
		Map<Feature, SortedSet<Feature>> compatibleFeatures = spl.getCompatibleFeatures();
		List<String> receivingClasses;
		
		// List with the rule instances detected
		List<IRuleInstance<MessageData>> listRuleInstances = new LinkedList<IRuleInstance<MessageData>>();
		
		Set<Feature> requiringFeatures = new TreeSet<Feature>();
		
		//@debug
		//System.out.println("\n\n Detecting inconsistencies ");
		
		// 1. For each feature
		for (Feature f : listFeatures) {
			
			//@debug
			//System.out.println("\nFeature " +  f.getName());
			
			// 2. get message data
			messageDataListF = mapFeatureToMessagesData.get(f);
			
			// Writes the header of the feature being checked
			spl.writeAnalysisResult(f.getName());
			
			// 3. for each message in f
			for (MessageData messageData : messageDataListF) {
				
				// 4. gets the classes the message targets
				receivingClasses = messageData.receivingClasses;
				
				//@debug
				//System.out.println("message " + messageData.name + " " + receivingClasses.size());
				
				// 5. check that the message is in each targeted class
				for (String receivingClass : receivingClasses) {
					//@debug
					//System.out.println("receiving class " + receivingClass);
							
					// if message is in the classes of the feature, ok and continue
					// if message is not present in the feature classes, search in compatible features
					if (!inFeature(messageData.name, receivingClass, mapFeatureToClasses.get(f))) {
						
						// creates a blank copy for the required features of this message
						requiringFeatures = new TreeSet<Feature>();
						
						// 6. Search in compatible features 
						for (Feature g : compatibleFeatures.get(f)) {
							// if present in a compatible class
							if (inFeature(messageData.name, receivingClass, mapFeatureToClasses.get(g))) {
								requiringFeatures.add(g);
								
								//@debug
								//System.out.println("\t defined in feature " + g.getName() + " class " + receivingClass);
								
							}
						} // of compatible features	
						
						// Adds the features that define the message operation
						listRuleInstances.add(new RuleInstance(messageData.name, f, requiringFeatures, true, messageData, receivingClass));
						
					} else { // It is present in the feature
						
						// @debug
						// System.out.println("\t present class " + receivingClass);
						
						// Adds a rule instance that does not need SC evaluation
						listRuleInstances.add(new RuleInstance(messageData.name, f, requiringFeatures, false, messageData, receivingClass));
					}
									
				} // in the target class?
			} // for each message data		
		} // for each feature f
		
		
		// ******************* Computes the propositional logic formulas	
		
		// Checks safe composition on each of the rule instances
		SCUtils.checkSafeComposition(this, listRuleInstances, spl);
	
		// Displays the result in a table
		String[] columnNames = {"Message", "QName", "SC", "Error", "Imp", "Consistent", "msec", "Remarks" };
		int[] columnWidths = { 100, 300, 100, 100, 100, 100, 100, 100};
		int[] columnAlignments = { SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.LEFT,SWT.LEFT, SWT.LEFT};
		
		RuleInstanceTableViewer<MessageData> tableViewer =  
			new RuleInstanceTableViewer<MessageData>("Rule 5 for product line " + spl.getSPLName(), columnNames, columnWidths, 
													 columnAlignments, new RuleLabelProvider(),
													 listRuleInstances);
		tableViewer.open();
		
		// @debug
		System.out.println("Rule 5 instances " + listRuleInstances.size());
		
		return null;
			
	} // of apply

	
	/**
	 * Method that checks the name of a message inside a features class
	 * @param messageName The name of the message to find.
	 * @param receivingClass The name of the class that is the target.
	 * @param classes The list of classes where to look for.
	 * @return
	 */
	public boolean inFeature(String messageName, String receivingClass, List<Class> classes)  {
		
		Class featureClass = findClass (receivingClass, classes);
		
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
	
	/**
	 * Searches a class in a list of Class objects.
	 * @param className The string name of the class.
	 * @param classes The list of Class objects.
	 * @return Null if the class is not in the list, otherwise the reference to the class
	 */
	public Class findClass (String className, List<Class> classes) {
		
		for (Class searchClass : classes) {
			if (searchClass.getName().equals(className)) return searchClass; 
		}
		return null;
	}
	
	// *****************************************************************************************
	// ********************* Auxiliary nested classes
	
	/** Private class for main association data to compare
	 */
	class MessageData {
		String name;							// name of the message
		List<String> receivingClasses;			// list of classes that may receive them
		String collaboration;
		String behavior;
		
		public MessageData(String name, List<String> receivingClasses, String collaboration, String behavior) {
			this.name = name;
			this.receivingClasses  = receivingClasses;
			this.collaboration = collaboration;
			this.behavior = behavior;
		}
		
		/* Empty constructor for variable initialization  */
		public MessageData() {
			this.name = new String();
			this.receivingClasses = new LinkedList<String>();
			this.collaboration = new String();
			this.behavior = new String();
		}
		
		/**
		 * Overrides default toString
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			String result = new String();
			result = "< "+ name +  ", [ " ;
			for (int i=0; i < receivingClasses.size(); i++) {
				result += receivingClasses.get(i) + " ";
			}
			result +=  " ] >" ;
			return result;
		}

		/**
		 * Computes the comma separated string for cvs output 
		 * @return
		 */
		public String commaSeparated () {
			String result = new String();
			result = name +  ", " ;
			for (int i=0; i < receivingClasses.size(); i++) {
				result += receivingClasses.get(i) + ", "; 
			}
			return result;
		}
		
	} // of class AssociationData
	
	
	/**
	 * Class that compares the message data 
	 * @author Roberto
	 *
	 */
	class MessageDataComparator implements Comparator<MessageData> {
		
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
		 * Orders them according to the association names
		 */
		public int compare (MessageData e1, MessageData e2) {
			
			// First compare the names of the messages
			int result = compareStrings(e1.name, e2.name);
			if (result != MESSAGE_EQUAL) return result;
			
			// Second compare the collaboration name
			result = compareStrings(e1.collaboration, e2.collaboration);
			if (result != MESSAGE_EQUAL) return result;
			
			// Third compare the behavior name
			return compareStrings(e1.behavior, e2.behavior);
			
		} // compare
		
	} // of AssociationDataComparator
	
	
	/** Inner class for comparing classes for sorting.
	 * Current implementation orders by the name of the classes
	 */
	class ClassComparator implements Comparator<Class> {
		public int compare (Class c1, Class c2) {
			return c1.getName().compareTo(c2.getName());
		}
	} // of ClassComparator
	
	
	
	
	
	/**
	 * This class contains the information for instances of this rule
	 * @author Roberto
	 *
	 */
	class RuleInstance implements IRuleInstance<MessageData>{
		String messageName;
		Feature sourceFeature;
		String targetClass;
		Set<Feature> requiringFeatures;
		List<List<Integer>> CNF;
		boolean needsSAT;
		List<Boolean> evaluationValues;
		long evaluationTime;
		boolean hasError;
		String errorMessage;
		MessageData messageData;
		
		public RuleInstance(String messageName, Feature sourceFeature, Set<Feature> requiringFeatures,
				boolean needsSAT, MessageData messageData, String targetClass) {
			this.messageName = messageName;
			this.sourceFeature = sourceFeature;
			this.targetClass = targetClass;
			this.requiringFeatures = requiringFeatures;
			CNF = new LinkedList<List<Integer>>();
			this.messageData = messageData;
			this.needsSAT = needsSAT;
			CNF = new LinkedList<List<Integer>>();
			evaluationValues = new LinkedList<Boolean>();
			evaluationTime = 0;
			hasError = false;
			errorMessage = new String();
		}
	
		
		// interface get methods
		public String getId() { return messageName;  }
		public List<List<Integer>> getCNF() { return CNF; }
		public boolean needsSATEvaluation() { return needsSAT; }
		public Set<Feature> getTargetFeatures() { return requiringFeatures; }
		public List<Boolean> getEvaluationValues() { return evaluationValues; }
		public long getEvaluationTime() { return evaluationTime; }
		public boolean hasError() { return hasError; }
		public String getErrorMesssage() { return errorMessage; }
		public Feature getSourceFeature() {  return sourceFeature; }
		public MessageData getRuleInstanceDataObject() { return messageData; }
		
		// Used in the table label provider
		public String getViewerColumnValue(int index) {
			switch (index) {
			case 0 : // transition name
				return messageName;
			case 1 : // qualified name
				return sourceFeature.getName() + "->" + targetClass;
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
		public String getMessageName() { return messageName; }
		public String getTargetClass() { return targetClass; }
		public Set<Feature> getRequiringFeatures() { return requiringFeatures; }
		
		
		/**
		 * Overrides the default method to display the information of the instances
		 */
		public String toString() {
			StringBuffer result = new StringBuffer();
			result.append("<" + messageName + "," + sourceFeature.getName() + "," + targetClass + ", [");
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
			IRuleInstance<MessageData> ruleInstance = (IRuleInstance<MessageData>) element;
			return ruleInstance.getViewerColumnValue(index);
		}
		
	} // of Label provider
	
	
	
} // of class


