/*
 * AtMostConstraint.java created on 14.05.2009
 * 
 * (c) alexander noehrer
 */

package at.jku.sea.sat.implementation.constraint;

import java.util.BitSet;

import at.jku.sea.sat.implementation.LiteralClause;

import com.seifrox.utils.constraints.NumericRangeConstraint;

/**
 * @author alexander noehrer
 */
public class AtLeastConstraint extends AbstractCNFConstraint {

  protected static BitSet convert(final long value) {
    final BitSet bits = new BitSet(64);
    long bitmask = 1;
    for (int i = 0; i < 64; i++) {
      bits.set(i, (value & bitmask) == bitmask);
      bitmask = bitmask << 1;
    }
    return bits;
  }

  public AtLeastConstraint(final int degree, final int... literals) {
    super();
    new NumericRangeConstraint<Integer>(0, literals.length).validate(degree);

    // literals: 1,2,3
    // degree: 0
    // no clause needed
    // degree: 1
    // picosat4J.addClause(1, 2, 3);
    // degree: 2
    // picosat4J.addClause(1, 2);
    // picosat4J.addClause(1, 3);
    // picosat4J.addClause(2, 3);
    // degree: 3
    // picosat4J.addClause(1);
    // picosat4J.addClause(2);
    // picosat4J.addClause(3);

    if (degree > 0) {
      final int clauseLength = literals.length + 1 - degree;
      final long cardinalNumber = 1 << literals.length;

      for (int i = 0; i < cardinalNumber; i++) {
        final BitSet bits = convert(i);
        if (bits.cardinality() == clauseLength) {
          final int[] clause = new int[clauseLength];
          int index = 0;
          int j = 0;
          index = bits.nextSetBit(index);
          while (index >= 0) {
            clause[j++] = literals[index];
            index = bits.nextSetBit(index + 1);
          }
          this.addClause(new LiteralClause(clause));
        }
      }
    }
  }
}
