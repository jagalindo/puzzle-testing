package fr.inria.diverse.refm.competition.common.utils.fitness;

import java.util.ArrayList;
import java.util.Collection;

import es.us.isa.ChocoReasoner.ChocoReasoner;
import es.us.isa.ChocoReasoner.questions.ChocoProductsQuestion;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.Product;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;
import fr.inria.diverse.graph.Arc;
import fr.inria.diverse.graph.Graph;
import fr.inria.diverse.graph.Vertex;
import fr.inria.diverse.refm.competition.common.utils.PCMQueryServices;

public class FitnessMetrics {
	
	// -------------------------------------------------------
	// Attributes
	// -------------------------------------------------------
	
	private Collection<Product> FMProducts = null;
	private PCMQueryServices pcmQueries = null;

	// -------------------------------------------------------
	// Constructor
	// -------------------------------------------------------
	
	public FitnessMetrics(){
		pcmQueries = PCMQueryServices.getInstance(); 
	}
	
	// -------------------------------------------------------
	// Methods
	// -------------------------------------------------------
	
	public FitnessMetricsVO compute(VariabilityModel fm, Graph<Vertex, Arc> dependenciesGraph, String PCM) {
		FitnessMetricsVO metrics = new FitnessMetricsVO();
		int numberOfFMProducts = computeNumberOfProducts(fm);
		int numberOfPCMProducts = computeNumberOfProducts(PCM);
		
		// Computing precision
		int intersection = computeIntersectionPCMFM(fm, PCM);
		double precision = intersection / numberOfFMProducts;
		metrics.setPrecision(precision);
		
		// Computing recall
		double recall = intersection / numberOfPCMProducts;
		metrics.setRecall(recall);
		
		// Computing safety
		double safety = 0;
		for (Arc arc : dependenciesGraph.getArcs()) {
			int productsRespectingDependency = getNumberOfRespectfulProducts(arc, fm);
			safety += (productsRespectingDependency/numberOfFMProducts);
		}
		metrics.setSafety(safety/dependenciesGraph.getArcs().size());
		return metrics;
	}
	
	public void computeProducts(VariabilityModel fm){
		if( FMProducts == null){
			FMProducts= chocoGetProducts(fm);
		}
	}
	
	public int computeNumberOfProducts(VariabilityModel fm) {
		this.computeProducts(fm);
		return FMProducts.size();
	}
	
	private Collection<Product> chocoGetProducts(VariabilityModel fm) {
		ChocoReasoner reasoner = new ChocoReasoner();
		fm.transformTo(reasoner);
		
		ChocoProductsQuestion pq= new ChocoProductsQuestion();
		reasoner.ask(pq);
		return pq.getAllProducts();
	}

	public int computeNumberOfProducts(String PCM) {
		return PCM.split("\n").length - 1;
	}

	private int computeIntersectionPCMFM(VariabilityModel fm, String PCM) {
		this.computeProducts(fm);
		pcmQueries.loadPCM(PCM);
		int intersection = 0;
		
		for (Product currentProduct : FMProducts) {
			ArrayList<String> productString = new ArrayList<String>();
			for (GenericFeature feature : currentProduct.getFeatures()) {
				productString.add(feature.getName());
			}
			if(pcmQueries.productExists(productString)){
				intersection++;
			}
		}
		
		return intersection;
	}

	
	/**
	 * Returns the number of products that respect the dependency expressed in the arc.
	 * In other words, returns the number of products in the fm such that there is not a product such that arc.from and not arc.to.
	 * @param arc
	 * @param fm
	 * @return
	 */
	private int getNumberOfRespectfulProducts(Arc dependency, VariabilityModel fm) {
		int respectfulProducts = 0;
		
		for (Product product : FMProducts) {
			if(this.respectDependency(dependency.getFrom().getIdentifier(), dependency.getTo().getIdentifier(), product))
				respectfulProducts ++;
		}
		
		return respectfulProducts;
	}

	/**
	 * Returns true if the product respects the dependency. That is from => to. 
	 * @param from
	 * @param to
	 * @param product
	 * @return
	 */
	private boolean respectDependency(String from, String to,
			Product product) {
		boolean containsFrom = false;
		for (GenericFeature feature : product.getFeatures()) {
			if(feature.getName().equals(from)){
				containsFrom = true;
				break;
			}
				
		}
		
		boolean containsTo = false;
		for (GenericFeature feature : product.getFeatures()) {
			if(feature.getName().equals(to)){
				containsTo = true;
				break;
			}
		}
		
		if(containsFrom)
			return containsTo;
		else
			return true;
	}

}
