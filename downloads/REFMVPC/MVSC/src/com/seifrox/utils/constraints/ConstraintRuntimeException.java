/*
 * ConstraintRuntimeException.java created on 20.06.2005
 * 
 * (c) alexander noehrer
 */

package com.seifrox.utils.constraints;

import java.io.Serializable;

/**
 * exception class for constraint runtime exceptions
 * 
 * @author alexander noehrer
 */
public final class ConstraintRuntimeException extends RuntimeException implements Serializable {

  /** serialVersionUID needed for the serialize mechanism */
  private static final long serialVersionUID = 561119809773236444L;

  /** holds the value of the property type */
  private final ConstraintExceptionType type;

  /**
   * constructor
   * 
   * @param type the type of the constraint violation
   */
  public ConstraintRuntimeException(final ConstraintExceptionType type) {
    this(type, type.toString());
  }

  /**
   * constructor
   * 
   * @param type the type of the constraint violation
   * @param message additional message
   */
  public ConstraintRuntimeException(final ConstraintExceptionType type, final String message) {
    super(message);
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
