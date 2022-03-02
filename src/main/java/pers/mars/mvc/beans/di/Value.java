package pers.mars.mvc.beans.di;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Value {

  /**
   * 注入的值
   *
   * 可以注入的类型:
   * - String
   * - Integer
   * - Float
   */
  String value();

}