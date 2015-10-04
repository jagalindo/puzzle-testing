/*
 * LiteralClause.java created on 14.05.2009
 * 
 * (c) alexander noehrer
 */

package at.jku.sea.sat.implementation;

import java.util.Arrays;

import at.jku.sea.sat.Clause;

import com.seifrox.utils.StringUtils;

/**
 * @author alexander noehrer
 */
public class LiteralClause implements Clause {

  private final int[] literals;
  private int index;

  public LiteralClause(final int... literals) {
    super();
    this.index = -1;
    this.literals = literals;
  }

  /*
   * @see at.jku.sea.sat.Clause#getIndex()
   */
  public int getIndex() {
    return this.index;
  }

  /*
   * @see at.jku.sea.sat.Clause#getLiterals()
   */
  public int[] getLiterals() {
    return this.literals;
  }

  /*
   * @see at.jku.sea.sat.Clause#setIndex(int)
   */
  public void setIndex(final int index) {
    this.index = index;
  }

  /*
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return StringUtils.printf("%i: %s", this.index, Arrays.toString(this.literals));
  }
}
