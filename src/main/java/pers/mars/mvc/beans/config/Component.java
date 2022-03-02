package pers.mars.mvc.beans.config;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;

/**
 * 具有此注解的类会被IoC容器托管
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Component {

  // bean的id
  String value();

  // bean的scope
  BeanScope scope() default BeanScope.SINGLETON;

}