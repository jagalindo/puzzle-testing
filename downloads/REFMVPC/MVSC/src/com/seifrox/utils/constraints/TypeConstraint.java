/*
 * TypeConstraint.java created on 01.02.2009
 * 
 * (c) alexander noehrer
 */

package com.seifrox.utils.constraints;

/**
 * @author (c) alexander noehrer
 */
public class TypeConstraint extends ObjectConstraint<Object> {
  private static final long serialVersionUID = 4734934686894776407L;

  private final Class<?> cls;

  /**
   * constructor
   * 
   * @param cls the Class to check against
   */
  public TypeConstraint(final Class<?> cls) {
    super(false);
    Constraints.NOT_NULL.validate(cls);
    this.cls = cls;
  }

  /*
   * @see com.seifrox.utils.constraints.ObjectConstraint#check(java.lang.Object)
   */
  @Override
  public void check(final Object object) throws ConstraintException {
    super.check(object);
    if (object instanceof Class) {
      if (!this.cls.isAssignableFrom((Class<?>) object)) {
        throw new ConstraintException(ConstraintExceptionType.TYPE_NOT_ALLOWED);
      }
    } else {
      if (!this.cls.isAssignableFrom(object.getClass())) {
        throw new ConstraintException(ConstraintExceptionType.TYPE_NOT_ALLOWED);
      }
    }
  }

  /*
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return this.cls.getName();
  }
}
