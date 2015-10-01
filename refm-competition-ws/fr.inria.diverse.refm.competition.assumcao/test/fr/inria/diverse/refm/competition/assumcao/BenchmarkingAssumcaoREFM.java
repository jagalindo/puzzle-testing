package fr.inria.diverse.refm.competition.assumcao;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import fr.inria.diverse.graph.Arc;
import fr.inria.diverse.graph.Graph;
import fr.inria.diverse.graph.Vertex;
import fr.inria.diverse.refm.competition.common.utils.FileUtils;

/**
 * JUnit class that is able to execute the REFM benchmark on the proposal of Lopez-Herrejon et al. 
 * 
 * @author David Mendez-Acuna
 *
 */
public class BenchmarkingAssumcaoREFM {

	// ---------------------------------------------------
	// Attributes
	// ---------------------------------------------------
	
	private AssumcaoREFM synthesizer;
	private int initialInstance;
	private int finalInstance;
	
	// ---------------------------------------------------
	// Scenarios loading
	// ---------------------------------------------------
	
	@Before
	public void loadScenarios(){
		synthesizer = new AssumcaoREFM();
		initialInstance = 1;
		finalInstance = 1;
	}
	
	@Test
	public void executeBenchmark() throws Exception{
		for (int i = initialInstance; i <= finalInstance; i++) {
			String matrix = FileUtils.readFileContent(new File("testdata/" + i + "_1_dependencies_graph.txt"));
			Graph<Vertex, Arc> graph = new Graph<>(matrix);
			synthesizer.execute(graph, "testdata/" + i + "_3_closed_pcm.txt");
			
//			(new PrintUtils()).printFitness(result);
		}
	}
}