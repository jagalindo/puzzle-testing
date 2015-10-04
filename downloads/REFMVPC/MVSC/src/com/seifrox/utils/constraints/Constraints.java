/*
 * Constraints.java created on 04.01.2007
 * 
 * (c) alexander noehrer
 */

package com.seifrox.utils.constraints;

import java.util.Collection;

/**
 * @author alexander noehrer
 */
public final class Constraints {

  /**
   * constraint for checking collections against not being empty against null
   * references
   */
  public static final CollectionConstraint<Collection<?>> NOT_EMPTY = new CollectionConstraint<Collection<?>>(1);

  /** constraint for checking against null references */
  public static final Constraint<Object> NOT_NULL = new ObjectConstraint<Object>(false);

  /** constraint for checking Integers if they are >= 0 */
  public static final NumericRangeConstraint<Integer> ID = new NumericRangeConstraint<Integer>(0, Integer.MAX_VALUE);

  /** constraint for checking Integer if they are > 0 */
  public static final NumericRangeConstraint<Integer> POSITIVE_INT = new NumericRangeConstraint<Integer>(1, Integer.MAX_VALUE);

  /** constraint for checking Longs if they are > 0 */
  public static final NumericRangeConstraint<Long> POSITIVE_LONG = new NumericRangeConstraint<Long>(1L, Long.MAX_VALUE);

  /** constraint for checking Strings against containing at least one character */
  public static final RegularExpressionConstraint NON_EMPTY_STRING = new RegularExpressionConstraint(".+");

  /**
   * private constructor
   */
  private Constraints() {
    throw new RuntimeException(this.getClass().getName() + ": call to the constructor makes no sense");
  }
}
