package pers.mars.mvc.servlet;

import jakarta.servlet.http.HttpServletRequest;

/**
 * HandlerMapping, 实现此接口的类会被 spring mvc ioc 容器管理.
 * 因此, 可以自己定义 HandlerMapping.
 */
public interface HandlerMapping {

  /**
   * 根据 HttpServletRequest 请求, 返回 HandlerExecutionChain
   *
   * @return request 对应的 handler 执行链;
   *         或者 <code>null</code>, 如果没有对应 handler
   */
  HandlerExecutionChain getHandler(HttpServletRequest request);

}
