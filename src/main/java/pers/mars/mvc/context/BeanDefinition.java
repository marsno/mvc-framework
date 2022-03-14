package pers.mars.mvc.context;

import pers.mars.mvc.context.annotation.BeanScope;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

// 用于存储一个bean的定义信息
public class BeanDefinition {

  // bean 的 id
  private String id;

  // singleton or prototype
  private BeanScope scope;

  // bean 的 class
  private Class<?> type;

  // BeanId
  private Map<Field,Object> fieldMap = new HashMap<>();

  // getter
  public Map<Field,Object> getFieldMap() {
    return this.fieldMap;
  }

  // getter
  public Class<?> getBeanClass() {
    return this.type;
  }

  // getter
  public String getId() {
    return this.id;
  }

  // getter
  public BeanScope getScope() {
    return this.scope;
  }

  // setter
  public void setType(Class<?> type) {
    this.type = type;
  }

  // setter
  public void setId(String id) {
    this.id = id;
  }

  // setter
  public void setScope(BeanScope scope) {
    this.scope = scope;
  }

  /**
   * 注册 bean 的一个字段.
   * @param field 字段的 Field 对象
   * @param value 字段的值
   */
  public void registerField(Field field, Object value) {
    this.fieldMap.put(field, value);
  }

  // constructor
  public BeanDefinition() {
    super();
  }

  // normal constructor
  public BeanDefinition(String id, BeanScope scope, Class<?> type) {
    this.id = id;
    this.scope = scope;
    this.type = type;
  }

}