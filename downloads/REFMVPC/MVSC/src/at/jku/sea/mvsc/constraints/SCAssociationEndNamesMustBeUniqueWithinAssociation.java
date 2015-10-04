/**
 * SCAssociationEndNamesMustBeUniqueWithinAssociation.java
 * Implements the constraint that Association End names must be unique within an association
 * Assumption: for this constraint we assume that for two associations to match one condition is that
 *             they should have the same number of end names.
 * @author Roberto E. Lopez-Herrejon
 * @author Johannes Kepler Universitat Linz 
 */
package at.jku.sea.mvsc.constraints;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Comparator;
import java.util.SortedSet;

import at.jku.sea.mvsc.*;
import at.jku.sea.mvsc.gui.RuleInstanceTableViewer;



import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.UMLPackage;

import org.eclipse.uml2.uml.Class;

import java.util.*;

public class SCAssociationEndNamesMustBeUniqueWithinAssociation extends SCConstraintDefinition implements ConflictingRule {
	
	// Class variables
	private static final String nameRule = "Rule 3. Association end names must be unique";

	/** Applies the constraint to a list of features of a SPL.
	 * It returns map of a feature to a set of features for which a feature has conflicts with 
	 */
	public Map<Feature, SortedSet<Feature>> apply (SPL spl) { 
		
		// Obtains the list of features of the SPL where this rule will be applied
		List<Feature> listFeatures = spl.getFeatures();
				
		
		// ***********************************************************************
		// ***********************************************************************
	
		// ******************* Information data structures	
		
		// Creates a map between Features and their sorted associations extracted data
		Map<Feature, List<AssociationData>> mapFeatureToAssociationsData = new HashMap<Feature, List<AssociationData>>();
		
		// List of association data for both features to compare against
		List<AssociationData> associationDataListF; 
		List<AssociationData> associationDataListG;
		
		// New association data entry
		AssociationData assocDataG = new AssociationData();
		String assocName;

		// Creates a map of conflicting features
		// Here SortedSets are ok because the are no repeated feature names
		Map<Feature, SortedSet<Feature>> mapFeatureToConflictFeatures = new HashMap<Feature,SortedSet<Feature>>();
		SortedSet<Feature> conflictSetF, conflictSetG; 
		
		// Map of a feature to the features it is compatible with
		Map<Feature, SortedSet<Feature>> compatibleFeatures = spl.getCompatibleFeatures();
		
		
		
		// ******************* Gathers the information from the features	
		
		// For each feature, obtain its sorted associations and add them to a map
		List<Association> associations;								// set of associations return from the class diagram
		for (Feature f : listFeatures) {
			
			// Creates a new list of association data
			associationDataListF = new LinkedList<AssociationData>();
			
			// if the feature is not a decision feature or has no diagram file defined
			if (f.getClassDiagram()!=null) {
			
				//@debug
				// System.out.println("\t\t loading diagram " + f.getName());
			
				// Get associations to extract the corresponding data
				associations = filterList(f.getClassDiagram().getPackagedElements(), UMLPackage.Literals.ASSOCIATION);
			
				// For each association create its entries in the association data
				for (Association assoc : associations) {
					assocName = assoc.getName();
					for (Property prop : assoc.getMemberEnds()) {
					associationDataListF.add(new AssociationData (assocName, prop.getName(), prop.getType().getName(),f));
					}
				} // end for each association
			
				Collections.sort(associationDataListF, new AssociationDataComparator()); 
			} //of null diagram
			
			// Add to the map between features and sorted associations
			mapFeatureToAssociationsData.put(f, associationDataListF);
			
		} // end for each feature
		
		
		
	
		
		// For all features create an initially empty ordered set of conflicting features
		for (Feature f : listFeatures) {
			conflictSetF = new TreeSet<Feature>(new FeatureComparator());
			mapFeatureToConflictFeatures.put(f, conflictSetF);	
		}
		
		/* *** At this point we have the information extracted from the associations and stored them in a sorted list 
		 * Two maps: Features->associationDataListF,  Features->compatibleFeatures
		 */
		
		// Writes the headers of the cvs file
		spl.writeAnalysisResult(nameRule); // name of rule
		spl.writeAnalysisResult("Feature, Compatible feature, Check, Assoc name, End name, End Type");
		
		// For each feature in the product line
		// 1. Obtain its association data
		// 2. For each feature it is compatible with
		//    2.1 Obtain its association data
		//    2.2 Traverse each element from 1. and binsearch for it in 2.1
		//        if the element is found, there is a conflict so add each other features to the list of conflicts 
	
		
		// List with the rule instances detected
		List<IRuleInstance<AssociationData>> listRuleInstances = new LinkedList<IRuleInstance<AssociationData>>();
		
		//  List of the features that re required by an instance of a rule
		Set<Feature> conflictingFeatures = new TreeSet<Feature>();
		
		// The associations an individual association can be in conflict with
		List<AssociationData> conflictingAssociations = new LinkedList<AssociationData>();
		
	
		// Traverse the list of features
		for (Feature f : listFeatures) {
			// 1. get association data and the conflict set of the feature
			associationDataListF = mapFeatureToAssociationsData.get(f);
			conflictSetF =  mapFeatureToConflictFeatures.get(f);
			
			// Writes the header of the feature being checked
			spl.writeAnalysisResult(f.getName());
			
			// 2. for all associations in F
			for (AssociationData assocDataF : associationDataListF) {
	
				// Clears the list of conflicting features
				conflictingFeatures = new TreeSet<Feature>();
					
				// Clears the list of conflicting associations
				conflictingAssociations = new LinkedList<AssociationData>();	
			
				// for all compatible features
				for (Feature g : compatibleFeatures.get(f)) {
					
					// 2.1
					associationDataListG = mapFeatureToAssociationsData.get(g);
					conflictSetG =  mapFeatureToConflictFeatures.get(g);
				
					// Writes the compatible feature being checked
					spl.writeAnalysisResult(","+ g.getName());
					
					int result = Collections.binarySearch(associationDataListG, assocDataF, new AssociationDataComparator());
					if (result>=0) {
						// the key was found so there is a potential conflicting element 
						
						assocDataG = associationDataListG.get(result);
						
						if (isConflicting (assocDataG, assocDataF)) {
							
							// adds an element to the conflicting associations lists
							conflictingAssociations.add(assocDataG);
							
							// adds the feature to the conflicting feature set
							conflictingFeatures.add(g);
							
							// As conflict are sets, the add is ignored if they are added more than one
							// @note: this is because composition is symmetric in our case
							// @pending
							
							conflictSetF.add(g); 
							conflictSetG.add(f);
	
							//@debug
							System.out.println("Conflict " + f.getName() + " and " + g.getName() + "->  " + assocDataF + " + " + assocDataG);
							
							spl.writeAnalysisResult(",,Conflict," + assocDataF.commaSeparated());
							
						} else {  // there was no conflict found 
							
							// @debug
							// System.out.println("OK " + f.getName() + " and " + g.getName() + "->  " + assocDataF);
							
							spl.writeAnalysisResult(",,OK," + assocDataF.commaSeparated());
						}
					} else { // the key was not found, so there are no conflicts
						
						// @debug
						// System.out.println("OK " + f.getName() + " and " + g.getName() + "->  " + assocDataF);

						spl.writeAnalysisResult(",,OK," + assocDataF.commaSeparated());
					}
					

				}  // of compatible features
				
				// create a new rule instance <association_name, source feature, conflictingFeatures, needs SAT evaluation, association data>
				// only if conflictingFeatures.size()>0 then using the SAT solver is necessary
				listRuleInstances.add(new RuleInstance(assocDataF.name, f, conflictingFeatures, conflictingFeatures.size()>0 , assocDataF, conflictingAssociations));
				
			} // search for all associations in f
	
		} // of foreach feature
		
		
		
		// @debug -- refactor once working
		// Adds the conflicting features report to the analysis file
		spl.writeAnalysisResult("Summary of Conflicting Features");
		StringBuffer conflicts = new StringBuffer();
		
		for (Feature f : listFeatures) {
			conflicts.append(f.getName());
			conflictSetF = mapFeatureToConflictFeatures.get(f);
			for (Feature g : conflictSetF) {
				conflicts.append("," + g.getName());
			}
			spl.writeAnalysisResult(conflicts.toString());
			conflicts = new StringBuffer();
		} // of listing conflicting features
		
		// Sets the conflicting features map in the SPL for this rule
		spl.setConflictingFeatures(SCConstraintDefinition.ASSOCIATION_ENDNAME_UNIQUE, mapFeatureToConflictFeatures);
	
		
		// ***********************************************************************
		// ***********************************************************************
		
		// ******************* Performs the Safe Composition Checking
		// Checks safe composition on each of the rule instances
		SCUtils.checkSafeComposition(this, listRuleInstances, spl);
	
		// Displays the result in a table
		String[] columnNames = {"Association", "Feature -> EndName:Type", "SC", "Imp", "Conflicts", "Consistent", "msec", "Remarks" };
		int[] columnWidths = { 100, 300, 100, 100, 100, 100, 100, 100};
		int[] columnAlignments = { SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.LEFT,SWT.LEFT, SWT.LEFT};
		
		// @important !!!!
		RuleInstanceTableViewer<AssociationData> tableViewer =  
			new RuleInstanceTableViewer<AssociationData>("Rule 3 for product line " + spl.getSPLName(), columnNames, columnWidths, 
													 columnAlignments, new RuleLabelProvider(),
													 listRuleInstances);
		tableViewer.open();
		
		// @debug
		System.out.println("Rule 3 instances " + listRuleInstances.size());
		
		// ***********************************************************************
		
		return null;
		
	} // of apply
	
	

	// *****************************************************************************************
	// ********************* Auxiliary functions
	
	/**
	 * Checks if two associations with equal name and endType from different features conflict 
	 * Conflict: if they dont have the same end names and their names are not the default ones (empty or dst)
	 * Note: dst is the default ending EMF UML editor adds to end names of navigable associations by default
	 * Note: when serialized the two edges are serialized even if it is only a uni-directional association
	 */
	public boolean isConflicting(AssociationData assocF, AssociationData assocG) {
		boolean result = false;
		if (!assocF.endName.equals(assocG.endName) && !assocF.endName.equals("dst") && !assocF.endName.equals("") &&
			!assocG.endName.equals("dst") && !assocG.endName.equals("")) result = true;
		return result;
	}
		
	
	/**
	 * For debugging purposes it displays the information of the associations
	 */
	private void print(Association assoc) {
		System.out.println("Name " + assoc.getName());
		List<Property> members = assoc.getMemberEnds();
		for (Property prop : members) {
			System.out.println(prop.getName() + "->" + prop.getType());
		}
	}

		

	// *****************************************************************************************
	// ********************* Auxiliary nested classes

	/** Private class for main association data to compare
	 */
	class AssociationData {
		String name;
		String endName;
		String endTypeName;
		Feature containingFeature;
		
		public AssociationData(String name, String endName, String endTypeName, Feature containingFeature) {
			this.name = name;
			this.endName = endName;
			this.endTypeName = endTypeName;
			this.containingFeature = containingFeature;
		}
		
		/*
		 * Empty constructor for variable initialization
		 */
		public AssociationData() {
			this.name = new String();
			this.endName = new String();
			this.endTypeName = new String();
		}
		
		/**
		 * Overrides default toString
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return "< "+ name + ", " + endName + ", " + endTypeName + " >";
		}

		/**
		 * Computes the comma separated string for cvs output 
		 * @return
		 */
		public String commaSeparated () {
			return name + "," + endName + ", " + endTypeName;
		}
		
	} // of class AssociationData


	/**
	 * Class that compares the association data by name, endName, and endTypeName
	 * @author Roberto
	 *
	 */
	class AssociationDataComparator implements Comparator<AssociationData> {
		
		/** Constants for association comparison */
		public static final int ASSOCIATION_EQUAL = 0;
		public static final int ASSOCIATION_LESSTHAN = -1;
		public static final int ASSOCIATION_GREATERTHAN = 1;
		
		/**
		 * Compares the string values of the data using compareTo
		 * @param s1 First string.
		 * @param s2 Second string.
		 */
		public int compareStrings(String s1, String s2) {
			int result =	ASSOCIATION_EQUAL;
		// First compares by names
			if (s1!=null) {
				if (s2!=null) {
					// both not null so they can be compared
					result = s1.compareTo(s2);
					if (result!=ASSOCIATION_EQUAL) return result;
				} else
				{ // e1 is not null but e2 is null
					return ASSOCIATION_LESSTHAN;
				}
			}else {
				// e1.name is null
				return ASSOCIATION_GREATERTHAN;
			}
			
			return result;
			
		} // compareStrings
		
		/**
		 * Method from interface Comparator 
		 * The assumption is that there cannot be two associations with the same name to the same type
		 */
		public int compare (AssociationData e1, AssociationData e2) {
			int result = ASSOCIATION_EQUAL;
			
			// First compares by names
			result = compareStrings(e1.name, e2.name);
			if (result!=ASSOCIATION_EQUAL) return result;
			
			// Second compares the names of the end types
			return compareStrings(e1.endTypeName, e2.endTypeName);
			
			// Comparing with the end name is not necessary
			
			/*
			// Second. Compare the names of the endTypeNames
			result = compareStrings(e1.endTypeName, e2.endTypeName);
			if (result!=ASSOCIATION_EQUAL) return result;
			
			
			// Third. Compare the names of the endNames
			return compareStrings(e1.endName, e2.endName);
			*/
		} // compare
		
		
	} // of AssociationDataComparator
	
	
	
	
	/**
	 * Class that holds the information of the rule instance for this associations
	 * @author Roberto
	 *
	 */
	class RuleInstance implements IRuleInstance<AssociationData> {
		String associationName;
		Feature sourceFeature;
		Set<Feature> conflictingFeatures;
		List<List<Integer>> CNF;
		boolean needsSAT;
		List<Boolean> evaluationValues;
		long evaluationTime;
		boolean hasError;
		String errorMessage;
		AssociationData associationData;
		List<AssociationData> conflictingAssociations;
		
		public RuleInstance(String associationName, Feature sourceFeature, Set<Feature> conflictingFeatures,
							boolean needsSAT, AssociationData associationData, List<AssociationData> conflictingAssociations) {
			this.associationName = associationName;
			this.sourceFeature = sourceFeature;
			this.conflictingFeatures = conflictingFeatures;
			this.associationData = associationData;
			this.needsSAT = needsSAT;
			this.conflictingAssociations = conflictingAssociations;
			CNF = new LinkedList<List<Integer>>();
			evaluationValues = new LinkedList<Boolean>();
			evaluationTime = 0;
			hasError = false;
			errorMessage = new String();
		}	

		// get methods
		public String getId() { return associationName; }
		public Feature getSourceFeature() { return sourceFeature; } 
		public Set<Feature> getTargetFeatures() { return conflictingFeatures; } 
		public boolean needsSATEvaluation() {return needsSAT; }
		public long getEvaluationTime() { return evaluationTime; }
		public List<Boolean> getEvaluationValues() { return evaluationValues; }
		public boolean hasError() { return hasError; }
		public String getErrorMesssage() { return errorMessage; }
		public AssociationData getRuleInstanceDataObject() { return associationData; }
		public List<List<Integer>> getCNF() { return CNF; }
		// Used in the table label provider
		public String getViewerColumnValue(int index) {
			switch (index) {
			case 0 : // association name
				return associationName;
			case 1 : // qualified name
				return associationData.containingFeature.getName() + "->" + associationData.endName + ":" +  associationData.endTypeName;
			case 2 : // requires safe composition
				return Boolean.toString(needsSAT);
			// The error was not necessary for this table	
			// case 3 : // has error
			//	return Boolean.toString(hasError);
			case 3 : // logical proposition
				if (!needsSAT) { // if does not need SAT or is an error return empty 
					return "";
				}
				return SCUtils.translatesConflictingImpl(sourceFeature, conflictingFeatures);
			case 4 : // list of conflicting names <endName,FeatureName>
				return translatesConflictingAssociations();
			case 5 : // is consistent (if satisfiable means is inconsistent, that is why it is negated)
				if (!needsSAT) { // if does not need SAT or is an error return empty 
					return "";
				}
				return translatesConsistencyResults(); 
			case 6 : // eval time
				if (!needsSAT) { // if does not need SAT or is an error return empty 
					return "";
				}
				return Long.toString(evaluationTime);
			case 7 : // remarks
				return errorMessage;
			default :
				return "unkown " + index;
			}
		} // getViewrColumnValue
		
		
		// **********************************************************************************
		// *** Set methods

		public void setCNF(List<List<Integer>> CNF) { this.CNF = CNF; }
		public void setEvaluationValues(List<Boolean> evaluationValues) { this.evaluationValues = evaluationValues; }
		public void setEvaluationTime(long evaluationTime) { this.evaluationTime = evaluationTime; }
		public void setError(boolean error) { this.hasError = error; }
		public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
		
		// **********************************************************************************
		// *** Auxiliary methods
		
		/**
		 * Returns the list of <endNames, Feature> 
		 */
		private String translatesConflictingAssociations() {
			StringBuffer result =  new StringBuffer();
			AssociationData conflictingAssociation;
			
			for (Feature feature : getTargetFeatures()) {
				conflictingAssociation = findConflictingAssociation(feature);
				result.append("<"); result.append(conflictingAssociation.endName); result.append(",");
				result.append(conflictingAssociation.containingFeature.getName());result.append(">");
			}
			return result.toString();
		}
		
		
		/**
		 * Returns the corresponding conflicting association given a feature
		 * The assumption is that for any single rule instance, there can only be one feature that causes conflict
		 * otherwise it would mean that the feature is already inconsistent before any composition
		 * @param feature
		 * @return
		 */
		private AssociationData findConflictingAssociation(Feature feature) {
			AssociationData result = null;
			for (AssociationData conflictingAssociation : conflictingAssociations) {
				if (conflictingAssociation.containingFeature == feature) return conflictingAssociation;
			}
			return result;
		}
		
		
		/**
		 * Returns the list of <conflicting feature, consistency result> 
		 * If the answer is evaluation is true it means it is not consistent, that it is why the result is negated
		 */
		private String translatesConsistencyResults() {
			StringBuffer result =  new StringBuffer();
			int index = 0;
			
			for (Feature feature : getTargetFeatures()) {
				result.append("<"); result.append(feature.getName()); result.append(",");
				result.append(Boolean.toString(!evaluationValues.get(index)));result.append(">");
				index++;
			}
			return result.toString();
		}

	} //of RuleInstance
	
	
	/**
	 * This class contains the information for instances of this rule
	 * @author Roberto
	 *
	 */
	class RuleLabelProvider extends LabelProvider implements ITableLabelProvider {
		
		public Image getColumnImage(Object element, int index) { return null; }
		
		public String getColumnText(Object element, int index) {
			IRuleInstance<AssociationData> ruleInstance = (IRuleInstance<AssociationData>) element;
			return ruleInstance.getViewerColumnValue(index);
		}
		
	} // of Label provider
	
	
	
	
} // of class AssociationEndName


