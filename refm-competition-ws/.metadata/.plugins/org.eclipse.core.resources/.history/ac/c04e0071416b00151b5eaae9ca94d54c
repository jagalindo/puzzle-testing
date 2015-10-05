package fr.inria.diverse.refm.competition.becan;

import org.junit.Before;
import org.junit.Test;

import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;
import fr.inria.diverse.refm.competition.common.utils.PrintUtils;

/**
 * JUnit class that is able to execute the REFM benchmark on the proposal of Lopez-Herrejon et al. 
 * 
 * @author David Mendez-Acuna
 *
 */
public class BenchmarkingBecanREFM {

	// ---------------------------------------------------
	// Attributes
	// ---------------------------------------------------
	
	private BecanREFM synthesizer;
	private int initialInstance;
	private int finalInstance;
	
	// ---------------------------------------------------
	// Scenarios loading
	// ---------------------------------------------------
	
	@Before
	public void loadScenarios(){
		synthesizer = new BecanREFM();
		initialInstance = 1;
		finalInstance = 5;
	}
	
	// ---------------------------------------------------
	// Test cases
	// ---------------------------------------------------
	
	@Test
	public void executeBenchmark() throws Exception{
		for (int i = initialInstance; i <= finalInstance; i++) {
			VariabilityModel result = synthesizer.execute("testdata/" + i + "_3_closed_pcm.txt");
			(new PrintUtils()).printFitness(result);
		}
	}
}
