/*
 * ConflictsConstraint.java created on 14.05.2009
 * 
 * (c) alexander noehrer
 */

package at.jku.sea.sat.implementation.constraint;

import java.util.List;

import at.jku.sea.sat.implementation.LiteralClause;

/**
 * @author alexander noehrer
 */
public class SupportsConstraint extends AbstractCNFConstraint {

  /**
   * constructor
   * 
   * @param literals
   *          the literals that are supported, meaning the first literal is supports all the other ones, in tuples that
   *          would mean: (l1, l2), (l1, l3), (l1, lN)
   */
  public SupportsConstraint(final int... literals) {
    super();
    if (literals.length < 2) {
      throw new IllegalArgumentException();
    }
    for (int i = 1; i < literals.length; i++) {
      this.addTupel(literals[0], literals[i]);
    }
  }

  /**
   * constructor
   * 
   * @param literals
   *          the literals that are supported, meaning the first literal is supports all the other ones, in tuples that
   *          would mean: (l1, l2), (l1, l3), (l1, lN)
   */
  public SupportsConstraint(final List<Integer> literals) {
    super();
    if (literals.size() < 2) {
      throw new IllegalArgumentException();
    }
    for (int i = 1; i < literals.size(); i++) {
      this.addTupel(literals.get(0), literals.get(i));
    }
  }

  private void addTupel(final int a, final int b) {
    this.addClause(new LiteralClause(-b, a));
  }
}
