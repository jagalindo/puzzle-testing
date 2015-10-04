/**
 * This class performs the analysis of the feature models.
 */
package at.jku.sea.mvsc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Comparator;

import at.jku.sea.sat.implementation.picosat.Picosat4J;
import at.jku.sea.sat.implementation.LiteralClause;

//import at.jku.sea.reasoning.sat.implementation.LiteralClause;
import at.jku.sea.reasoning.sat.Clause;
import at.jku.sea.reasoning.sat.implementation.LiteralSet;
import at.jku.sea.reasoning.sat.implementation.Picosat4JSolver;


public class FeatureModelAnalysis {
	

	/**
	 * Computes the pairwise compatibility of the SPL
	 * @param spl The product line compare
	 */
	public static Map<Feature, SortedSet<Feature>> computeCompatibility(SPL spl) {
		
		Map<Feature, SortedSet<Feature>> compatibleFeatures = new HashMap<Feature, SortedSet<Feature>>();
		
	
		// The features of the SPL
		List<Feature> features = spl.features;
			
		// initializes the compatible feature sets
		for (Feature f : features) compatibleFeatures.put(f,  new TreeSet<Feature>(new CompatibleFeatureComparator()));
		
		// Computes the CNF of the spl
		
		Picosat4J picosat4J = Picosat4J.getInstance();
		
		// Clears any existing clauses
		picosat4J.clearClauses();
		
		// Initializes the SAT solver with the domain constraints
		List<List<Integer>> domainConstraints = spl.getDomainConstraints();
		
		// defines the CNF clauses of the product line
		LiteralClause[] splClauses = new LiteralClause[domainConstraints.size()];
		int clauseIndex =0;
		for (List<Integer> cnf : domainConstraints) {
			Integer[] arrayCNF = cnf.toArray(new Integer[0]);
			int[] arrayCNFInt = new int[arrayCNF.length];
			for (int i=0; i< arrayCNF.length; i++) arrayCNFInt[i] = arrayCNF[i].intValue();
			splClauses[clauseIndex] = new LiteralClause(arrayCNFInt);
			clauseIndex++;
		}
		
		// Does the pairwise check
		// For all features f
		for (Feature f : features) {
			
			//@debug
			// System.out.print("Compatiblity feature " + f.getName() + " -> ");
			
			// For the features f+1 to g
			for (Feature g : features.subList(features.indexOf(f) + 1, features.size())) {
				// Clears the SAT solver
				picosat4J.clearClauses();
				
				// Adds the SPL clauses to the solver
				for (LiteralClause clause : splClauses)  {
					if (clause==null) System.out.println("Literal clause is null");
					picosat4J.addClause(clause);
				}
				
				// Add clause for f
				picosat4J.addClause(spl.getVariableFromFeature(f));

				// Add clause for g
				picosat4J.addClause(spl.getVariableFromFeature(g));
				
				// @ debug
				// System.out.print( g.getName() );
				
				// if it is satisfiable then record as compatible of both f and g
				if (picosat4J.isSatisifable()) { 
					(compatibleFeatures.get(f)).add(g);
					(compatibleFeatures.get(g)).add(f);
					
					// System.out.print("OK ");
				}
				
			} // for remaining features			

		} // for all feature f
		
		return compatibleFeatures;
		
	} // of computability
	

/**
 * Computes the pairwise compatibility of the SPL
 * @param spl The product line compare
 */
public static void computePairwiseFrequency(SPL spl) {
	
	Map<Feature, SortedSet<Feature>> compatibleFeatures = new HashMap<Feature, SortedSet<Feature>>();
	

	// The features of the SPL
	List<Feature> features = spl.features;
		
	// initializes the compatible feature sets
	for (Feature f : features) compatibleFeatures.put(f,  new TreeSet<Feature>(new CompatibleFeatureComparator()));
	
	// Computes the CNF of the spl
	
	Picosat4JSolver picosat4J = Picosat4JSolver.getInstance();
	
	// Clears any existing clauses
	picosat4J.clearClauses();
	
	// Initializes the SAT solver with the domain constraints
	List<List<Integer>> domainConstraints = spl.getDomainConstraints();
	
	// defines the CNF clauses of the product line
	LiteralClause[] splClauses = new LiteralClause[domainConstraints.size()];
	int clauseIndex =0;
	for (List<Integer> cnf : domainConstraints) {
		Integer[] arrayCNF = cnf.toArray(new Integer[0]);
		int[] arrayCNFInt = new int[arrayCNF.length];
		for (int i=0; i< arrayCNF.length; i++) arrayCNFInt[i] = arrayCNF[i].intValue();
		splClauses[clauseIndex] = new LiteralClause(arrayCNFInt);
		clauseIndex++;
	}

	// Adds the SPL clauses to the solver
	// @testing for (LiteralClause clause : splClauses)  {
	for (LiteralClause clause : splClauses)  {
		if (clause==null) System.out.println("Literal clause is null");
		picosat4J.addClause((Clause)clause);
	}
	
	// Shows all the solutions
	int count =0;
	System.out.println("Computing solutions ");
	long startTime2 = System.currentTimeMillis();
	boolean res = picosat4J.isSatisifable(); 
	long endTime2 = System.currentTimeMillis();
	long elapsedTime2 = endTime2 - startTime2;
	System.out.println("Satisfiability Elapsed computation time (msec)" + elapsedTime2 + " " + res);
	
} // of pair-wise compatibility

} // Feature model analysis

// Internal class for the comparison of compatible sets of features
class CompatibleFeatureComparator implements Comparator<Feature>{
	public int compare (Feature f, Feature g) {
		return f.getName().compareTo(g.getName());
	}
}
