package fr.inria.diverse.refm.competition.common.utils.fitness;

import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFeatureModel;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;
import es.us.isa.utils.FMStatistics;

public class TopologyMetrics {

	public FMStatistics computeTopologyMetrics(VariabilityModel fm){
		FMStatistics statistics = new FMStatistics((FAMAFeatureModel) fm);
		System.out.println("statistics: " + statistics);
		return statistics;
	}
}