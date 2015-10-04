/*
 * CNFConstraint.java created on 14.05.2009
 * 
 * (c) alexander noehrer
 */

package at.jku.sea.sat;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author alexander noehrer
 */
public interface CNFConstraint extends Serializable {
  public boolean contains(final int clauseIndex);

  public boolean contains(final Clause clause);

  public Collection<Clause> getClauses();
}
