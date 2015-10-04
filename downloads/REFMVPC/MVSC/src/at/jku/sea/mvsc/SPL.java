package at.jku.sea.mvsc;

import java.io.*;
import java.util.*;

import org.eclipse.emf.common.util.URI;
import org.eclipse.uml2.uml.UMLPackage;

import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.StateMachine;
import org.eclipse.uml2.uml.Collaboration;

import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.models.featureModel.GenericFeatureModel;

import at.jku.sea.mvsc.constraints.SCConstraintDefinition;
import at.jku.sea.mvsc.constraints.SCUtils;
import at.jku.sea.mvsc.gui.RuleInstanceTableViewer;
import at.jku.sea.sat.implementation.picosat.Picosat4J;


/**
 * This class loads the models of the product lines and computes the corresponding PLf propositional formula
 * @author Roberto E. Lopez-Herrejon
 *
 */
public  class SPL {

	/** List of features in the SPL	 */
	protected List<Feature> features = new LinkedList<Feature>(); 
	
	// Features that are common to all the SPL
	protected List<Integer> commonFeatures = new LinkedList<Integer>();
	
	// Product line nodes
	protected List<FODANode> featureModel = new LinkedList<FODANode>();
	
	// This is the CNF representation of the product line constraints
	protected List<List<Integer>> domainConstraints = new LinkedList<List<Integer>>();
	
	
	// Array of the conflicting features for each constraint rule
	protected Vector< Map<Feature, SortedSet<Feature>> >  conflictingFeaturesVector = 
		new Vector<Map<Feature, SortedSet<Feature>>>(SCConstraintDefinition.NUMBER_CONSTRAINT_RULES);
	
	// A vector of maps that contains the CNF clauses of the conflicts ( i, CNF representation of ith rule)
	protected Vector<Map<Feature,List<List<Integer>>>> conflictingCNFVector = 
		new Vector<Map<Feature,List<List<Integer>>>>(SCConstraintDefinition.NUMBER_CONSTRAINT_RULES);
	
	// Maps between features and variables for SAT
	protected Map<Feature, Integer> mapFeatureToVariable = new HashMap<Feature,Integer>();
	protected Map<Integer, Feature> mapVariableToFeature = new HashMap<Integer, Feature>();
	
	
	protected List<FODANode> fodaNodes = new LinkedList<FODANode>();
	protected Feature root;
	
	// @update
	// Feature model compatibility variables
	private Map<Feature, SortedSet<Feature>> compatibleFeatures = new HashMap<Feature, SortedSet<Feature>>();
	
	// Counter for variable assignment
	private int variableValue = 0;
	
	// Default constants index for case true
	public static int TRUE_INDEX = -1;
	
	// Name of the product line and the directory where it is based
	private String SPLName;
	private String directory;
	
	// Name of the analysis file where the conflicts and non-conflicts will be shown
	private String analysisFile;
	public final String analysisExtension = ".analysis.cvs";
	public PrintWriter analysisOutput;
	
	// Contains the log file of the product line
	private String logFile;
	public final String logExtension = ".log";
	public PrintWriter logOutput;
	
	// ********************************************
	// ********* 
	
	/**
	 * Methods that keeps track of the variables assigned for the clauses
	 * @return the value of variableValue
	 */
	public int getVariableValue() {
		variableValue++;
		return variableValue;
	}
	
	/**
	 * Resets the counter of the variable for features
	 */
	public void resetVariableValue() {
		variableValue = 0;
	}
	
	/**
	 * Sets the name of the SPL
	 * @param name
	 */
	public void setSPLName(String name) {
		SPLName = name;
	}
	
	/**
	 * Gets the name of the SPL
	 * @return
	 */
	public String getSPLName() { 
		return SPLName;
	}
	
	public String getDir() { return directory; }
	public void setDir(String directory) { this.directory = directory; } 
	
	/**
	 * Default constructor
	 * @param String splName the name of the product line
	 * @param String directory the folder where all features of the product line are located
	 * @param String rootName Name of the root where to load the features
	 */
	public SPL(String splName, String dir, String rootName) {
		SPLName = splName;
		directory = dir;
		
		// Creates the analysis file
		createAnalysisFile();
		
		// Creates the log file
		createLogFile();
		
		// loads and creates the lists of features
		loadFeatures(dir, rootName);	
		
		// Vector initialization
		for (int i=0; i < SCConstraintDefinition.NUMBER_CONSTRAINT_RULES; i++) {
			
			// vector for conflicting features
			conflictingFeaturesVector.add(new HashMap<Feature,SortedSet<Feature>>());
		
			// vector for conflicting CNF clauses
			conflictingCNFVector.add(new HashMap<Feature,List<List<Integer>>>());
			
		} // of vector initialization
		
	} // of constructor
	
	/** @debug for testing only of parsing SPLOT files
	 * Constructor called when feature model created from file
	 * @param String splName the name of the product line, and also made the name of the root feature
	 */
	public SPL(String rootName) {
		SPLName = rootName;
		
		this.setRoot(new Feature(rootName));
		
		// directory = dir;
		
		// loads and creates the lists of features
		// loadFeatures(dir, rootName);
	
		// Creates the analysis file
		// createAnalysisFile();
	
		/*
		// Vector initialization
		for (int i=0; i < SCConstraintDefinition.NUMBER_CONSTRAINT_RULES; i++) {
			
			// vector for conflicting features
			conflictingFeaturesVector.add(new HashMap<Feature,SortedSet<Feature>>());
		
			// vector for conflicting CNF clauses
			conflictingCNFVector.add(new HashMap<Feature,List<List<Integer>>>());
			
		} // of vector initialization
		*/
	} // of constructor

	
	/**
	 * Constructor called from the simulation package
	 * It loads a SPL from its FAMA-based format
	 * Creates the feature mappings and computes the PLF term
	 * @param file
	 * @param simulationDirectory
	 */
	public SPL(String fileName, String simulationDirectory) {
	
		//A feature model from an XML file is loaded 
		QuestionTrader qt = new QuestionTrader();
		GenericFeatureModel fm = null;
		fm = (GenericFeatureModel) qt.openFile(fileName);
		
		// Traverses the list of features to create the mappings
		
	} // SPL
	
	
	/*
	 * Returns the features of the SPL
	 */
	public List<Feature> getFeatures() {
		return features;
	}
	
	// ************************** Creation of feature model
	
	/**
	 * Method that construct the feature model by adding FODA nodes and their respective constraints
	 */
	public void addFODANode(FODANode node) {
		// Adds a node to the feature model
		featureModel.add(node);
		
		// adds the constraint
		addDomainConstraint(node);
		
	} // addFODANode


	/*
	 * Computes the set of pairwise frequency
	 * This method should be called after the addition of the constraints in the product tline
	 * @todo: for now it is just a pairwise comparison. It should be refined to consider only those that according 
	 * to the FODA model can actually appear in the same configuration
	 */
	public void computePairwiseFrequency() {
		FeatureModelAnalysis.computePairwiseFrequency(this);
	}
	
	
	// **********************************************************************************************
	// **********************************************************************************************
	
	// ************************** File Management 
	
	/** Loads and creates the Features 
	 *  Reads the UML model diagrams that contain them.
	 */
	public void loadFeatures(String dirPath, String rootName) {
		// As each of the features is loaded
		// Create both maps each way, updating the static value
		File dir = new File(dirPath);
		Feature feature;  // Feature (String name, String dirPath)
		String featureName;
		int currentVal;
		
		//@log
		writeLogFile("Loading Directory Path " + dir.getAbsolutePath());
		
		// Looks for other directories in the SPL directory
		for (File file : dir.listFiles()) { 
			
			// Checks if it is a directory/folder
			if (file.isDirectory()) {
				
				// Create a new feature for that directory
				featureName = file.getName();
				try {
					
					// Adds a feature to the SPL, checking if it already exists or not
					feature  = addFeatureSPL(featureName);
					
					//@debug
					System.out.println("feature path "  + feature.getFeaturePath(featureName, file.getAbsolutePath()));
					
					feature.classDiagram = DiagramManager.loadPackage(feature.getFeaturePath(featureName, file.getAbsolutePath()));
					
					// @debug
					System.out.println("feature diagram " + feature.name + " " + feature.classDiagram);
					
					// Adds the 
					/*
					// @temp for now all the directories are assumed to be true features
					feature = new Feature (featureName, file.getAbsolutePath(), false);
					features.add(feature);
					
					
					//@log
					writeLogFile("Feature " + feature.getName() + " read");
					
					// Assigns the root if it finds it
					if (featureName.equals(rootName)) root = feature;
					
					// Adds the mapping references to both maps
					currentVal = getVariableValue();
					mapFeatureToVariable.put(feature, currentVal);
					mapVariableToFeature.put(currentVal, feature);
					*/
					
				}catch(Exception e) {
					// In case the feature cannot be created
					writeLogFile("Error loading feature " + featureName + " " + e.getMessage());
					e.printStackTrace();
				}
				
				//@log
				// writeLogFile("Read Feature " + featureName + "  " + file.getAbsolutePath());
				
			} // feature directory found
			
		} // for all the files in SPL directory
		
		// Here it reads the SPLOT format definition of the feature model
		
		// Reads the feature model and its constraints
		
		// Sets the root of the feature model 
		// root = feature
		
	} // of loadFeatures

	
	// ******************************************************************************************
	// ******************************************************************************************
	
	// **************   Management of the analysis file
	
	/** Creates the file for the analysis output */
	public void createAnalysisFile() {
			// creates the reference to the analysis file
		analysisFile = directory + File.separator + SPLName + analysisExtension;
		try {
			analysisOutput = new PrintWriter(new BufferedWriter(new FileWriter(analysisFile)));
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}
	} // of createAnalysisFile
	
	/** Writes a string of the analysis results to the file */
	public void writeAnalysisResult(String result) {
		analysisOutput.write(result+"\n");
	} // of write analysis
	
	
	/** Closes the file of the analysis results */
	public void closeAnalysisResult() {
		analysisOutput.close();
	} // of close analysis
	
	
	// **************   Management of log file
	
	/** Creates the file for the analysis output */
	public void createLogFile() {
		// creates the reference to the analysis file
		logFile = directory + "/" + SPLName + logExtension;
		
		//@debug
		// System.out.println(" logFile name " + logFile);
		
		try {
			logOutput = new PrintWriter(new BufferedWriter(new FileWriter(logFile)));
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}
	} // of createAnalysisFile
	
	/** Writes a string of the analysis results to the file */
	public void writeLogFile(String result) {
		logOutput.write(result+"\n");
	} // of write analysis
	
	
	/** Closes the file of the analysis results */
	public void closeLogFile() {
		logOutput.close();
	} // of close analysis
	
	
	
	// **********************************************************************************************
	// ************************** Feature management in the SPL

	/**
	 * Adds a feature to SPL, by selecting its. Updates counters and maps to-from var-features.
	 * @param feature is the feature to add to the product line. 
	 */
	public Feature addFeatureSPL(String featureName) {
		// Checks if there is a feature with that name already
		// if so returns that feature data
		for(Feature feature : features) if (feature.getName().equals(featureName)) return feature;
		
		// In case there is no such feature name creates the new feature
		Feature feature = new Feature(featureName);
		
		// updates the mappings
		int currentVal = getVariableValue();
		mapFeatureToVariable.put(feature, currentVal);
		mapVariableToFeature.put(currentVal, feature);	
		
		// Adds the entry to the feature list
		features.add(feature);
		
		return feature;		
	} 
	
	/**
	 * Sets the root of the product line
	 * @param feature
	 */
	public void setRoot(Feature feature) {
		root = feature;
	}
	
	
	/**
	 * Gets the reference to the root feature
	 * @return Feature that is the root of the model
	 */
	public Feature getRoot() {
		return root;
	}
	
	/**
	 * Creates an optional feature.
	 * @param child name of the child that is optional
	 * @param parent of optional feature
	 */
	public void createOptionalFeature(String child, String parent) {
		Feature kid = addFeatureSPL(child);
		Feature father = addFeatureSPL(parent); 
		
		// parent, child, and SPL
		addFODANode(new Optional(father.getName(), kid.getName(),this));
		
	} // createOptional
	
	/**
	 * Creates a mandatory feature. 
	 * @param child name of the child that is mandatory
	 * @param parent of mandatory feature
	 */
	public void createMandatoryFeature(String child, String parent) {
		Feature kid = addFeatureSPL(child);
		Feature father = addFeatureSPL(parent); 
		
		// parent, child, and SPL
		addFODANode(new Mandatory(father.getName(), kid.getName(),this));
		
	} // createMandatory
	
	
	/**
	 * Creates an exclusive or feature.
	 * @param parent
	 * @param children
	 */
	public void createExclusiveOrFeature(String parent, List<String> children) {	
		addFeatureSPL(parent);	// adds the feature name if it does not have it
		for (String childName : children) addFeatureSPL(childName);  // adds the children to the SPL		
		addFODANode(new ExclusiveOR(parent,children, this));
	} // createExclusiveOr
	
	
	/**
	 * Creates an inclusive or feature.
	 * @param parent
	 * @param children
	 */
	public void createInclusiveOrFeature(String parent, List<String> children) {
		addFeatureSPL(parent);	// adds the feature name if it does not have it
		for (String childName : children) addFeatureSPL(childName);  // adds the children to the SPL		
		addFODANode(new InclusiveOR(parent,children, this));
	} // createInclusiveOr
	
	
	
	// **********************************************************************************************
	// ************************** Constraints and logic programming
	
	/**
	 * The concrete classes in this method will define the domain constraints for the concrete SPL.
	 * These constraints are the configuration of the FODA model.
	 */
	public  void defineDomainConstraints() {}
	
	
	/**
	 * The concrete classes will define the additional decision features, that do not have an
	 * implementation, as they are only used for structuring the FODA model
	 */
	public  void defineDecisionFeatures() {}
	
	/**
	 * Add the root set as true to the domain constraints
	 */
	protected void addRootConstraint() {
		List<Integer> rootClause = new LinkedList<Integer>();
		rootClause.add(mapFeatureToVariable.get(root));
		domainConstraints.add(rootClause); // sets the root to true as it is always selected
	}
	
	/**
	 * Set up the constraints and computes the set of compatible features based on them
	 */
	public void setUpConstraints() {
		defineDecisionFeatures();
		defineDomainConstraints();
		addRootConstraint();
		computeCompatibleFeatures();    // computes the features that are compatible for composition 
	}
	
	
	// ************************** Constraint management
	
	/**
	 * Adds constraints to the domain constraints
	 */
	public void addDomainConstraint(FODANode node) {
		// Gets the constraints from the FODA node
		domainConstraints.addAll(node.convertPropositionalFormula()); 
	}
	
	
	/**
	 * Adds a general constraint in CNF form.
	 * @param constraint a list of integers
	 */
	public void addCNFConstraint(List<Integer> constraint) {
		domainConstraints.add(constraint);
	}
	
	/**
	 * Returns the list of CNFs that correspond to the domain constraints
	 * @return
	 */
	public List<List<Integer>> getDomainConstraints() {
		return domainConstraints;
	}
	
	/**
	 * Computes the constraints for the product line
	 */
	public void computeCNF() {
		// traverses each of the FODA nodes and merges the lists of the constraints formed 
		// by each of the constraints
	
	}
	
	/**
	 * Sets the conflicting features map in the corresponding index of the constraint rule
	 * @param index
	 * @param conflictMap
	 */
	public void setConflictingFeatures (int index, Map<Feature, SortedSet<Feature>> conflictMap) {
		//@debug
		System.out.println("Size vector " + conflictingFeaturesVector.size());
		conflictingFeaturesVector.set(index, conflictMap);
	}
	
	/**
	 * Gets the conflicting features for a given rule
	 * @param index
	 * @return
	 */
	public Map<Feature, SortedSet<Feature>> getConflictingFeatures (int index) {
		return conflictingFeaturesVector.get(index);
	}
	
	/**
	 * Sets the conflicting CNF clauses that will be used to call the SAT solver
	 * @param index
	 * @param conflictCNFClauses
	 */
	public void setConflictingCNF (int index, Map<Feature, List<List<Integer>>> conflictMapCNFClauses) {
		conflictingCNFVector.set(index, conflictMapCNFClauses);
	}
	
	/**
	 * Returns the list of CNF clauses that will be used to call the SAT solver for a given rule evaluation
	 * @param index The number of rule that is evaluated
	 * @return
	 */
	public Map<Feature,List<List<Integer>>> getConflictingCNF (int index) {
		return conflictingCNFVector.get(index);
	}
	

	
	
	
	/**
	 * Computes the SPL-wide common features by successively calling the SAT solver
	 * if PLF ^ !F is not satisfiable, it means it is SPL-common because there is no configuration where F is not selected
	 * @return A list with the SPL-wide common features.
	 */
	public void computeSPLWideCommonFeatures() {
		// List with SPL-wide common features
		commonFeatures.clear();
		
		// Using pico for generating solutions
		Picosat4J picosat4J = Picosat4J.getInstance();
		picosat4J.printStatistics();
		
		// Sets up the SAT solver with the domain constraints PLf
		SCUtils.initializeDomainConstraintsSAT(picosat4J, getDomainConstraints());
		
		// Feature id
		int featureID = 0;
		
		// Checks all the features of the product line
		for (Feature feature : features) {
			featureID = getVariableFromFeature(feature);
			
			// If feature is SPL-wide common feature
			// There does NOT exist a configuration where F is NOT selected
			if (!picosat4J.isSatisifable(LogicTransformation.NOT(featureID))) {
				commonFeatures.add(featureID);
			}
		}
	
	} // of compute SPL-wide common features
	
	public List<Integer> getSPLWideCommonFeatures() {
		return commonFeatures;
	}
	
	
	// **********************************************************************************************
	// ************************** Management of references hash maps
	
	/**
	 * Returns the assigned variable number for the corresponding feature
	 */
	public int getVariableFromFeature(Feature feature) {
		//@debug
		// System.out.println("null map " + mapFeatureToVariable);
		// System.out.println("feature name " + mapFeatureToVariable.get(feature));
		for (Feature f : mapFeatureToVariable.keySet()) {
			if (f.equals(feature)) return mapFeatureToVariable.get(f);
		}
		return Integer.MIN_VALUE;  // This indicates and error
	}
	
	/**
	 * Return the assigned feature from its corresponding variable number
	 */
	public Feature getFeatureFromVariable(int feature) {
		return mapVariableToFeature.get(feature);
	}
	
	/**
	 * Finds the reference to a feature based on its reference
	 * @param featureName String of the feature name to search
	 * @return the reference to the Feature object or null if the feature does not exist
	 */
	public Feature findFeature(String featureName) {
		for (Feature result :  features) if (featureName.equals(result.name)) return result;
		return null;
	}
	
	
	// ************************** Manipulation of compatible feature sets
	
	/*
	 * Returns the set of features that are compatible. They can at some point be composed together.
	 */
	public Map<Feature, SortedSet<Feature>> getCompatibleFeatures() {
		return compatibleFeatures;
	}
	
	
	// @update
	/*
	 * Computes the set of compatible features.
	 * This method should be called after the addition of the constraints in the product tline
	 * @todo: for now it is just a pairwise comparison. It should be refined to consider only those that according 
	 * to the FODA model can actually appear in the same configuration
	 */
	public void computeCompatibleFeatures() {
		compatibleFeatures = FeatureModelAnalysis.computeCompatibility(this);
		
	} // computeCompatibleFeatures
	
	
	
	// ************************** Auxiliary functions
	
	/**
	 * Translates into a String with the feature names the list of constraints specified in DIMACS
	 */
	public String translateConstraint(List<Integer> variables) {
		String result ="";
		String featureName = "";
		
		int size = variables.size();
		// @debug
		// System.out.println("Clause length " + size);
		
		int counter = 0;
		for(int variable : variables) {
			if (LogicTransformation.isNegated(variable)) { 
				featureName = "!";
				//@debug
				// System.out.println("Variable value " + variable);
				featureName = featureName + mapVariableToFeature.get(LogicTransformation.unNegate(variable)).name;
			} else {  // variable is not negated
				featureName = mapVariableToFeature.get(variable).name;
			}
			
			result += featureName + " ";
			if (counter < size-1)  result+= " v ";
			counter++;
		}
		
		return result;
	}
	
	/**
	 * Overrides default to String to print the contents of the SPL
	*/
	public String toString() {
		StringBuffer string = new StringBuffer();
		string.append("\n\n SPL name = " + SPLName + "\n");
		
		// Adds root information
		// string.append("Root = " + root.name + "\n");
		
		// Adds the names of the features and their corresponding diagram names
		for (Feature feature : features) {
			string.append("Feature = " + feature.name + " Variable = " +  getVariableFromFeature(feature) + " decision feature " 
					+ feature.isDecisionFeature + "\n");
		
			// Temporarily commented
			/*
			if (feature.classDiagram!=null) {
				string.append("\t Class diagram = " + feature.classDiagram.getName() + "\n");
			} else
				
				string.append("\t Class diagram = " + null + "\n");
			
			//Adds the State machine diagrams
			string.append("\t State machines \n");
			for (StateMachine sm : feature.stateMachineDiagram) {
				string.append("\t\t " + sm.getName() + "\n" ) ;
			} // for all state files
			
			
			// Adds the collaboration diagrams
			string.append("\t Sequence Diagrams \n");
			for (Collaboration col : feature.sequenceDiagram) {
				string.append("\t\t " + col.getName());
			}
			*/
			
		} // for all features
		
		// Adds the feature model description
		string.append(toStringFeatureModel());
		
		// Displays the domain constraints
		string.append("\n SPL Domain Constraints \n");
		for (List<Integer> constraint : domainConstraints) {
			// System.out.println(translateConstraint(constraint));
			string.append(translateConstraint(constraint) + "\n");
		}
		
		// Display variable assignment
		
		return string.toString();
		
	} // of toString
	
	// @update
	/**
	 * Returns the string with the set of compatible features for each feature
	 */
	public String toStringCompatibleFeatures() {
		StringBuffer string = new StringBuffer();
		string.append("\n\n SPL name " + SPLName + " Compatibility Results \n");
		
		// Adds root information
		// string.append("Root = " + root.name + "\n");
		
		// Adds the names of the features and their corresponding diagram names
		for (Feature feature : features) {
			string.append("Feature = " + feature.name + " = ");
			for (Feature compatibleFeature : compatibleFeatures.get(feature)) {
				string.append(compatibleFeature.getName() +",");
			}
			string.append("\n");
	  }
		
		return string.toString();
	} // toStringCompatibleFeatures
	
	
	/**
	 * Computes a String of the feature model.
	 * @return
	 */
	public String toStringFeatureModel() {
		StringBuffer string = new StringBuffer();
		string.append("\n\n SPL name " + SPLName + " Feature Model \n");
		
		// prints each constraint and 
		for(FODANode node : featureModel) string.append(node.toStringNodes() + "\n");
		
		return string.toString();
		
	} // string feature model
	
} // of SPL
