package pers.mars.mvc.web.servlet.config.annotation;

import pers.mars.mvc.web.servlet.handler.HandlerInterceptor;
import java.util.ArrayList;
import java.util.List;

public class InterceptorRegistry {

  protected List<InterceptorRegistration> registrations = new ArrayList<InterceptorRegistration>();

  public InterceptorRegistration addInterceptor(HandlerInterceptor interceptor) {
    InterceptorRegistration registration = new InterceptorRegistration(interceptor);
    this.registrations.add(registration);
    return registration;
  }

  /**
   * 根据 URI, 返回对应的所有 HandlerInterceptors
   *
   * @param   URI 当前 HTTP request 的 URI
   *
   * @return  含有所有 HandlerInterceptor 的 List.
   */
  public List<HandlerInterceptor> getInterceptors(String URI) {
    List<HandlerInterceptor> handlerInterceptors = new ArrayList<HandlerInterceptor>();
    for (InterceptorRegistration registration : this.registrations) {
      if (registration.matches(URI)) {
        handlerInterceptors.add(registration.getHandlerInterceptor());
      }
    }
    return handlerInterceptors;
  }

  public InterceptorRegistry() {
    super();
  }

}