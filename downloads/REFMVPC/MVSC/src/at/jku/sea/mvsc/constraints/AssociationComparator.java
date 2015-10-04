/**
 * Comparators that compares 
 */
package at.jku.sea.mvsc.constraints;

import java.util.Comparator;

import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Property;

import org.eclipse.emf.common.util.EList;

import at.jku.sea.mvsc.Feature;

public class AssociationComparator implements Comparator<Association> {
	
	/** Constants for association comparison */
	public static final int ASSOCIATION_EQUAL = 0;
	public static final int ASSOCIATION_LESSTHAN = -1;
	public static final int ASSOCIATION_GREATERTHAN = 1;
	
	public int compare (Association e1, Association e2) {
		int result = ASSOCIATION_EQUAL;
		
		if (e1.getName() == null) {
			if (e2.getName()!= null) {
				// If e1 does not have name and e2 does have a name
				result = ASSOCIATION_GREATERTHAN;
			}
		} else {
			if (e2.getName()== null) {
				// if e1 has a name but e2 does not have a name 
				result = ASSOCIATION_LESSTHAN;
			}
		}	
	
		return result;

	}
}
