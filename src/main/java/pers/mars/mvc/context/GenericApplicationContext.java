package pers.mars.mvc.context;

import pers.mars.mvc.context.annotation.Autowired;
import pers.mars.mvc.context.annotation.BeanId;
import pers.mars.mvc.context.annotation.BeanScope;
import pers.mars.mvc.context.annotation.Lazy;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ioc 容器管理的 bean, bean 的形式有 2 种
 * @code BeanScope.SINGLETON, 单例的规范为, 单例的依赖只能是单例, 单例支持循环依赖
 * @code BeanScope.PROTOTYPE, 原型的规范为, 原型的依赖可以是单例, 也可以是原型, 原型不支持循环依赖
 *
 * 容器初始化时, 会检测 BeanDefinition 的单例和原型是否符合规范.
 * 若单例的依赖有原型, 会抛异常
 * 若原型有循环依赖, 也会抛异常
 */
public class GenericApplicationContext implements ApplicationContext, BeanDefinitionRegistry {

  protected BeanDefinitionGenerator beanDefinitionGenerator = new BeanDefinitionGenerator();

  // key 是 bean name, value 是 bean 的 BeanDefinition
  protected Map<String, BeanDefinition> beanDefinitionMap = null;

  // 单例 bean name -> singleton instance
  protected Map<String, Object> singletonObjects = new HashMap<>();

  // in creating
  protected Map<String, Object> earlySingletonObjects = new HashMap<>();
  protected Map<String, Object> earlyPrototypeObjects = new HashMap<>();

  /**
   * 注册 bean, 交由 ioc 容器管理
   * @param id bean 的 id
   * @param scope singleton or prototype
   * @param type bean 对应的 class
   */
  public void registerBean(String id, BeanScope scope, Class<?> type) {
    // 构建 bean 的 BeanDefinition
    BeanDefinition beanDefinition = new BeanDefinition(id, scope, type);
    for ( Field field : type.getDeclaredFields() ) {
      if ( field.isAnnotationPresent(Autowired.class) ) {
        BeanId fieldBeanId = new BeanId( field.getAnnotation(Autowired.class).value() );
        beanDefinition.registerField( field, fieldBeanId );
      }
    }
    this.beanDefinitionMap.put(id, beanDefinition);
  }

  /**
   * 添加一个 BeanDefinition
   * @param beanDefinition 要注册的 BeanDefinition
   */
  @Override
  public void registerBeanDefinition( BeanDefinition beanDefinition ) {

    this.beanDefinitionMap.put(beanDefinition.getId(), beanDefinition);
  }

  /**
   * 从容器中获取 bean 的实例
   * @return 返回 bean 的实例, 如果容器中没有此 id 对应的 bean, 返回 null
   * @param beanId 想要获取 bean 实例的 bean id
   */
  @Override
  public synchronized Object getBean(String beanId) {
    BeanDefinition beanDefinition = this.beanDefinitionMap.get(beanId);
    if (beanDefinition == null)
      return null;

    BeanScope beanScope = beanDefinition.getScope();
    if (beanScope == BeanScope.SINGLETON)
      return this.getSingletonObject(beanDefinition);
    return this.getPrototypeObject(beanDefinition);
  }

  /**
   * 根据类型获取 bean
   * @return 有返回 bean instance, 没有返回 null
   */
  @Override
  public synchronized <T> T getBean(Class<T> requiredType) {
    for (BeanDefinition beanDefinition : this.beanDefinitionMap.values()) {
      boolean isMatched = requiredType.isAssignableFrom(beanDefinition.getBeanClass());
      if (isMatched) return requiredType.cast(this.getBean(beanDefinition.getId()));
    }
    return null;
  }

  // 仅被 this.getBean(String beanId) 调用
  protected Object getSingletonObject(BeanDefinition beanDefinition) {
    try {
      String beanId = beanDefinition.getId();
      Object processingSingletonObject;

      // 从单例池中尝试获取 bean
      processingSingletonObject = this.singletonObjects.getOrDefault(beanId, null);
      if (processingSingletonObject != null) return processingSingletonObject;

      // 单利池中没有此 bean, 从构建池中获取
      processingSingletonObject = this.earlySingletonObjects.getOrDefault(beanId, null);
      if (processingSingletonObject != null) return processingSingletonObject;

      // 如果都没有, 说明需要生成 bean
      processingSingletonObject = beanDefinition.getBeanClass().getDeclaredConstructor().newInstance();
      this.earlySingletonObjects.put(beanId, processingSingletonObject);

      // 属性注入
      for (Map.Entry<Field,Object> entry : beanDefinition.getFieldMap().entrySet()) {
        Field field = entry.getKey();
        field.setAccessible(true);
        String fieldBeanId = ((BeanId) entry.getValue()).getValue();
        field.set( processingSingletonObject, this.getBean(fieldBeanId) );
      }
      this.earlySingletonObjects.remove(beanId);
      this.singletonObjects.put(beanId, processingSingletonObject);
      return processingSingletonObject;
    }
    catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      e.printStackTrace();
    }
    return null;
  }

  // 仅被 this.getBean(String beanId) 调用
  protected Object getPrototypeObject(BeanDefinition beanDefinition) {
    try {
      String beanId = beanDefinition.getId();
      Object processingPrototypeObject;

      // 是否在构建
      processingPrototypeObject = this.earlyPrototypeObjects.getOrDefault(beanId, null);
      if (processingPrototypeObject != null) {
        return null;
      }

      // 新建 bean
      processingPrototypeObject = beanDefinition.getBeanClass().getDeclaredConstructor().newInstance();
      this.earlyPrototypeObjects.put(beanId, processingPrototypeObject);

      // 注入属性
      for (Map.Entry<Field,Object> entry : beanDefinition.getFieldMap().entrySet()) {
        Field field = entry.getKey();
        field.setAccessible(true);
        String fieldBeanId = ((BeanId) entry.getValue()).getValue();
        field.set( processingPrototypeObject, this.getBean(fieldBeanId) );
      }
      return processingPrototypeObject;
    }
    catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * 将带有指定注解的 bean 归入容器控制
   * @param packageName 要扫描的包, 如果是 <code>""</code>, 扫描所有包
   */
  protected void initializeBeanDefinitionMap(String packageName) {
    List<Class<?>> classObjects = ClassScanner.scan(packageName);
    if (classObjects == null) {
      this.beanDefinitionMap = new HashMap<>();
      return;
    }
    this.beanDefinitionMap = this.beanDefinitionGenerator.generateBeanDefinitionMap(classObjects);
  }

  // 根据 this.beanDefinitionMap 创建所有单例
  protected void doCreateSingletonObjects() {
    for (BeanDefinition beanDefinition : this.beanDefinitionMap.values()) {
      if (beanDefinition.getBeanClass().isAnnotationPresent(Lazy.class)) {
        continue;
      }
      if (beanDefinition.getScope() == BeanScope.SINGLETON) {
        this.getBean(beanDefinition.getId());
      }
    }
  }

  public GenericApplicationContext() {
    super();
  }

}