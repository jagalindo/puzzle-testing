/**
 * Example of SPL used for reading the file from SPLOT repository
 */
package at.jku.sea.mvsc.examples;




import at.jku.sea.mvsc.*;

public class SPLOT extends SPL {
	
	public SPLOT(String splName) { super (splName); }
	
	/**
	 * No constraints added for now. They have to be added from the file
	 */
	public void defineDomainConstraints() {  }
	
	/**
	 * They will have to be added manually
	 */
	public void defineDecisionFeatures() { }


}
