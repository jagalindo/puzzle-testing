package fr.inria.diverse.refm.competition.mendez;

import java.io.File;

import vm.PFeatureModel;
import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFeatureModel;
import fr.inria.diverse.graph.Arc;
import fr.inria.diverse.graph.Graph;
import fr.inria.diverse.graph.Vertex;
import fr.inria.diverse.puzzle.vmsynthesis.impl.FromPFeatureModelToFAMA;
import fr.inria.diverse.puzzle.vmsynthesis.impl.VmSynthesis;
import fr.inria.diverse.refm.competition.common.utils.FileUtils;

public class MendezREFM {
	
	public FAMAFeatureModel execute(Graph<Vertex, Arc> dependenciesGraph, String PCMFile) throws Exception {
		PFeatureModel openFM = VmSynthesis.getInstance().synthesizeOpenFeatureModel(dependenciesGraph);
		String PCM = FileUtils.readFileContent(new File(PCMFile));
		PFeatureModel closedFM = VmSynthesis.getInstance().synthesizeClosedFeatureModel(PCM, openFM);
		FAMAFeatureModel finalFM = FromPFeatureModelToFAMA.getInstance().fromPFeatureModelToFAMA(closedFM);
		return finalFM;
	}
}
