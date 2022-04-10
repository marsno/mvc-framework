package pers.mars.mvc.web.servlet.support;

import jakarta.servlet.http.HttpServletRequest;
import pers.mars.mvc.web.bind.annotation.RequestMethod;
import pers.mars.mvc.web.servlet.HandlerMapping;
import pers.mars.mvc.web.servlet.config.annotation.InterceptorRegistry;
import pers.mars.mvc.web.servlet.HandlerExecutionChain;
import pers.mars.mvc.web.servlet.handler.HandlerMethod;
import java.util.HashMap;
import java.util.Map;

public class RequestMappingHandlerMapping implements HandlerMapping {

  /**
   * String: uri, 可以是以下 2 种情况
   *
   * - "/question/9463"         精确的
   * - "/question/*"            带 "*" 匹配多个字符的
   */
  private Map<String, HandlerMethod> handlerMethodMap = new HashMap<>();

  private InterceptorRegistry interceptorRegistry = null;

  /**
   * 根据 HttpServletRequest 请求, 返回 HandlerExecutionChain.
   *
   * @param   request http request.
   *
   * @return  request 对应的 HandlerExecutionChain;
   *          或者 <code>null</code>, 如果没有对应 handler.
   */
  @Override
  public HandlerExecutionChain getHandler(HttpServletRequest request) {
    HandlerExecutionChain chain = new HandlerExecutionChain();
    boolean hasHandler = this.addHandler(chain, request);
    if (!hasHandler) {
      return null;
    }
    chain.registerInterceptors(
      this.interceptorRegistry.getInterceptors(request.getRequestURI())
    );
    return chain;
  }

  /**
   * 注册 handler 方法
   * @param uri 请求的 uri
   * @param handlerMethod uri 对应的 HandlerMethod 对象
   */
  public void registerHandlerMethod( String uri, HandlerMethod handlerMethod ) {
    this.handlerMethodMap.put( uri, handlerMethod );
  }

  // setter
  public void setInterceptorRegistry(InterceptorRegistry registry) {
    this.interceptorRegistry = registry;
  }

  /**
   * 添加 Handler
   *
   * @param   chain 需要添加 Handler 的 HandlerExecutionChain
   * @param   request 当前的 HTTP 请求
   *
   * @return  若有对应的 Handler, 并且已经添加, return true;
   *          若找不到对饮的 Handler, return false.
   */
  protected boolean addHandler(HandlerExecutionChain chain, HttpServletRequest request) {

    String URI = request.getRequestURI();
    String requestMethod = request.getMethod();

    // 尝试精确匹配
    HandlerMethod handlerMethod = this.handlerMethodMap.getOrDefault(URI, null);
    if (handlerMethod != null) {

      // URI 匹配成功, 看是否能接受此 request method
      for (RequestMethod method : handlerMethod.getRequestMethods()) {
        if ( method.toString().equals(requestMethod) ) {
          chain.setHandler(handlerMethod);
          return true;
        }
      }

      // 有对应的 URI, 但不支持 request method
      return false;

    }

    // 精确匹配失败, regex matching
    for (Map.Entry<String,HandlerMethod> entry : this.handlerMethodMap.entrySet()) {
      boolean isMatching = URI.matches( entry.getKey() );
      if (isMatching) {

        // URI 匹配成功, 看是否能接受此 request method
        for ( RequestMethod method : entry.getValue().getRequestMethods() ) {
          if ( method.toString().equals(requestMethod) ) {
            chain.setHandler( entry.getValue() );
            return true;
          }
        }

        // 匹配到了 URI, 但是不支持 request method
        return false;

      }
    }

    // 没有对应 URI
    return false;

  }

  public RequestMappingHandlerMapping() {
    super();
  }

}
