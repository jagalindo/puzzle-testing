package fr.inria.diverse.refm.competition.haslinger;

import java.io.File;

import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;
import fm.FeatureModelException;
import fma.test.TestApplication;
import fr.inria.diverse.refm.competition.common.utils.FileUtils;
import fr.inria.diverse.refm.competition.common.utils.SPLXReader;

public class HaslingerREFM {

	public VariabilityModel execute(String PCMFile) throws Exception {
		String originalPCM = FileUtils.readFileContent(new File(PCMFile));
		PCMFormatTranslator translator = new PCMFormatTranslator();
		translator.loadPCM(originalPCM);
		String adaptedPCM = translator.fromPuzzleToBecanFormat(originalPCM);
		FileUtils.saveFile(adaptedPCM, "input/" + PCMFile + "-PCM.txt");
		
		TestApplication.callAnalyzer("input/" + PCMFile + "-PCM.txt", "output/" + PCMFile + "-FM.xml");
		
		SPLXReader reader = new SPLXReader();
		VariabilityModel model = null;
		
		try{
			String modelPath = "output/" + PCMFile + "-FM.xml";
			model = reader.parseFile(modelPath);
		}catch(FeatureModelException e){
			System.out.println("Errors parsing the model " + e.getMessage());
		}
		
		return model;
	}
}
