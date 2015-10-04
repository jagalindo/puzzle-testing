package at.jku.sea.mvsc.fixing.simulation;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import es.us.isa.FAMA.Reasoner.Question;
import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.Reasoner.questions.NumberOfProductsQuestion;
import es.us.isa.FAMA.Reasoner.questions.ProductsQuestion;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.GenericFeatureModel;
import es.us.isa.FAMA.models.featureModel.Product;

import at.jku.sea.mvsc.FAMAFeatureModelParser;
import at.jku.sea.mvsc.SPL;
import at.jku.sea.mvsc.SPLOT2FAMA;
import at.jku.sea.mvsc.XMLFeatureModelParserSample;
import at.jku.sea.mvsc.examples.SPLOT;


/**
 * This class drives the simulation of the heuristics for fixing the inconsistencies.
 * @author Roberto
 *
 */
public class Main {

	
	// Simple test for basic functionality
	public void testOne() {
			
	String targetDirectory = "pairwise-comm";
		
		
		//	targetDirectory.concat(File.separator).concat("src/at/jku/sea/mvsc/fixing/simulation/pairwise-comm/FAMA-smart_home_fm.xml");
		// targetFileName = targetFileName.substring(0, targetFileName.lastIndexOf(".")) + ".csv";
		
		/* Paths that did not work
		 ./src/at/jku/sea/mvsc/fixing/simulation/pairwise-comm/  --> but this one was able to open the whole file
		 at/jku/sea/mvsc/fixing/simulation/fama-models/
		 
		 Read the file but still not working
		 "./src/at/jku/sea/mvsc/fixing/simulation/fama-models/FAMA-smart_home_fm.xml"
		 
		 */
		
		FAMAFeatureModelAnalysis fma = new FAMAFeatureModelAnalysis();
		String targetFileName= "pairwise-comm/FAMA-smart_home_fm.csv";
		File file = new File("fama-models/FAMA-smart_home_fm.xml");
		
		// @debug 
		System.out.println("File name " + file.getAbsolutePath() + " " + file.exists() + file.canRead());
		System.out.println("Target name " + targetFileName);
		
		fma.pairWiseCompatibility(file, targetFileName);
		
		// Test deserialize
		PairwiseCommonality pwc = 
				fma.deserializationPairwiseCommonality("pairwise-comm/FAMA-smart_home_fm.pwc");
		System.out.println("Deserialization --> Features " + pwc.numberOfFeatures + " products " + pwc.numberOfProducts);
		Map<IndexMatrix, PairwiseCommonalityMeasurement> measurementMap =  pwc.measurementMap;
		System.out.println("" + measurementMap.size());
		
	} // of testOne
	
	
	public void testTwo() {
		FAMAFeatureModelAnalysis fma = new FAMAFeatureModelAnalysis();
		String targetDir= "pairwise-comm/";
		
		// String sourceDir = "fama-models/";
		
		String sourceDir = "fama-models-realcases/";
		fma.multiplePairWiseCompatibility(sourceDir, targetDir);
	
	}
	
	/**
	 * Tests the parsing of FAMA files and the creation of a SPL
	 */
	public void testFAMAParsing() {
		// Holds the names of the FAMA, SPLOT, pairwise commonality and csv files 
		String famaFileName, splotFileName, pwcFileName, csvFileName;
		
		famaFileName= "fama-models/FAMA-GPL.xml";
		splotFileName= 	"./GPLSPLOT/feature-model.xml";
		csvFileName= "pairwise-comm/FAMA-GPL.csv";
		pwcFileName= "pairwise-comm/FAMA-GPL.pwc";
		
		// Creates the parser to load the GPL example in SPLOT to later convert to FAMA
		
		SPLOT2FAMA splotTranslator = new SPLOT2FAMA();
		
		XMLFeatureModelParserSample splotParser = new XMLFeatureModelParserSample();
		
		SPL spl = new SPLOT("GPL");
		
		// Creates the object SPL from the SPLOT-format file
		// splotParser.buildsFeatureModel(spl, "/GPLSPLOT/feature-model.xml");
		
		// In doing the translation,  sets up the name of the feature
		splotTranslator.splot2FAMA(spl, splotFileName, famaFileName);
		
		// @debug
		System.out.print("FAMA translation done !! ");
		
		QuestionTrader qt = new QuestionTrader();
		GenericFeatureModel fm = (GenericFeatureModel) qt.openFile(famaFileName);
		qt.setVariabilityModel(fm);
		
		NumberOfProductsQuestion npq = (NumberOfProductsQuestion) qt.createQuestion("#Products");
		long startTime = System.currentTimeMillis();
		qt.ask(npq);
		long endTime = System.currentTimeMillis();
		long elapsedTime = endTime - startTime;
		System.out.println("Number of features : " + fm.getFeatures().size());
		System.out.println("Number of products : " + npq.getNumberOfProducts());
		
		
		// @debug
		System.out.println("\n\nFAMA Analysis");
		
		FAMAFeatureModelAnalysis fma = new FAMAFeatureModelAnalysis();
		String targetFileName= csvFileName;
		File file = new File(famaFileName);
		
		// @debug 
		System.out.println("File name " + file.getAbsolutePath() + " " + file.exists() + file.canRead());
		System.out.println("Target name " + targetFileName);
		
		fma.pairWiseCompatibility(file, targetFileName);
		
		// Test deserialize
		PairwiseCommonality pwc = 
				fma.deserializationPairwiseCommonality(pwcFileName);
		System.out.println("Deserialization --> Features " + pwc.numberOfFeatures + " products " + pwc.numberOfProducts);
		Map<IndexMatrix, PairwiseCommonalityMeasurement> measurementMap =  pwc.measurementMap;
		System.out.println("" + measurementMap.size());
		
		
		
		// Simulation simulation = new Simulation();
		
		// At this point spl has the feature model information and fm has the model for FAMA analysis
		
		// Loads a fama model (GPL in this example) into a SPL data structure
		FAMAFeatureModelParser fmParser = new FAMAFeatureModelParser();
		SPL famaSPL = fmParser.buildsFeatureModel("fama-models/FAMA-GPL.xml");
		
		// Here we start the computation of the minimal sets
		
	} // of FAMA parsing
	
	public void testComputationSatisfyingSets() {
		
		// Loads a fama model (GPL in this example) into a SPL data structure
		FAMAFeatureModelParser fmParser = new FAMAFeatureModelParser();
		SPL famaSPL = fmParser.buildsFeatureModel("fama-models/FAMA-GPL.xml");
		
		
	}
	
	// *** WCRE Example
	/**
	 * Tests the parsing of FAMA files and the creation of a SPL
	 */
	public void testWCREExample() {
		// Holds the names of the FAMA, SPLOT, pairwise commonality and csv files 
		String famaFileName; //, splotFileName, pwcFileName, csvFileName;
		
		famaFileName= "fama-models/FAMA-WCRE_Example.xml";
		// splotFileName= 	"./GPLSPLOT/feature-model.xml";
		// csvFileName= "pairwise-comm/FAMA-GPL.csv";
		// pwcFileName= "pairwise-comm/FAMA-GPL.pwc";
		
		// Creates the parser to load the GPL example in SPLOT to later convert to FAMA
		
		// SPLOT2FAMA splotTranslator = new SPLOT2FAMA();
		
		// XMLFeatureModelParserSample splotParser = new XMLFeatureModelParserSample();
		
		// SPL spl = new SPLOT("GPL");
		
		// Creates the object SPL from the SPLOT-format file
		// splotParser.buildsFeatureModel(spl, "/GPLSPLOT/feature-model.xml");
		
		// In doing the translation,  sets up the name of the feature
		// splotTranslator.splot2FAMA(spl, splotFileName, famaFileName);
		
		// @debug
		// System.out.print("FAMA translation done !! ");
		
		QuestionTrader qt = new QuestionTrader();
		GenericFeatureModel fm = (GenericFeatureModel) qt.openFile(famaFileName);
		qt.setVariabilityModel(fm);
		
		NumberOfProductsQuestion npq = (NumberOfProductsQuestion) qt.createQuestion("#Products");
		long startTime = System.currentTimeMillis();
		qt.ask(npq);
		long endTime = System.currentTimeMillis();
		long elapsedTime = endTime - startTime;
		System.out.println("Number of features : " + fm.getFeatures().size());
		System.out.println("Number of products : " + npq.getNumberOfProducts());
		
		
		Question question = qt.createQuestion("Products");
		qt.ask(question);
		
		ProductsQuestion products = (ProductsQuestion) question;
		
		
		
		Question q = qt.createQuestion("Products");
		qt.ask(q);
		ProductsQuestion pq = (ProductsQuestion) q;
		long imax = pq.getNumberOfProducts();
		Iterator<Product> it = pq.getAllProducts().iterator();
		int i = 0;
		while (it.hasNext()){
			i++;
			Product p = it.next();
	/*		ValidProductQuestion vpq = (ValidProductQuestion) qt.createQuestion("ValidProduct");
			vpq.setProduct(p);
			qt.ask(vpq);*/
			System.out.print("PRODUCT "+ i + " <");
			Iterator<GenericFeature> it2 = p.getFeatures().iterator();
			while (it2.hasNext()){
				System.out.print(it2.next().getName() + ", ");
			}
			System.out.println(">");
			// System.out.println("\nValid: "+vpq.isValid());
		}
		
		
	
		int counter = 1;
		// @debug
		/*System.out.println("List products " +  products.getAllProducts().size());
		for (Product product : products.getAllProducts()) {
			System.out.print("Product " + counter + " <");
			for (GenericFeature feature: product.getFeatures()) {
				System.out.print(feature.getName() + " " );
			}
			System.out.println(">");
		}*/
		
		// @debug
		System.out.println("\n\nDone!");
		
		// @debug
		// System.out.println("\n\nFAMA Analysis");
		
		// FAMAFeatureModelAnalysis fma = new FAMAFeatureModelAnalysis();
		// String targetFileName= csvFileName;
		// File file = new File(famaFileName);
		
		// @debug 
		// System.out.println("File name " + file.getAbsolutePath() + " " + file.exists() + file.canRead());
		// System.out.println("Target name " + targetFileName);
		
		// fma.pairWiseCompatibility(file, targetFileName);
		
		// Test deserialize
		// PairwiseCommonality pwc = 
		// 		fma.deserializationPairwiseCommonality(pwcFileName);
		// System.out.println("Deserialization --> Features " + pwc.numberOfFeatures + " products " + pwc.numberOfProducts);
		// Map<IndexMatrix, PairwiseCommonalityMeasurement> measurementMap =  pwc.measurementMap;
		// System.out.println("" + measurementMap.size());
		
		
		
		// Simulation simulation = new Simulation();
		
		// At this point spl has the feature model information and fm has the model for FAMA analysis
		
		// Loads a fama model (GPL in this example) into a SPL data structure
		// FAMAFeatureModelParser fmParser = new FAMAFeatureModelParser();
		// SPL famaSPL = fmParser.buildsFeatureModel("fama-models/FAMA-GPL.xml");
		
		// Here we start the computation of the minimal sets
		
	} // of FAMA parsing
	
	
	public static void main(String[] args) {
	
		Main m = new Main();
		
		// test --> working OK
		// m.testFAMAParsing();
		
		// test 
		m.testWCREExample();
		
	} // of main
	
	
}
