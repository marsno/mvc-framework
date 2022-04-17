package pers.mars.mvc.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pers.mars.mvc.servlet.interceptor.HandlerInterceptor;

import java.util.List;

// 由 HandlerMapping 返回, consists of handler and HandlerInterceptors
public class HandlerExecutionChain {

  // 主要执行对象
  private Object handler = null;

  // handler 对应的所有 HandlerInterceptor
  private List<HandlerInterceptor> interceptors = null;

  // setter
  public void setHandler(Object handler) {
    this.handler = handler;
  }

  // getter
  public Object getHandler() {
    return this.handler;
  }

  public void registerInterceptors(List<HandlerInterceptor> interceptors) {
    this.interceptors = interceptors;
  }

  /**
   * 执行所有 HandlerInterceptor 的 preHandle
   *
   * @param   request 当前的 HTTP request
   * @param   response 当前的 HTTP response
   *
   * @return  若所有 HandlerInterceptor 的 preHandle 都执行成功, return true;
   *          如果被其中一个 preHandle 拦截, return false.
   */
  public boolean applyPreHandle(HttpServletRequest request, HttpServletResponse response) {

    for (HandlerInterceptor interceptor : this.interceptors) {
      if (!interceptor.preHandle(request, response, this.handler)) {
        return false;
      }
    }

    return true;

  }

  public void applyPostHandle(HttpServletRequest request, HttpServletResponse response, ModelAndView modelAndView) {
    for (int index = this.interceptors.size() - 1; index >= 0; index--) {
      HandlerInterceptor interceptor = this.interceptors.get(index);
      interceptor.postHandle(request, response, this.handler, modelAndView);
    }
  }

  public HandlerExecutionChain() {
    super();
  }

}