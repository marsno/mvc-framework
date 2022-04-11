package pers.mars.mvc.web.servlet.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// 生成 argument 的类
public interface HandlerMethodArgumentResolver {

  // 判断是否支持 parameter 的类型
  boolean supportsParameter(MethodParameter parameter);

  // 生成 argument
  Object resolveArgument(HttpServletRequest request, HttpServletResponse response,
    HandlerMethod handlerMethod, MethodParameter parameter);

}
