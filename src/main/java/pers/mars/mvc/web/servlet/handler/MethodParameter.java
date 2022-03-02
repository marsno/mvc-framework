package pers.mars.mvc.web.servlet.handler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * 提供了一个二次封装的类, 表示 parameter, 并且存储了 parameter 所在的 method 的信息.
 * 将 parameter 与其所在的 method 绑定了起来, 比较于 jdk 提供的 Parameter 更方便,
 * 使用时不会书写重复的代码.
 *
 * 这个类只针对普通方法, 不能用于构造方法.
 */
public class MethodParameter {

  /**
   * @param method parameter 所在的 method
   * @param index 0 means the 1st parameter, 1 means the 2nd
   * parameter, etc.
   */
  public MethodParameter(Method method, int index) {
    this.method = method;
    this.index = index;
    this.parameter = method.getParameters()[index];
  }

  /** parameter 所在的 method */
  private Method method = null;

  /** parameter 所在的方法的 index */
  private int index = 0;

  /** parameter 的反射对象 */
  private Parameter parameter = null;

  /**
   * 在这个 parameter 上是否有指定的注解.
   * @param annotationType 注解的 type
   * @return 有返回 {@code true}, 没有返回 {@code false}.
   */
  public <A extends Annotation> boolean hasParameterAnnotation(Class<A> annotationType) {
    return this.parameter.isAnnotationPresent(annotationType);
  }

  /**
   * 获取 parameter 上指定的 annotation
   * @param annotationType 指定的 annotation
   * @return 注解对象, 或 null 如果 parameter 上没有此注解
   */
  public <A extends Annotation> A getParameterAnnotation(Class<A> annotationType) {
    return this.parameter.getAnnotation(annotationType);
  }

  /**
   * 在这个 parameter 所在的 method 上是否有指定的注解.
   * @param annotationType 查询的注解的 type
   * @return 有返回 {@code true}, 没有返回 {@code false}.
   */
  public <A extends Annotation> boolean hasMethodAnnotation(Class<A> annotationType) {
    return this.method.isAnnotationPresent(annotationType);
  }

  /**
   * 获取 parameter 所在的方法上指定的 annotation
   * @param annotationType 指定 annotation
   * @return 注解对象, 或 null, 如果方法上没有此注解
   */
  public <A extends Annotation> A getMethodAnnotation(Class<A> annotationType) {
    return this.method.getAnnotation(annotationType);
  }

  /** 获取参数的 type */
  public Class<?> getParameterType() {
    return this.parameter.getType();
  }

}
