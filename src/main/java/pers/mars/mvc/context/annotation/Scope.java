package pers.mars.mvc.context.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Scope {

  BeanScope value() default BeanScope.SINGLETON;

}
