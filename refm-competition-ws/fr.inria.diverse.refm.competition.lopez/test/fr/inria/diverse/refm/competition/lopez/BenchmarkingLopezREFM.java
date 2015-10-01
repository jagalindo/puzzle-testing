package fr.inria.diverse.refm.competition.lopez;

import org.junit.Before;
import org.junit.Test;

public class BenchmarkingLopezREFM {

	private LopezREFM synthesizer;
	
	@Before
	public void loadScenarios(){
		synthesizer = new LopezREFM();
	}
	
	@Test
	public void executeBenchmark() throws Exception{
		for (int i = 1; i <= 2; i++) {
			synthesizer.execute("testdata/" + i + "_3_closed_pcm.txt");
		}
	}
	
}
