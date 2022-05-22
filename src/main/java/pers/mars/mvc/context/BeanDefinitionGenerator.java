package pers.mars.mvc.context;

import pers.mars.mvc.context.annotation.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeanDefinitionGenerator {

  protected BeanNameGenerator beanNameGenerator = new BeanNameGenerator();

  /**
   * 创建所有 BeanDefinition
   * @param classList 所有类的 class 对象
   * @return map. K 是 bean 的 id, V 是 bean 的 BeanDefinition
   */
  public Map<String, BeanDefinition> generateBeanDefinitionMap(List<Class<?>> classList) {
    Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();

    for (Class<?> classObject : classList) {

      if (!classObject.isAnnotationPresent(Component.class)
        && !classObject.isAnnotationPresent(Controller.class)
        && !classObject.isAnnotationPresent(Service.class)
        && !classObject.isAnnotationPresent(Repository.class)
        && !classObject.isAnnotationPresent(Configuration.class) ) {

        continue;
      }

      BeanDefinition beanDefinition = new BeanDefinition();
      String beanName = this.beanNameGenerator.generateBeanName(classObject);
      beanDefinition.setId(beanName);
      beanDefinition.setType(classObject);
      if (!classObject.isAnnotationPresent(Scope.class)) {
        beanDefinition.setScope(BeanScope.SINGLETON);
      } else {
        beanDefinition.setScope(classObject.getAnnotation(Scope.class).value());
      }
      for (Field field : classObject.getDeclaredFields()) {
        if (field.isAnnotationPresent(Autowired.class)) {
          BeanId fieldBeanName = new BeanId(field.getAnnotation(Autowired.class).value());
          beanDefinition.registerField(field, fieldBeanName);
        }
      }
      beanDefinitionMap.put(beanDefinition.getId(), beanDefinition);

    }

    return beanDefinitionMap;
  }

}
