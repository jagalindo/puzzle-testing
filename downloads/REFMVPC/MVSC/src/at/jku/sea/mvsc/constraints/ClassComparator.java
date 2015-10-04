/**
 * Auxiliary class for comparasion of Class objects.
 */
package at.jku.sea.mvsc.constraints;

import java.util.Comparator;

import org.eclipse.uml2.uml.Class;

/** Current implementation orders by the name of the classes */
public class ClassComparator implements Comparator<Class> {
	
	public int compare (Class c1, Class c2) {
		return c1.getName().compareTo(c2.getName());
	}
	
} // of ClassComparator

