/*
 * AtMostConstraint.java created on 14.05.2009
 * 
 * (c) alexander noehrer
 */

package at.jku.sea.sat.implementation.constraint;

import at.jku.sea.sat.implementation.Utils;

/**
 * @author alexander noehrer
 */
public class AtMostConstraint extends AtLeastConstraint {

  public AtMostConstraint(final int degree, final int... literals) {
    super(literals.length - degree, Utils.invert(literals));
  }
}
