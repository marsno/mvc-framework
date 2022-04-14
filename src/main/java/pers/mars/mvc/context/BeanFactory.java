package pers.mars.mvc.context;

public interface BeanFactory {

  /**
   * 从容器中获取bean的实例
   * @param beanId 获取bean实例的 bean id
   * @return bean的实例或者 {@code null}, 如果没有此id的bean,
   */
  Object getBean(String beanId);

  <T> T getBean(Class<T> requiredType);

}
