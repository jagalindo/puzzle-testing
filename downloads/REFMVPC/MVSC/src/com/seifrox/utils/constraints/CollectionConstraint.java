/*
 * CollectionConstraint.java created on 15.05.2006
 * 
 * (c) alexander noehrer
 */
package com.seifrox.utils.constraints;

import java.util.Collection;

/**
 * @author alexander noehrer
 * @param <T>
 */
public class CollectionConstraint<T extends Object & Collection<?>> extends ObjectConstraint<T> {
  private static final long serialVersionUID = 2780468744540345066L;

  private final long minNumberOfElements;
  private final long maxNumberOfElements;

  /**
   * constructor
   * 
   * @param minNumberOfElements the minimum number of elements the collection
   *          has to contain
   */
  public CollectionConstraint(final long minNumberOfElements) {
    this(minNumberOfElements, Integer.MAX_VALUE);
  }

  /**
   * constructor
   * 
   * @param minNumberOfElements the minimum number of elements the collection
   *          has to contain
   * @param maxNumberOfElements the maximum number of elements the collection
   *          has to contain
   */
  public CollectionConstraint(final long minNumberOfElements, final long maxNumberOfElements) {
    super(false);
    if (minNumberOfElements > maxNumberOfElements) {
      throw new ConstraintRuntimeException(ConstraintExceptionType.MINIMUM_ABOVE_MAXIMUM);
    }
    this.minNumberOfElements = minNumberOfElements;
    this.maxNumberOfElements = maxNumberOfElements;
  }

  @Override
  public void check(final T collection) throws ConstraintException {
    super.check(collection);
    if (collection != null) {
      if (collection.size() < this.minNumberOfElements) {
        throw new ConstraintException(ConstraintExceptionType.VALUE_BELOW_MINIMUM);
      }
      if (collection.size() > this.maxNumberOfElements) {
        throw new ConstraintException(ConstraintExceptionType.VALUE_ABOVE_MAXIMUM);
      }
    }
  }

  /**
   * @return returns the maxNumberOfElements.
   */
  public long getMaxNumberOfElements() {
    return this.maxNumberOfElements;
  }

  /**
   * @return returns the minNumberOfElements.
   */
  public long getMinNumberOfElements() {
    return this.minNumberOfElements;
  }
}
