/*
 * NumericRangeConstraint.java created on 24.05.2005
 * 
 * (c) alexander noehrer
 */

package com.seifrox.utils.constraints;

/**
 * @author alexander noehrer
 * @param <T>
 */
public class NumericRangeConstraint<T extends Number & Comparable<T>> extends ObjectConstraint<T> {
  private static final long serialVersionUID = -8032415882868127413L;

  private T maxValue;
  private T minValue;

  /**
   * constructor
   * 
   * @param minValue
   * @param maxValue
   */
  public NumericRangeConstraint(final T minValue, final T maxValue) {
    this(false, minValue, maxValue);
  }

  /**
   * constructor
   * 
   * @param optional
   * @param minValue
   * @param maxValue
   */
  public NumericRangeConstraint(final boolean optional, final T minValue, final T maxValue) {
    super(optional);
    if (minValue.compareTo(maxValue) > 0) {
      throw new ConstraintRuntimeException(ConstraintExceptionType.MINIMUM_ABOVE_MAXIMUM);
    }
    this.minValue = minValue;
    this.maxValue = maxValue;
  }

  /**
   * @param number
   * @throws ConstraintException
   */
  @Override
  public void check(final T number) throws ConstraintException {
    super.check(number);
    if (number != null) {
      if (number.compareTo(this.minValue) < 0) {
        throw new ConstraintException(ConstraintExceptionType.VALUE_BELOW_MINIMUM);
      }
      if (number.compareTo(this.maxValue) > 0) {
        throw new ConstraintException(ConstraintExceptionType.VALUE_ABOVE_MAXIMUM);
      }
    }
  }

  /**
   * getter for property maxValue
   * 
   * @return value of the maxValue
   */
  public T getMaxValue() {
    return this.maxValue;
  }

  /**
   * getter for property minValue
   * 
   * @return value of the minValue
   */
  public T getMinValue() {
    return this.minValue;
  }
}
