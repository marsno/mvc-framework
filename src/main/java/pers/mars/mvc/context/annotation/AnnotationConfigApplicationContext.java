package pers.mars.mvc.context.annotation;

import pers.mars.mvc.context.GenericApplicationContext;

public class AnnotationConfigApplicationContext extends GenericApplicationContext {

  protected ClassPathBeanDefinitionScanner scanner;

  /**
   * 通过配置类构建 AnnotationConfigApplicationContext
   * @param configuration 配置类的 class 对象
   */
  public AnnotationConfigApplicationContext(Class<?> configuration) {
    if (configuration == null) {
      return;
    }
    if ( !configuration.isAnnotationPresent(Configuration.class) ) {
      return;
    }
    if ( configuration.isAnnotationPresent(ComponentScan.class) ) {
      String basePackageName = configuration.getAnnotation(ComponentScan.class).value();
      this.initializeBeanDefinitionMap(basePackageName);
      this.doCreateSingletonObjects();
    }
  }

}
