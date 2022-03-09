package pers.mars.mvc.web.servlet.context;

import pers.mars.mvc.context.BeanDefinition;
import pers.mars.mvc.context.BeanDefinitionStrategy;
import pers.mars.mvc.context.config.BeanId;
import pers.mars.mvc.context.config.BeanScope;
import pers.mars.mvc.context.config.Component;
import pers.mars.mvc.context.di.Autowired;
import pers.mars.mvc.context.di.Value;
import pers.mars.mvc.web.servlet.HandlerMapping;
import pers.mars.mvc.web.bind.annotation.Controller;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServletBeanDefinitionStrategy implements BeanDefinitionStrategy {

  /**
   * 创建所有 BeanDefinition
   * @param classList 所有类的 class 对象
   * @return map. K 是 bean 的 id, V 是 bean 的 BeanDefinition
   */
  @Override
  public Map<String, BeanDefinition> build(List<Class<?>> classList) {
    // 构建 spring mvc 内部使用的 bean
    Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();

    // 根据以下 3 种情况, 构建 BeanDefinition
    // - 存在 @Component 注解
    // - 存在 @Controller 注解
    // - 是 HandlerMapping 的实现类
    for (Class<?> c : classList) {
      BeanDefinition beanDefinition = null;
      if (c.isInterface())
        continue;
      else if ( c.isAnnotationPresent(Component.class) )
        beanDefinition = this.buildByComponent(c);
      else if ( c.isAnnotationPresent(Controller.class) )
        beanDefinition = this.buildByController(c);
      else if ( HandlerMapping.class.isAssignableFrom(c) )
        beanDefinition = this.buildByHandlerMapping(c);

      if (beanDefinition != null)
        beanDefinitionMap.put(beanDefinition.getId(), beanDefinition);
    }

    return beanDefinitionMap;
  }

  /**
   * 根据 @Component 创建 BeanDefinition
   *
   * invoked by this.build()
   */
  private BeanDefinition buildByComponent(Class<?> classObject) {
    BeanDefinition beanDefinition = new BeanDefinition();
    beanDefinition.setId( classObject.getAnnotation(Component.class).value() );
    beanDefinition.setType( classObject );
    beanDefinition.setScope( classObject.getAnnotation(Component.class).scope() );
    for (Field field : classObject.getDeclaredFields()) {
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
    return beanDefinition;
  }

  /**
   * 根据 @Controller 创建 BeanDefinition
   *
   * invoked by this.build()
   */
  private BeanDefinition buildByController(Class<?> classObject) {
    BeanDefinition beanDefinition = new BeanDefinition();

    // build bean id
    String beanId = classObject.getAnnotation(Controller.class).value();
    if (beanId.equals("")) {
      String className = classObject.getSimpleName();
      beanId = className.substring(0,1).toLowerCase()
              + className.substring(1);
    }

    // build BeanDefinition
    beanDefinition.setId( beanId );
    beanDefinition.setType( classObject );
    beanDefinition.setScope( classObject.getAnnotation(Controller.class).scope() );
    for (Field field : classObject.getDeclaredFields()) {
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

    return beanDefinition;
  }

  /**
   * 创建 BeanDefinition 为了所有实现了 HandlerMapping 的接口
   *
   * invoked by this.build()
   */
  private BeanDefinition buildByHandlerMapping(Class<?> classObject) {
    BeanDefinition beanDefinition = new BeanDefinition();

    // build bean id
    String className = classObject.getSimpleName();
    String beanId = className.substring(0,1).toLowerCase()
            + className.substring(1);

    // build BeanDefinition
    beanDefinition.setId( beanId );
    beanDefinition.setType( classObject );
    beanDefinition.setScope( BeanScope.SINGLETON );
    for (Field field : classObject.getDeclaredFields()) {
      if (field.isAnnotationPresent(Autowired.class)) {
        beanDefinition.registerField(
                field,
                new BeanId( beanId ) );
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

    return beanDefinition;
  }

}
