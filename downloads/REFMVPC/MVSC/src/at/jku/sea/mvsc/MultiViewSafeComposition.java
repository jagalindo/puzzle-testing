/**
 * Multiview Safe Composition Implementation
 */
package at.jku.sea.mvsc;

import java.util.*;
import java.net.*;

import at.jku.sea.sat.implementation.LiteralClause;
import at.jku.sea.sat.implementation.picosat.Picosat4J;
import at.jku.sea.mvsc.constraints.*;

import at.jku.sea.mvsc.examples.*;

import at.jku.sea.mvsc.gui.*;

 // Importing emf and UML classes
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;

import org.eclipse.uml2.uml.*;

import org.eclipse.uml2.uml.AggregationKind;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Enumeration;
import org.eclipse.uml2.uml.EnumerationLiteral;
import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.LiteralUnlimitedNatural;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.PrimitiveType;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.resource.UMLResource;

import java.io.IOException;
import java.io.File;
import org.eclipse.emf.ecore.xmi.*;

import java.util.Iterator;

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
import java.util.Iterator;

// import org.eclipse.emf.common.util.EList;
// import org.eclipse.emf.common.util.URI;
// import org.eclipse.emf.common.util.WrappedException;
// import org.eclipse.emf.ecore.EObject;
// import org.eclipse.emf.ecore.resource.Resource;
// import org.eclipse.emf.ecore.resource.ResourceSet;
// import org.eclipse.emf.ecore.resource.URIConverter;
// import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
// import org.eclipse.emf.ecore.util.EcoreUtil;
// import org.eclipse.uml2.uml.Element;
// import org.eclipse.uml2.uml.UMLPackage;
// import org.eclipse.uml2.uml.resource.UMLResource;
// import org.eclipse.uml2.uml.util.UMLUtil;


// import org.eclipse.emf.common.util.*;

public class MultiViewSafeComposition {
	
	
	/*
	 * Code example on how to read a UML file
	 * Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put(
UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE);

URI fileURI =URI.createFileURI(new
File("model/bpel.uml").getAbsolutePath());
org.eclipse.uml2.uml.Package umlpackage=load(fileURI);

the code of load method:
protected static org.eclipse.uml2.uml.Package load(URI uri) {
org.eclipse.uml2.uml.Package package_ = null;

try {
	Resource resource = RESOURCE_SET.getResource(uri, true);

	package_ = (org.eclipse.uml2.uml.Package) EcoreUtil
				.getObjectByType(resource.getContents(),
					UMLPackage.Literals.PACKAGE);
		} catch (WrappedException we) {
			err(we.getMessage());
			System.exit(1);
		}

	return package_;
	}
	 * 
	 */
	
	/*
	 protected static org.eclipse.uml2.uml.Package load(URI uri) {
		org.eclipse.uml2.uml.Package package_ = null;

		try {
			Resource resource = RESOURCE_SET.getResource(uri, true);

			package_ = (org.eclipse.uml2.uml.Package) EcoreUtil
				.getObjectByType(resource.getContents(),
					UMLPackage.Literals.PACKAGE);
		} catch (WrappedException we) {
			err(we.getMessage());
			System.exit(1);
		}

		return package_;
	}
	 */
	
	// ***************************************************************************************
	// ***************************************************************************************
	// ****** Copied from Getting started with UML	

	protected static Model createModel(String name) {
		Model model = UMLFactory.eINSTANCE.createModel();
		model.setName(name);

		System.out.println("Model '" + model.getQualifiedName() + "' created.");
		return model;
	}

	protected static org.eclipse.uml2.uml.Package createPackage(org.eclipse.uml2.uml.Package nestingPackage, String name) {
		org.eclipse.uml2.uml.Package package_ = nestingPackage.createNestedPackage(name);

		System.out.println("Package '" + package_.getQualifiedName() + "' created.");
		return package_;
	}

	protected static PrimitiveType createPrimitiveType(org.eclipse.uml2.uml.Package package_, String name) {
		PrimitiveType primitiveType = (PrimitiveType) package_.createOwnedPrimitiveType(name);

		System.out.println("Primitive type '" + primitiveType.getQualifiedName() + "' created.");
		return primitiveType;
	}

	protected static Enumeration createEnumeration(org.eclipse.uml2.uml.Package package_, String name) {
		Enumeration enumeration = (Enumeration) package_.createOwnedEnumeration(name);

		System.out.println("Enumeration '" + enumeration.getQualifiedName() + "' created.");
		return enumeration;
	}

	protected static EnumerationLiteral createEnumerationLiteral(Enumeration enumeration, String name) {
		EnumerationLiteral enumerationLiteral = enumeration.createOwnedLiteral(name);

		System.out.println("Enumeration literal '" + enumerationLiteral.getQualifiedName() + "' created.");
		return enumerationLiteral;
	}

	protected static org.eclipse.uml2.uml.Class createClass(org.eclipse.uml2.uml.Package package_, String name, boolean isAbstract) {
		org.eclipse.uml2.uml.Class class_ = package_.createOwnedClass(name, isAbstract);

		System.out.println("Class '" + class_.getQualifiedName() + "' created.");
		return class_;
	}

	protected static Generalization createGeneralization(Classifier specificClassifier, Classifier generalClassifier) {
		Generalization generalization = specificClassifier.createGeneralization(generalClassifier);

		System.out.println("Generalization " + specificClassifier.getQualifiedName() + " ->> " + generalClassifier.getQualifiedName() + " created.");
		return generalization;
	}

	protected static Property createAttribute(org.eclipse.uml2.uml.Class class_, String name, Type type, int lowerBound, int upperBound) {
		Property attribute = class_.createOwnedAttribute(name, type, lowerBound, upperBound);

		StringBuffer sb = new StringBuffer();
		sb.append("Attribute '");
		sb.append(attribute.getQualifiedName());
		sb.append("' : ");
		sb.append(type.getQualifiedName());
		sb.append(" [");
		sb.append(lowerBound);
		sb.append("..");
		sb.append(LiteralUnlimitedNatural.UNLIMITED == upperBound
			? "*"
			: String.valueOf(upperBound));
		sb.append("]");
		sb.append(" created.");

		System.out.println(sb.toString());
		return attribute;
	}

	protected static Association createAssociation(Type type1,
			boolean end1IsNavigable, AggregationKind end1Aggregation,
			String end1Name, int end1LowerBound, int end1UpperBound,
			Type type2, boolean end2IsNavigable,
			AggregationKind end2Aggregation, String end2Name,
			int end2LowerBound, int end2UpperBound) {

		Association association = type1.createAssociation(end1IsNavigable,
			end1Aggregation, end1Name, end1LowerBound, end1UpperBound, type2,
			end2IsNavigable, end2Aggregation, end2Name, end2LowerBound,
			end2UpperBound);

		StringBuffer sb = new StringBuffer();

		sb.append("Association ");

		if (null == end1Name || 0 == end1Name.length()) {
			sb.append('{');
			sb.append(type1.getQualifiedName());
			sb.append('}');
		} else {
			sb.append("'");
			sb.append(type1.getQualifiedName());
			sb.append(NamedElement.SEPARATOR);
			sb.append(end1Name);
			sb.append("'");
		}

		sb.append(" [");
		sb.append(end1LowerBound);
		sb.append("..");
		sb.append(LiteralUnlimitedNatural.UNLIMITED == end1UpperBound
			? "*"
			: String.valueOf(end1UpperBound));
		sb.append("] ");

		sb.append(end2IsNavigable
			? '<'
			: '-');
		sb.append('-');
		sb.append(end1IsNavigable
			? '>'
			: '-');
		sb.append(' ');

		if (null == end2Name || 0 == end2Name.length()) {
			sb.append('{');
			sb.append(type2.getQualifiedName());
			sb.append('}');
		} else {
			sb.append("'");
			sb.append(type2.getQualifiedName());
			sb.append(NamedElement.SEPARATOR);
			sb.append(end2Name);
			sb.append("'");
		}

		sb.append(" [");
		sb.append(end2LowerBound);
		sb.append("..");
		sb.append(LiteralUnlimitedNatural.UNLIMITED == end2UpperBound
			? "*"
			: String.valueOf(end2UpperBound));
		sb.append("]");

		sb.append(" created.");

		System.out.println(sb.toString());
		return association;
	}
	
	
	// ***************************************************************************************
	// ***************************************************************************************
	
	public static String uriName ="";
	/*
	 * Creates a UML model based on the Eclipse tutorial example
	 */
	public static void createUMLModel() {

//		if (1 != args.length) {
//			err("Usage: java GettingStartedWithUML2 <URI>");
//			System.exit(1);
//		}

		File sourceFile = new File("./src/at/jku/sea/mvsc/TutorialExample.uml");
		uriName = sourceFile.getAbsolutePath();
//		System.out.println("Absolute Path " + sourceFile.getAbsolutePath());
//		URI umlModel = URI.createFileURI(sourceFile.getAbsolutePath());
		
		registerResourceFactories();

		System.out.println("Creating model...");

		Model epo2Model = createModel("epo2");

		System.out.println("Creating primitive types...");

		PrimitiveType intPrimitiveType = createPrimitiveType(epo2Model, "int");
		PrimitiveType stringPrimitiveType = createPrimitiveType(epo2Model, "String");
		PrimitiveType datePrimitiveType = createPrimitiveType(epo2Model, "Date");
		PrimitiveType skuPrimitiveType = createPrimitiveType(epo2Model, "SKU");

		System.out.println("Creating enumerations...");

		Enumeration orderStatusEnumeration = createEnumeration(epo2Model, "OrderStatus");

		System.out.println("Creating enumeration literals...");

		createEnumerationLiteral(orderStatusEnumeration, "Pending");
		createEnumerationLiteral(orderStatusEnumeration, "BackOrder");
		createEnumerationLiteral(orderStatusEnumeration, "Complete");

		System.out.println("Creating classes...");

		org.eclipse.uml2.uml.Class supplierClass = createClass(epo2Model, "Supplier", false);
		org.eclipse.uml2.uml.Class customerClass = createClass(epo2Model, "Customer", false);
		org.eclipse.uml2.uml.Class purchaseOrderClass = createClass(epo2Model, "PurchaseOrder", false);
		org.eclipse.uml2.uml.Class itemClass = createClass(epo2Model, "Item", false);
		org.eclipse.uml2.uml.Class addressClass = createClass(epo2Model, "Address", true);
		org.eclipse.uml2.uml.Class usAddressClass = createClass(epo2Model, "USAddress", false);
		org.eclipse.uml2.uml.Class globalAddressClass = createClass(epo2Model, "GlobalAddress", false);
		org.eclipse.uml2.uml.Class globalLocationClass = createClass(epo2Model, "GlobalLocation", false);

		System.out.println("Creating generalizations...");

		createGeneralization(usAddressClass, addressClass);
		createGeneralization(globalAddressClass, addressClass);
		createGeneralization(globalAddressClass, globalLocationClass);

		System.out.println("Creating attributes...");

		createAttribute(supplierClass, "name", stringPrimitiveType, 0, 1);
		createAttribute(customerClass, "customerID", intPrimitiveType, 0, 1);
		createAttribute(purchaseOrderClass, "comment", stringPrimitiveType, 0, 1);
		createAttribute(purchaseOrderClass, "orderDate", datePrimitiveType, 0, 1);
		createAttribute(purchaseOrderClass, "status", orderStatusEnumeration, 0, 1);
		createAttribute(purchaseOrderClass, "totalAmount", intPrimitiveType, 0, 1);
		createAttribute(itemClass, "productName", stringPrimitiveType, 0, 1);
		createAttribute(itemClass, "quantity", intPrimitiveType, 0, 1);
		createAttribute(itemClass, "USPrice", intPrimitiveType, 0, 1);
		createAttribute(itemClass, "comment", stringPrimitiveType, 0, 1);
		createAttribute(itemClass, "shipDate", datePrimitiveType, 0, 1);
		createAttribute(itemClass, "partNum", skuPrimitiveType, 0, 1);
		createAttribute(addressClass, "name", stringPrimitiveType, 0, 1);
		createAttribute(addressClass, "country", stringPrimitiveType, 0, 1);
		createAttribute(usAddressClass, "street", stringPrimitiveType, 0, 1);
		createAttribute(usAddressClass, "city", stringPrimitiveType, 0, 1);
		createAttribute(usAddressClass, "state", stringPrimitiveType, 0, 1);
		createAttribute(usAddressClass, "zip", intPrimitiveType, 0, 1);
		createAttribute(globalAddressClass, "location", stringPrimitiveType, 0, LiteralUnlimitedNatural.UNLIMITED);
		createAttribute(globalLocationClass, "countryCode", intPrimitiveType, 0, 1);

		System.out.println("Creating associations...");

		createAssociation(supplierClass, true, AggregationKind.COMPOSITE_LITERAL, "orders", 0,
			LiteralUnlimitedNatural.UNLIMITED, purchaseOrderClass, false, AggregationKind.NONE_LITERAL, "", 1, 1);

		createAssociation(supplierClass, true, AggregationKind.NONE_LITERAL, "pendingOrders", 0, LiteralUnlimitedNatural.UNLIMITED,
			purchaseOrderClass, false, AggregationKind.NONE_LITERAL, "", 0, 1);

		createAssociation(supplierClass, true, AggregationKind.NONE_LITERAL, "shippedOrders", 0, LiteralUnlimitedNatural.UNLIMITED,
			purchaseOrderClass, false, AggregationKind.NONE_LITERAL, "", 0, 1);

		createAssociation(supplierClass, true, AggregationKind.COMPOSITE_LITERAL, "customers", 0, LiteralUnlimitedNatural.UNLIMITED, customerClass, false,
			AggregationKind.NONE_LITERAL, "", 1, 1);

		createAssociation(customerClass, true, AggregationKind.NONE_LITERAL, "orders", 0, LiteralUnlimitedNatural.UNLIMITED, purchaseOrderClass,
			true, AggregationKind.NONE_LITERAL, "customer", 1, 1);

		createAssociation(purchaseOrderClass, true, AggregationKind.NONE_LITERAL, "previousOrder", 0, 1,
			purchaseOrderClass, false, AggregationKind.NONE_LITERAL, "", 0, 1);

		createAssociation(purchaseOrderClass, true, AggregationKind.COMPOSITE_LITERAL, "items", 0,
			LiteralUnlimitedNatural.UNLIMITED, itemClass, true, AggregationKind.NONE_LITERAL, "order", 1, 1);

		createAssociation(purchaseOrderClass, true, AggregationKind.COMPOSITE_LITERAL, "billTo", 1, 1, addressClass,
			false, AggregationKind.NONE_LITERAL, "", 1, 1);

		createAssociation(purchaseOrderClass, true, AggregationKind.COMPOSITE_LITERAL, "shipTo", 0, 1, addressClass,
			false, AggregationKind.NONE_LITERAL, "", 1, 1);

		System.out.println("Saving model...");

		save(epo2Model, URI.createURI(uriName).appendSegment("ExtendedPO2").appendFileExtension(UMLResource.FILE_EXTENSION));
	
	}

	// ***************************************************************************************
	// ***************************************************************************************
	// ****** Copied from UML2Article
	protected static final ResourceSet RESOURCE_SET = new ResourceSetImpl();
	
	/**
	 * Registers the resource factory. Part of the process to load UML files.
	 */
	protected static void registerResourceFactories() {
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put(
			UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE);
		System.out.println("UML Extension " + UMLResource.FILE_EXTENSION);
		
	}
	
	protected static void registerPathmaps(URI uri) {
		URIConverter.URI_MAP.put(URI.createURI(UMLResource.LIBRARIES_PATHMAP),
			uri.appendSegment("libraries").appendSegment(""));

		URIConverter.URI_MAP.put(URI.createURI(UMLResource.METAMODELS_PATHMAP),
			uri.appendSegment("metamodels").appendSegment(""));

		URIConverter.URI_MAP.put(URI.createURI(UMLResource.PROFILES_PATHMAP),
			uri.appendSegment("profiles").appendSegment(""));
	}
	
	/* Create an instance of UMLPackage and register it
	 * EPackage var = UMLPackage.Instance
	 * resourceSet.getPackageRegustry.put( var.getNsPrefix(), var);
	 */

	/*
	 *  Saves the package file
	 */
	protected static void save(org.eclipse.uml2.uml.Package package_, URI uri) {
		Resource resource = RESOURCE_SET.createResource(uri);
		
		System.out.println("URI " + uri);
		System.out.println("saving resource " + resource);
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

			System.out.println("Done.");
		} catch (IOException ioe) {
			System.out.println("IO Exception" + ioe.getMessage());
		}
	}


	/**
	 * Loads a uml file and returns a package reference. 
	 */
	protected static org.eclipse.uml2.uml.Package load(URI uri) {
		org.eclipse.uml2.uml.Package package_ = null;

		try {
			Resource resource = RESOURCE_SET.getResource(uri, true);
			// Resource resource = new ResourceSetImpl().getResource(uri, true);
			EList<EObject> cont = resource.getContents();
			System.out.println("Contents " + cont.size());
			System.out.println("element 0 " + cont.get(0)); 
			System.out.println("Class element 0 " + cont.get(0).eClass());
			System.out.println("Class element 0 name " + cont.get(0).eClass().getName());
			
			System.out.println("Class element 0 name " + cont.get(0).getClass().getName());
			// System.out.println("Looking for classes " + EcoreUtil.getObjectsByType(cont, UMLPackage.Literals.PACKAGE));
			package_ = (org.eclipse.uml2.uml.Package) EcoreUtil.getObjectByType(resource.getContents(), UMLPackage.Literals.PACKAGE);
			
			// UMLUtil.Ecore2UMLConverter converter = new UMLUtil.Ecore2UMLConverter();
			// System.out.println("converting " + converter.caseEClass(cont.get(0).eClass()));
			
			// Creates a package, make it a reference
			// package_ = UMLFactory.eINSTANCE.createPackage();
			
			// package_ = (org.eclipse.uml2.uml.Package) EcoreUtil.getObjectByType(cont, UMLPackage.Literals.MODEL);
			
			// package_ = (org.eclipse.uml2.uml.Package) cont.get(0);
			// package_ = (org.eclipse.uml2.uml.Package) EcoreUtil.getObjectByType(resource.getContents(), package_);
				} catch (WrappedException we) {
					System.out.println(we.getMessage());
					System.exit(1);
				} catch (Exception e) {
					System.out.println("Other exception " + e.getMessage());
				}

		return package_;
	}

	// ***************************************************************************************
	// ***************************************************************************************
	// ***************************************************************************************
	// ***************************************************************************************
	
	
	/* Test of model manipulation
	 */
	public void modelManagement() {
		
		// Does the set up of the model management
		registerResourceFactories(); // Sets up the resources
		
		// Sets in the package registry an instance of a UML package
		RESOURCE_SET.getPackageRegistry().put(UMLPackage.eNS_URI, UMLPackage.eINSTANCE);
				
		// Model model = UMLFactory.eINSTANCE.createModel();
		// URI umlModel =  URI.createFileURI(new File("./src/at/jku/sea/mvsc/FeatureA.uml").getAbsolutePath());
		File sourceFile = new File("./src/at/jku/sea/mvsc/FeatureA.uml");
		System.out.println("Absolute Path " + sourceFile.getAbsolutePath());
		URI umlModel = URI.createFileURI(sourceFile.getAbsolutePath());
		
		// Registers the path maps  ??? Seems to have no effect
		registerPathmaps(umlModel);
		
		System.out.println("File? " + umlModel.isFile());
		// URI umlModel =  URI.createFileURI(new File("platform:/resource/MultiViewSC/src/at/jku/sea/mvsc/FeatureA.uml").getAbsolutePath());
		//		org.eclipse.uml2.uml.Package umlpackage=load(fileURI);
		Package p = load (umlModel);
		
		
		System.out.println("P " + p );
		if (p!=null) {
			System.out.println("File loaded correctly "); 
			System.out.println("package name " + p.getName());
			System.out.println("the contents of the package " + p);
			for (PackageableElement pel : p.getPackagedElements()) {
				System.out.println("packaged element name " + pel.getName() + " and type " + pel.getClass());
				if (pel instanceof org.eclipse.uml2.uml.Class) System.out.println("Class found");
			}
		} else { 
			System.out.println("The contents is null");
		}
		// UMLFactory.eINSTANCE.createModel();
		
		// Testing loading the package with the generic load method
		System.out.println("\n\n Testing generic package loading ");
		try {
		p = DiagramManager.loadPackage("./Features/F");
		if (p!=null) {
			System.out.println("File loaded correctly "); 
			System.out.println("package name " + p.getName());
			System.out.println("the contents of the package " + p);
			for (PackageableElement pel : p.getPackagedElements()) {
				System.out.println("packaged element name " + pel.getName() + " and type " + pel.getClass());
				if (pel instanceof org.eclipse.uml2.uml.Class) System.out.println("Class found");
			}
		} else { 
			System.out.println("The contents is null");
		}
		} catch(Exception e) {
			System.out.println("Error " + e.getMessage());
		}
		
		
		// Testing loadin state machines
		System.out.println("\n\n Testing loading State Machines ");
		try {
		List <StateMachine> listStateMachines = DiagramManager.loadStateMachines("./Features/F");
		for(StateMachine sm : listStateMachines) {
			System.out.println("State machine name " + sm.getName());
		}
		System.out.println("loading SM done");
		} catch(Exception e) {
			System.out.println("Error in loading " + e.getMessage());
		}
		
		// Testing creation of Features and displaying of association details
		System.out.println("\n\n Testing Features and associations");
		// Feature f = new Feature("F","./Features/F");
		// Feature g = new Feature("G","./Features/G");
		
		SCAssociationEndNamesMustBeUniqueWithinAssociation constraint1 = new SCAssociationEndNamesMustBeUniqueWithinAssociation();
		List<Feature> featuresList = new LinkedList<Feature>();
		// featuresList.add(f);
		// featuresList.add(g);
		
		// For now eliminate this call
		// @debug
		// constraint1.apply(featuresList);
		
	} // of modelManagement
	
	
	public static void SATManager() {
		Picosat4J picosat4J = Picosat4J.getInstance();
		picosat4J.addClause(1, 2, 3, -4);
		picosat4J.addClause(-1,-2);
		picosat4J.addClause(new LiteralClause(-1, -3));
//		picosat4J.addClause(-1, -3);
		picosat4J.addClause(-1, 4);
		picosat4J.addClause(-2, -3);
		picosat4J.addClause(-2, 4);     // added now through the list
		// picosat4J.addClause(-3, 4);  // added now in t1
		picosat4J.printStatistics();
		
		// Checking that it can receive arrays of integers as parameters
		int[] t1 = {-3,4};
		picosat4J.addClause(t1);
		List<Integer> t2 = new LinkedList<Integer>();
		t2.add(-2);
		t2.add(4);
		int[] t3 = new int[t2.size()]; 
		for(int i=0; i< t2.size(); i++) t3[i]= t2.get(i);
		picosat4J.addClause(t3);
		System.out.println("Computing satisfiability");
		while (picosat4J.isSatisifable(4)) {
			int[] assignments = picosat4J.next();
			System.out.println(Arrays.toString(assignments));
		}
		System.out.println("Done");
		int[] failedAssumptions = picosat4J.getFailedAssumptions();
		System.out.println(Arrays.toString(failedAssumptions));
		System.out.println("finished");
	
		// Clears the clauses
		picosat4J.clearClauses();	
	}

	
	/**
	 * This method creates GPL and performs consistency analysis
	 */
	public static void verifyGPL() {
		
		// Set up for loading UML files
		DiagramManager.registerResourceFactories();
		
		// Sets in the package registry an instance of a UML package
		DiagramManager.setPackageRegistry();

		
		// RESOURCE_SET.getPackageRegistry().put(UMLPackage.eNS_URI, UMLPackage.eINSTANCE);
				
		// Model model = UMLFactory.eINSTANCE.createModel();
		// URI umlModel =  URI.createFileURI(new File("./src/at/jku/sea/mvsc/FeatureA.uml").getAbsolutePath());
		/*
		File sourceFile = new File("./src/at/jku/sea/mvsc/FeatureA.uml");
		System.out.println("Absolute Path " + sourceFile.getAbsolutePath());
		URI umlModel = URI.createFileURI(sourceFile.getAbsolutePath());
		
		// Registers the path maps  ??? Seems to have no effect // one for each file read?
		registerPathmaps(umlModel);
		*/
		
		// Create the SPL reference (SPL name, directory, root feature)
		//@TEMP We leave this example for now
		/*
		GPL gpl = new GPL("GPL-SPL","./GPL","GPL");
		gpl.setUpConstraints();		
		System.out.println(gpl);
		*/
		
		// Create the SPL for VPL
		// @debug
		System.out.println("\n\n VPL Product Line");
		SPL vpl = new VPL("VPL-SPL","./VPL","VOD");
		vpl.setUpConstraints();		
		System.out.println(vpl);
		
		// @debug
		System.out.println("Testing Rule 3");
		SCAssociationEndNamesMustBeUniqueWithinAssociation rule3 = new SCAssociationEndNamesMustBeUniqueWithinAssociation();
		vpl.computeCompatibleFeatures(); 
		rule3.apply(vpl);
		
		System.out.println("\n\nTesting Rule 4");
		SCAtMostOneAssociationEndMayBeAggregationOrComposition rule4 = new SCAtMostOneAssociationEndMayBeAggregationOrComposition();
		rule4.apply(vpl);
		
		// closes the analysis file
		vpl.closeAnalysisResult();
		
	} // of verifyGPL

	
	
	/** Checks consistency rules in VODSPL product line*/
	public static void verifyVODSPL() {
		
		// Set up for loading UML files
		DiagramManager.registerResourceFactories();
		
		// Sets in the package registry an instance of a UML package
		DiagramManager.setPackageRegistry();
		
		// Create the SPL for VPL
		// @debug
		System.out.println("\n\n Video on Demand SPL Product Line");
		SPL vodspl = new VODSPL("VOD-SPL","./VODSPL","VOD");
		
		//@log
		vodspl.writeLogFile("VODSPL read");
		
		vodspl.setUpConstraints();
		
		//@log
		vodspl.writeLogFile(vodspl.toString());
		
		//@log
		/*
		vodspl.writeLogFile("Testing Rule 3");
		SCAssociationEndNamesMustBeUniqueWithinAssociation rule3 = new SCAssociationEndNamesMustBeUniqueWithinAssociation();
		// vodspl.computeCompatibleFeatures(); 
		rule3.apply(vodspl);
		*/
		
		System.out.println("\n\nTesting Rule 4");
		SCAtMostOneAssociationEndMayBeAggregationOrComposition rule4 = new SCAtMostOneAssociationEndMayBeAggregationOrComposition();
		rule4.apply(vodspl);
	
		/*
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setText("SPL Rule Viewer");
		shell.setBounds(100, 100, 175, 125);
		shell.setLayout(new FillLayout());
		TabFolder tabFolder = new TabFolder(shell, SWT.BORDER);
		TabItem tabItem = new TabItem(tabFolder,SWT.NULL);
		tabItem.setText("Rule 4");
		Composite composite = new Composite (tabFolder, SWT.NULL);
		rule4.computeRuleInstances(vodspl, composite, shell, display); // ).open();
		tabItem.setControl(rule4.computeRuleInstances(vodspl, composite, shell, display).getTable());
		// Table newTable = new Table(composite, SWT.SINGLE | SWT.FULL_SELECTION);
		
		// Table newTable = rule4.computeRuleInstances(vodspl, composite, shell, display).getTable();
		shell.open();
		while(!shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
		display.dispose();
		// tabItem.setControl(table);
		*/
		
		// TabTableViewer ttv = new TabTableViewer((rule4.computeRuleInstances(vodspl)).getTable());
		// ttv.open();
		
		// @debugging
		System.out.println("\n\n Testing Rule 5");
		SCMessageActionInReceiversClassMethods rule5 = new SCMessageActionInReceiversClassMethods();
		rule5.apply(vodspl);
		
		
		
		System.out.println("\n\n Testing Rule 6");
		SCStatechartActionInOwnerClassMethod rule6 =  new SCStatechartActionInOwnerClassMethod();
		rule6.apply(vodspl);
		
		System.out.println("\n\n Testing Rule 7");
		SCMessageDirectionInClassAssociation rule7 =  new SCMessageDirectionInClassAssociation();
		rule7.apply(vodspl);
		
		// closes the analysis file
		vodspl.closeAnalysisResult();
		vodspl.closeLogFile();
	}
	
	
	/**
	 * Tests and computes the statistics of a splot file
	 * @param splotFile
	 */
	public void testCase(String splotFile, String name) {
		// Loading files from SPLOT repository
		// System.out.println("\nParsing SPLOT Sample File");
		
		// parserFile.parse("./SPLOT/REAL-FM-5.xml");
		
		System.out.println("Creating feature model ");
		XMLFeatureModelParserSample parserFile = new XMLFeatureModelParserSample();
		SPL splotGPL = new SPLOT(name);
		parserFile.buildsFeatureModel(splotGPL, splotFile);
		
		System.out.println("Feature model read ");
		System.out.println(splotGPL);
		
		System.out.println("Computing compatible features");
		long startTime = System.currentTimeMillis();
		splotGPL.computeCompatibleFeatures(); 
		long endTime = System.currentTimeMillis();
		long elapsedTime = endTime - startTime;
		System.out.println(splotGPL.toStringCompatibleFeatures());
		System.out.println("Elapsed computation time (msec)" + elapsedTime);
	}
	
	
	
	/**
	 * Tests the load and Safe Composition on the GPL case study
	 */
	public void testGPL(String SPLName, String directoryName) {
		String splotFile = directoryName + "/feature-model.xml";
		
		//@debug
		System.out.println("Creating feature model " + splotFile);
		
		XMLFeatureModelParserSample parserFile = new XMLFeatureModelParserSample();
		SPL splotGPL = new SPLOT(SPLName);
		parserFile.buildsFeatureModel(splotGPL, splotFile);
		
		splotGPL.setDir(directoryName);
		
		// @debug
		System.out.println("Feature model read ");
		System.out.println(splotGPL);
		
		// Creates the analysis file
		splotGPL.createAnalysisFile();
		
		// Creates the log file
		splotGPL.createLogFile();
		
	
		// Set up for loading UML files
		DiagramManager.registerResourceFactories();
		
		// Sets in the package registry an instance of a UML package
		DiagramManager.setPackageRegistry();
		
		
		// loads and creates the lists of features
		splotGPL.loadFeatures(directoryName, SPLName);
		
		// computes the compatible features
		splotGPL.computeCompatibleFeatures();
		
		
		// @debug
		System.out.println("Loading diagrams from features Done!");
		
		
		// @debug
		System.out.println("Status New - Feature model");
		System.out.println(splotGPL);
		
		// @debug
		System.out.println("Creation of Constraint Rule Managers");
		
		
		SCAssociationEndNamesMustBeUniqueWithinAssociation rule3 = new SCAssociationEndNamesMustBeUniqueWithinAssociation();
		SCAtMostOneAssociationEndMayBeAggregationOrComposition rule4 = new SCAtMostOneAssociationEndMayBeAggregationOrComposition();
		SCMessageActionInReceiversClassMethods rule5 = new SCMessageActionInReceiversClassMethods();
		SCStatechartActionInOwnerClassMethod rule6 =  new SCStatechartActionInOwnerClassMethod();
		SCMessageDirectionInClassAssociation rule7 =  new SCMessageDirectionInClassAssociation();
	
		//  @debug
		System.out.println("Applying rules ");
		
		rule3.apply(splotGPL);
		
		rule4.apply(splotGPL);
		rule5.apply(splotGPL);
		rule6.apply(splotGPL);
		rule7.apply(splotGPL);
		
		// closes the analysis file
		splotGPL.closeAnalysisResult();
		splotGPL.closeLogFile();
		
		// @debug
		System.out.println("Constraint Checking Done!");
		
		// Rule application
		
		// Create the SPL for VPL
		// @debug
		/*
		System.out.println("\n\n Video on Demand SPL Product Line");
		SPL vodspl = new VODSPL("VOD-SPL","./VODSPL","VOD");
		
		//@log
		vodspl.writeLogFile("VODSPL read");
		
		vodspl.setUpConstraints();
		
		//@log
		vodspl.writeLogFile(vodspl.toString());
		*/
		
		//@log
		/*
		vodspl.writeLogFile("Testing Rule 3");
		SCAssociationEndNamesMustBeUniqueWithinAssociation rule3 = new SCAssociationEndNamesMustBeUniqueWithinAssociation();
		// vodspl.computeCompatibleFeatures(); 
		rule3.apply(vodspl);
		*/
		/*
		System.out.println("\n\nTesting Rule 4");
		SCAtMostOneAssociationEndMayBeAggregationOrComposition rule4 = new SCAtMostOneAssociationEndMayBeAggregationOrComposition();
		rule4.apply(vodspl);
	
		
		
		// @debugging
		System.out.println("\n\n Testing Rule 5");
		SCMessageActionInReceiversClassMethods rule5 = new SCMessageActionInReceiversClassMethods();
		rule5.apply(vodspl);
		
		
		
		System.out.println("\n\n Testing Rule 6");
		SCStatechartActionInOwnerClassMethod rule6 =  new SCStatechartActionInOwnerClassMethod();
		rule6.apply(vodspl);
		
		System.out.println("\n\n Testing Rule 7");
		SCMessageDirectionInClassAssociation rule7 =  new SCMessageDirectionInClassAssociation();
		rule7.apply(vodspl);
		
		// closes the analysis file
		vodspl.closeAnalysisResult();
		vodspl.closeLogFile();
		
		*/
		
		
		
	}
	
	
	// ************************************************
	// Testing method
	// Copied from OCL translator project
public static void readPackageFromFile() {
		
		// The reference to the load package diagram
		Package classDiagramPackage = null;
	
		// Set up for loading UML files
		DiagramManager.registerResourceFactories();
		
		// Sets in the package registry an instance of a UML package
		DiagramManager.setPackageRegistry();
	
		File file = new File("GPLSPLOT/Connected/Connected.uml");
		
		//@debug
		System.out.println("Testing file " +  file.getAbsolutePath() + " " + file.exists());
		
		// /EList<EObject> contents = null;
		
		try {
			classDiagramPackage = DiagramManager.loadPackage(file.getAbsolutePath());
			
			//@debug content of class diagram package
			System.out.println("Class Diagram " + classDiagramPackage);
			
		} catch (Exception e) {
			// @debug
			System.out.println("Problems loading the package ");
			e.printStackTrace();
		}
		
	}

	
	/**
	 * Main method.
	 * @param args Non utilized so far.
	 */
	public static void main(String[] args) {
		
		// Testing the sat manager
		// SATManager();
		
		// Tests loading a file
		
		/*
		System.out.println("Attempting to load UML file");
		MultiViewSafeComposition obj = new MultiViewSafeComposition();
		obj.modelManagement();
		*/
		
		// Analysis and creation of GPL
		// verifyGPL();
		
		MultiViewSafeComposition tester = new MultiViewSafeComposition();
		// tester.testCase("./SPLOT/REAL-FM-5.xml", "Graph Product Line");
		
		// Other test case
		// tester.testCase("./SPLOT/REAL-FM-4.xml", "E-Shop");
		
		// tester.testCase("./SPLOT/SPLOT-3CNF-FM-10000-1000-0.10-SAT-1.xml", "Random10000");
		
		// tester.testCase("./SPLOT/SPLOT-3CNF-FM-500-50-1.00-SAT-1.xml", "Random500");
		
		// tester.testCase("./SPLOT/SPLOT-3CNF-FM-1000-100-1.00-SAT-1.xml", "Random1000");
		
		// This test works so fat
		// tests the tab generated table
		// TabTableViewer tabViewer = new TabTableViewer();
		// tabViewer.open();
		
		// Test loading GPL case study
		tester.testGPL("GPL-SPLOT","./GPLSPLOT");
		
		// another example
		
		// Calls the SPL rules
		// verifyVODSPL();
		
		
		// Testing reading of independent uml files
		// readPackageFromFile();
		
		System.out.println("Done!");
		
	} // of main

	
	
	

}
