package pers.mars.mvc.web.servlet.context;

import pers.mars.mvc.context.BeanDefinition;
import pers.mars.mvc.context.annotation.AnnotationConfigApplicationContext;
import pers.mars.mvc.context.annotation.Controller;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 针对 spring mvc 的 ioc 容器
 *
 * 会控制的bean
 * - @Component
 * - @Controller
 */
public class WebApplicationContext extends AnnotationConfigApplicationContext {

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
      controllerBeanDefinitionList.add( beanDefinition );
    }

    if (controllerBeanDefinitionList.size() == 0) return null;
    return controllerBeanDefinitionList;

  }

  /**
   * 指定一个配置类
   */
  public WebApplicationContext(Class<?> configuration) {
    super(configuration);
  }

}