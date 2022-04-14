package pers.mars.mvc.context.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 在配置类上使用此注解, 配置扫描的包
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ComponentScan {

  // 扫描的包名, 形如 "mvc.context"
  String value() default "";

}
