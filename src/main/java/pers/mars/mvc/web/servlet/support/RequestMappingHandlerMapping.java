package pers.mars.mvc.web.servlet.support;

import jakarta.servlet.http.HttpServletRequest;
import pers.mars.mvc.web.bind.annotation.RequestMethod;
import pers.mars.mvc.web.servlet.HandlerMapping;
import pers.mars.mvc.web.servlet.context.WebApplicationContext;
import pers.mars.mvc.web.servlet.HandlerExecutionChain;
import pers.mars.mvc.web.servlet.handler.HandlerMethod;

import java.util.HashMap;
import java.util.Map;

public class RequestMappingHandlerMapping
  implements HandlerMapping {

  private WebApplicationContext context;

  /**
   * String: uri, 可以是以下 2 种情况
   *
   * - "/question/9463"         精确的
   * - "/question/*"            带 "*" 匹配多个字符的
   */
  private Map<String, HandlerMethod> handlerMethodMap = new HashMap<>();

  /**
   * 根据 HttpServletRequest 请求, 返回 HandlerExecutionChain.
   * @param request http request.
   * @return request 对应的 HandlerExecutionChain;
   *         或者 <code>null</code>, 如果没有对应 handler.
   */
  @Override
  public HandlerExecutionChain getHandler(HttpServletRequest request) {

    String URI = request.getRequestURI();
    String requestMethod = request.getMethod();

    // 尝试精确匹配
    HandlerMethod handlerMethod = this.handlerMethodMap.getOrDefault(URI, null);
    if (handlerMethod != null) {

      // URI 匹配成功, 看是否能接受此 request method
      for (RequestMethod method : handlerMethod.getRequestMethods()) {
        if ( method.toString().equals(requestMethod) ) {
          HandlerExecutionChain chain = new HandlerExecutionChain();
          chain.setHandler(handlerMethod);
          return chain;
        }
      }

      // 有对应的 URI, 但不支持 request method
      return null;

    }

    // 精确匹配失败, regex matching
    for (Map.Entry<String,HandlerMethod> entry : this.handlerMethodMap.entrySet()) {
      boolean isMatching = URI.matches( entry.getKey() );
      if (isMatching) {

        // URI 匹配成功, 看是否能接受此 request method
        for ( RequestMethod method : entry.getValue().getRequestMethods() ) {
          if ( method.toString().equals(requestMethod) ) {
            HandlerExecutionChain chain = new HandlerExecutionChain();
            chain.setHandler( entry.getValue() );
            return chain;
          }
        }

        // 匹配到了 URI, 但是不支持 request method
        return null;

      }
    }

    // 没有对应 URI
    return null;

  }

  /**
   * 注册 handler 方法
   * @param uri 请求的 uri
   * @param handlerMethod uri 对应的 HandlerMethod 对象
   */
  public void registerHandlerMethod( String uri,
                                     HandlerMethod handlerMethod ) {

    this.handlerMethodMap.put( uri, handlerMethod );

  }

  public RequestMappingHandlerMapping() {
  }

}