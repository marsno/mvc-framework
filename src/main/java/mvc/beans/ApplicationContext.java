package mvc.beans;

import mvc.beans.config.BeanScope;
import mvc.beans.config.BeanId;
import mvc.beans.di.Autowired;
import mvc.beans.di.Value;
import mvc.beans.support.BasicBeanDefinitionStrategy;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicationContext implements BeanFactory {

  /** no param constructor */
  public ApplicationContext() {
    super();
  }

  /**
   * 初始化容器时, 指定扫描的包
   * @param packageName 指定扫描的包名, 若为 <code>""</code>, 扫描所有包.
   */
  public ApplicationContext(String packageName) {
    super();
    this.beanDefinitionStrategy = new BasicBeanDefinitionStrategy();
    this.initBeanDefinitionMap(packageName);
    this.initSingletonObjects();
  }

  /** parent context */
  protected ApplicationContext parentContext = null;

  /** 用于获取所有类的 class 对象 */
  protected Scanner scanner = new Scanner();

  /**
   * 用于初始化 this.beanDefinitionMap 的 builder
   *
   * BeanDefinitionBuilder 是一个 interface, 根据使用场景,
   * 实现此接口, 可以定制不同的 ioc 容器
   *
   * 默认使用的实现类是: BasicBeanDefinitionStrategy
   */
  protected BeanDefinitionStrategy beanDefinitionStrategy = null;

  /**
   * 所有 bean 的 BeanDefinition, 包括在 this.singletonObjects
   * 中已经实例化了的单例 bean 的 BeanDefinition
   *
   * String: bean的id
   */
  protected Map<String, BeanDefinition> beanDefinitionMap = null;

  /**
   * 单例map
   *
   * String: bean的id
   * Object: bean的单例
   */
  protected Map<String,Object> singletonObjects = new HashMap<String,Object>();

  /**
   * 从容器中获取bean的实例, 如果此容器中没有, 尝试从父容器中获取
   * @param beanId 想要获取bean实例的 bean id
   * @return 返回 bean 的实例, 如果容器中没有此 id 对应的 bean, 返回 null.
   */
  public Object getBean(String beanId) {
    // BeanFactory 没有这个bean, 返回{@Code null}
    if (this.beanDefinitionMap.get(beanId) == null)
      if (this.parentContext != null)
        return this.parentContext.getBean(beanId);
      else
        return null;

    // BeanFactory 有这个bean, 则根据scope判断返回单例bean, 还是新的原型bean
    if (this.beanDefinitionMap.get(beanId).getScope() == BeanScope.SINGLETON) {
      return this.singletonObjects.get(beanId);
    }
    else {
      try {
        BeanDefinition beanDefinition = this.beanDefinitionMap.get(beanId);
        Object object = beanDefinition.getBeanClass().newInstance();
        for (Map.Entry<Field,Object> entry : beanDefinition.getFieldMap().entrySet()) {
          Field field = entry.getKey();
          field.setAccessible(true);
          if (entry.getValue().getClass() == BeanId.class) {
            String fieldBeanId = ((BeanId) entry.getValue()).getValue();
            field.set( object, this.getBean(fieldBeanId) );
          }
          else {
            field.set( object, entry.getValue() );
          }
        }
        return object;
      }
      catch (InstantiationException | IllegalAccessException e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  /**
   * 将带有指定注解的 bean 归入容器控制
   * @param packageName 要扫描的包, 如果是 <code>""</code>, 扫描所有包
   */
  protected void initBeanDefinitionMap(String packageName) {
    List<Class<?>> classObjects = this.scanner.scan(packageName);
    if (classObjects == null) {
      this.beanDefinitionMap = new HashMap<>();
      return;
    }
    this.beanDefinitionMap = this.beanDefinitionStrategy.build(classObjects);
  }

  /** 根据 this.beanDefinitionMap 创建所有单例 */
  protected void initSingletonObjects() {
    if (this.beanDefinitionMap.isEmpty()) return;

    // 先创建所有单例bean, 但并未注入fields
    // 这些创建的单例bean都放入this.singletonObjects
    // 同时, 这些单例也被rawSingletonBeans所引用, 表示还未注入属性
    Map<String,Object> rawSingletonBeans = new HashMap<>();
    for (Map.Entry<String,BeanDefinition> entry : this.beanDefinitionMap.entrySet()) {
      if (entry.getValue().getScope() == BeanScope.SINGLETON) {
        try {
          Object singleBean = null;
          singleBean = entry.getValue().getBeanClass().newInstance();
          this.singletonObjects.put( entry.getValue().getId(), singleBean );
          rawSingletonBeans.put( entry.getValue().getId(), singleBean );
        }
        catch (InstantiationException | IllegalAccessException e) {
          e.printStackTrace();
        }
      }
    }

    // 遍历初始化单例bean
    for (Map.Entry<String,Object> entry : rawSingletonBeans.entrySet()) {
      Object rawSingletonBean = entry.getValue();
      Map<Field,Object> fieldDefinitionMap = this.beanDefinitionMap.get(entry.getKey()).getFieldMap();
      for (Map.Entry<Field,Object> fieldDefinition : fieldDefinitionMap.entrySet()) {
        Object fieldValue = fieldDefinition.getValue();
        try {
          if (fieldValue.getClass() == BeanId.class) {
            String beanId = ((BeanId) fieldValue).getValue();
            Object newFieldValue = this.getBean(beanId);
            Field field = fieldDefinition.getKey();
            field.setAccessible(true);
            field.set( rawSingletonBean, newFieldValue );
          }
          else {
            Field field = fieldDefinition.getKey();
            field.setAccessible(true);
            field.set( rawSingletonBean, fieldValue );
          }
        }
        catch (IllegalAccessException e) {
          e.printStackTrace();
        }
      }
    }
  }

  /** setter */
  public void setParentContext(ApplicationContext context) {
    this.parentContext = context;
  }

  /**
   * 注册 bean, 交由 ioc 容器管理
   * @param id bean 的 id
   * @param scope singleton or prototype
   * @param type bean 对应的 class
   * @return 是否注册成功
   */
  public boolean registerBean( String id, BeanScope scope, Class<?> type ) {

    // 构建 bean 的 BeanDefinition
    BeanDefinition beanDefinition = new BeanDefinition(id, scope, type);
    for ( Field field : type.getDeclaredFields() ) {
      if ( field.isAnnotationPresent(Autowired.class) ) {
        BeanId fieldBeanId = new BeanId( field.getAnnotation(Autowired.class).value() );
        beanDefinition.registerField( field, fieldBeanId );
      }
      else if ( field.isAnnotationPresent(Value.class) ) {
        String value = field.getAnnotation(Value.class).value();
        if ( field.getType() == String.class )
          beanDefinition.registerField( field, value );
        else if ( field.getType() == Integer.class )
          beanDefinition.registerField( field, Integer.parseInt(value) );
        else if ( field.getType() == Float.class )
          beanDefinition.registerField( field, Float.parseFloat(value) );
      }
    }
    this.beanDefinitionMap.put(id,beanDefinition);

    // 如果是单例, new 出单例对象
    if (scope == BeanScope.SINGLETON) {
      this.addSingleton(id);
    }

    return true;
  }

  /**
   * 根据给定的 bean id 和 this.beanDefinitionMap 中的定义, 构建单例
   * @param id bean id
   */
  public void addSingleton(String id) {
    BeanDefinition beanDefinition = this.beanDefinitionMap.getOrDefault(id,null);
    if (beanDefinition == null) return;
    try {
      Object singletonObject = beanDefinition.getBeanClass().newInstance();
      this.singletonObjects.put( id, singletonObject );
      for (Map.Entry<Field,Object> entry : beanDefinition.getFieldMap().entrySet()) {
        Field field = entry.getKey();
        Object fieldValue = entry.getValue();
        if (fieldValue.getClass() == BeanId.class) {
          String beanId = ((BeanId) fieldValue).getValue();
          Object newFieldValue = this.getBean(beanId);
          field.setAccessible(true);
          field.set(singletonObject, newFieldValue);
        }
        else {
          field.setAccessible(true);
          field.set(singletonObject, fieldValue);
        }
      }
    }
    catch (InstantiationException | IllegalAccessException e) {
      e.printStackTrace();
    }
  }

}
