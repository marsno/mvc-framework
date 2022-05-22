package pers.mars.mvc.context.annotation;

import pers.mars.mvc.context.BeanDefinition;
import pers.mars.mvc.context.GenericApplicationContext;
import java.util.ArrayList;
import java.util.List;

public class AnnotationConfigApplicationContext extends GenericApplicationContext {

  protected ClassPathBeanDefinitionScanner scanner;

  public AnnotationConfigApplicationContext() {
    super();
  }

  /**
   * 通过配置类构建 AnnotationConfigApplicationContext
   * @param configuration 配置类的 class 对象
   */
  public AnnotationConfigApplicationContext(Class<?> configuration) {
    super();
    if (configuration == null || !configuration.isAnnotationPresent(Configuration.class)
      || !configuration.isAnnotationPresent(ComponentScan.class))
      return;

    String basePackageName = configuration.getAnnotation(ComponentScan.class).value();
    this.initializeBeanDefinitionMap(basePackageName);
    this.doCreateSingletonObjects();
  }

  /**
   * 遍历 this.singletonObjects, 返回所有 controller 的 BeanDefinition.
   * @return 所有 controller 的 BeanDefinition;
   *         或者 <code>null</code>, 如果没有 controller.
   */
  public List<BeanDefinition> getControllers() {
    List<BeanDefinition> controllerBeanDefinitionList = new ArrayList<>();
    for (BeanDefinition beanDefinition : this.beanDefinitionMap.values()) {
      if ( !beanDefinition.getBeanClass().isAnnotationPresent(Controller.class) )
        continue;
      controllerBeanDefinitionList.add(beanDefinition);
    }
    if (controllerBeanDefinitionList.size() == 0) return null;
    return controllerBeanDefinitionList;
  }

}
