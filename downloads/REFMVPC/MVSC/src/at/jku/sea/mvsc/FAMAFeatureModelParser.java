/**
 * Parses a FAMA model and creates the corresponding SPL object
 */

package at.jku.sea.mvsc;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import at.jku.sea.mvsc.constraints.SCUtils;
import at.jku.sea.sat.implementation.picosat.Picosat4J;

import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.models.FAMAfeatureModel.Dependency;
import es.us.isa.FAMA.models.FAMAfeatureModel.ExcludesDependency;
import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFeatureModel;
import es.us.isa.FAMA.models.FAMAfeatureModel.Relation;
import es.us.isa.FAMA.models.FAMAfeatureModel.RequiresDependency;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.GenericFeatureModel;
import es.us.isa.FAMA.models.featureModel.GenericRelation;

public class FAMAFeatureModelParser {
	
	/**
	 * Builds the constraints of the SPL Based on a configuration file
	 * @param spl
	 */
	public SPL buildsFeatureModel(String featureModelFile) {
		
		// Loads up the FAMA file
		QuestionTrader qt = new QuestionTrader();
		GenericFeatureModel fm = (GenericFeatureModel) qt.openFile(featureModelFile);
		FAMAFeatureModel famaFM = (FAMAFeatureModel) qt.openFile(featureModelFile);
		
		// Creates the resulting SPL from the 
		String rootName = famaFM.getRoot().getName();
		SPL resultSPL = new SPL(rootName);
		
		// Creates the features in the SPL
		for (es.us.isa.FAMA.models.FAMAfeatureModel.Feature feature : famaFM.getFeatures()) {
			resultSPL.addFeatureSPL(feature.getName());
		}
		
		// *********************************************
		// Creates the cross-tree constraints
		
		// First, it adds the root
		Mandatory rootConstraint = new Mandatory( LogicTransformation.TRUE, rootName, resultSPL);
		resultSPL.addDomainConstraint(rootConstraint);
		
		// @note : number of destination is the list of children features
		for (GenericRelation relation : famaFM.getRelations()) {
			Relation famaRelation = famaFM.searchRelationByName(relation.getName());
			
			// @debug
			System.out.print("relation " + famaRelation.getName() + " parent " + famaRelation.getParent().getName() + " mandatory " + 
					famaRelation.isMandatory() + " ");
			Iterator<es.us.isa.FAMA.models.FAMAfeatureModel.Feature> iteratorChildren = famaRelation.getDestination();
			
			while(iteratorChildren.hasNext()){
				es.us.isa.FAMA.models.FAMAfeatureModel.Feature child = iteratorChildren.next();
				System.out.print(" " + child.getName());
			} // of iterating over children
			
			
			/* ------------------------------
			 * Types of domains constraints derived from the feature model
			 * ----------------------------- 
			 * Mandatory relation 1..1
			 * 	if num_children > 1 then is an exclusive-or relation
			 *  if num_children = 1 then is a mandatory feature
			 * Optional relation  0..1
			 *  if num_children > 1 then is an exclusive or relation ?? 
			 *  if num_children = 1 then is an optional feature
			 * Neither mandatory nor optional means has different cardinalities
			 *  @pending By now just assume that they are inclusive or relation 
			 */
			
			if (famaRelation.isMandatory()) {
				// if it is a solitary feature
				if (famaRelation.getNumberOfDestination() == 1) {
					// Create a mandatory feature and add them to the domain constraints
					Mandatory mandatory = new Mandatory( famaRelation.getParent().getName(), famaRelation.getDestinationAt(0).getName(), 
														  resultSPL); // inserts parent and then child
					resultSPL.addDomainConstraint(mandatory); 
				} else {
					// It is an exclusive-or relation because it has many children
					
					List<String> listChildrenNames = new LinkedList<String>();
					Iterator<es.us.isa.FAMA.models.FAMAfeatureModel.Feature> iteratorFeatures = famaRelation.getDestination();
					
					while(iteratorFeatures.hasNext()){
						es.us.isa.FAMA.models.FAMAfeatureModel.Feature child = iteratorFeatures.next();
						listChildrenNames.add(child.getName());
					} // of iterating over children features
					
					ExclusiveOR eor = new ExclusiveOR(famaRelation.getParent().getName(),listChildrenNames,resultSPL);
					resultSPL.addDomainConstraint(eor);
				}
				
			} else
				if (famaRelation.isOptional()) {
					if (famaRelation.getNumberOfDestination() == 1) {
						// Create a mandatory feature and add them to the domain constraints
						Optional optional = new Optional( famaRelation.getParent().getName(), famaRelation.getDestinationAt(0).getName(), 
															  resultSPL); // inserts parent and then child
						resultSPL.addDomainConstraint(optional); 
					} else {
						// @pending
						// It is an exclusive-or relation because it has many children ???? -> double check
						
						List<String> listChildrenNames = new LinkedList<String>();
						Iterator<es.us.isa.FAMA.models.FAMAfeatureModel.Feature> iteratorFeatures = famaRelation.getDestination();
						
						while(iteratorFeatures.hasNext()){
							es.us.isa.FAMA.models.FAMAfeatureModel.Feature child = iteratorFeatures.next();
							listChildrenNames.add(child.getName());
						} // of iterating over children features
						
						ExclusiveOR eor = new ExclusiveOR(famaRelation.getParent().getName(),listChildrenNames,resultSPL);
						resultSPL.addDomainConstraint(eor);
					}
					
				} else { //it is neither mandatory nor optional then it can have any cardinality, for now we consider it as inclusive or
					// @pending check the validity of this claim
					
					List<String> listChildrenNames = new LinkedList<String>();
					Iterator<es.us.isa.FAMA.models.FAMAfeatureModel.Feature> iteratorFeatures = famaRelation.getDestination();
					
					while(iteratorFeatures.hasNext()){
						es.us.isa.FAMA.models.FAMAfeatureModel.Feature child = iteratorFeatures.next();
						listChildrenNames.add(child.getName());
					} // of iterating over children features
					
					InclusiveOR inor = new InclusiveOR(famaRelation.getParent().getName(),listChildrenNames,resultSPL);
					resultSPL.addDomainConstraint(inor);
				} // of else
					

			// @debug
			System.out.println("");
			
		} // for all relations
		
		// Adds the cross-tree constraints
		Iterator<Dependency> dependencies = famaFM.getDependencies();
		while(dependencies.hasNext()){
			Dependency dependency = dependencies.next();
			String origin = dependency.getOrigin().getName();
			String destination = dependency.getDestination().getName();
			
			if (dependency instanceof ExcludesDependency) {
				Excludes excludes = new Excludes(destination,origin,resultSPL);
				resultSPL.addDomainConstraint(excludes);
			}
			
			if (dependency instanceof RequiresDependency) {
				Requires requires = new Requires(destination,origin,resultSPL);
				resultSPL.addDomainConstraint(requires);
			}
		} // of iterating over dependencies
	
		
		return resultSPL;
		
	} // buildsFeatureModel
	
	// Main method for testing purposes
	public static void main(String[] args) {
		
		FAMAFeatureModelParser fmp = new FAMAFeatureModelParser();
		
		// Creates a SPL for the GPL examples
		SPL gpl = fmp.buildsFeatureModel("./fama-models/FAMA-GPL.xml");
		
		// @debug
		System.out.println("\n\nPrinting SPL");
		System.out.println(gpl.toString());
		
		
		// @todo: create CNF formulas, execture the SAT solver to get all possible solutions	
		
		// Using pico for generating solutions
		Picosat4J picosat4J = Picosat4J.getInstance();
		picosat4J.printStatistics();
		
		// Sets up the SAT solver with the domain constraints PLf
		SCUtils.initializeDomainConstraintsSAT(picosat4J, gpl.getDomainConstraints());
		
		// Iterates over the solutions found
		int index = 1;
		while (picosat4J.isSatisifable()) {
			int[] assignments = picosat4J.next();
			System.out.println(index + " " + Arrays.toString(assignments));
			index++;
		}
		
	} // main

} // FAMAFeatureModelParser
