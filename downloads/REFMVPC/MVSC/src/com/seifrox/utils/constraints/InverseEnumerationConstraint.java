/*
 * EnumerationConstraint.java created on 27.12.2005
 * 
 * (c) alexander noehrer
 */

package com.seifrox.utils.constraints;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

/**
 * @author alexander noehrer
 * @param <T>
 */
public class InverseEnumerationConstraint<T extends Object> extends ObjectConstraint<T> {
  private static final long serialVersionUID = 6679946595457906237L;

  private final Collection<T> elements;

  /**
   * constructor
   * 
   * @param elements the elements to add
   */
  public InverseEnumerationConstraint(final T... elements) {
    this(false, elements);
  }

  /**
   * constructor
   * 
   * @param optional determines if null values are allowed
   * @param elements the elements to add
   */
  public InverseEnumerationConstraint(final boolean optional, final T... elements) {
    super(optional);
    this.elements = new LinkedList<T>();
    for (final T element : elements) {
      this.elements.add(element);
    }
  }

  /*
   * @see com.seifrox.utils.constraints.ObjectConstraint#validate(T)
   */
  @Override
  public void check(final T object) throws ConstraintException {
    super.check(object);
    if (object != null) {
      if (this.elements.contains(object)) {
        throw new ConstraintException(ConstraintExceptionType.VALUE_IN_THE_LIST);
      }
    }
  }

  /**
   * adds an element to the valid values
   * 
   * @param element the element to add
   */
  public void addElement(final T element) {
    if (element != null) {
      this.elements.add(element);
    }
  }

  /**
   * adds elements to the valid values
   * 
   * @param elements the elements to add
   */
  public void addElements(final T... elements) {
    for (final T element : elements) {
      this.elements.add(element);
    }
  }

  /**
   * adds elements to the valid values
   * 
   * @param elements the elements to add
   */
  public void addElements(final Collection<T> elements) {
    this.elements.addAll(elements);
  }

  /**
   * getter for property elements
   * 
   * @return value of the elements
   */
  public Collection<T> getElements() {
    return Collections.unmodifiableCollection(this.elements);
  }
}
