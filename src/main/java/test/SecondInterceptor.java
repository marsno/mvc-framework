package test;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pers.mars.mvc.servlet.ModelAndView;
import pers.mars.mvc.servlet.interceptor.HandlerInterceptor;

public class SecondInterceptor implements HandlerInterceptor {

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    System.out.println("[SecondInterceptor] preHandle");
    return true;
  }

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
    System.out.println("[SecondInterceptor] postHandle");
  }

}