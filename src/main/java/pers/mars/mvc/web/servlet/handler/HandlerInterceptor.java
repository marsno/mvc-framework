package pers.mars.mvc.web.servlet.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pers.mars.mvc.web.servlet.ModelAndView;

public interface HandlerInterceptor {

  // 在 Handler 之前调用
  default boolean preHandle( HttpServletRequest request, HttpServletResponse response, Object handler ) {
    return true;
  }

  // 在 Handler 之后调用
  default void postHandle( HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler,
                           ModelAndView modelAndView ) {
  }

}