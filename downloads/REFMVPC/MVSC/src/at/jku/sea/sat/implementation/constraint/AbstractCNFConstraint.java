/*
 * AbstractCNFConstraint.java created on 14.05.2009
 * 
 * (c) alexander noehrer
 */

package at.jku.sea.sat.implementation.constraint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import at.jku.sea.sat.CNFConstraint;
import at.jku.sea.sat.Clause;

import com.seifrox.utils.StringUtils;

/**
 * @author alexander noehrer
 */
public abstract class AbstractCNFConstraint implements CNFConstraint {

  protected final Collection<Clause> clauses;

  protected AbstractCNFConstraint() {
    super();
    this.clauses = new ArrayList<Clause>();
  }

  protected void addClause(final Clause clause) {
    // System.out.println(clause);
    this.clauses.add(clause);
  }

  /*
   * @see at.jku.sea.sat.CNFConstraint#contains(int)
   */
  public boolean contains(final int clauseIndex) {
    for (final Clause clause : this.clauses) {
      if (clause.getIndex() == clauseIndex) {
        return true;
      }
    }
    return false;
  }

  /*
   * @see at.jku.sea.sat.CNFConstraint#contains(at.jku.sea.sat.Clause)
   */
  public boolean contains(final Clause clause) {
    return this.clauses.contains(clause);
  }

  /*
   * @see at.jku.sea.sat.CNFConstraint#getClauses()
   */
  public Collection<Clause> getClauses() {
    return Collections.unmodifiableCollection(this.clauses);
  }

  /*
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return StringUtils.printf("%s: %s", this.getClass().getSimpleName(), this.clauses);
  }
}
