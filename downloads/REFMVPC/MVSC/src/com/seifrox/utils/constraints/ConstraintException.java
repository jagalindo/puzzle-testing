/*
 * ConstraintException.java created on 24.05.2005
 * 
 * (c) alexander noehrer
 */

package com.seifrox.utils.constraints;

/**
 * exception class for constraint exceptions
 * 
 * @author alexander noehrer
 */
public final class ConstraintException extends Exception {

  /**
   * comment for <code>serialVersionUID</code>
   */
  private static final long serialVersionUID = 8812686837480221817L;

  private final ConstraintExceptionType type;

  /**
   * constructor
   * 
   * @param type the type of the constraint violation
   */
  public ConstraintException(final ConstraintExceptionType type) {
    this(type, "");
  }

  /**
   * constructor
   * 
   * @param type the type of the constraint violation
   * @param message additional message
   */
  public ConstraintException(final ConstraintExceptionType type, final String message) {
    super(type.toString() + message);
    this.type = type;
  }

  /**
   * getter for property type
   * 
   * @return value of the type
   */
  public ConstraintExceptionType getType() {
    return this.type;
  }
}
