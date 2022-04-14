package pers.mars.mvc.web.servlet.handler.adapter.support;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pers.mars.mvc.web.servlet.handler.configuration.PathVariable;
import pers.mars.mvc.web.servlet.handler.HandlerMethod;
import pers.mars.mvc.web.servlet.handler.adapter.HandlerMethodArgumentResolver;
import pers.mars.mvc.web.servlet.handler.MethodParameter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Resolves method arguments annotated with an {@link PathVariable} */
public class PathVariableMethodArgumentResolver
  implements HandlerMethodArgumentResolver {

  /** no parameter constructor */
  public PathVariableMethodArgumentResolver() {
    super();
  }

  /**
   * 是否支持指定的方法参数. 是否有 annotation {@link PathVariable}
   * @param parameter 查询是否支持的 MethodParameter
   * @return 支持返回 true, 不支持返回 false
   */
  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.hasParameterAnnotation(PathVariable.class);
  }

  /**
   * 如果有 annotation {@link PathVariable}, 则可以调用此方法,
   * 根据 request 中的信息生成 调用方法的 argument.
   *
   * @param     parameter 指定想要生成 argument 的 parameter
   * @param     request http request
   * @return    一个生成的 argument, 或 null, 如果不支持此
   *            parameter, 或者 argumentName 为空, 或则没有
   *            name 为指定名字的 argument
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
    String argumentName = parameter.getParameterAnnotation(PathVariable.class).value();
    if ( argumentName.equals("") )
      return null;

    String regex = handlerMethod.getRegexURI();
    Matcher matcher = Pattern.compile(regex).matcher(request.getRequestURI());
    String[] argumentNames = handlerMethod.getArgumentNames();
    List<String> argumentValues = new ArrayList<String>();
    if ( matcher.find() )
      for (int i = 1; i <= matcher.groupCount(); i++)
        argumentValues.add(matcher.group(i));

    for (int i = 0; i < argumentNames.length; i++)
      if (argumentNames[i].equals(argumentName))
        return (Object) argumentValues.get(i);

    return null;
  }

}
