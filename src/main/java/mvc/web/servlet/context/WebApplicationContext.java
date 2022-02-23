package mvc.web.servlet.context;

import mvc.beans.ApplicationContext;
import mvc.beans.BeanDefinition;
import mvc.beans.BeanDefinitionStrategy;
import mvc.web.servlet.config.Controller;

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
public class WebApplicationContext extends ApplicationContext {

  /**
   * 遍历 this.singletonObjects, 返回所有 controller 的 BeanDefinition.
   * @return 所有 controller 的 BeanDefinition;
   *         或者 <code>null</code>, 如果没有 controller.
   */
  public List<BeanDefinition> getControllers() {
    List<BeanDefinition> controllerBeanDefinition = new ArrayList<>();
    for ( Map.Entry<String,BeanDefinition> entry : this.beanDefinitionMap.entrySet() ) {
      if ( !entry.getValue().getBeanClass().isAnnotationPresent(Controller.class) )
        continue;
      controllerBeanDefinition.add( entry.getValue() );
    }
    if (controllerBeanDefinition.size() == 0)
      return null;
    return controllerBeanDefinition;
  }

  public WebApplicationContext() {
    super();
  }

  /**
   * @param beanDefinitionStrategy 指定一个 strategy
   */
  public WebApplicationContext(BeanDefinitionStrategy beanDefinitionStrategy) {
    super();
    this.beanDefinitionStrategy = beanDefinitionStrategy;
  }

}