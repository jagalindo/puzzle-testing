/*
 * FODANode
 * Interface implemented by each of the types of node of a FODA model
 * @author Roberto E. Lopez-Herrejon
 */
package at.jku.sea.mvsc;

import java.util.*;

public interface FODANode {
	public List<List<Integer>> convertPropositionalFormula();
	public String toStringNodes();
	public String toStringFormula();
}
