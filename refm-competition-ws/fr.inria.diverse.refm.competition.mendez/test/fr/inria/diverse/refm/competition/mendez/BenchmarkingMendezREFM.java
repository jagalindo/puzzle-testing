package fr.inria.diverse.refm.competition.mendez;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;
import fr.inria.diverse.graph.Arc;
import fr.inria.diverse.graph.Graph;
import fr.inria.diverse.graph.Vertex;
import fr.inria.diverse.refm.competition.common.utils.FileUtils;
import fr.inria.diverse.refm.competition.common.utils.PrintUtils;

/**
 * JUnit class that is able to execute the REFM benchmark on the proposal of Mendez-Acuna et al. 
 * 
 * @author David Mendez-Acuna
 *
 */
public class BenchmarkingMendezREFM {

	// ---------------------------------------------------
	// Attributes
	// ---------------------------------------------------
	
	private MendezREFM synthesizer;
	private int initialInstance;
	private int finalInstance;
	
	// ---------------------------------------------------
	// Scenarios loading
	// ---------------------------------------------------
	
	@Before
	public void loadScenarios(){
		synthesizer = new MendezREFM();
		initialInstance = 1;
		finalInstance = 27;
	}
	
	// ---------------------------------------------------
	// Execution
	// ---------------------------------------------------
	
	@Test
	public void executeBenchmark() throws Exception{
		String resultString = "";
		PrintUtils utils = new PrintUtils();
		for (int i = initialInstance; i <= finalInstance; i++) {
			String matrix = FileUtils.readFileContent(new File("testdata/" + i + "_1_dependencies_graph.txt"));
			Graph<Vertex, Arc> dependenciesGraph = new Graph<Vertex, Arc>(matrix);
			VariabilityModel result = synthesizer.execute(dependenciesGraph, "testdata/" + i + "_3_closed_pcm.txt");
			String originalPCM = FileUtils.readFileContent(new File("testdata/" + i + "_3_closed_pcm.txt"));
			resultString += utils.exportMetrics(i, result, dependenciesGraph, originalPCM);
			utils.printMetrics(i, result, dependenciesGraph, originalPCM);
		}
		utils.exportToCVS(resultString, "results-" + initialInstance + "-" + finalInstance + ".csv");
	}
}