package pers.mars.mvc.context.annotation;

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

  // bean name
  String value() default "";

  // bean的scope
  BeanScope scope() default BeanScope.SINGLETON;

}