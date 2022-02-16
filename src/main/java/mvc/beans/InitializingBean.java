package mvc.beans;

/**
 * IoC 容器, 创建 bean, 设置所有 fields
 * 后会调用 afterPropertiesSet 方法对
 * bean instance 进行复杂的初始化工作
 */
public interface InitializingBean {

  void afterPropertiesSet();

}
