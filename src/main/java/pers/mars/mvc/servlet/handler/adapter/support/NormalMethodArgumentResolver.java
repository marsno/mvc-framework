package pers.mars.mvc.servlet.handler.adapter.support;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pers.mars.mvc.servlet.ModelAndView;
import pers.mars.mvc.servlet.handler.HandlerMethod;
import pers.mars.mvc.servlet.handler.adapter.HandlerMethodArgumentResolver;
import pers.mars.mvc.servlet.handler.MethodParameter;

public class NormalMethodArgumentResolver implements HandlerMethodArgumentResolver {

  /**
   * 支持:
   * - HttpServletRequest
   * - HttpServletResponse
   * - ModelAndView
   */
  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    Class<?> parameterType = parameter.getParameterType();
    return parameterType == HttpServletRequest.class
        || parameterType == HttpServletResponse.class
        || parameterType == ModelAndView.class;
  }

  /**
   * 生成 argument
   * @return argument, 或 <code>null</code>, 如果不支持
   */
  @Override
  public Object resolveArgument(HttpServletRequest request,
                                HttpServletResponse response,
                                HandlerMethod handlerMethod,
                                MethodParameter parameter) {

    if ( !this.supportsParameter(parameter) )
      return null;

    Class<?> parameterType = parameter.getParameterType();

    if (parameterType == HttpServletRequest.class)
      return (Object) request;
    else if (parameterType == HttpServletResponse.class)
      return (Object) response;
    else if (parameterType == ModelAndView.class)
      return (Object) new ModelAndView();
    else
      return null;

  }

}