/**
 * SPLOT2FAMA
 * Does the translation of SPLOT models into FAMA models
 * Used for computing statistics on pair-wise commonality
 * @author Roberto E. Lopez-Herrejon at JKU
 */
package at.jku.sea.mvsc;

import java.util.Collections;
import java.util.List;
import java.util.LinkedList;
import java.util.Collection;

import constraints.PropositionalFormula;
import constraints.BooleanVariable;

import fm.FeatureGroup;
import fm.FeatureModel;
import fm.FeatureModelStatistics;
import fm.FeatureTreeNode;
import fm.RootNode;
import fm.SolitaireFeature;
import fm.XMLFeatureModel;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;

public class SPLOT2FAMA {

	int relationCounter=0;
	private static final String NEWLINE = "\n";
	private static final String TAB = "\t";
	StringBuffer result = new StringBuffer();
	
	/**
	 * Performs a multiple translation from SPLOT to FAMA
	 * @param spl 
	 * @param directory
	 */
	public void multipleSplot2Fama(SPL spl, String sourceDir, String targetDir) {
		File dir = new File(sourceDir);
		for (File file : dir.listFiles()) {
			if(file.getName().equals(".DS_Store"))
				continue;
			result = new StringBuffer();
			splot2FAMA(spl,file.getPath(), 
					targetDir.concat(File.separator).concat(file.getName()));
		} // for all files
	} // of multiple translation
	
	/**
	 * Translates one file from SPLOT format to FAMA format and also creates a SPL object.
	 * @refactor: separate translation from loading as it is not necessary always to create a SPL object.
	 * @param spl
	 */
	public void splot2FAMA(SPL spl, String featureModelFile, String famaModelFile) {
	
		//@debug
		System.out.println("Translating " + featureModelFile + " to " + famaModelFile);
		
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
		
		// Creates the result string that will be serialized into a file 
		result.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		result.append(NEWLINE);
		result.append("<feature-model xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"http://www.tdg-seville.info/benavides/featuremodelling/feature-model.xsd\">"
);
		result.append(NEWLINE);
		
		// Adds the root feature
		result.append("<feature name=\"" + featureModel.getRoot().getName()+ "\">");result.append(NEWLINE);
		
		// Creates the feature model constraints
		spl.setSPLName(featureModel.getName());
		spl.resetVariableValue();
		
		// Creates the root and adds it to the SPL
		Feature root = spl.addFeatureSPL(featureModel.getRoot().getName());
		spl.setRoot(root);
		spl.addRootConstraint();  // adds the root to the constraints
		
		// Traverses the tree and adds the constraints 
		// Launches the start from the children of the root, special case
		// Traverses the rest of the trees
		
		for( int i = 0 ; i < featureModel.getRoot().getChildCount() ; i++ ) {
			traverseModel(spl,(FeatureTreeNode)featureModel.getRoot().getChildAt(i), null, TAB);
		}

		
		// Notice here that requires and excludes clauses have to be added to SPL with addDomainConstraint
		// and not with addFODANode as they are strictly speaking part of the feature model
		// System.out.println("Reads crosstree constraints");
		
		// Closes the feature tag
		result.append("</feature>");result.append(NEWLINE);
		
		// Here go the cross-tree constraints
		// Note: constraints that are not directly requires or exclude will be left as comments
		traverseConstraints(featureModel,spl);
		
		// Closes the feature-model tag
		result.append("</feature-model>");result.append(NEWLINE);
		
		// @debug
		// System.out.println(result.toString());
		
		// At this point we can serialize the file for the output
		serialize2FAMA(result.toString(), famaModelFile);
		
		
	} // of builds Feature Model
	

	// *************************************************************************************
	// ************************** Model traversals
	
	/**
	 * Traverses a feature model and creates its FODA model representation
	 * @param spl The SPL to add the features and their constraints.
	 * @param node Current node being traversed
	 * @param parent Parent of the current node
	 */
	public void traverseModel(SPL spl, FeatureTreeNode node, FeatureTreeNode parent, String indent) {
		
		//@debug
		// System.out.println(indent + "@debug " + node.getName());
	
		
		// Mandatory or optional features
		if ( node instanceof SolitaireFeature ) {
			// <binaryRelation name="id">
			result.append(indent);
			result.append("<binaryRelation name=\""+ relationCounter + "\">");
			result.append(NEWLINE);
			relationCounter++;
			
			// Optional Feature
			if ( ((SolitaireFeature)node).isOptional()) {		
				// <cardinality max="1" min="0"/>
				result.append(indent);
				result.append("<cardinality max=\"1\" min=\"0\"/>");
				result.append(NEWLINE);
			}
			else { // Mandatory Feature
				// <cardinality max="1" min="1"/>
				result.append(indent);
				result.append("<cardinality max=\"1\" min=\"1\"/>");
				result.append(NEWLINE);
			}
			
			// <solitaryFeature name="name">
			result.append(indent); 
			// @update
			result.append("<solitaryFeature name=\"" + node.getName() + "\">");
			
			// result.append("<solitaryFeature name=\"" + node.getID() + "\">");
			result.append(NEWLINE);
			
			// Recursion if it has children
			for( int i = 0 ; i < node.getChildCount() ; i++ ) {
				traverseModel(spl,(FeatureTreeNode )node.getChildAt(i), node, indent + TAB);
			}
			
			// </solitaryFeature>
			result.append(indent);
			result.append("</solitaryFeature>");
			result.append(NEWLINE);result.append(NEWLINE);	
			
			// </binaryRelation>
			result.append(indent);
			result.append("</binaryRelation>");
			result.append(NEWLINE);	result.append(NEWLINE);			
			
		} // of SolitaireFeature	
		else // Feature Group
		if (node instanceof FeatureGroup ) {
			// <setRelation name="id">
			result.append(indent);
			result.append("<setRelation name=\""+ relationCounter + "\">");
			result.append(NEWLINE);
			relationCounter++;
			
			int minCardinality = ((FeatureGroup)node).getMin();
			int maxCardinality = ((FeatureGroup)node).getMax();
			
			// When a * is found as maximum cardinality, it becomes the number of children
			if (maxCardinality < 0) maxCardinality = node.getChildCount();
			
			//@debug
			// System.out.println(indent + "@debug grouped featured children " + ((FeatureGroup)node).getChildCount());
			
			// @DEBUG check special case with the star
			// <cardinality max="1" min="1"/>  -->
			result.append(indent);
			result.append("<cardinality max=\"" + maxCardinality + "\" min=\"" + minCardinality + "\"/>");
			result.append(NEWLINE);
			
			// Recursion if it has children
			FeatureTreeNode childNode;
			for( int i = 0 ; i < node.getChildCount() ; i++ ) {
				// <groupedFeature name="id">
				result.append(indent);
				
				//@modified
				result.append("<groupedFeature name=\"" + ((FeatureTreeNode)node.getChildAt(i)).getName() + "\">");
				
				// result.append("<groupedFeature name=\"" + ((FeatureTreeNode)node.getChildAt(i)).getID() + "\">");
				result.append(NEWLINE);			
				
				childNode = (FeatureTreeNode)node.getChildAt(i);
				//@debug
				// System.out.println(indent + "@debug children " + childNode.getName() + "->"+ childNode.getChildCount());
				
				traverseModel(spl,childNode, node, indent + TAB);
				// traverseModel(spl,node.getChildAt(i), node, indent + TAB);
				
				
				// </groupedFeature>
				result.append(indent);
				result.append("</groupedFeature>");
				result.append(NEWLINE);			
			
			} // for children of groupedFeatures
			
			// </setRelation>
			result.append(indent);
			result.append("</setRelation>");
			result.append(NEWLINE);result.append(NEWLINE);			
			
			
		} // of featureGroup
		else {
			//@debug
			// System.out.println(indent + "@debug something else ");
			for( int i = 0 ; i < node.getChildCount() ; i++ ) {
				traverseModel(spl,(FeatureTreeNode )node.getChildAt(i), node, indent + TAB);
			}
		}
		
	
	} // of traverseModel
	
	
	/**
	 * Reads the cross-tree constraints of the feature model and adds them to the SPL
	 * @param featureModel to read the constraints from
	 * @param spl to add the domain constraints to
	 */
	public void traverseConstraints(FeatureModel featureModel, SPL spl) {
		
		for( PropositionalFormula formula : featureModel.getConstraints() ) {
		
			
			// Returns the string of the constraint transformed if requires or excludes
			String requiresOrExcludes = isRequiresOrExcludes(formula.getVariables(), formula.getName(), featureModel);
			
			// @debug
			// System.out.println("@debug Requires/Excludes clause" + requiresOrExcludes);
			
			// Check if added as requires or excludes 
			if (requiresOrExcludes.length()!=0) {
				result.append(requiresOrExcludes);
			} else
			{ // add as XML comment for manual transformation
				result.append("<!-- " + formula.toString() + " -->"); 
				result.append(NEWLINE);
			}
			
	
		} // for all formulas
		
	} // of traverse constraints	

	/**
	 * Checks if the constraint read is a requires or excludes relation
	 * @param variables
	 * @return
	 */
	public String isRequiresOrExcludes(Collection<BooleanVariable> variables, String name, FeatureModel fm) {
		String res = "";
		
		if (variables.size()!=2) return res;
		BooleanVariable first = null, second = null;
		
		// Assigns the first and second variables
		for (BooleanVariable var : variables) {
			if (first==null) {
				first = var;
			} else
				second = var;
		}

		//@debug
		// System.out.println("@debug requires/excludes " + (first==null) + " " + (second==null));
		// System.out.println(first.isPositive() + " " + second.isPositive());
		
		String firstName = fm.getNodeByID(first.getID()).getName();
		String secondName = fm.getNodeByID(second.getID()).getName();
		// Check if requires or excludes tag is added
		if (!first.isPositive()) { 
			// !first v second is first -> second   requires
			if (second.isPositive()) {
				res = requiresString(firstName, secondName, name);
			} else
			{ // !first v !second is first -> !second    excludes
				res = excludesString(firstName, secondName, name);
			}
		} else
		{	
			// first v second
			if (second.isPositive()) {
				// Note: this most likely is an error
			} else
			{ // first v !second is second -> first    requires
				res = requiresString(secondName, firstName, name);
			}			
		}
		
		return res;
	} // is requires or excludes
	
	/**
	 * Computes the requires tag for the requires constraint
	 * @param first variable
	 * @param second variable
	 * @param name of the formula
	 * @return
	 */
	public String requiresString(String first, String second, String name) {
		StringBuffer requires = new StringBuffer();
		requires.append("<requires feature=\"" + first + "\" name=\"" + name + "\" " + "requires=\"" + second + "\"/>");
		requires.append(NEWLINE);
		return requires.toString();
	}
	
	/**
	 * Computes the requires tag for the requires constraint
	 * @param first variable
	 * @param second variable
	 * @param name of the formula
	 * @return
	 */
	public String excludesString(String first, String second, String name) {
		StringBuffer excludes = new StringBuffer();
		excludes.append("<excludes feature=\"" + first + "\" name=\"" + name + "\" " + "excludes=\"" + second + "\"/>");
		excludes.append(NEWLINE);
		return excludes.toString();
	}
	
	
	// *************************************************************************************
	// ************************** Auxiliary functions
	// *************************************************************************************
	
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
	
	/**
	 * Serializes the translated 
	 * @param contents String with the tag contents computed for the file
	 * @param famaModelFile 
	 */
	public void serialize2FAMA(String contents, String famaModelFile) {
		FileWriter outputStream = null;
		try {
			outputStream = new FileWriter(famaModelFile);
			outputStream.write(contents);
			outputStream.flush();
			outputStream.close();
        } catch(IOException ioe) {
        	System.out.println("Serialization error " + ioe.getMessage());
        	ioe.printStackTrace();
		}		
	} // of serialization
	
} // of class
