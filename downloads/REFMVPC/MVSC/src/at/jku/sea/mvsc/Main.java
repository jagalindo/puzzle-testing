package at.jku.sea.mvsc;

import java.io.File;

import at.jku.sea.mvsc.examples.SPLOT;

public class Main {

	/*********************************************************
	 * The following SPLOT related methods were copied from 
	 * Workspace: D:\Research\Projects\MVSComposition\ParsingTests\TestCompatibility
	 * File:   \src\at\jku\sea\mvsc\SPLOT2FAMA.java
     * They are samples for translating from SPLOT to FAMA format for further analysis
	 */
	
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
		
		
		System.out.println("Computing all products");
		long startTime2 = System.currentTimeMillis();
		splotGPL.computePairwiseFrequency(); 
		long endTime2 = System.currentTimeMillis();
		long elapsedTime2 = endTime2 - startTime2;
		System.out.println("Elapsed computation time (msec)" + elapsedTime2);
	}
	
	
	// Testing the SPLOT to FAMA translation
	public void testSplot2Fama(String splotFile, String name, String famaFile) {
		// Loading files from SPLOT repository
		// System.out.println("\nParsing SPLOT Sample File");
		
		// parserFile.parse("./SPLOT/REAL-FM-5.xml");
		
		System.out.println("Creating feature model in FAMA");
		SPLOT2FAMA parserFile = new SPLOT2FAMA();
		SPL splotGPL = new SPLOT(name);
		parserFile.splot2FAMA(splotGPL, splotFile, famaFile);
		
	} // test	
	
	public void multipleTestSplot2Fama(String splotDir, String famaDir) {
		System.out.println("Creating feature model in FAMA");
		SPLOT2FAMA transformer = new SPLOT2FAMA();
		SPL splotGPL = new SPLOT("dummy");
		transformer.multipleSplot2Fama(splotGPL, splotDir, famaDir);
	} // multipleTesting
	
	
	// Translation of evelyn's models
	
	public static void translateEvelynSplot2Fama() {
		System.out.println("Creating feature model in FAMA");
		SPLOT2FAMA transformer = new SPLOT2FAMA();
		SPL splotGPL = new SPLOT("dummy"); // only to fill the purposes
		String[] directories = {"./evelyn-models/splot/Models_WithConstr_15Features",
								"./evelyn-models/splot/Models_WithConstr_20Features",
								"./evelyn-models/splot/Models_WithoutConstr_10Features",
								"./evelyn-models/splot/Models_WithoutConstr_20Features"};
		
		// For all directories to translate
		String famaDir;
		for (String directory : directories) {
			famaDir = directory.replace("splot", "fama");
			
			// Makes the target directory
			File targetDir = new File(famaDir);
			targetDir.mkdirs();
			
			// @debug
			System.out.println("Translating splot dir= " + directory + " FAMA dir= " + famaDir);
			transformer.multipleSplot2Fama(splotGPL, directory, famaDir);
	
		}
		
	} // multipleTesting
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Hello");
		
		MultiViewSafeComposition tester = new MultiViewSafeComposition();
		
		// Tests creating a SPL for GPL - OK
		// tester.testGPL("GPL-SPLOT","./GPLSPLOT");
		
		// Test the translation of 
		translateEvelynSplot2Fama();
		
	} // of main

}
