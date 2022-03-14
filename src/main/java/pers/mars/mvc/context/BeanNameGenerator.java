package pers.mars.mvc.context;

import pers.mars.mvc.context.annotation.*;

public class BeanNameGenerator {

  /**
   * 将类名首字母小写
   */
  protected String generateBeanNameByClassName(String className) {
    return className.substring(0,1).toLowerCase() + className.substring(1);
  }

  /**
   * @return bean name, 如果不含有对应注解, return null
   */
  public <T> String generateBeanName(Class<T> classObject) {
    if (classObject.isAnnotationPresent(Component.class)) {
      String beanName = classObject.getAnnotation(Component.class).value();
      if (!beanName.equals("")) {
        return beanName;
      }
      else {
        return this.generateBeanNameByClassName(classObject.getSimpleName());
      }
    }
    else if (classObject.isAnnotationPresent(Controller.class)) {
      String beanName = classObject.getAnnotation(Controller.class).value();
      if (!beanName.equals("")) {
        return beanName;
      }
      else {
        return this.generateBeanNameByClassName(classObject.getSimpleName());
      }
    }
    else if (classObject.isAnnotationPresent(Service.class)) {
      String beanName = classObject.getAnnotation(Service.class).value();
      if (!beanName.equals("")) {
        return beanName;
      }
      else {
        return this.generateBeanNameByClassName(classObject.getSimpleName());
      }
    }
    else if (classObject.isAnnotationPresent(Repository.class)) {
      String beanName = classObject.getAnnotation(Repository.class).value();
      if (!beanName.equals("")) {
        return beanName;
      }
      else {
        return this.generateBeanNameByClassName(classObject.getSimpleName());
      }
    }
    else if (classObject.isAnnotationPresent(Configuration.class)) {
      String beanName = classObject.getAnnotation(Configuration.class).value();
      if (!beanName.equals("")) {
        return beanName;
      }
      else {
        return this.generateBeanNameByClassName(classObject.getSimpleName());
      }
    }
    else {
      return null;
    }
  }

  public BeanNameGenerator() {
    super();
  }

}