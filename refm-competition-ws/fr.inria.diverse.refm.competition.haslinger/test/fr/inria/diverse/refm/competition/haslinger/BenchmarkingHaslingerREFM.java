package fr.inria.diverse.refm.competition.haslinger;

import org.junit.Before;
import org.junit.Test;

import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;
import fr.inria.diverse.refm.competition.common.utils.PrintUtils;

public class BenchmarkingHaslingerREFM {

	// ---------------------------------------------------
	// Attributes
	// ---------------------------------------------------
	
	private HaslingerREFM synthesizer;
	private int initialInstance;
	private int finalInstance;
	
	// ---------------------------------------------------
	// Scenarios loading
	// ---------------------------------------------------
	
	@Before
	public void loadScenarios(){
		synthesizer = new HaslingerREFM();
		initialInstance = 1;
		finalInstance = 1;
	}
	
	// ---------------------------------------------------
	// Test cases
	// ---------------------------------------------------
	
	@Test
	public void executeBenchmark() throws Exception{
		for (int i = initialInstance; i <= finalInstance; i++) {
			VariabilityModel result = synthesizer.execute("testdata/" + i + "_3_closed_pcm.txt");
//			(new PrintUtils()).printFitness(result);
		}
	}
}