package fr.inria.diverse.refm.competition.common.utils.fitness;

/**
 * Value object encapsulating the information of the fitness metrics for
 * a given features model. 
 * 
 * @author David Mendez-Acuna
 *
 */
public class FitnessMetricsVO {

	// ------------------------------------------------
	// Attributes
	// ------------------------------------------------
	
	private double precision;
	private double recall;
	private double safety;
	private double fMeasure;
	
	// ------------------------------------------------
	// Constructor
	// ------------------------------------------------
	
	public FitnessMetricsVO(){
		
	}
	
	// ------------------------------------------------
	// Getters and setters
	// ------------------------------------------------
	
	public double getPrecision() {
		return precision;
	}

	public void setPrecision(double precision) {
		this.precision = precision;
	}

	public double getRecall() {
		return recall;
	}

	public void setRecall(double recall) {
		this.recall = recall;
	}

	public double getSafety() {
		return safety;
	}

	public void setSafety(double safety) {
		this.safety = safety;
	}

	public double getfMeasure() {
		return fMeasure;
	}

	public void setfMeasure(double fMeasure) {
		this.fMeasure = fMeasure;
	}
}