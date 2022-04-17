package pers.mars.mvc.servlet;

import java.util.HashMap;
import java.util.Map;

public class ModelAndView {

  public ModelAndView() {
    super();
  }

  // View 实例, 或者视图名的 String
  private Object view;

  /**
   * 所有的 model 对象
   *
   * K: 对象的变量名
   * V: 对象的实例
   */
  private Map<String,Object> modelMap = new HashMap<>();

  public void setViewName(String viewName) {
    this.view = viewName;
  }

  /**
   * @return view name, or {@code null}
   * 如果现在不是 view name String, 而是 View 实例.
   */
  public String getViewName() {
    return (this.view instanceof String ? (String) this.view : null);
  }

  /**
   * 添加一个对象
   * @param name 对象的变量名
   * @param value 对象的实例
   */
  public void addObject(String name, Object value) {
    this.modelMap.put(name, value);
  }

  /** getter */
  public Map<String, Object> getModelMap() {
    return this.modelMap;
  }

}
