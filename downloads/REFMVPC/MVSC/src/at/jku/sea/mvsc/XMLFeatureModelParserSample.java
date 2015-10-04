
/*
 * Generative Software Development Lab (http://gsd.uwaterloo.ca/)
 * University of Waterloo
 * Waterloo, Ontario, Canada
 * January, 2009
 * 
 * This program shows how to read a feature model in the SXFM (simple XML feature model) format and 
 * print it on the standard output. Note that this program requires the "fmapi.jar" jar file
 * to be accessible.
 * 
 * For further assistance on how to use the "fmapi" library please contact the 
 * Generative Software Development Lab at gsd@swen.uwaterloo.ca
 * 
 * File created by Marcilio Mendonca on Jan 30th, 2009.
 * 
 * Modified by Roberto E. Lopez-Herrejon February 23, 2010.
 */

package at.jku.sea.mvsc;

import java.util.List;
import java.util.LinkedList;

import constraints.PropositionalFormula;
import constraints.BooleanVariable;

import fm.FeatureGroup;
import fm.FeatureModel;
import fm.FeatureModelStatistics;
import fm.FeatureTreeNode;
import fm.RootNode;
import fm.SolitaireFeature;
import fm.XMLFeatureModel;

public class XMLFeatureModelParserSample {

	/*
	public static void main(String args[]) {
		new XMLFeatureModelParserSample().parse();
	} 
	*/
	
	public void parse(String featureModelFile) {
		
		try {

			// String featureModelFile = "c:\\feature_models\\my_feature_model.xml";
			
			/* Creates the Feature Model Object
			 * ********************************
			 * - Constant USE_VARIABLE_NAME_AS_ID indicates that if an ID has not been defined for a feature node
			 *   in the XML file the feature name should be used as the ID. 
			 * - Constant SET_ID_AUTOMATICALLY can be used to let the system create an unique ID for feature nodes 
			 *   without an ID specification
			 *   Note: if an ID is specified for a feature node in the XML file it will always prevail
			 */			
			FeatureModel featureModel = new XMLFeatureModel(featureModelFile, XMLFeatureModel.USE_VARIABLE_NAME_AS_ID);
			
			// Load the XML file and creates the feature model
			featureModel.loadModel();
			
			// A feature model object contains a feature tree and a set of contraints			
			// Let's traverse the feature tree first. We start at the root feature in depth first search.
			System.out.println("FEATURE TREE --------------------------------");
			traverseDFS(featureModel.getRoot(), 0);
			
			// Now, let's traverse the extra constraints as a CNF formula
			System.out.println("EXTRA CONSTRAINTS ---------------------------");
			// traverseConstraints(featureModel);

			// Now, let's print some statistics about the feature model
			FeatureModelStatistics stats = new FeatureModelStatistics(featureModel);
			stats.update();
			
			stats.dump();
			
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	
	public void traverseDFS(FeatureTreeNode node, int tab) {
		for( int j = 0 ; j < tab ; j++ ) {
			System.out.print("\t");
		}
		// Root Feature
		if ( node instanceof RootNode ) {
			System.out.print("Root");
		}
		// Solitaire Feature
		else if ( node instanceof SolitaireFeature ) {
			// Optional Feature
			if ( ((SolitaireFeature)node).isOptional())
				System.out.print("Optional");
			// Mandatory Feature
			else
				System.out.print("Mandatory");
		}
		// Feature Group
		else if ( node instanceof FeatureGroup ) {
			int minCardinality = ((FeatureGroup)node).getMin();
			int maxCardinality = ((FeatureGroup)node).getMax();
			System.out.print("Feature Group[" + minCardinality + "," + maxCardinality + "]"); 
		}
		// Grouped feature
		else {
			System.out.print("Grouped");
		}
		System.out.print( "(ID=" + node.getID() + ", NAME=" + node.getName() + ")\r\n");
		for( int i = 0 ; i < node.getChildCount() ; i++ ) {
			traverseDFS((FeatureTreeNode )node.getChildAt(i), tab+1);
		}
	}
	

	/**
	 * Builds the constraints of the SPL Based on a configuration file
	 * @param spl
	 */
	public void buildsFeatureModel(SPL spl, String featureModelFile) {
		/* Creates the Feature Model Object
		 * ********************************
		 * - Constant USE_VARIABLE_NAME_AS_ID indicates that if an ID has not been defined for a feature node
		 *   in the XML file the feature name should be used as the ID. 
		 * - Constant SET_ID_AUTOMATICALLY can be used to let the system create an unique ID for feature nodes 
		 *   without an ID specification
		 *   Note: if an ID is specified for a feature node in the XML file it will always prevail
		 */			
		FeatureModel featureModel = new XMLFeatureModel(featureModelFile, XMLFeatureModel.USE_VARIABLE_NAME_AS_ID);
		
		try {
			// Load the XML file and creates the feature model
			featureModel.loadModel();
		} catch(Exception e) {
			e.printStackTrace();
			return;
		}
		
		// Creates the feature model constraints
		spl.setSPLName(featureModel.getName());
		spl.resetVariableValue();
		
		// Creates the root and adds it to the SPL
		Feature root = spl.addFeatureSPL(featureModel.getRoot().getName());
		spl.setRoot(root);
		spl.addRootConstraint();  // adds the root to the constraints
		
		// Traverses the tree and adds the constraints 
		traverseModel(spl, featureModel.getRoot(),null);
		
		// Reads the crosstree constraints
		// Notice here that requires and excludes clauses have to be added to SPL with addDomainConstraint
		// and not with addFODANode as they are strictly speaking part of the feature model
		System.out.println("Reads crosstree constraints");
		traverseConstraints(featureModel,spl);
		
	} // of builds Feature Model
	

	// *************************************************************************************
	// ************************** Model traversals
	
	/**
	 * Traverses a feature model and creates its FODA model representation
	 * @param spl The SPL to add the features and their constraints.
	 * @param node Current node being traversed
	 * @param parent Parent of the current node
	 */
	public void traverseModel(SPL spl, FeatureTreeNode node, FeatureTreeNode parent) {
		
		// Mandatory or optional features
		if ( node instanceof SolitaireFeature ) {
			// Optional Feature
			if ( ((SolitaireFeature)node).isOptional())
				spl.createOptionalFeature(node.getName(), parent.getName());
				// System.out.print("Optional");
			
			else { // Mandatory Feature
				// System.out.print("Mandatory");
				spl.createMandatoryFeature(node.getName(), parent.getName());
			}
		}
				
		// Feature Group
		if (node instanceof FeatureGroup ) {
			int minCardinality = ((FeatureGroup)node).getMin();
			int maxCardinality = ((FeatureGroup)node).getMax();
			// System.out.print("Feature Group[" + minCardinality + "," + maxCardinality + "]");
	
			// Checks if it is inclusive or exclusive or
			if (maxCardinality!=1) {
				// Create an inclusive or with the names of the children
				spl.createInclusiveOrFeature(parent.getName(), obtainChildrenName(node));
			} else {
				spl.createExclusiveOrFeature(parent.getName(), obtainChildrenName(node));
			}	
		}
		
		// Grouped features are the features that belong to a group. They are taken care of at the group node.
		
		/* System.out.print( "(ID=" + node.getID() + ", NAME=" + node.getName() + ")\r\n");
		*/
		
		// Traverses the rest of the trees
		for( int i = 0 ; i < node.getChildCount() ; i++ ) {
			traverseModel(spl,(FeatureTreeNode )node.getChildAt(i), node);
		}
		
	
	} // of traverseModel
	
	
	/**
	 * Reads the cross-tree constraints of the feature model and adds them to the SPL
	 * @param featureModel to read the constraints from
	 * @param spl to add the domain constraints to
	 */
	public void traverseConstraints(FeatureModel featureModel, SPL spl) {
		List<Integer> cnfConstraint = new LinkedList<Integer>();
		
		for( PropositionalFormula formula : featureModel.getConstraints() ) {
			// System.out.println(formula);
			cnfConstraint = new LinkedList<Integer>();
			for ( BooleanVariable variable : formula.getVariables()) { 
				String variableName = (featureModel.getNodeByID(variable.getID())).getName();
				Feature feature = spl.findFeature(variableName);
				int featureIndex = spl.getVariableFromFeature(feature);
				// It is a positive item
				if (variable.isPositive()) { 	
					cnfConstraint.add(featureIndex);
				} else {
					cnfConstraint.add(LogicTransformation.NOT(featureIndex));
				}
				// System.out.println((featureModel.getNodeByID(variable.getID())).getName() + " " + variable.isPositive()); 
			};
			spl.addCNFConstraint(cnfConstraint);
		} // for all formulas
		
	} // of traverse constraints

	// *************************************************************************************
	// ************************** Auxiliary functions
	
		/**
	 * Extracts the names of the children in a node. Used for exclusive and inclusive ors
	 * @param node The tree to get the children from
	 * @return
	 */
	public List<String> obtainChildrenName(FeatureTreeNode node) {
		List<String> childrenName = new LinkedList<String>();
		for( int i = 0 ; i < node.getChildCount() ; i++ ) childrenName.add(((FeatureTreeNode )node.getChildAt(i)).getName());
		return childrenName;
	}
	
	
}
