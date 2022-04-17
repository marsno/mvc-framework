package pers.mars.mvc.servlet;

public interface ViewResolver {

  /**
   * 传入 view name, 获取 View 对象.
   *
   * @return    the View object, or <code>null</code>
   *            if view name 没有被定义.
   */
  View resolveViewName(String viewName);

}
