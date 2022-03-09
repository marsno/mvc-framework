package pers.mars.mvc.context.support;

import pers.mars.mvc.context.BeanDefinition;
import pers.mars.mvc.context.BeanDefinitionStrategy;
import pers.mars.mvc.context.config.BeanId;
import pers.mars.mvc.context.config.Component;
import pers.mars.mvc.context.di.Autowired;
import pers.mars.mvc.context.di.Value;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** BeanDefinitionMapBuilder for spring ioc */
public class BasicBeanDefinitionStrategy implements BeanDefinitionStrategy {

  @Override
  public Map<String, BeanDefinition> build(List<Class<?>> classObjects) {
    Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();
    for (Class<?> c : classObjects) {
      // 不存在 @Component 注解, 跳过
      if ( ! c.isAnnotationPresent(Component.class) ) continue;

      // 构建 bean
      BeanDefinition beanDefinition = new BeanDefinition();
      String beanId = c.getAnnotation(Component.class).value();
      beanDefinition.setId( beanId );
      beanDefinition.setType( c );
      beanDefinition.setScope( c.getAnnotation(Component.class).scope() );
      for (Field field : c.getDeclaredFields()) {
        if (field.isAnnotationPresent(Autowired.class)) {
          beanDefinition.registerField(
                  field,
                  new BeanId( field.getAnnotation(Autowired.class).value() ) );
        }
        else if (field.isAnnotationPresent(Value.class)) {
          String value = field.getAnnotation(Value.class).value();
          if ( field.getType() == String.class )
            beanDefinition.registerField( field, value );
          else if ( field.getType() == Integer.class )
            beanDefinition.registerField( field, Integer.parseInt(value) );
          else if ( field.getType() == Float.class )
            beanDefinition.registerField( field, Float.parseFloat(value) );
        }
      }

      // BeanDefinition 构建完成后, 放入 beanDefinitionMap
      beanDefinitionMap.put( beanId, beanDefinition );
    }
    return beanDefinitionMap;
  }

}