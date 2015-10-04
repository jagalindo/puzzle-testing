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


import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Message;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.Collaboration;
import org.eclipse.uml2.uml.Interaction;
import org.eclipse.uml2.uml.Behavior;
import org.eclipse.uml2.uml.Lifeline;
import org.eclipse.uml2.uml.MessageSort;
import org.eclipse.uml2.uml.MessageEnd;
import org.eclipse.uml2.uml.MessageOccurrenceSpecification;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Type;

import at.jku.sea.mvsc.Feature;
import at.jku.sea.mvsc.SPL;
import at.jku.sea.mvsc.gui.RuleInstanceTableViewer;



import at.jku.sea.mvsc.constraints.IRuleInstance;
import at.jku.sea.mvsc.gui.*;

public class SCMessageDirectionInClassAssociation extends SCConstraintDefinition implements RequiringRule {

	// Class variables
	private static final String nameRule = "Rule 7. Message direction must match association direction";
	
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
		MessageEnd messageSendEnd;
		
		// Message newMessage;
		
		List<String> targetTypeNames = new LinkedList<String>();
		List<String> sourceTypeNames = new LinkedList<String>();
		
		
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
			
			// Filters the collaborations from which to get the interations with their messages
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
							messageSendEnd = message.getSendEvent();
							
							// If the message received is not the occurence of a message specification then get next message
							if(!(messageReceiveEnd instanceof MessageOccurrenceSpecification) || 
									((MessageOccurrenceSpecification)messageReceiveEnd).getCovereds() == null) 
								continue;
							
							// Otherwise extract the names of the source and target classes where to look for the message name
							targetTypeNames = new LinkedList<String>();
							sourceTypeNames = new LinkedList<String>();
							
							// Gets the life lines associated to the a message received end
							for (Lifeline lifeline : ((MessageOccurrenceSpecification)messageReceiveEnd).getCovereds()) {
								// System.out.println("life line " + lifeline.getName());
								// System.out.println( " receiving class " + lifeline.getRepresents().getType().getName());
								targetTypeNames.add(lifeline.getRepresents().getType().getName());
							} // for all lifelines
							
							
							// Gets the life lines associated to the a message sent end
							for (Lifeline lifeline : ((MessageOccurrenceSpecification)messageSendEnd).getCovereds()) {
								// System.out.println("life line " + lifeline.getName());
								// System.out.println( " receiving class " + lifeline.getRepresents().getType().getName());
								sourceTypeNames.add(lifeline.getRepresents().getType().getName());
							} // for all lifelines
							
							// Adds a new element of MessageData and adds it to the corresponding list 
							messageDataListF.add(new MessageData(message.getName(), sourceTypeNames, targetTypeNames, 
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
		// Note: I am assuming that the line in the diagram and the attribute are not split in different features
		// Note: I am considering only messages with only one target class. That means it is not checking that all targets are met.
		//       Perhaps it work for examples of broadcast messages or something like that.
		/* Algorithm to follow
		 1. For each feature f
		  2. for each message in f
		     3. get the classes of message sources and their targets classes
		        4. if message has same source and target -> it is ok continue to next message
		        5. for each sc source class  in sources of message m
		          6. if sc is defined in f 
		          	 if class has attribute of a type target and belongs to an association
		          	   no need to check for safe composition as it is met by the same feature
		          	   continue
		          7. otherwise --- if sc not defined in f or does not define the required association
		             create a new rule instance <message_name, source feature, source class, target class, requiringFeatures>
		           8. for each compatible feature g
		            9. if sc is defined in g
		                 if class has attribute of type target and belongs to an association
		                   add the feature to requiringFeatures of rule instance  
		                  
		*/
		
		// Map of a feature to the features it is compatible with
		Map<Feature, SortedSet<Feature>> compatibleFeatures = spl.getCompatibleFeatures();
		List<String> receivingClasses;
		List<String> sendingClasses;
		
		// List with the rule instances detected
		List<IRuleInstance<MessageData>> listRuleInstances = new LinkedList<IRuleInstance<MessageData>>();
		
		//  List of the features that re required by an instance of a rule
		Set<Feature> requiringFeatures = new TreeSet<Feature>();
		
		//@debug
		//System.out.println("\n\n Detecting inconsistencies ");
		
		// 1. For each feature
		for (Feature f : listFeatures) {
			
			//@debug
			//System.out.println("\nFeature " +  f.getName());
			
			// Get message data
			messageDataListF = mapFeatureToMessagesData.get(f);
			
			// Writes the header of the feature being checked
			spl.writeAnalysisResult(f.getName());
			
			// 2. for each message in f
			for (MessageData messageData : messageDataListF) {
				
				// 3. gets the classes the message sending and receives 
				receivingClasses = messageData.receivingClasses;
				sendingClasses = messageData.sendingClasses;
				
				//@debug
				//System.out.println("message " + messageData.name ); // + " " + sendingClasses.size() + " " + receivingClasses.size());
				
				// 4. if message has same source and target -> it is ok continue to next message
				if (sameSourceAndTarget(messageData, sendingClasses, receivingClasses, f, spl)) {
					listRuleInstances.add(new RuleInstance(messageData.name, f, requiringFeatures, false, messageData));	
					continue;
				}
				
				// 5. for each sending class in sources of message m 
				for (String sendingClass : sendingClasses) {
					//@debug
					 //System.out.println("  sending class " + sendingClass);
												
					// 6. if sending class is defined in feature f
					Class foundClass = SCUtils.findClass(sendingClass, mapFeatureToClasses.get(f));
					if (foundClass!=null) {
						
						// @debug
						//System.out.println("   message in feature " + f.getName());
						
						// if sending class has attribute of type target that belongs to an association
						if (inSendingClass(foundClass, receivingClasses, mapFeatureToClasses.get(f))) {
							// @debug
							//System.out.println(" Message association defined in same feature " + messageData.name);
							
							listRuleInstances.add(new RuleInstance(messageData.name, f, requiringFeatures, false, messageData));	
							continue;
						}
						
					}	

					// 7. otherwise --- if sc not defined in f or does not define the required association
					// creates a blank copy for the required features of this message
					requiringFeatures = new TreeSet<Feature>();
					
					// 8.for each compatible feature g
					for (Feature g : compatibleFeatures.get(f)) {
						
						// 9. if sc is defined in g, and the sending class has an attribute for the association
						foundClass = SCUtils.findClass(sendingClass, mapFeatureToClasses.get(g)); 
						if (foundClass!=null &&
							inSendingClass(foundClass, receivingClasses, mapFeatureToClasses.get(g))) {
							// add the feature to requiringFeatures of rule instance  
							requiringFeatures.add(g);
							
							//@debug
							//System.out.println("\t defined in feature " + g.getName() + " class " + sendingClass);
							
						} // of 9.
					} // of compatible features	
					
					// create a new rule instance <message_name, source feature, requiringFeatures, needs SAT evaluation, message data>
					listRuleInstances.add(new RuleInstance(messageData.name, f, requiringFeatures, true, messageData));	
				
									
				} // in the sending classes
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
			new RuleInstanceTableViewer<MessageData>("Rule 7 for product line " + spl.getSPLName(), columnNames, columnWidths, 
													 columnAlignments, new RuleLabelProvider(),
													 listRuleInstances);
		tableViewer.open();
			
		// @debug
		System.out.println("Rule 7 instances " + listRuleInstances.size());
		
		return null;
		
	} // of apply

	
	
	// *****************************************************************************************
	// ********************* Auxiliary functions
	
	/**
	 * Checks if a message has the same source and target. In that case, it does not need to create an instance of the rule because
	 * we assume it is satisfied.
	 * Note: @pending It assumes that the for this to be truth both sending classes and receiving classes have the same elements.
	 *       It can be improved or modified depending on the semantics of multiple receiving or targeting classes
	 */
	public boolean sameSourceAndTarget(MessageData message, List<String> sendingClasses, List<String> receivingClasses, Feature f, SPL spl) {
		boolean result = true;
		
		// If they are not of the same length
		if (sendingClasses.size()!= receivingClasses.size()) return false;
		
		// Checks that all sending classes are the same as receiving classes
		for (String sendingClass : sendingClasses) {
			if (!receivingClasses.contains(sendingClass)) return false; 
		}
		
		// Inverse, checks that all receiving classes are contained in the sending classes
		for (String receivingClass : receivingClasses) {
			if (!sendingClasses.contains(receivingClass)) return false; 
		}
		
		return result;
	} // of sameSourceAndTarget
	
	
	
	/**
	 * Checks if a sending class has an attribute with the type of receiving class, and that it is in an association
	 * The
	 * @param sendKlass The class that send the message.
	 * @param receivingClasses The names of the classes that receive the messages.
	 * @param featureClasses The classes of the feature.
	 * @return
	 */
	public boolean inSendingClass(Class sendKlass, List<String> receivingClasses, List<Class> featureClasses) {
		boolean result = false;
		
		// Remove from the receiving classes those that are not in the feature classes
		List<Class> receivingClassesInFeature = new LinkedList<Class>();
		Class classFound = null;
		for (String receivingClass : receivingClasses) {
			classFound =  SCUtils.findClass(receivingClass, featureClasses);
			if (classFound!=null) receivingClassesInFeature.add(classFound);
		}
		
		// Traverses the attributes of the sending class looking for the association attribute in the classes
		for(Property property : sendKlass.getAttributes()) {
			if ( matchesType(property.getType(), receivingClassesInFeature) && property.getAssociation()!=null) return true;
		}
		
		return result;
	} // of inSendingClass
	
	
	/**
	 * Checks that a type matches the names of the receiving classes
	 * @param type The type to check the name against
	 * @param receivingClasses The list of receiving classes (or types) to compare against.
	 * @return
	 */
	public boolean matchesType(Type type, List<Class> receivingClassesInFeature) {
		
		String typeName = type.getName();
		
		for (Class klass : receivingClassesInFeature) {
			if (klass.getName().equals(typeName)) return true;
		}
		
		return false;
		
	} // of matchesType
	
	
	
	
	/**
	 * Checks that a class is defined in a list of Classes that correspond to a feature 
	 * @param className The name of the class to look in the feature.
	 * @param classes
	 * @return
	 */
	public boolean isClassInFeature(String className, List<Class> classes) {
		for (Class klass : classes) {
			if (klass.getName().equals(className)) return true;
		}
		return false;
	}
	
	
	
	
	// *****************************************************************************************
	// ********************* Auxiliary nested classes
	
	/** Private class for main association data to compare
	 */
	class MessageData {
		String name;							// name of the message
		List<String> sendingClasses;
		List<String> receivingClasses;			// list of classes that may receive them
		String collaboration;
		String behavior;
		
		public MessageData(String name, List<String> sendingClasses, List<String> receivingClasses, String collaboration, String behavior) {
			this.name = name;
			this.sendingClasses = sendingClasses;  
			this.receivingClasses  = receivingClasses;
			this.collaboration = collaboration;
			this.behavior = behavior;
		}
		
		/* Empty constructor for variable initialization  */
		public MessageData() {
			this.name = new String();
			this.sendingClasses = new LinkedList<String>();
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
			for (int i=0; i < sendingClasses.size(); i++) {
				result += sendingClasses.get(i) + " ";
			}
			result +=  " ], [" ;
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
			for (int i=0; i < sendingClasses.size(); i++) {
				result += sendingClasses.get(i) + " ";
			}
			result +=  " , " ;
			for (int i=0; i < receivingClasses.size(); i++) {
				result += receivingClasses.get(i) + " "; 
			}
			return result;
		}
		
	} // of class MessageData
	
	
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
	class RuleInstance implements IRuleInstance <MessageData> {
		String messageName;
		Feature sourceFeature;
		// String targetClass;
		Set<Feature> requiringFeatures;
		List<List<Integer>> CNF;
		boolean needsSAT;
		List<Boolean> evaluationValues;
		long evaluationTime;
		boolean hasError;
		String errorMessage;
		MessageData messageData;
		
		public RuleInstance(String messageName, Feature sourceFeature, Set<Feature> requiringFeatures,
							boolean needsSAT, MessageData messageData) {
			this.messageName = messageName;
			this.sourceFeature = sourceFeature;
			this.requiringFeatures = requiringFeatures;
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
				return computeQualifiedName(sourceFeature, messageData);
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
		
		// Additional get methods
		public String getMessageName() { return messageName; }
		
		// *** Auxiliary methods
		
		/**
		 * Computes the qualified name for table display
		 */
		private String computeQualifiedName(Feature sourceFeature, MessageData messageData) {
			StringBuffer result = new StringBuffer();
			result.append(sourceFeature.getName()); result.append(".");
			result.append(messageData.collaboration); result.append(".");
			result.append(messageData.behavior); result.append(".");
			result.append(computeCommaSeparatedListStrings(messageData.sendingClasses));
			result.append("->");
			result.append(computeCommaSeparatedListStrings(messageData.receivingClasses));
			return result.toString();
		}
		
		/**
		 * Computes string of the list of sending classes for display
		 * @param messageData
		 * @return
		 */
		private String computeCommaSeparatedListStrings(List<String> listStrings) {
			StringBuffer result = new StringBuffer();
			int size =  listStrings.size();
			int counter = 0;
			for (String className : listStrings) {
				result.append(className);
				if (counter < size -1) result.append(",");
			}
			return result.toString();
		}
		
		
		/** Overrides the default method to display the information of the instances */
		public String toString() {
			StringBuffer result = new StringBuffer();
			result.append("<" + messageName + "," + sourceFeature.getName() + ", ");
			result.append( sourceFeature.getName() + ", [");
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
	
	
} // of class SCMessageDirectionClassAssociation
