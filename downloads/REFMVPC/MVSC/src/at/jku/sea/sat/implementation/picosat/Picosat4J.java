/*
 * Picosat4J.java created on 13.05.2009
 * 
 * (c) alexander noehrer
 */

package at.jku.sea.sat.implementation.picosat;

import at.jku.sea.sat.CNFConstraint;
import at.jku.sea.sat.Clause;
import at.jku.sea.sat.Solver;
import at.jku.sea.sat.implementation.LiteralClause;

import com.seifrox.utils.StringUtils;

/**
 * @author alexander noehrer
 */
public class Picosat4J implements Solver {

  private static final int PICOSAT_UNKNOWN = 0;
  private static final int PICOSAT_SATISFIABLE = 10;
  private static final int PICOSAT_UNSATISFIABLE = 20;

  private static Picosat4J instance;

  static {
    final String libraryName = Picosat4J.class.getSimpleName();
    try {
      System.loadLibrary(libraryName);
      System.out.println(StringUtils.printf("%s (using picosat-%s) library successfully loaded", libraryName, version()));
    } catch (final UnsatisfiedLinkError e) {
      e.printStackTrace();
    }
    instance = null;
  }

  /**
   * @return the Picosat4J singleton instance
   */
  public static Picosat4J getInstance() {
    if (instance == null) {
      instance = new Picosat4J();
    }
    return instance;
  }

  private int numberOfLiterals;
  private int clauseIndex;

  /**
   * constructor
   */
  private Picosat4J() {
    super();
    this.initPicosat4J();
  }

  private void initPicosat4J() {
    this.numberOfLiterals = 0;
    // TODO check whether it starts with 0 or 1
    this.clauseIndex = 0;
    init();
  }

  public boolean isSatisifable() {
    final int result = sat(-1);
    if (result == PICOSAT_UNSATISFIABLE) {
      return false;
    } else if (result == PICOSAT_SATISFIABLE) {
      return true;
    } else if (result == PICOSAT_UNKNOWN) {
      System.out.println("result was PICOSAT_UNKNOWN");
    }
    throw new IllegalArgumentException();
  }

  public boolean isSatisifable(final Iterable<Integer> assumedLiterals) {
    return this.isSatisifable(assumedLiterals, null);
  }

  public boolean isSatisifable(final Iterable<Integer> assumedLiterals, final Integer test) {
    for (final int literal : assumedLiterals) {
      if (literal == 0) {
        throw new IllegalArgumentException("assumed literal must not be 0");
      }
      assume(literal);
    }
    if (test != null) {
      assume(test);
    }
    return this.isSatisifable();
  }

  public boolean isSatisifable(final int test, final int... assumedLiterals) {
    for (final int literal : assumedLiterals) {
      if (literal != 0) {
        assume(literal);
      } else {
        System.out.println(StringUtils.printf("assumed literal must not be 0"));
      }
    }
    assume(test);
    return this.isSatisifable();
  }

  public boolean isSatisifable(final int assumedLiteral) {
	  return this.isSatisifable(new int[]{assumedLiteral});
  }
  
  public boolean isSatisifable(final int... assumedLiterals) {
    for (final int literal : assumedLiterals) {
      if (literal == 0) {
        throw new IllegalArgumentException("assumed literal must not be 0");
      }
      assume(literal);
    }
    return this.isSatisifable();
  }

  public int[] getAssignments() {
    final int[] literals = new int[this.numberOfLiterals];
    for (int i = 1; i <= this.numberOfLiterals; i++) {
      literals[i - 1] = deref(i) * i;
    }
    return literals;
  }

  public int[] getTrivialAssignments() {
    final int[] literals = new int[this.numberOfLiterals];
    for (int i = 1; i <= this.numberOfLiterals; i++) {
      literals[i - 1] = deref_toplevel(i) * i;
    }
    return literals;
  }

  // TODO
  public int[] next() {
    final int[] last = this.getAssignments();
    final int[] clause = new int[last.length];
    for (int i = 0; i < clause.length; i++) {
      clause[i] = -last[i];
    }
    this.addClause(clause);
    return last;
  }

  /*
   * @see at.jku.sea.sat.Solver#addClause(int[])
   */
  public Clause addClause(final int... literals) {
    for (final int literal : literals) {
      this.numberOfLiterals = Math.max(this.numberOfLiterals, Math.abs(literal));
      add(literal);
    }
    add(0);
    final LiteralClause clause = new LiteralClause(literals);
    clause.setIndex(this.clauseIndex++);
    // System.out.println(StringUtils.printf("clause added: %s", clause));
    return clause;
  }

  /*
   * @see at.jku.sea.sat.Solver#addClause(at.jku.sea.sat.Clause)
   */
  public Clause addClause(final Clause clause) {
    final Clause temp = this.addClause(clause.getLiterals());
    clause.setIndex(temp.getIndex());
    return clause;
  }

  /*
   * @see at.jku.sea.sat.Solver#addClauses(at.jku.sea.sat.CNFConstraint)
   */
  public void addClauses(final CNFConstraint clauseContainer) {
    for (final Clause clause : clauseContainer.getClauses()) {
      this.addClause(clause);
    }
  }

  /*
   * @see at.jku.sea.sat.Solver#getInfos()
   */
  public String getInfos() {
    return StringUtils.printf("version=%s, config=%s, copyright=%s", version(), config(), copyright());
  }

  /**
   * clears the clauses and resets the picosat solver
   */
  public void clearClauses() {
    reset();
    this.initPicosat4J();
  }

  /*
   * @see java.lang.Object#finalize()
   */
  @Override
  protected void finalize() throws Throwable {
    reset();
    super.finalize();
  }

  /**
   * @return the numberOfLiterals
   */
  public int getNumberOfLiterals() {
    return this.numberOfLiterals;
  }

  /**
   * prints the statistics
   */
  public void printStatistics() {
    System.out.println("---Picosat4J statistics---");
    System.out.println(StringUtils.printf("version=%s, %s", version(), copyright()));
    System.out.println(StringUtils.printf("variables=%i(%i), clauses=%i(%i)", variables(), this.numberOfLiterals, added_original_clauses(), this.clauseIndex));
    System.out.println(StringUtils.printf("max memory consumption=%s", StringUtils.getHumanReadableSize(max_bytes_allocated())));
  }

  public int[] getFailedAssumptions() {
    final int[] literals = new int[this.numberOfLiterals];
    for (int i = 1; i <= this.numberOfLiterals; i++) {
      literals[i - 1] = failed_assumption(i) * i;
    }
    return literals;
  }

  private static native String version();

  private static native String config();

  private static native String copyright();

  private static native void init();

  private static native void reset();

  private static native void set_global_default_phase(int phase);

  private static native void set_default_phase_lit(int lit, int phase);

  private static native void set_more_important_lit(int lit);

  private static native void enable_trace_generation();

  private static native int inc_max_var();

  private static native void adjust(int max_idx);

  private static native int variables();

  private static native int added_original_clauses();

  private static native int max_bytes_allocated();

  private static native void add(int lit);

  private static native void assume(int lit);

  private static native int sat(int decision_limit);

  private static native int deref(int lit);

  private static native int deref_toplevel(int lit);

  private static native int inconsistent();

  private static native int failed_assumption(int lit);

  private static native int changed();

  private static native int coreclause(int i);

  private static native int corelit(int lit);

  private static native int usedlit(int lit);
}
