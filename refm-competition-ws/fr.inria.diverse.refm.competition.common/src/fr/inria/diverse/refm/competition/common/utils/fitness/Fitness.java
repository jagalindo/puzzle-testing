package fr.inria.diverse.refm.competition.common.utils.fitness;

import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFeatureModel;

public interface Fitness {

	public String getName();
	
	public double compute(FAMAFeatureModel fm);
}
