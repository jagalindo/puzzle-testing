package fr.inria.diverse.refm.competition.assumcao;

import java.io.IOException;

import run.RunNSGAIIFM_3obj;
import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFeatureModel;

public class AssumcaoREFM {

	public FAMAFeatureModel execute(String string) throws IOException, InterruptedException {
		
		run.RunNSGAIIFM_3obj hola = new RunNSGAIIFM_3obj();
		String[] args = new String[1];
		args[0] = string;
		hola.main(args);
		return null;
	}
}
