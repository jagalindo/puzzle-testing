/**
 * Orders the sets of PairwiseCommonality values in ascending order
 */
package at.jku.sea.mvsc.fixing.simulation;

import java.util.Comparator;

public class PairwiseCommonalityPairComparator implements Comparator<PairwiseCommonalityPair> {

	/** Sorts in descending order.
	 * If positive it means that o1 > o2, if equal that they are equal, and otherwise.
	 */
	@Override
	public int compare(PairwiseCommonalityPair o1, PairwiseCommonalityPair o2) {
		if (o1.numProducts > o2.numProducts) return -1;
		if (o1.numProducts < o2.numProducts) return 1;
		return 0;
	}
	

}
