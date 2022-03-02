package pers.mars.mvc.web.servlet.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pers.mars.mvc.web.servlet.ModelAndView;

public interface HandlerInterceptor {

  // 在 Handler 之前调用
  public void preHandle( HttpServletRequest request,
                         HttpServletResponse response,
                         Object handler );

  // 在 Handler 之后调用
  public void postHandle( HttpServletRequest request,
                          HttpServletResponse response,
                          Object handler,
                          ModelAndView modelAndView );

  // 在 DispatcherServlet 完成处理完流程后调用
  public void afterCompletion( HttpServletRequest request,
                               HttpServletResponse response,
                               Object handler );

}
