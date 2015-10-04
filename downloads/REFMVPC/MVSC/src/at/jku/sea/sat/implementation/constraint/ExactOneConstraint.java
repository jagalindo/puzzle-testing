/*
 * ExactOneConstraint.java created on 14.05.2009
 * 
 * (c) alexander noehrer
 */

package at.jku.sea.sat.implementation.constraint;

import at.jku.sea.sat.implementation.LiteralClause;


/**
 * @author alexander noehrer
 */
public class ExactOneConstraint extends AtMostConstraint {

  public ExactOneConstraint(final int... literals) {
    super(1, literals);
    this.clauses.add(new LiteralClause(literals));
  }
}
