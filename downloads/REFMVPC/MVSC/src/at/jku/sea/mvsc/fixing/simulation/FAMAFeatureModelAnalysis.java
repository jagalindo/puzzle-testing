package at.jku.sea.mvsc.fixing.simulation;

/**
 * This program implements the feature model analysis operations necessary for the
 * Fixing inconsistencies simulation.
 */


import java.util.*;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import at.jku.sea.mvsc.FAMAFeatureModelParser;
import at.jku.sea.mvsc.Feature;
import at.jku.sea.mvsc.SPL;
import at.jku.sea.mvsc.constraints.SCUtils;
import at.jku.sea.sat.implementation.picosat.Picosat4J;
import at.jku.sea.mvsc.LogicTransformation;

//import es.us.isa.FAMA.Exceptions.FAMAException;
import es.us.isa.FAMA.Reasoner.Question;
import es.us.isa.FAMA.Reasoner.QuestionTrader;
//import es.us.isa.FAMA.Reasoner.questions.CommonalityQuestion;
//import es.us.isa.FAMA.Reasoner.questions.FilterQuestion;
import es.us.isa.FAMA.Reasoner.questions.FilterQuestion;
import es.us.isa.FAMA.Reasoner.questions.NumberOfProductsQuestion;
import es.us.isa.FAMA.Reasoner.questions.ProductsQuestion;
import es.us.isa.FAMA.Reasoner.questions.SetQuestion;
//import es.us.isa.FAMA.Reasoner.questions.SetQuestion;
import es.us.isa.FAMA.Reasoner.questions.ValidProductQuestion;
import es.us.isa.FAMA.Reasoner.questions.ValidQuestion;
//import es.us.isa.FAMA.models.FAMAfeatureModel.Feature;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.GenericFeatureModel;
import es.us.isa.FAMA.models.featureModel.Product;



public class FAMAFeatureModelAnalysis {

	public static final String NEWLINE="\n"; 
	
	/**
	 * Loads the files generated from splot to see if they parse adequately
	 */
	public void structuralCheckFM(String sourceDir) {
		//The main question trader class is instantiated 
		QuestionTrader qt = new QuestionTrader();
	
		//A feature model from an XML file is loaded 
		GenericFeatureModel fm = null;
		
		File dir = new File(sourceDir);
		
		for (File file : dir.listFiles()) {
			
			System.out.println("Checking " + file.getPath() + "<-");
			
			// Loads the feature model
			try {
				qt = new QuestionTrader();
				fm = (GenericFeatureModel) qt.openFile(file.getPath());
				qt.setVariabilityModel(fm);
			}catch(Exception e) {
				System.out.println("Faulty " + file.getPath());
				System.exit(0);
			}
		} // checks all the files in directory
		
		// @debug
		System.out.println("Structural checking  done!");
		
	} // of structuralCheckFM
	
	
	/**
	 * Batch computation of pair-wise compatibility 
	 */
	public void multiplePairWiseCompatibility(String sourceDir, String targetDirectory) {
		File dir = new File(sourceDir);
		
		for (File file : dir.listFiles()) {
			//@debug
			System.out.println("\n\n Feature model " + file.getName());
			
			
			FileWriter outputStream = null;
			String fileName=targetDirectory.concat(File.separator).concat(file.getName());
			fileName = fileName.substring(0, fileName.lastIndexOf(".")) + ".csv";
			
			
			pairWiseCompatibility(file, fileName);
		} // for all files
	} // 
	
	/**
	 * Computes the pair-wise compatibility.
	 * Receives the name of the source FAMA feature file
	 */
	public void pairWiseCompatibility(File file, String targetFile) {
		
		//The main question trader class is instantiated 
		QuestionTrader qt = new QuestionTrader();
	
		//A feature model from an XML file is loaded 
		GenericFeatureModel fm = null;
		
		// Loads the feature model
		// fm = (GenericFeatureModel) qt.openFile("fm-examples/SPLOT-REAL-FM-18.xml");
		//@debug
		System.out.println("File with the model "  + file.getPath());
		
		fm = (GenericFeatureModel) qt.openFile(file.getPath());
	
		// fm = (GenericFeatureModel) qt.openFile(file.getAbsolutePath());
		qt.setVariabilityModel(fm);
		
		// Obtains the names of the product line features
		List<String> featureNames = new LinkedList<String>();
		for(GenericFeature genf : fm.getFeatures()) { featureNames.add(genf.getName()); }		
		Collections.sort(featureNames);
	
		// Checks if the model is valid
		ValidQuestion vq = (ValidQuestion) qt.createQuestion("Valid");
		qt.ask(vq);
		
		// If the model is not valid, pair-wise commonality cannot be computed
		if (!vq.isValid()) {		
			System.out.println("Your feature model is not valid");
			return;
		}		
		
		
		/* Creates the output file for the pairwise commonality values
		 * Format is in text separated by commas
		 * File_name, number of features, number SPL products, elapsed time
		 * feature_x, feature_y, number common products, computation time
		 * ....
		 * accumulates time of computing all pairwise values
		*/
		
		FileWriter outputStream = null;
		// Creates the target file 
		try {
			outputStream = new FileWriter(targetFile);
        } catch(IOException ioe) {
        	System.out.println("Serialization error " + ioe.getMessage());
        	ioe.printStackTrace();
        	return;
		}		
		
  
		
		// Creates the number of products question for all the product line
		NumberOfProductsQuestion npq = (NumberOfProductsQuestion) qt.createQuestion("#Products");
		long startTime = System.currentTimeMillis();
		qt.ask(npq);
		long endTime = System.currentTimeMillis();
		long elapsedTime = endTime - startTime;
		System.out.println("Number of features : " + fm.getFeatures().size());
		System.out.println("Number of products : " + npq.getNumberOfProducts());
		System.out.println("Elapsed computation time (msec)" + elapsedTime);	
		
		// **********************************************************************************
		// Adds the data collected to the PairwiseCommonality
		Map<IndexMatrix, PairwiseCommonalityMeasurement> measurementMap =  
			new HashMap <IndexMatrix, PairwiseCommonalityMeasurement>();
		Map<Integer, String> mapIdToFeature = new HashMap<Integer, String>();
		Map<String, Integer> mapFeatureToId = new HashMap<String,Integer>();
		
		
		PairwiseCommonality pwc = new PairwiseCommonality(file.getAbsolutePath(),mapIdToFeature, mapFeatureToId, measurementMap);
		pwc.setNumberOfFeatures(fm.getFeatures().size());
		pwc.setNumberOfProducts(npq.getNumberOfProducts());
		pwc.setElapsedTime(elapsedTime);
		
		// Computes the mapping from ids to feature string names (serialized)
		// and features->ids (not serialized) for computing the commonality values
		int indexId = 0;
		
		for (GenericFeature feature : fm.getFeatures()) {
			mapIdToFeature.put(indexId, feature.getName());
			mapFeatureToId.put(feature.getName(), indexId);
			indexId++;
		}
		

		
		
		// ************************************************************************************
	    // Stores the header
		try{
    	outputStream.write(file.getName()+" , " + fm.getFeatures().size() + " , " + npq.getNumberOfProducts()+" , "+elapsedTime+NEWLINE);
		}catch(IOException ioe) {
        	System.out.println("Serialization error " + ioe.getMessage());
        	ioe.printStackTrace();
        	return;
		}
			
		
		// Accumulated time to compute the  
		long accumulatedTime = 0;
		long numberEntries = 0;
		
		// Does the double loop to compute the pair-wise commonality
		for (String firstName : featureNames) {
			// Eliminates the features already computed
			for (String secondName : featureNames.subList(featureNames.indexOf(firstName) + 1, featureNames.size())) {
								
				// Counting entries
				numberEntries++;
				
				// Sets the two features to filter
				FilterQuestion fq = (FilterQuestion) qt.createQuestion("Filter");
				fq.addValue(fm.searchFeatureByName(firstName), 1);
				fq.addValue(fm.searchFeatureByName(secondName), 1);
				
				// Creates a new question set
				SetQuestion sq = (SetQuestion) qt.createQuestion("Set");			
				
				NumberOfProductsQuestion  filtered_npq = (NumberOfProductsQuestion) qt.createQuestion("#Products");
				
				sq.addQuestion(fq);
				sq.addQuestion(filtered_npq);
				
				// Asks the question
				startTime = System.currentTimeMillis();
				qt.ask(sq);
				endTime = System.currentTimeMillis();
				elapsedTime = endTime - startTime;
				
				//@debug
				// System.out.println("Elapsed time " + firstName + "," + secondName + "," + filtered_npq.getNumberOfProducts() + "," +  elapsedTime );
				
				//****************************************************************************************************
				// Creates and stores the values of the measurement map
				measurementMap.put( new IndexMatrix(mapFeatureToId.get(firstName), mapFeatureToId.get(secondName)), 
									new PairwiseCommonalityMeasurement(filtered_npq.getNumberOfProducts(),elapsedTime) );
				
				
				try{
			    	outputStream.write(firstName+" , "+secondName+ " , "+ filtered_npq.getNumberOfProducts()+" , "+elapsedTime+NEWLINE);
					}catch(IOException ioe) {
			        	System.out.println("Serialization error in pair-wise commonality" + ioe.getMessage());
			        	System.out.println("Elapsed time " + firstName + "," + 
								secondName + "," + elapsedTime + "," + filtered_npq.getNumberOfProducts());
			        	ioe.printStackTrace();
			        	return;
					}
				accumulatedTime+=elapsedTime;

			} // for 
		} // for all features
		
		System.out.println("Total accumulated time (msec)" + accumulatedTime);
				
		/** @Note: Attempted to use a list iteration directly over the Collection of GenericFeature
		 * in fm.getFeatures but it is not easy to eliminate those already computed from the second iteration
		 * so for the time being it will do the computation based on the names
		 */
		
		
		// Saves the final accumulated time, flushes any file contents and close the file
		try{
			outputStream.write(accumulatedTime + "," + numberEntries + NEWLINE);
			outputStream.flush();
			outputStream.close();
		}catch(IOException ioe) {
	       	System.out.println("Serialization error at closing file " + ioe.getMessage());
	       	ioe.printStackTrace();
	       	return;
		}
	 	
		// ***********************************
		// Serializes the values of pair-wise commonality
		try
	      {
	         FileOutputStream fileOut = 
	        	 new FileOutputStream(targetFile.substring(0, targetFile.lastIndexOf(".")) + ".pwc");
	         ObjectOutputStream out = new ObjectOutputStream(fileOut);
	         out.writeObject(pwc); out.close();
	          fileOut.close();
	      }catch(IOException i) {
	          i.printStackTrace();
	      }

	} // of pairWiseCompatibility

	/*
	 * Returns the data read for PWC
	 */
	public PairwiseCommonality deserializationPairwiseCommonality(String fileName) {
		PairwiseCommonality pwc = null;
        try
        {
           FileInputStream fileIn = new FileInputStream(fileName);
           ObjectInputStream in = new ObjectInputStream(fileIn);
           pwc = (PairwiseCommonality) in.readObject();
           in.close();
           fileIn.close();
       }catch(IOException i) {
           i.printStackTrace();
           return pwc;
       }catch(ClassNotFoundException c) {
           System.out.println("PWC class not found");
           c.printStackTrace();
           return pwc;
       }

       return pwc;
	}
	
	/**
	 * Computes the CSV files for the statistics of PWC
	 */
	public void computePWCStatistics(String sourceStatisticsDir, String targetFileName) {
		
		// For reading the files in the directory
		File dir = new File(sourceStatisticsDir);
		
		// Creates three output streams one for all metrics considered now FSLength, CommonRatio, ComputationTime
		FileWriter outputFile = null;

		// Obtains the extension name
		// String targetFileName = targetStatisticsDir.concat(File.separator) + "PWCStatistics.csv";
				
		//@debug
		System.out.println("Target file name " + targetFileName);
		
		// Creates the target files
		try {
			outputFile = new FileWriter(targetFileName);
        } catch(IOException ioe) {
        	System.out.println("Cannot create PWC statistics file " + ioe.getMessage());
        	ioe.printStackTrace();
        	return;
		}		
        
 		// Adds the headers to the files
        // Stores the header
		try{
			String tableHeader = "No, File, No.Features, No.Products, Total Elapsed Time" + NEWLINE; 
			outputFile.write("General Pairwise Commonality Statistics" + NEWLINE);
			outputFile.write(tableHeader);	
		}catch(IOException ioe) {
        	System.out.println("Errors in writing statistics headers " + ioe.getMessage());
        	ioe.printStackTrace();
        	return;
		}
		
		// Traverses the list of files, filtering those that have the required file extension based on the policy 
		int count =0;
		PairwiseCommonality splPWC;
		
		for (File file : dir.listFiles(new FileFilter() {
			public boolean accept(File pathName) {
				return (pathName.getAbsolutePath()).endsWith(".pwc");
			}} )) {
			
			//@debug
			System.out.println("Recording " + file.getName());
			
			// Loads the statistics file
			splPWC = deserializationPairwiseCommonality(file.getAbsolutePath());
			
			// Writes the entry details
			try {
				outputFile.write(count + "," + file.getName() + "," + splPWC.getNumberOfFeatures() + "," +
						splPWC.getNumberOfProducts() + "," + splPWC.getTotalComputationElapsedTime()+ NEWLINE);
			} catch (IOException e) {
				System.out.println("File error at recording " + file.getName() + " " + e.getMessage());
				e.printStackTrace();
			}		
			
			// Increases entry counter
			count++;
			
		} // of all the filtered files
			
		// Saves the final accumulated time, flushes any file contents and close the file
		try{
			outputFile.flush();
			outputFile.close();
		}catch(IOException ioe) {
	       	System.out.println("Statistics Serialization error at closing file " + ioe.getMessage());
	       	ioe.printStackTrace();
	       	return;
		}
		
	} // of computeSatisfying Set statistics

	
	
	
	// ***********************************************************************
	// ********** Fixing Sets
	
	/** 
	 * Computes the Minimal Satisfying Feature Sets MSFS
	 * These are the sets of features that for a given F would fix all requiring inconsistencies
	 * Note that this sets may not be unique.
	 * IMPORTANT: Assumes that the pwc file exists and has the correct values
	 * @param file 
	 */
	public void computeSatisfyingFeatureSets(SPL spl, PairwiseCommonality pwc, String targetFile) {
		// @debug
		// System.out.println("Computing Satisfying Feature Sets ");
		
		// Calculates the list of SPL-wide mandatory features
		spl.computeSPLWideCommonFeatures();
		
		// Select from all features 
		SelectFromAll selectFromAll = new SelectFromAll();
		selectFromAll.computeFixingSet(pwc, spl, SelectionPolicy.SELECT_FROM_ALL,SelectionPolicy.EXTENSION_SELECT_FROM_ALL, targetFile);
		
		// Excludes root features
		ExcludesRoot excludeRoot  = new ExcludesRoot();
		excludeRoot.computeFixingSet(pwc, spl, SelectionPolicy.EXCLUDES_ROOT, SelectionPolicy.EXTENSION_EXCLUDES_ROOT, targetFile);
		
		// Excludes SPL-wide mandatory features
		ExcludesSPLMandatory excludeMandatory  = new ExcludesSPLMandatory();
		excludeMandatory.computeFixingSet(pwc, spl, SelectionPolicy.EXCLUDES_SPL_MANDATORY,SelectionPolicy.EXTENSION_EXCLUDES_SPL_MANDATORY, targetFile);	
		
	} // compute fixing set
		
	
	/**
	 * Batch computation of satisfying sets of multiple SPLs
	 */
	public void multipleSatisfyingFeatureSets(String famaModelsDir, String pwcDir, String targetDirectory) {
		
		File dir = new File(famaModelsDir);
		
		// Creates the parser for the FAMA model
		FAMAFeatureModelParser fmp = new FAMAFeatureModelParser();
		SPL spl;
		
		// Computed file names for the PWC and fixing sets files
		String fileName;
		String pwcFileName;
		String fixingSetFilesName;  // without the extension
		for (File file : dir.listFiles()) {
			
			fileName = file.getName();
			//@debug
			System.out.println("\n\n Feature model " + file.getAbsolutePath());
			
			// Creates a SPL object fromt a FAMA file
			spl = fmp.buildsFeatureModel(file.getAbsolutePath());
			
			// Calculates the name of the pwc file
			pwcFileName = pwcDir + File.separator + (fileName).substring(0, fileName.lastIndexOf("."))+".pwc";
			
			// Loads up the pairwise commonality file
			PairwiseCommonality pwc = deserializationPairwiseCommonality(pwcFileName);
			
			// Calculates the name of the target files, without the extensions
			fixingSetFilesName = targetDirectory + File.separator +  fileName.substring(0,fileName.lastIndexOf("."));
			
			computeSatisfyingFeatureSets(spl, pwc, fixingSetFilesName);
			
		} // for all files
	} // 
	
	/**
	 * Computes the CSV files for the statistics of fixing sets
	 */
	public void computeSatisfyingSetStatistics(String sourceStatisticsDir, String targetStatisticsDir, final String policyFileExtension) {
		
		// For reading the files in the directory
		File dir = new File(sourceStatisticsDir);
		
		// Creates three output streams one for all metrics considered now FSLength, CommonRatio, ComputationTime
		FileWriter outputFile = null;

		// Obtains the extension name
		String targetFileName = targetStatisticsDir.concat(File.separator) + (policyFileExtension.substring(1, policyFileExtension.length())).toUpperCase();
		
		//@debug
		System.out.println("Target file name " + targetFileName);
		
		// Creates the target files
		try {
			outputFile = new FileWriter(targetFileName + ".csv");
        } catch(IOException ioe) {
        	System.out.println("Cannot create statistics file " + ioe.getMessage());
        	ioe.printStackTrace();
        	return;
		}		
        
 		// Adds the headers to the files
        // Stores the header
		try{
			String tableHeader = "No, File, No.Features, No.Products, Avg. FSLength, Avg. Commo Ratio, Avg. Computation Time" + NEWLINE; 
			outputFile.write("General Fixing Set Statistics" + NEWLINE);
			outputFile.write(tableHeader);	
		}catch(IOException ioe) {
        	System.out.println("Errors in writing statistics headers " + ioe.getMessage());
        	ioe.printStackTrace();
        	return;
		}
		
		// Traverses the list of files, filtering those that have the required file extension based on the policy 
		int count =0;
		SPLFixingSets splFixingSets;
		
		for (File file : dir.listFiles(new FileFilter() {
			public boolean accept(File pathName) {
				return (pathName.getAbsolutePath()).endsWith(policyFileExtension);
			}} )) {
			
			//@debug
			System.out.println("Recording " + file.getName());
			
			// Loads the statistics file
			splFixingSets = deserializationFixingSets(file.getAbsolutePath());
			
			// Writes the entry details
			try {
				outputFile.write(count + "," + file.getName() + "," + splFixingSets.getNumberOfFeatures() + "," +
						splFixingSets.getNumberOfProducts() + "," + splFixingSets.getAverageFixingSetLength() + "," + 
						splFixingSets.getAverageCommonRatio() + "," + splFixingSets.getAverageComputationTime() + NEWLINE);
			} catch (IOException e) {
				System.out.println("File error at recording " + file.getName() + " " + e.getMessage());
				e.printStackTrace();
			}		
			
			// Increases entry counter
			count++;
			
		} // of all the filtered files
			
		// Saves the final accumulated time, flushes any file contents and close the file
		try{
			outputFile.flush();
			outputFile.close();
		}catch(IOException ioe) {
	       	System.out.println("Statistics Serialization error at closing file " + ioe.getMessage());
	       	ioe.printStackTrace();
	       	return;
		}
		
	} // of computeSatisfying Set statistics
	
	
	
	/**
	 * Deserializes a file with the a fixing sets
	 * @param fileName
	 * @return
	 */
	public SPLFixingSets deserializationFixingSets(String fileName) {
		SPLFixingSets splFixingSets = null;
        try
        {
           FileInputStream fileIn = new FileInputStream(fileName);
           ObjectInputStream in = new ObjectInputStream(fileIn);
           splFixingSets = (SPLFixingSets) in.readObject();
           in.close();
           fileIn.close();
       }catch(IOException i) {
           i.printStackTrace();
           return splFixingSets;
       }catch(ClassNotFoundException c) {
           System.out.println("splFixingSets class not found");
           c.printStackTrace();
           return splFixingSets;
       }

       return splFixingSets;
	} // of deserialization
	
	
	//********************************* Testing methods
	
	// Tests the creation of the statistics file in csv format
	public static void testExportStatistics() {
		FAMAFeatureModelAnalysis tester = new FAMAFeatureModelAnalysis();
		tester.computeSatisfyingSetStatistics("./fixing-sets", "./statistics", SelectionPolicy.EXTENSION_EXCLUDES_SPL_MANDATORY);
	} // of
	
	
	
	// Test computing multiple satisfying tests for GPL
	public static void testMultipleSatisfyingSets() {
		FAMAFeatureModelAnalysis tester = new FAMAFeatureModelAnalysis();
		tester.multipleSatisfyingFeatureSets("./fama-models", "./pairwise-comm", "./fixing-sets") ;
	}
	
	
	// Test satisfying tests for GPL
	public static void testSatisfyingSets() {
		FAMAFeatureModelAnalysis tester = new FAMAFeatureModelAnalysis();
		
		// Tests the computation of satisfying sets
		FAMAFeatureModelParser fmp = new FAMAFeatureModelParser();
		SPL gpl = fmp.buildsFeatureModel("./fama-models/FAMA-GPL.xml");
		System.out.println("\nComputes PWC for GPL");
		tester.pairWiseCompatibility(new File("./fama-models/FAMA-GPL.xml"), "pairwise-comm/FAMA-GPL.pwc");
		System.out.println("\nComputer satisfying sets for GPL");
		// Loads up the pairwise commonality file
		PairwiseCommonality pwc = tester.deserializationPairwiseCommonality("pairwise-comm/FAMA-GPL.pwc");
		tester.computeSatisfyingFeatureSets(gpl, pwc, "fixing-sets/FAMA-GPL");
	}
	
	// Test if the values serialized are actually those that were originally displayed
	public static void testDeserializationSatisfyingSets() {
		
		FAMAFeatureModelAnalysis tester = new FAMAFeatureModelAnalysis();
		SPLFixingSets splFixingSetsAll = tester.deserializationFixingSets("fixing-sets/FAMA-GPL.all");
		SPLFixingSets splFixingSetsNR = tester.deserializationFixingSets("fixing-sets/FAMA-GPL.nr");
		SPLFixingSets splFixingSetsNM = tester.deserializationFixingSets("fixing-sets/FAMA-GPL.nm");
		
		// Print the results
		System.out.println(splFixingSetsAll);
		System.out.println("\n\n");
		System.out.println(splFixingSetsNR);
		System.out.println("\n\n");
		System.out.println(splFixingSetsNM);
	}
	
	// *******************************************************************
	// **** Pairwise Commonality tests
	
	// Test the creation of a CSV with the collected PWC measurements
	public static void testPWCStatisticsComputation() {
		FAMAFeatureModelAnalysis tester = new FAMAFeatureModelAnalysis();
		tester.computePWCStatistics("./pairwise-comm", "./statistics/PWCStats.csv");
	}
	
	// Test the computation of multiple PWC values
	public static void testPWCCalculation() {
		FAMAFeatureModelAnalysis tester = new FAMAFeatureModelAnalysis();
		String targetDirectory = "pairwise-comm";
		tester.multiplePairWiseCompatibility("fama-models", targetDirectory);
	}

	// Test the computation of multiple PWC values
	public static void testPWCCalculationRealCases() {
		FAMAFeatureModelAnalysis tester = new FAMAFeatureModelAnalysis();
		String targetDirectory = "pairwise-comm";
		tester.multiplePairWiseCompatibility("fama-models-realcases", targetDirectory);
	}
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		// FAMAFeatureModelAnalysis tester = new FAMAFeatureModelAnalysis();
		// tester.structuralCheckFM("splot-examples");
	
		// computes the pairWiseCompatibility 
		/*
		String targetDirectory = "pairwise-comm";
		tester.multiplePairWiseCompatibility("fama-stats", targetDirectory);
		 */
	

	  
		// testPWCCalculationRealCases();

		
		// **********************************
		// Pairwise commonality
		
		// Tests the calculation of PWC for multiple FAMA files
		// testPWCCalculation();
		
		// Test the creation of a PWC stats file
		// testPWCStatisticsComputation();
		
		// **********************************
		// Satisfying sets
		
		// Test the computation of the satisfying sets
		// testSatisfyingSets();
		
		// Test the deserialization
		// testDeserializationSatisfyingSets();
		
		// Test multipleSatisfying sets
		testMultipleSatisfyingSets();
		
		// Test the computation of satisfying sets statistics file
		testExportStatistics();
		
		
	} // of main

} // of class 


