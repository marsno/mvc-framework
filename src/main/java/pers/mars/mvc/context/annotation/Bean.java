package pers.mars.mvc.context.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 还不支持此注解
 *
 * 在配置类的方法上使用此注解, ioc容器调用方法替代直接 {@code new} 对象
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Bean {
}
