//package at.jku.sea.mvsc.examples;
//
//public class  {
//
//}

/**
* Definition of Video On Demand example product line
* This is the example used in the paper
*/

package at.jku.sea.mvsc.examples;

import java.util.Arrays;

import at.jku.sea.mvsc.*;



public class VODSPL extends SPL {
	
	public VODSPL(String splName, String dir, String rootName) { super (splName, dir, rootName); }
	
	/**
	 * Here we define the 2 constraints of new version of the Video on Demand SPL
	 */
	public void defineDomainConstraints() { 
		// 1) InclusiveOR VOD - Record, Play
		InclusiveOR incor1 = new InclusiveOR("VOD",Arrays.asList("Record","Play"),this);
		addDomainConstraint(incor1);
		
		// 2) ExclusiveOR Play - TV, Mobile
		ExclusiveOR eor2 = new ExclusiveOR("Play",Arrays.asList("TV","Mobile"),this);
		addDomainConstraint(eor2);
	
	}
	
	/**
	 * No decision features for VPL
	 */
	public void defineDecisionFeatures() { 	}


}
