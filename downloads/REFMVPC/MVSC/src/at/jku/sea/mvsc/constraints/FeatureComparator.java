/**
 * For ordered elements that contain features
 * Orders according to names of the features
 * @author Roberto E. Lopez-Herrejon
 */
package at.jku.sea.mvsc.constraints;

import java.util.Comparator;
import at.jku.sea.mvsc.Feature;

public class FeatureComparator implements Comparator<Feature> {
	
	public int compare (Feature f1, Feature f2) {
		return f1.getName().compareTo(f2.getName());
	}
	
}
