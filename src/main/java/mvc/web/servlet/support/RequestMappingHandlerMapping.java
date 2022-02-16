package mvc.web.servlet.support;

import jakarta.servlet.http.HttpServletRequest;
import mvc.beans.InitializingBean;
import mvc.web.servlet.HandlerMapping;
import mvc.web.servlet.context.WebApplicationContext;
import mvc.web.servlet.HandlerExecutionChain;
import mvc.web.servlet.handler.HandlerMethod;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RequestMappingHandlerMapping
    implements HandlerMapping {

  // no param constructor
  public RequestMappingHandlerMapping() {}

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

    // get uri
    String URI = request.getRequestURI();

    // 尝试精确匹配
    HandlerMethod handlerMethod = this.handlerMethodMap.getOrDefault(URI, null);
    if (handlerMethod != null) {
      HandlerExecutionChain chain = new HandlerExecutionChain();
      chain.setHandler(handlerMethod);
      return chain;
    }

    // 精确匹配失败, regex matching
    for (Map.Entry<String,HandlerMethod> entry : this.handlerMethodMap.entrySet()) {
      boolean isMatching = URI.matches( entry.getKey() );
      if (isMatching) {
        HandlerExecutionChain chain = new HandlerExecutionChain();
        chain.setHandler( entry.getValue() );
        return chain;
      }
    }

    // uri 没有对应的 HandlerMethod
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

}
