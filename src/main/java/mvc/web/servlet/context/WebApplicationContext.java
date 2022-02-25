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

    List<BeanDefinition> controllerBeanDefinitionList = new ArrayList<>();
    for ( Map.Entry<String,BeanDefinition> entry : this.beanDefinitionMap.entrySet() ) {
      if ( !entry.getValue().getBeanClass().isAnnotationPresent(Controller.class) )
        continue;
      controllerBeanDefinitionList.add( entry.getValue() );
    }

    if (controllerBeanDefinitionList.size() == 0) return null;

    return controllerBeanDefinitionList;

  }

  /**
   * 创建 WebApplicationContext 并制定一个 strategy 用于定义 bean definition
   * @param beanDefinitionStrategy 指定一个 strategy
   */
  public WebApplicationContext(BeanDefinitionStrategy beanDefinitionStrategy) {
    super();
    this.beanDefinitionStrategy = beanDefinitionStrategy;
  }

}