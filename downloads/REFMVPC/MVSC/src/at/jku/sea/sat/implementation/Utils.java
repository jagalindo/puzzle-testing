/*
 * Utils.java created on 14.05.2009
 * 
 * (c) alexander noehrer
 */

package at.jku.sea.sat.implementation;


/**
 * @author alexander noehrer
 */
public class Utils {

  public static int[] invert(final int... literals) {
    final int[] inverted = new int[literals.length];
    for (int i = 0; i < inverted.length; i++) {
      inverted[i] = -literals[i];
    }
    return inverted;
  }

  // public static Collection<Integer> asCollection(final int... literals) {
  // final Collection<Integer> result = new ArrayList<Integer>(literals.length);
  // for (final int literal : literals) {
  // result.add(literal);
  // }
  // return result;
  // }
}
