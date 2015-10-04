/*
 * Solver.java created on 13.05.2009
 * 
 * (c) alexander noehrer
 */

package at.jku.sea.sat;

/**
 * @author alexander noehrer
 */
public interface Solver {
  public String getInfos();

  public Clause addClause(final int... literals);

  public Clause addClause(final Clause clause);

  public void addClauses(final CNFConstraint clauses);

  public boolean isSatisifable();

  public boolean isSatisifable(final int... assumedLiterals);

}
