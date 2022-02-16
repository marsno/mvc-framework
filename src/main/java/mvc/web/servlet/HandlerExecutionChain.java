package mvc.web.servlet;

import mvc.web.servlet.handler.HandlerInterceptor;
import mvc.web.servlet.handler.HandlerMethod;

import java.util.ArrayList;
import java.util.List;

// 由 HandlerMapping 返回, consists of handler and HandlerInterceptors
public class HandlerExecutionChain {

  // no parameter constructor
  public HandlerExecutionChain() {
    super();
  }

  // 主要执行对象
  private Object handler = null;

  // handler 对应的所有 HandlerInterceptor
  private List<HandlerInterceptor> interceptors = new ArrayList<>();

  // setter
  public void setHandler(Object handler) {
    this.handler = handler;
  }

  // getter
  public Object getHandler() {
    return this.handler;
  }

  /**
   * 添加 HandlerInterceptor
   * @param interceptor 要添加的 interceptor
   */
  public void registerInterceptor(HandlerInterceptor interceptor) {
    this.interceptors.add(interceptor);
  }

}
