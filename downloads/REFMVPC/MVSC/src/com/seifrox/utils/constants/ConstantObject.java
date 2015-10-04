/*
 * ConstantObject.java created on 24.05.2005
 * 
 * (c) alexander noehrer
 */

package com.seifrox.utils.constants;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * @author alexander noehrer
 */
public class ConstantObject implements Serializable {

  /** serialVersionUID needed for the serialize mechanism */
  private static final long serialVersionUID = -239909427883313788L;

  // TODO master index überdenken nicht gscheida a Map<String, T> für jede abgeleitete?
  private static final Map<Type, Map<String, ConstantObject>> MASTER_INDEX = new HashMap<Type, Map<String, ConstantObject>>();

  /** holds the value of the property name */
  protected String name;

  /**
   * constructor
   * 
   * @param name
   *          the unique name of the constant
   */
  protected ConstantObject(final String name) {
    if (name == null) {
      throw new NullPointerException("the name may not be null");
    }
    Map<String, ConstantObject> map = ConstantObject.MASTER_INDEX.get(this.getClass());
    if (map == null) {
      map = new HashMap<String, ConstantObject>();
      ConstantObject.MASTER_INDEX.put(this.getClass(), map);
    }
    if (map.containsKey(name)) {
      throw new IllegalArgumentException("a constant with this identifer already exists for this class");
    }
    this.name = name;
    map.put(name, this);
  }

  /**
   * looks up a constant by it's name
   * 
   * @param <T>
   * @param type
   * @param name
   * @return a ConstantObject
   */
  public static final <T extends ConstantObject> T lookup(final Class<T> type, final String name) {
    final Map<String, ? extends ConstantObject> map = ConstantObject.MASTER_INDEX.get(type);
    if (map != null) {
      return type.cast(map.get(name));
    }
    return null;
  }

  /**
   * resolve a read in object
   * 
   * @return the resolved object read in
   * @throws ObjectStreamException
   *           if there is a problem reading the object.
   * @throws RuntimeException
   *           If the read object doesn't exist.
   */
  protected Object readResolve() throws ObjectStreamException {
    final Object result = ConstantObject.lookup(this.getClass(), this.getName());
    if (result == null) {
      throw new RuntimeException("constant not found for name: " + this.getName());
    }
    return result;
  }

  /**
   * getter for property name
   * 
   * @return value of the name
   */
  public String getName() {
    return this.name;
  }

  /*
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return this.getClass().getName() + "." + this.name;
  }
}
