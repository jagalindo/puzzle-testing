/*
 * Constraint.java created on 06.10.2008
 * 
 * (c) alexander noehrer
 */

package com.seifrox.utils.constraints;

import java.io.Serializable;

/**
 * @author (c) alexander noehrer
 * @param <T>
 */
public interface Constraint<T extends Object> extends Serializable {

  /**
   * validates the given object according to the settings throws a ConstraintRuntimeException if it fails
   * 
   * @param object the value that should be validated
   */
  public void validate(final T object);

  /**
   * validates the given object according to the settings
   * 
   * @param object the value that should be validated
   * @throws ConstraintException if the validation fails
   */
  public void check(final T object) throws ConstraintException;

  /**
   * validates the given object according to the settings and returns a boolean indicating if it is allowed
   * 
   * @param object the value that should be validated
   * @return a boolean indicating if the value is allowed
   */
  public boolean isAllowed(final T object);

  /**
   * getter for property optional
   * 
   * @return value of the optional
   */
  public boolean isOptional();

}
