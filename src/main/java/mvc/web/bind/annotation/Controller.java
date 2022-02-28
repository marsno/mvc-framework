package mvc.web.bind.annotation;

import mvc.beans.config.BeanScope;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Controller {

  // bean id
  String value() default "";

  BeanScope scope() default BeanScope.SINGLETON;

}