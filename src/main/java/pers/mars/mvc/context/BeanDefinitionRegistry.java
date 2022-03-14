package pers.mars.mvc.context;

public interface BeanDefinitionRegistry {

  /**
   * 添加一个 BeanDefinition
   * @param beanDefinition 要注册的 BeanDefinition
   */
  void registerBeanDefinition(BeanDefinition beanDefinition);

}