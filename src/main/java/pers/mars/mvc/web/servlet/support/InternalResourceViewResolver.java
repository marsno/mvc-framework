package pers.mars.mvc.web.servlet.support;

import pers.mars.mvc.web.servlet.View;
import pers.mars.mvc.web.servlet.ViewResolver;

public class InternalResourceViewResolver implements ViewResolver {

  // 前后缀
  private String prefix = "";
  private String suffix = "";

  /**
   * 传入 view name, 获取 View 对象.
   *
   * @return  the View object, or {@Code null} if view
   *          name 没有被定义.
   */
  @Override
  public View resolveViewName(String viewName) {
    viewName = this.prefix + viewName + this.suffix;
    return new InternalResourceView(viewName);
  }

}
