package fr.inria.diverse.refm.competition.becan;

import java.io.File;

import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFeatureModel;
import fr.inria.diverse.refm.competition.common.utils.FileUtils;

public class BecanREFM {

	public FAMAFeatureModel execute(String PCMFile) throws Exception {
		String originalPCM = FileUtils.readFileContent(new File(PCMFile));
		PCMFormatTranslator translator = new PCMFormatTranslator();
		translator.loadPCM(originalPCM);
		String adaptedPCM = translator.fromPuzzleToBecanFormat(originalPCM);
		FileUtils.saveFile(adaptedPCM, "input/input_matrix.cvs");
		return null;
	}
}