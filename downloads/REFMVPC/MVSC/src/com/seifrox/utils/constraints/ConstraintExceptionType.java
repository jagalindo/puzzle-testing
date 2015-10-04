/*
 * ConstraintExceptionType.java created on 24.05.2005
 * 
 * (c) alexander noehrer
 */

package com.seifrox.utils.constraints;

import java.io.Serializable;

import com.seifrox.utils.constants.ConstantObject;

/**
 * provides constant objects that are used for ConstraintException objects
 * 
 * @author alexander noehrer
 */
public final class ConstraintExceptionType extends ConstantObject implements Serializable {

  /** serialVersionUID needed for the serialize mechanism */
  private static final long serialVersionUID = 160929651270100424L;

  /** type for when a null value is given but not allowed */
  public static final ConstraintExceptionType NULL_NOT_ALLOWED = new ConstraintExceptionType("NULL_NOT_ALLOWED");

  /** type for when the value given is below the allowed minimum */
  public static final ConstraintExceptionType MINIMUM_ABOVE_MAXIMUM = new ConstraintExceptionType("MINIMUM_ABOVE_MAXIMUM");

  /** type for when the value given is below the allowed minimum */
  public static final ConstraintExceptionType VALUE_BELOW_MINIMUM = new ConstraintExceptionType("VALUE_BELOW_MINIMUM");

  /** type for when the value given is above the allowed maximum */
  public static final ConstraintExceptionType VALUE_ABOVE_MAXIMUM = new ConstraintExceptionType("VALUE_ABOVE_MAXIMUM");

  /**
   * type for when the string given does not match the regular expression
   * pattern
   */
  public static final ConstraintExceptionType DOES_NOT_MATCH_PATTERN = new ConstraintExceptionType("DOES_NOT_MATCH_PATTERN");

  /** type for when the given value is in the list of not allowed values */
  public static final ConstraintExceptionType VALUE_IN_THE_LIST = new ConstraintExceptionType("VALUE_IN_THE_LIST");

  /** type for when the given value is not in the list of allowed values */
  public static final ConstraintExceptionType VALUE_NOT_IN_THE_LIST = new ConstraintExceptionType("VALUE_NOT_IN_THE_LIST");

  /** type for when the the given value could not be converted to the given type */
  public static final ConstraintExceptionType CONVERT_ERROR = new ConstraintExceptionType("CONVERT_ERROR");

  /** type for when the the given value does not match the bounds */
  public static final ConstraintExceptionType OUT_OF_BOUNDS = new ConstraintExceptionType("OUT_OF_BOUNDS");

  /** type for when a object is not of the allowed class type */
  public static final ConstraintExceptionType TYPE_NOT_ALLOWED = new ConstraintExceptionType("TYPE_NOT_ALLOWED");

  /** type for when the constraint violation happened for another reason */
  public static final ConstraintExceptionType UNKOWN = new ConstraintExceptionType("UNKOWN");

  /**
   * constructor
   * 
   * @param name the name of the constraint type
   */
  private ConstraintExceptionType(final String name) {
    super(name);
  }

  /*
   * @see com.seifrox.utils.constants.ConstantObject#toString()
   */
  @Override
  public String toString() {
    return this.getName();
  }
}
