/**
 * DiagramManager.java
 * Class that loads all diagrams and returns their corresponding UML object representations.
 * @author Roberto E. Lopez-Herrejon
 * @author SEA. Johannes Kepler Universitat
 */
package at.jku.sea.mvsc;

import org.eclipse.uml2.uml.AggregationKind;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Collaboration;
import org.eclipse.uml2.uml.Enumeration;
import org.eclipse.uml2.uml.EnumerationLiteral;
import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.LiteralUnlimitedNatural;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.PrimitiveType;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.StateMachine;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.resource.UMLResource;

import java.io.IOException;
import java.io.File;
import java.io.FilenameFilter;

import org.eclipse.emf.ecore.xmi.*;
import org.eclipse.emf.ecore.EClass;

// import java.util.Iterator;

import org.eclipse.emf.common.util.*;
// import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;

// import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.eclipse.uml2.uml.util.UMLUtil;
import org.eclipse.uml2.uml.Package;

import java.io.IOException;
// import java.util.Iterator;

import java.util.*;

public class DiagramManager {
	
	/** Resource set for managing files */
	protected static final ResourceSet RESOURCE_SET = new ResourceSetImpl();
	
	// *** Constants for the naming convention of diagrams inside features
	protected static final String UML_EXTENSION = ".uml";
	protected static final String CLASS_DIAGRAM = "ClassDiagram.uml";
	protected static final String STATEMACHINE_DIAGRAM = "StateMachine";
	
	/** Registers the resource factory. Part of the process to load UML files.
	 */
	protected static void registerResourceFactories() {
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put(
			UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE);
		// System.out.println("UML Extension " + UMLResource.FILE_EXTENSION);
		
	}
	
	/**
	 * Sets the package registry.
	 */
	protected static void setPackageRegistry() {
		// Sets in the package registry an instance of a UML package
		RESOURCE_SET.getPackageRegistry().put(UMLPackage.eNS_URI, UMLPackage.eINSTANCE);	
	}
	
	
	/** Registers the path maps. Required to load and save UML files.
	 * @param uri
	 */
	protected static void registerPathmaps(URI uri) {
		URIConverter.URI_MAP.put(URI.createURI(UMLResource.LIBRARIES_PATHMAP),
			uri.appendSegment("libraries").appendSegment(""));

		URIConverter.URI_MAP.put(URI.createURI(UMLResource.METAMODELS_PATHMAP),
			uri.appendSegment("metamodels").appendSegment(""));

		URIConverter.URI_MAP.put(URI.createURI(UMLResource.PROFILES_PATHMAP),
			uri.appendSegment("profiles").appendSegment(""));
	}
	
	
	/**
	 * Saves a package into a resource.
	 * @param package_  The reference to the Package to save.
	 * @param uri The URI for the file to save.
	 */
	protected static void save(org.eclipse.uml2.uml.Package package_, URI uri) {
		Resource resource = RESOURCE_SET.createResource(uri);
		
		// @Debug System.out.println("URI " + uri);
		// @Debug System.out.println("saving resource " + resource);
		EList<EObject> contents = resource.getContents();

		contents.add(package_);

		for (Iterator<EObject> allContents = UMLUtil.getAllContents(package_, true, false); allContents.hasNext();) {
			EObject eObject = (EObject) allContents.next();
			if (eObject instanceof Element) {
				contents.addAll(((Element) eObject).getStereotypeApplications());
			}
		}

		try {
			resource.save(null);
			// @Debug System.out.println("Done.");
		} catch (IOException ioe) {
			System.out.println("IO Exception" + ioe.getMessage());
		}
	}


	/**
	 * Loads a URI into a package.
	 * @param uri URI of the diagram to load.
	 * @return Returns a reference to a package
	 */
	protected static org.eclipse.uml2.uml.Package load(URI uri) {
		org.eclipse.uml2.uml.Package package_ = null;

		try {
			Resource resource = RESOURCE_SET.getResource(uri, true);
			EList<EObject> cont = resource.getContents();	
			package_ = (org.eclipse.uml2.uml.Package) EcoreUtil.getObjectByType(cont, UMLPackage.Literals.PACKAGE);
		} catch (WrappedException we) {
			// @Debug
			System.out.println(we.getMessage());
			System.exit(1);
		} catch (Exception e) {
			System.out.println("Other exception " + e.getMessage());
		}
		return package_;
	}
	
	/**
	 * Generic method that loads types of diagram.
	 * @param <S> The type of diagram to read.
	 * @param uri The URI of the source to load.
	 * @param literalType The type of UML that it will look for in the loaded diagram.
	 * @return The diagram load.
	 */
	public static<S> S loadDiagram (URI uri, EClass literalType) throws Exception {
		S diagram = null;
		Resource resource = RESOURCE_SET.getResource(uri, true);
		EList<EObject> cont = resource.getContents();
		
		// @debug
		System.out.println("Instance of the literal type? " + literalType.isInstance(cont.get(0)));
		System.out.println(cont.get(0));
		diagram = (S) EcoreUtil.getObjectByType(cont, literalType);		
		return diagram;
	}
	
	/*
	public static<S> S loadDiagram (URI uri, EClass literalType) {
		S diagram = null;
		try {
			Resource resource = RESOURCE_SET.getResource(uri, true);
			EList<EObject> cont = resource.getContents();	
			diagram = (S) EcoreUtil.getObjectByType(cont, literalType);
		} catch (WrappedException we) {
			// @Debug
			System.out.println(we.getMessage());
			System.exit(1);
		} catch (Exception e) {
			System.out.println("Other exception " + e.getMessage());
		}		
		return diagram;
	}
	*/
	
	/**
	 * Loads a UML Package from the directory that contains the feature elements.
	 * @param dirPath String that holds the path of the feature directory.
	 * @return Returns an object of type Package of UML API.
	 */
	public static Package loadPackage(String fullPath) throws Exception {
		// @debug
		System.out.println("Load package file " + fullPath);
		
		File sourceFile = new File(fullPath);
		
		// If there is no class diagram return the empty Package
		if (!sourceFile.exists()) { 
			
			// @DEBUG
			// System.out.println("\n\nClass diagram does not exist");
			return null; 
		}
		
		//@Debug
		// System.out.println("Absolute Path " + sourceFile.getAbsolutePath());
		
		URI uri = URI.createFileURI(sourceFile.getAbsolutePath());
		Package diagram = loadDiagram(uri, UMLPackage.Literals.PACKAGE);
		return diagram;
	}
	
	/**
	 * Loads the state machines of a feature. It follows the naming convention of StateMachineX.uml where X is any suffix.
	 * @param dirPath Directory that holds the contents of the feature.
	 * @return A list of StateMachine UML objects, one for each uml file read.
	 */
	public static List<StateMachine> loadStateMachines(String dirPath) throws Exception {
		List<StateMachine> listStateMachines = new LinkedList<StateMachine>();
		return listStateMachines;
	}
	
	//@debug from loading state machines
		/*
		StateMachine sm;
		URI uri;
		
		// Gets the names of all the diagrams whose names starts with StateMachine
		File dir = new File(dirPath);
		
		//@ DEBUG
		// System.out.println("Directory path " + dir.getAbsolutePath());
		
		for (File file : dir.listFiles()) { 
			String fileName = file.getName();
			if (fileName.startsWith(STATEMACHINE_DIAGRAM) && fileName.endsWith(UML_EXTENSION)) {
				uri = URI.createFileURI(file.getAbsolutePath());
				sm = loadDiagram(uri, UMLPackage.Literals.STATE_MACHINE);
				listStateMachines.add(sm);
			}
			
			// @DEBUG
			// System.out.println("Files in dir " + file.getAbsolutePath() + " file name " + file.getName());
			
		}
		*/
		
	
	/** Sequence diagram of feature. Depending on the actual serialization using UML it may be a package instead */
	public static List<Collaboration> loadSequenceDiagram(String dirPath) throws Exception {
		List<Collaboration> listSequenceDiagrams = new LinkedList<Collaboration>();
		return listSequenceDiagrams;
	}// of loadSequenceDiagrams		Collaboration col; 

	//@debug for reading sequence diagrams
	/*
		// List<StateMachine> listStateMachines = new LinkedList<StateMachine>();
		// StateMachine sm;
		URI uri;
		
		// Gets the names of all the diagrams whose names starts with StateMachine
		File dir = new File(dirPath);
		
		//@ DEBUG
		// System.out.println("Directory path " + dir.getAbsolutePath());
		
		for (File file : dir.listFiles()) { 
			String fileName = file.getName();
			if (fileName.startsWith(STATEMACHINE_DIAGRAM) && fileName.endsWith(UML_EXTENSION)) {
				uri = URI.createFileURI(file.getAbsolutePath());
				col = loadDiagram(uri, UMLPackage.Literals.COLLABORATION);
				listSequenceDiagrams.add(col);
			}
		}
	*/
	
}
