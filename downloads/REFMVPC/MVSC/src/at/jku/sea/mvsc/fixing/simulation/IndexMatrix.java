/**
 * The class contains the pair of indices that are part of the
 * Key object of the mapping between feature pairs and their measurments.
 */
package at.jku.sea.mvsc.fixing.simulation;

import java.io.Serializable;

public class IndexMatrix implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5L;
	int x, y;
	public int getX() { return x; }
	public int getY() { return y; }
	
	public IndexMatrix (int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Overrides default equals to compare indices of the matrix
	 * Two indices are equal if they have the same values of they are 
	 * swapped.
	 */
	public boolean equals (Object obj) {
		
		// System.out.print("over eq ");
		
		boolean result = false;
		if (!(obj instanceof IndexMatrix)) return false;
		IndexMatrix other = (IndexMatrix) obj;
		result = ((other.x==this.x) && (other.y==this.y));
		if (result) return result;
		return (other.x==this.y) && (other.y==this.x);
				
		// @debug
		// 
		// result = ((other.x==this.x) && (other.y==this.y)) ||
		//		 ((other.x==this.y) && (other.y==this.x));
	}
	
	
} // of IndexMatrix
