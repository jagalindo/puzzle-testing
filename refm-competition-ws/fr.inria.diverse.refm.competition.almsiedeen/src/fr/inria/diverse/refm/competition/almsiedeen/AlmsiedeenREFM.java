package fr.inria.diverse.refm.competition.almsiedeen;

import java.io.File;

import process.run;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;
import fr.inria.diverse.refm.competition.common.utils.FileUtils;
import fr.inria.diverse.refm.competition.common.utils.XMLReader;

public class AlmsiedeenREFM {

	public VariabilityModel execute(String PCMFile, int executionIndex) throws Exception {
		String originalPCM = FileUtils.readFileContent(new File(PCMFile));
		PCMFormatTranslator translator = new PCMFormatTranslator();
		translator.loadPCM(originalPCM);
		String adaptedPCM = translator.fromPuzzleToAlmsiedeenFormat(originalPCM);
		FileUtils.saveFile(adaptedPCM, "input/input_matrix.txt");
		
		String[] args = new String[2];
		args[0] = "input/input_matrix.txt";
		args[1] = "out-" + executionIndex + ".xml";
		run.main(args);
		
		XMLReader reader = new XMLReader();
		VariabilityModel model = null;
		model = reader.parseFile(args[1]);
		
		return model;
	}
}
