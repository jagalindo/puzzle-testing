/*
 * ConflictsConstraint.java created on 14.05.2009
 * 
 * (c) alexander noehrer
 */

package at.jku.sea.sat.implementation.constraint;

import java.util.Arrays;
import java.util.List;

import at.jku.sea.sat.implementation.LiteralClause;

import com.seifrox.utils.StringUtils;
import com.seifrox.utils.constraints.Constraints;

/**
 * @author alexander noehrer
 */
public class ConflictsConstraint extends AbstractCNFConstraint {

  /**
   * constructor
   * 
   * @param literals
   *          the literals that conflict, meaning the first literal is in conflict with all the other ones, in tuples
   *          that would mean: (l1, l2), (l1, l3), (l1, lN)
   */
  public ConflictsConstraint(final int... literals) {
    super();
    Constraints.NOT_NULL.validate(literals);
    if (literals.length < 2) {
      System.err.println(StringUtils.printf("invalid conflict literal list: %s", Arrays.toString(literals)));
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
   *          the literals that conflict, meaning the first literal is in conflict with all the other ones, in tuples
   *          that would mean: (l1, l2), (l1, l3), (l1, lN)
   */
  public ConflictsConstraint(final List<Integer> literals) {
    super();
    Constraints.NOT_NULL.validate(literals);
    if (literals.size() < 1) {
      System.err.println(StringUtils.printf("invalid conflict literal list: %s", literals));
      throw new IllegalArgumentException();
    }
    for (int i = 1; i < literals.size(); i++) {
      this.addTupel(literals.get(0), literals.get(i));
    }
  }

  private void addTupel(final int a, final int b) {
    this.addClause(new LiteralClause(-a, -b));
  }
}
