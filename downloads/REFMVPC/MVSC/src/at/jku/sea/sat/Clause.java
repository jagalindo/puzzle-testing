/*
 * Clause.java created on 13.05.2009
 * 
 * (c) alexander noehrer
 */

package at.jku.sea.sat;

import java.io.Serializable;

/**
 * @author alexander noehrer
 */
public interface Clause extends Serializable {
  public void setIndex(int index);

  public int getIndex();

  public int[] getLiterals();
}
