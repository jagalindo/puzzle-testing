package fr.inria.diverse.refm.competition.lopez;

import java.io.File;

import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFeatureModel;
import fm.experiments.utils.FMExtractorFacade;
import fr.inria.diverse.refm.competition.common.FileUtils;

public class LopezREFM {

	public FAMAFeatureModel execute(String PCMFile) throws Exception{
		String originalPCM = FileUtils.readFileContent(new File(PCMFile));
		String adaptedPCM = PCMFromatTranslator.fromPuzzleToLopezFormat(originalPCM);
		String formatedFilePath = "formated-testdata/" + PCMFile;
		FileUtils.saveFile(adaptedPCM, formatedFilePath);
		return FMExtractorFacade.generateFM(formatedFilePath);
	}
}