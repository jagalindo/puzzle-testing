package fr.inria.diverse.refm.competition.haslinger;

import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;
import fma.test.TestApplication;

public class HaslingerREFM {

	public VariabilityModel execute(String PCMFile) throws Exception {
		String[] args = new String[1];
		args[0] = "testDirectory/REAL-FM-10.xml";
		TestApplication.main(args);
		
		return null;
	}
}