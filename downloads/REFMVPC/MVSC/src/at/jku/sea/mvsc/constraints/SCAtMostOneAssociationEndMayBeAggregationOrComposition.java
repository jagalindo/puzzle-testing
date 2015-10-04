/**
 * This class implements constraint that at most one association end may be either aggregation or composition
 */
package at.jku.sea.mvsc.constraints;


import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Comparator;
import java.util.SortedSet;

import at.jku.sea.mvsc.*;
import at.jku.sea.mvsc.constraints.SCAssociationEndNamesMustBeUniqueWithinAssociation.AssociationData;
import at.jku.sea.mvsc.constraints.SCAssociationEndNamesMustBeUniqueWithinAssociation.RuleInstance;
import at.jku.sea.mvsc.constraints.SCAssociationEndNamesMustBeUniqueWithinAssociation.RuleLabelProvider;
import at.jku.sea.mvsc.gui.RuleInstanceTableViewer;
//import at.jku.sea.sat.CNFConstraint;
//import at.jku.sea.sat.implementation.LiteralClause;
import at.jku.sea.sat.implementation.picosat.Picosat4J;

import org.eclipse.emf.ecore.EObject; 
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClassifier; 
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Display;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.PackageableElement;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.AggregationKind;



import java.util.*;

public class SCAtMostOneAssociationEndMayBeAggregationOrComposition extends SCConstraintDefinition implements ConflictingRule{


	// Class variables
	private static final String nameRule = "Rule 4. At most one association end may be an aggregation or composition";
	
	
	/** Applies the constraint to a list of features of a SPL.
	 * It returns map of a feature to a set of features for which a feature has conflicts with 
	 */
	public  Map<Feature, SortedSet<Feature>> apply (SPL spl) { 
		
		// Obtains the list of features of the SPL where this rule will be applied
		List<Feature> listFeatures = spl.getFeatures();
		
		
		// Creates a map between Features and their sorted associations extracted data
		Map<Feature, List<AssociationData>> mapFeatureToAssociationsData = new HashMap<Feature, List<AssociationData>>();
		
		// List of association data for both features to compare against
		List<AssociationData> associationDataListF; 
		List<AssociationData> associationDataListG;
		
		// New association data entry
		AssociationData assocDataG = new AssociationData();
		String assocName;
		
		// For each feature, obtain its sorted associations and add them to a map
		List<Association> associations;							// set of associations return from the class diagram
		// @refactor
		List<String> endTypesNames = new LinkedList<String>();
		List<AggregationKind> aggregationTypes  = new LinkedList<AggregationKind>();
		
		// ******************* Gathers the information from the features	
		
		for (Feature f : listFeatures) {
			
			// Creates a new list of association data
			associationDataListF = new LinkedList<AssociationData>();
			
			// if diagram is null
			if (f.getClassDiagram()!=null) {
				// Get associations to extract the corresponding data
				associations = filterList(f.getClassDiagram().getPackagedElements(), UMLPackage.Literals.ASSOCIATION);
			
				// For each association create its entries in the association data
				for (Association assoc : associations) {
					assocName = assoc.getName();
					for (Property prop : assoc.getMemberEnds()) {
						endTypesNames.add(prop.getType().getName());
						aggregationTypes.add(prop.getAggregation());
					}
					associationDataListF.add(new AssociationData (assocName, endTypesNames, aggregationTypes, f) );
					endTypesNames = new LinkedList<String>();
					aggregationTypes = new LinkedList<AggregationKind>();
				
				} // end for each association
			
			
				// The list of associations will be sorted by name of association
				Collections.sort(associationDataListF, new AssociationDataComparator()); 
			
			} // of diagram null
			
			// Add to the map between features and sorted associations
			mapFeatureToAssociationsData.put(f, associationDataListF);
			
		} // end for each feature
		
		
		
		
		// *** Data structures
		// Creates a map of conflicting features
		// Here SortedSets are ok because the are no repeated feature names
		Map<Feature, SortedSet<Feature>> mapFeatureToConflictFeatures = new HashMap<Feature,SortedSet<Feature>>();
		SortedSet<Feature> conflictSetF, conflictSetG; 
		
		// Map of a feature to the features it is compatible with
		Map<Feature, SortedSet<Feature>> compatibleFeatures = spl.getCompatibleFeatures();
		
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
		
		// @modified definition 
		// List<IRuleInstance<T>> listRuleInstances = new LinkedList<IRuleInstance<T>>();
		
		// @original definition
		List<IRuleInstance<AssociationData>> listRuleInstances = new LinkedList<IRuleInstance<AssociationData>>();
		
		//  List of the features that re required by an instance of a rule
		Set<Feature> conflictingFeatures = new TreeSet<Feature>();
		
		// The associations an individual association can be in conflict with
		List<AssociationData> conflictingAssociations = new LinkedList<AssociationData>();
		
		// List of end conflicts per association
		List<FeatureEndsConflicts> endConflicts = new LinkedList<FeatureEndsConflicts>();
		List<FeatureEndsConflicts> associationConflicts = new LinkedList<FeatureEndsConflicts>();
		
		// Traverse the list of features
		for (Feature f : listFeatures) {
			// 1. get association data and the conflict set of the feature
			associationDataListF = mapFeatureToAssociationsData.get(f);
			conflictSetF =  mapFeatureToConflictFeatures.get(f);
			
			// Writes the header of the feature being checked
			spl.writeAnalysisResult(f.getName());
			
			
			// 2. for each association
			for (AssociationData assocDataF : associationDataListF) {	

				// Clears the list of conflicting features
				conflictingFeatures = new TreeSet<Feature>();
					
				// Clears the list of conflicting associations
				conflictingAssociations = new LinkedList<AssociationData>();	
				
				// Clears the list of conflicts for an association
				endConflicts = new LinkedList<FeatureEndsConflicts>();
				
				// Writes the compatible feature being checked
				// spl.writeAnalysisResult(","+ g.getName());
				
				// 3 for each compatible features
				for (Feature g : compatibleFeatures.get(f)) {
				
					// 3.1
					associationDataListG = mapFeatureToAssociationsData.get(g);
					conflictSetG =  mapFeatureToConflictFeatures.get(g);				
					
					int result = Collections.binarySearch(associationDataListG, assocDataF, new AssociationDataComparator());
					if (result>=0) {
						// the key was found so there is a potential conflicting element 
						
						assocDataG = associationDataListG.get(result);
						
						associationConflicts = computeConflictingEnds(assocDataF, result, associationDataListG, g);
						
						// if conflicts were found for this feature
						if (associationConflicts.size()!=0) {
							
							// Accumulates the 
							endConflicts.addAll(associationConflicts);
							
							// As conflict are sets, the add is ignored if they are added more than one
							conflictSetF.add(g); 
							conflictSetG.add(f);
						
							//@debug
							System.out.println("Conflict " + f.getName() + " and " + g.getName() + "->  " + assocDataF + " + " + assocDataG);
							// spl.writeAnalysisResult("Conflict " + f.getName() + " and " + g.getName() + "->  " + assocDataF);
							spl.writeAnalysisResult(",,Conflict," + assocDataF.commaSeparated());
							
						} else { // no conflicts were found
							
							// System.out.println("OK " + f.getName() + " and " + g.getName() + "->  " + assocDataF + " + " + assocDataG );
							
							// spl.writeAnalysisResult("OK " + f.getName() + " and " + g.getName() + "->  " + assocDataF);
							spl.writeAnalysisResult(",,OK," + assocDataF.commaSeparated());
						}
					} else { // there was not matching associaiton in feature g
						
						// @debug
						// System.out.println("OK " + f.getName() + " and " + g.getName() + "->  " + assocDataF);
						
						// spl.writeAnalysisResult("OK " + f.getName() + " and " + g.getName() + "->  " + assocDataF);
						spl.writeAnalysisResult(",,OK," + assocDataF.commaSeparated());
					}
					
				} // for each compatible feature g
				
				// create a new rule instance <association_name, source feature, conflictingFeatures, needs SAT evaluation, association data>
				// only if conflictingFeatures.size()>0 then using the SAT solver is necessary
				listRuleInstances.add(new RuleInstance(assocDataF.name, f, conflictingFeatures, conflictingFeatures.size()>0 , assocDataF, 
													   conflictingAssociations, endConflicts));

				
			} // for each association in f
			
		} // of foreach feature
		
		
	

		// ***********************************************************************
		// ***********************************************************************
		
		// ******************* Performs the Safe Composition Checking
		// Checks safe composition on each of the rule instances
		SCUtils.checkSafeComposition(this, listRuleInstances, spl);
	
		// Displays the result in a table
		String[] columnNames = {"Association", "Feature -> EndName:Type", "SC", "Imp", "Conflicts", "Consistent", "msec", "Remarks" };
		int[] columnWidths = { 100, 300, 100, 100, 100, 100, 100, 100};
		int[] columnAlignments = { SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.LEFT,SWT.LEFT, SWT.LEFT};
		
		// T corresponds to AssociationData
		RuleInstanceTableViewer<AssociationData> tableViewer =  
			new RuleInstanceTableViewer<AssociationData>("Rule 4 for product line " + spl.getSPLName(), columnNames, columnWidths, 
													 columnAlignments, new RuleLabelProvider(),
													 listRuleInstances);
		
		// @debug
		tableViewer.open();
		
		// @debug
		System.out.println("Rule 4 Instances " + listRuleInstances.size());
		
		// return clashingFeatures
		return null;

	} // of apply
	
	

	
	
	/**
	 * Checks if two associations conflict or not. 
	 * At this point, the associations have the same name
	 * @param assocF the association to match
	 * @param position The place where a match was found
	 * @param associationDataListG The sorted list that has the associations of feature G
	 * Conflict conditions:
	 *   End types in assocF are the same as those of an element of associationDataListG from position onwards and
	 *   the corresponding aggregation types pairwise are different
	 *   Special case: aggregation type NONE does not causes conflict if composed with aggregation or composition
	 * Assumption: 
	 *   Conflicting associations must have the same number of member ends. In other words, FOSD composition does not add
	 *   member ends to an existing association. 
	 */
	public List<FeatureEndsConflicts> computeConflictingEnds(AssociationData assocF, int position, List<AssociationData> associationDataListG, Feature g) {
		List<FeatureEndsConflicts>  conflicts = new LinkedList<FeatureEndsConflicts>();
		
		boolean matchingAssociationFound = false;
		
		AssociationData assocG = associationDataListG.get(position);
		
		// While the matching association in G is not found and association elements in assocListG still have the same name 
		while(!matchingAssociationFound && assocG.name.equals(assocF.name) && position < associationDataListG.size()) {
			assocG = associationDataListG.get(position);
			matchingAssociationFound = checkMatchingAssociations(assocF, assocG);
			position++;
		}
		
		// If there was no matching association found, there is no conflict
		if (!matchingAssociationFound) return conflicts;
		
		// If a matching aggregation is found, then we need to check that their aggregation type does not conflict
		int posF=0, posG=0;
		AggregationKind endF, endG;
		
		// Creates the new fetureEnd conflicts object
		FeatureEndsConflicts feConflicts = new FeatureEndsConflicts(g);
		
		for (String endTypeF : assocF.endTypesNames) {
			posF = assocF.endTypesNames.indexOf(endTypeF);
			posG = assocG.endTypesNames.indexOf(endTypeF);
			endF = assocF.aggregationTypes.get(posF);
			endG = assocG.aggregationTypes.get(posG);
			
			if ((endG!=endF) && !hasAtMostOneShareOrComposition(assocF, assocG)) {
				feConflicts.addConflict(endTypeF,endF);
			}
			
			// posF++;
		} // of list of endType names
		
		// No conflict was found
		return conflicts ;
	
	} // is conflicting
	
	
	/**
	 * Checks the condition when two associations are conflicting or not
	 * Condition:
	 *  There is at most one association End whose aggregation kind is not NONE
	 * @param assocF
	 * @param assocG
	 * @return
	 */
	public boolean hasAtMostOneShareOrComposition(AssociationData assocF, AssociationData assocG) {
		int count = 0;
		for (AggregationKind kindF : assocF.aggregationTypes) {
			if (kindF.compareTo(AggregationKind.NONE_LITERAL)!=0) count++;
		}
		for (AggregationKind kindG : assocG.aggregationTypes) {
			if (kindG.compareTo(AggregationKind.NONE_LITERAL)!=0) count++;
		}
		return (count <= 1);
	}
	
	/**
	 * Checks that two associations have the same set of endTypes. At this point both associations have the same name.
	 * @param assocF
	 * @param assocG
	 * @return
	 */
	public boolean checkMatchingAssociations(AssociationData assocF, AssociationData assocG) {
		boolean result = true;
		
		// if they have different number of end types
		if (assocF.endTypesNames.size()!= assocG.endTypesNames.size()) return false;
		
		
		// for each end type in assocF 
		for (String endTypeF : assocF.endTypesNames) {
			
			// if it is not contained in the other association, then these two associations do not match
			if (!assocG.endTypesNames.contains(endTypeF)) return false;
		}
		
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

	
	/*
	 * Auxiliary classes for association manipulation
	 */

	/** Private class for main association data to compare
	 */
	class AssociationData {
		String name;
		List<String> endTypesNames = new LinkedList<String>();
		List<AggregationKind> aggregationTypes = new LinkedList<AggregationKind>();
		Feature containingFeature;
		
		public AssociationData(String name, List<String> endTypesNames, List<AggregationKind> aggregationTypes, Feature containingFeature) {
			this.name = name;
			this.endTypesNames = endTypesNames;
			this.aggregationTypes = aggregationTypes;
			this.containingFeature = containingFeature;
		}
		
		/*
		 * Empty constructor for variable initialization
		 */
		public AssociationData() {
			this.name = new String();
		}
		
		/**
		 * Overrides default toString
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			String result = new String();
			result = "< "+ name +  ", " ;
			for (int i=0; i < endTypesNames.size(); i++) {
				result += "(" + endTypesNames.get(i) + "," + aggregationTypes.get(i) + ") ";
			}
			result +=  " >" ;
			return result;
		}

		/**
		 * Computes the comma separated string for cvs output 
		 * @return
		 */
		public String commaSeparated () {
			String result = new String();
			result = name +  ", " ;
			for (int i=0; i < endTypesNames.size(); i++) {
				result += "(" + endTypesNames.get(i) + "," + aggregationTypes.get(i) + ") ";
			}
			return result;
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
		 * Orders them according to the association names
		 */
		public int compare (AssociationData e1, AssociationData e2) {
			
			return compareStrings(e1.name, e2.name);
			
		} // compare
		
	} // of AssociationDataComparator

	
	
	/**
	 * Captures the conflicts between the ends of an association and the features where the conflicts happen
	 * @author Roberto
	 *
	 */
	class FeatureEndsConflicts {
		Feature conflictingFeature;
		List<String> endNames;
		List<AggregationKind> endTypesKinds;
		public FeatureEndsConflicts(Feature conflictingFeature, List<String> endNames, List<AggregationKind> endTypeKinds) {
			this.conflictingFeature = conflictingFeature;
			this.endNames = endNames;
			this.endTypesKinds = endTypeKinds;
		}
		public FeatureEndsConflicts(Feature conflictingFeature){
			this.conflictingFeature = conflictingFeature;
			this.endNames = new LinkedList<String>();
			this.endTypesKinds = new LinkedList<AggregationKind>();
		}
		public void addConflict(String endTypeF, AggregationKind endF) {
			endNames.add(endTypeF);
			endTypesKinds.add(endF);
		}
		public List<String> getEndNames() { return endNames; }
		public List<AggregationKind> getEndTypesKinds() { return endTypesKinds; }
		
	} // of FeatureEndsConflicts

	
	
	/**
	 * Class that holds the information of the rule instance for this associations
	 * @author Roberto
	 *
	 */
	// @modified definition --- did not quite work
	// class RuleInstance implements IRuleInstance<T> {
	
	// @original definition
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
		List<FeatureEndsConflicts> featureEndsConflicts;
		
		public RuleInstance(String associationName, Feature sourceFeature, Set<Feature> conflictingFeatures,
							boolean needsSAT, AssociationData associationData, List<AssociationData> conflictingAssociations,
							List<FeatureEndsConflicts> featureEndsConflicts) {
			this.associationName = associationName;
			this.sourceFeature = sourceFeature;
			this.conflictingFeatures = conflictingFeatures;
			this.associationData = associationData;
			this.needsSAT = needsSAT;
			this.conflictingAssociations = conflictingAssociations;
			this.featureEndsConflicts = featureEndsConflicts;
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
				return translatesQualifiedName(); 
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
		 * Returns the list of <Feature : (endName_i, endType_i)> 
		 */
		private String translatesConflictingAssociations() {
			StringBuffer result =  new StringBuffer();
			FeatureEndsConflicts conflictingEnds;
			
			for (Feature feature : getTargetFeatures()) {
				result.append("<");result.append(feature.getName()); result.append(":");
				conflictingEnds = findConflictingAssociation(feature);
				for (int i = 0 ; i< conflictingEnds.endNames.size(); i++ ) {
					result.append("(");
					result.append(conflictingEnds.endNames.get(i)); result.append(",");
					result.append(conflictingEnds.endTypesKinds.get(i).getLiteral());
					/*
					switch(conflictingEnds.endTypesKinds.get(i)) {
					case AggregationKind.COMPOSITE : 
						result.append("Composite"); break;
					case AggregationKind.SHARED :
						result.append("Shared"); break;
					default :
							result.append("None");
					}
					*/
					result.append(")");
				}
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
		private FeatureEndsConflicts findConflictingAssociation(Feature feature) {
			FeatureEndsConflicts  result = null;

			for (FeatureEndsConflicts endsFeature : featureEndsConflicts) {
				if (endsFeature.conflictingFeature == feature) return endsFeature;
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

		/**
		 * Returns the qualified name, feature : <endName_i, endType_i> 
		 * @return
		 */
		private String translatesQualifiedName() {
			StringBuffer result = new StringBuffer();
			result.append(associationData.containingFeature.getName()); result.append(" : ");
			for (int i=0; i < associationData.endTypesNames.size(); i++ ){
				result.append("<"); result.append(associationData.endTypesNames.get(i)); result.append(",");
				result.append(associationData.aggregationTypes.get(i).getName()); result.append("> ");
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

	
	
	
	
} // of class  AtMostOne




