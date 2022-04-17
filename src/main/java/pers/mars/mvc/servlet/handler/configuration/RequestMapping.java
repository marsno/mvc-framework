package pers.mars.mvc.servlet.handler.configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target( {ElementType.TYPE, ElementType.METHOD} )
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {

  // URI
  String value();

  // 当前 URI 路径下, 能够接收的 request method
  RequestMethod[] method() default {};

}
