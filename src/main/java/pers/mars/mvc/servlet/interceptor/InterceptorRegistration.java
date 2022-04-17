package pers.mars.mvc.servlet.interceptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InterceptorRegistration {

  protected HandlerInterceptor handlerInterceptor = null;
  protected List<String> pathPatterns = new ArrayList<>();

  /**
   * 是否匹配 URI
   *
   * @param   URI 当前 HTTP request 的 URI
   *
   * @return  如果匹配, return true, 不匹配, return false
   */
  public boolean matches(String URI) {
    for (String pathPattern : this.pathPatterns) {
      if (URI.matches(pathPattern)) {
        return true;
      }
    }
    return false;
  }

  // getter
  public HandlerInterceptor getHandlerInterceptor() {
    return this.handlerInterceptor;
  }

  public InterceptorRegistration addPathPatterns(String... patterns) {
    this.pathPatterns.addAll(Arrays.asList(patterns));
    return this;
  }

  public InterceptorRegistration(HandlerInterceptor handlerInterceptor) {
    super();
    this.handlerInterceptor = handlerInterceptor;
  }

}