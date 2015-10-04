/*
 * This class holds the information required for each feature.
 * It loads the .uml files that correspond to each of the 
 */
package at.jku.sea.mvsc;

// import org.eclipse.uml2.uml.AggregationKind;
// import org.eclipse.uml2.uml.Association;
// import org.eclipse.uml2.uml.Classifier;
// import org.eclipse.uml2.uml.Enumeration;
// import org.eclipse.uml2.uml.EnumerationLiteral;
// import org.eclipse.uml2.uml.Generalization;
// import org.eclipse.uml2.uml.LiteralUnlimitedNatural;
// import org.eclipse.uml2.uml.Model;
// import org.eclipse.uml2.uml.NamedElement;
// import org.eclipse.uml2.uml.PrimitiveType;
// import org.eclipse.uml2.uml.Property;
// import org.eclipse.uml2.uml.Type;
// import org.eclipse.uml2.uml.UMLFactory;
// import org.eclipse.uml2.uml.resource.UMLResource;

import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.StateMachine;
import org.eclipse.uml2.uml.Collaboration;
import java.util.*;
import java.io.File;
import java.lang.StringBuffer;


// import java.io.IOException;
// import java.io.File;
// import org.eclipse.emf.ecore.xmi.*;

// import java.util.Iterator;

// import org.eclipse.emf.common.util.*;

public class Feature implements Comparable<Feature> {
	/** Name of the feature */
	protected String name;
	
	/** Directory path that contains files of the feature */
	protected String dirPath;
	
	/** A decision feature does not have an implementation. It helps structure the FODA model. */
	protected boolean isDecisionFeature = false;
	
	/** Class diagram of the feature */
	protected Package classDiagram; 
	
	/** State machine of the feature */
	protected List<StateMachine> stateMachineDiagram = new LinkedList<StateMachine>();
	
	/** Sequence diagram of feature. Depending on the actual serialization using UML it may be a package instead */
	protected List<Collaboration> sequenceDiagram  = new LinkedList<Collaboration>(); 

	/** Empty feature, used for the*/
	public static Feature NULL_FEATURE = new Feature();
	
	/** Empty constructor only used for null Feature	 */
	private Feature() { };
	
	/** Constructor that loads the diagrams of the features */
	public Feature (String name, String dirPath, boolean decisionFeature) throws Exception {
		this.name = name;
		isDecisionFeature = decisionFeature;
		
		//@ temp for now, it only loads the package file
		classDiagram = DiagramManager.loadPackage(getFeaturePath(name, dirPath));
		
		//@debug
		System.out.println("class diagram read " +  classDiagram);
		
		// System.out.println("\n\n class diagram read ");
		
		
		stateMachineDiagram = DiagramManager.loadStateMachines(dirPath);
		// System.out.println("\n\n state machine diagram read");
		
		sequenceDiagram = DiagramManager.loadSequenceDiagram(dirPath);
		// System.out.println("\n\n sequence diagram read");
		
	}
	
	/**
	 * This auxiliary function performs the naming policy to read a feature file
	 * @param featureName The name of the feature to locate
	 * @param dirPath The source directory of the feature
	 * @return The concatenated names of the path plus the feature name with extension .uml
	 */
	public String getFeaturePath(String featureName, String dirPath) {
		return dirPath.concat("/" + featureName + DiagramManager.UML_EXTENSION);
	}
	
	/**
	 * Constructor that creates decision features, used for structuring the FODA model
	 * @param name String with the name of the feature
	 */
	public Feature (String name) {
		this.name = name;
		isDecisionFeature = false;
		
		// References to the diagrams are made null
		classDiagram = null;
		stateMachineDiagram = null;
		sequenceDiagram = null;
	}
	
	/** Returns the name of the feature */
	public String getName() { return name; }
	
	/** Returns whether or not a feature is of decision or not */
	public boolean isDecisionFeature() { return isDecisionFeature; }
	
	/** Sets the value of the decision feature flag */
	public void setDecisionFeature(boolean  isDecisionFeature) { this.isDecisionFeature = isDecisionFeature; }
		
	/** Returns the class diagram */
	public Package getClassDiagram() { return classDiagram; }
	
	/** Returns the state machine diagram */
	public List<StateMachine> getStateMachine() { return stateMachineDiagram; }
	
	/** Returns the sequence diagram */
	public List<Collaboration> getSequenceDiagram() { return sequenceDiagram; }
	
	/** Displays the contents of the features */
	public String toString() {
	  StringBuffer buffer = new StringBuffer();
	  buffer.append("\n Feature name " + this.name);
	  
	  return buffer.toString();
	}
	
	/**
	 * Implements method of Comparable interface
	 */
	public int compareTo(Feature f) {
		int result = name.compareTo(f.getName());
		return result;
	}
	
	/** 
	 * Overrides the default equals. 
	 * Returns true if the names of the features is the same.
	 */
	public boolean equals(Object obj) {
		if (! (obj instanceof Feature)) return false;
		Feature other = (Feature) obj;
		return this.name.equals(other.name);
	}
	
} // of Feature
