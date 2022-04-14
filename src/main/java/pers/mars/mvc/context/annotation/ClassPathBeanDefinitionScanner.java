package pers.mars.mvc.context.annotation;

import pers.mars.mvc.context.BeanDefinitionRegistry;
import pers.mars.mvc.context.ClassScanner;
import java.util.List;

/**
 * 给定一个实现了 BeanDefinitionRegistry 接口的 ioc 容器构造此对象
 * @see BeanDefinitionRegistry
 *
 * 这个类可以读取带有注解的类, 通过反射读取 class 对象的信息,
 * 生成 BeanDefinition 并注册到给定的 ioc 容器中
 * @see pers.mars.mvc.context.annotation.Component
 * @see pers.mars.mvc.context.annotation.Controller
 * @see pers.mars.mvc.context.annotation.Service
 * @see pers.mars.mvc.context.annotation.Repository
 */
public class ClassPathBeanDefinitionScanner {

  protected BeanDefinitionRegistry beanDefinitionRegistry;

  /**
   * 扫描一个指定的包, 会将扫描到的带有指定注解的 bean 注册到 ioc 容器中
   * @param packageName 只扫描哪个包, 如果等于 <code>""</code> 扫描 classpath
   */
  public void registerBean(String packageName) {
    List<Class<?>> classes = ClassScanner.scan(packageName);
  }

  public BeanDefinitionRegistry getBeanDefinitionRegistry() {
    return this.beanDefinitionRegistry;
  }

  public ClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry) {
    this.beanDefinitionRegistry = registry;
  }

}
