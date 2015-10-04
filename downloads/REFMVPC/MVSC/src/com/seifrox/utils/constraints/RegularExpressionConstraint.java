/*
 * RegularExpressionConstraint.java created on 20.12.2005
 * 
 * (c) alexander noehrer
 */

package com.seifrox.utils.constraints;

import java.util.regex.Pattern;

import com.seifrox.utils.StringUtils;

/**
 * @author alexander noehrer
 */
public class RegularExpressionConstraint extends ObjectConstraint<String> {
  private static final long serialVersionUID = -50571347955651679L;

  private final Pattern pattern;

  /**
   * constructor
   * 
   * @param regularExpression
   */
  public RegularExpressionConstraint(final String regularExpression) {
    this(false, regularExpression);
  }

  /**
   * constructor
   * 
   * @param optional
   * @param regularExpression
   */
  public RegularExpressionConstraint(final boolean optional, final String regularExpression) {
    super(optional);
    this.pattern = Pattern.compile(regularExpression);
    if (regularExpression == null) {
      throw new ConstraintRuntimeException(ConstraintExceptionType.NULL_NOT_ALLOWED);
    }
  }

  /*
   * @see com.seifrox.utils.constraints.ObjectConstraint#validate(T)
   */
  @Override
  public void check(final String string) throws ConstraintException {
    super.check(string);
    if ((string != null) && !this.pattern.matcher(string).matches()) {
      throw new ConstraintException(ConstraintExceptionType.DOES_NOT_MATCH_PATTERN, " " + string);
    }
  }

  /**
   * getter for property pattern
   * 
   * @return value of the pattern
   */
  public Pattern getPattern() {
    return this.pattern;
  }

  /*
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return StringUtils.printf("%s %s", RegularExpressionConstraint.class.getName(), this.pattern);
  }
}
