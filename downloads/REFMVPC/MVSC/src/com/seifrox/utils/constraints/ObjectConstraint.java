/*
 * ObjectConstraint.java created on 24.05.2005
 * 
 * (c) alexander noehrer
 */

package com.seifrox.utils.constraints;

import com.seifrox.utils.StringUtils;

/**
 * base class for all types of non primitive constraints
 * 
 * @author alexander noehrer
 * @param <T>
 *          the type for which the constraint should be created
 */
public class ObjectConstraint<T extends Object> implements Constraint<T> {
  private static final long serialVersionUID = 6262913041388876106L;

  /** holds the value of the property optional */
  private final boolean optional;

  /**
   * constructor
   * 
   * @param optional
   *          determines if null values are allowed
   */
  public ObjectConstraint(final boolean optional) {
    super();
    this.optional = optional;
  }

  /*
   * @see com.seifrox.utils.constraints.Constraint#validate(T)
   */
  public void validate(final T object) {
    try {
      this.check(object);
    } catch (final ConstraintException e) {
      System.err.println(StringUtils.printf("[%s] not valid for [%s]", object, this.toString()));
      throw new ConstraintRuntimeException(e.getType(), e.getMessage());
    }
  }

  /*
   * @see com.seifrox.utils.constraints.Constraint#isAllowed(java.lang.Object)
   */
  public boolean isAllowed(final T object) {
    try {
      this.check(object);
      return true;
    } catch (final ConstraintException e) {
      return false;
    }
  }

  /*
   * @see com.seifrox.utils.constraints.Constraint#check(T)
   */
  public void check(final T object) throws ConstraintException {
    if ((object == null) && (!this.optional)) {
      throw new ConstraintException(ConstraintExceptionType.NULL_NOT_ALLOWED);
    }
  }

  /*
   * @see com.seifrox.utils.constraints.Constraint#isOptional()
   */
  public boolean isOptional() {
    return this.optional;
  }
}
