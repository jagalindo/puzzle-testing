package fr.inria.diverse.refm.competition.lopez;

import org.junit.Before;
import org.junit.Test;

import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFeatureModel;
import fr.inria.diverse.refm.competition.common.utils.PrintUtils;

/**
 * JUnit class that is able to execute the REFM benchmark on the proposal of Lopez-Herrejon et al. 
 * 
 * @author David Mendez-Acuna
 *
 */
public class BenchmarkingLopezREFM {

	// ---------------------------------------------------
	// Attributes
	// ---------------------------------------------------
	
	private LopezREFM synthesizer;
	private int initialInstance;
	private int finalInstance;
	
	// ---------------------------------------------------
	// Scenarios loading
	// ---------------------------------------------------
	
	@Before
	public void loadScenarios(){
		synthesizer = new LopezREFM();
		initialInstance = 1;
		finalInstance = 5;
	}
	
	@Test
	public void executeBenchmark() throws Exception{
		for (int i = initialInstance; i <= finalInstance; i++) {
			FAMAFeatureModel result = synthesizer.execute("testdata/" + i + "_3_closed_pcm.txt");
			(new PrintUtils()).printFitness(result);
		}
	}

	
	
}
