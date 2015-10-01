package fr.inria.diverse.refm.competition.common.utils;

import java.util.ArrayList;
import java.util.List;

import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFeatureModel;
import fr.inria.diverse.refm.competition.common.utils.fitness.Fitness;
import fr.inria.diverse.refm.competition.common.utils.fitness.Precision;

public class PrintUtils {

	private List<Fitness> fitnessFunctions;
	
	public PrintUtils(){
		fitnessFunctions = new ArrayList<Fitness>();
		fitnessFunctions.add(new Precision());
	}
	
	public void printFitness(FAMAFeatureModel fm) {
		System.out.println("***");
		System.out.println("******");
		System.out.println("Printing the evaluation metrics for the resulting feature model");
		for (Fitness fitness : fitnessFunctions) {
			System.out.println("  - " + fitness.getName() + ": " + fitness.compute(fm));
		}
	}
}
