package fr.inria.diverse.refm.competition.common.utils;

import java.io.IOException;

import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;
import fr.inria.diverse.graph.Arc;
import fr.inria.diverse.graph.Graph;
import fr.inria.diverse.graph.Vertex;
import fr.inria.diverse.refm.competition.common.utils.fitness.FitnessMetrics;
import fr.inria.diverse.refm.competition.common.utils.fitness.FitnessMetricsVO;
import fr.inria.diverse.refm.competition.common.utils.fitness.TopologyMetrics;

/**
 * Utility for printing the fitness functions of a given feature model.
 * 
 * @author David Mendez-Acuna
 *
 */
public class PrintUtils {

	// -------------------------------------------------
	// Constructor
	// -------------------------------------------------
	
	public PrintUtils(){
	}
	
	// -------------------------------------------------
	// Methods
	// -------------------------------------------------
	
	/**
	 * Prints the value of all the registered fitness functions on the given feature model.
	 * @param fm
	 */
	public void printFitness(VariabilityModel fm, Graph<Vertex, Arc> dependenciesGraph, String PCM) {
		System.out.println("***");
		System.out.println("******");
		System.out.println("Printing the evaluation metrics for the resulting feature model");
		FitnessMetrics metrics = new FitnessMetrics();
		FitnessMetricsVO vo = metrics.compute(fm, dependenciesGraph, PCM);
		System.out.println(" - Precision: " + vo.getPrecision());
		System.out.println(" - Recall: " + vo.getRecall());
		System.out.println(" - F-Measure: " + vo.getSafety());
		System.out.println(" - Safety: " + vo.getSafety());
	}
	
	public void printTopologyMetrics(VariabilityModel fm){
		TopologyMetrics tp = new TopologyMetrics();
		tp.computeTopologyMetrics(fm);
	}
	
	public void printMetrics(int instanceId,
			VariabilityModel fm, Graph<Vertex, Arc> dependenciesGraph, String PCM){
		
		FitnessMetrics metrics = new FitnessMetrics();
		FitnessMetricsVO vo = metrics.compute(fm, dependenciesGraph, PCM);
		
		TopologyMetrics tp = new TopologyMetrics();
		FMStatistics statistics = tp.computeTopologyMetrics(fm);
		
		String metricsLabel = "No. " + instanceId + ", Precision: " +
				vo.getPrecision() + ", Recall: " + vo.getRecall() + ", F-Measure: " + vo.getfMeasure() +
				", Safety: " + vo.getSafety() +
				", Mandatory: " + statistics.getNoMandatory() + ", Optional: " + statistics.getNoOptional() +
				", XORs: " + statistics.getNoAlternative() + ", ORs: " + statistics.getNoOr() + ", Requires: " + 
				statistics.getNoRequires() + ", Excludes: " + statistics.getNoExcludes() + 
				", LeafFeatures: " + statistics.getNoLeafFeatures() + 
				", TopFeatures: " + statistics.getNoTopFeatures() +
				", FoC: " + statistics.getFoC() + 
				", Deep: " + statistics.getDeepTree();
		
		System.out.println(metricsLabel);
	}
	
	public String exportMetrics(int instanceId,
			VariabilityModel fm, Graph<Vertex, Arc> dependenciesGraph, String PCM){
		
		FitnessMetrics metrics = new FitnessMetrics();
		FitnessMetricsVO vo = metrics.compute(fm, dependenciesGraph, PCM);
		
		TopologyMetrics tp = new TopologyMetrics();
		FMStatistics statistics = tp.computeTopologyMetrics(fm);
		
		String metricsLabel = instanceId + "," + vo.getPrecision() + "," + vo.getRecall() + "," + vo.getfMeasure() +
				"," + vo.getSafety() +
				"," + statistics.getNoMandatory() + "," + statistics.getNoOptional() +
				"," + statistics.getNoAlternative() + "," + statistics.getNoOr() + "," + 
				statistics.getNoRequires() + "," + statistics.getNoExcludes() + "," + 
				statistics.getNoLeafFeatures() + "," +
				statistics.getNoTopFeatures() + "," +
				statistics.getFoC() + "," +
				statistics.getDeepTree() + "\n";
		
		return metricsLabel;
	}
	
	public void exportToCVS(String metrics, String filePath) throws IOException{
		String content = "No,Precision,Recall,F-Measure,Safety,Mandatory,Optional,XORs,ORs,Requires,Excludes,NLeaf,NTop,FoC,Deep\n";
		content += metrics;
		FileUtils.saveFile(content, filePath);
	}
}
