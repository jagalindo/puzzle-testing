/**
 * LogicTransformation
 * This class provides basic propositional logic transformations to fit the CNF and DIMACS format 
 */
package at.jku.sea.mvsc;

public class LogicTransformation {

	/**
	 * Provides negation for DIMACS format
	 * @param value
	 * @return
	 */
	public static int NOT(Integer value) {
		return value * -1;
	}
	
	/**
	 * Checks whether or not a variable is negated in a constraint
	 * @param value to check against
	 * @return boolean if the value is negated or false otherwise
	 */
	public static boolean isNegated(int value) {
		if (value < 0) return true;
		else return false;
	}
	
	
	/** Unnegates the variable name to look for it in the maps
	 * @param value of the variable value to unnegate
	 * @return value * -1
	 */
	public static int unNegate(int value) {
		return value * -1;
	}
	
	/**	Static  */
	public static String TRUE = "true";
	
}
