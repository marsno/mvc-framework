package mvc.beans;

import java.util.List;
import java.util.Map;

/**
 * 是 ApplicationContext 的 field, 实现此接口,
 * 可以定制不同的 ApplicationContext.
 */
public interface BeanDefinitionStrategy {

  /**
   * 创建所有 BeanDefinition
   * @param classObjects 所有类的 class 对象
   * @return bean 的所有定义
   */
  Map<String,BeanDefinition> build(List<Class<?>> classObjects);

}
