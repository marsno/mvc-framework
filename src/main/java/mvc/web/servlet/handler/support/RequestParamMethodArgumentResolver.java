package mvc.web.servlet.handler.support;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mvc.web.servlet.config.RequestParam;
import mvc.web.servlet.handler.HandlerMethod;
import mvc.web.servlet.handler.HandlerMethodArgumentResolver;
import mvc.web.servlet.handler.MethodParameter;

/**
 * 带有 {@link RequestParam}
 * 注解的 parameter, 由这个类生成 argument.
 */
public class RequestParamMethodArgumentResolver
    implements HandlerMethodArgumentResolver {

  /**
   * 有 {@link RequestParam} 注解的 parameter, 支持, return ture;
   * 没有此注解, 不支持, return false.
   */
  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.hasParameterAnnotation(RequestParam.class);
  }

  /**
   * 从 {argument request} 中获取 argument
   */
  @Override
  public Object resolveArgument(HttpServletRequest request,
                                HttpServletResponse response,
                                HandlerMethod handlerMethod,
                                MethodParameter parameter) {

    // 如果不支持 parameter, return null
    if ( !this.supportsParameter(parameter) )
      return null;

    // 获取 argument name, 不规范, return null
    String argumentName = parameter.getParameterAnnotation(RequestParam.class).value();
    if ( argumentName.equals("") )
      return null;

    // 从 request 中获取 {@link RequestParam} 指定的 argument
    String argumentValue = request.getParameter(argumentName);

    // 获取 parameter 的 class type
    Class<?> parameterType = parameter.getParameterType();

    // 只支持以下 5 种
    if (parameterType != String.class &&
        parameterType != Integer.class &&
        parameterType != Float.class &&
        parameterType != int.class)
      return null;

    // 类型转换, 如果无法转换, return null
    if (parameterType == String.class) {
      return argumentValue;
    }
    else if (parameterType == Integer.class || parameterType == int.class) {
      if ( !this.isInteger(argumentValue) ) {
        return null;
      }
      else {
        return Integer.valueOf(argumentValue);
      }
    }
    else {
      if ( !this.isFloat(argumentValue) ) {
        return null;
      }
      else {
        return Float.valueOf(argumentValue);
      }
    }

  }

  // 判断是否是整数
  private boolean isInteger(String string) {
    for ( char c : string.toCharArray() )
      if (c < '0' || c > '9')
        return false;

    return true;
  }

  // 判断是否是浮点数, 包括整数
  private boolean isFloat(String string) {
    int pointNumber = 0;
    for ( char c : string.toCharArray() ) {
      if ( (c < '0' || c > '9') && c != '.' ) {
        return false;
      }
      if (c == '.') {
        pointNumber++;
      }
    }

    return pointNumber <= 1;

  }

}
