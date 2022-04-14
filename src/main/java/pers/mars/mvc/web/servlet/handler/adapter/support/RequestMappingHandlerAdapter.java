package pers.mars.mvc.web.servlet.handler.adapter.support;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pers.mars.mvc.web.servlet.handler.HandlerMethod;
import pers.mars.mvc.web.servlet.ModelAndView;
import pers.mars.mvc.web.servlet.handler.MethodParameter;
import pers.mars.mvc.web.servlet.handler.adapter.HandlerAdapter;
import pers.mars.mvc.web.servlet.handler.adapter.HandlerMethodArgumentResolver;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public class RequestMappingHandlerAdapter implements HandlerAdapter {

  // 用于在调用 handler 时, 生成 argument
  private final List<HandlerMethodArgumentResolver> argumentResolverList = new ArrayList<>();

  /**
   * @param handler 仅处理类型为 {@link HandlerMethod} 的 handler
   *
   * @return {@link ModelAndView}
   */
  @Override
  public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) {

    // 调用 controller 的 handler
    Object controllerResult = this.invokeHandlerMethod( request ,response, (HandlerMethod) handler );

    // 处理调用 controller 的结果
    if (controllerResult == null) {
      return new ModelAndView();
    }
    else if (controllerResult.getClass() == ModelAndView.class) {
      return (ModelAndView) controllerResult;
    }
    else {
      return new ModelAndView();
    }

  }

  /**
   * 调用 controller method, 返回 object,
   * 类型只会是 {@link ModelAndView}, {@link String}
   * @return {@link ModelAndView}, {@link String}, null
   */
  protected Object invokeHandlerMethod(
    HttpServletRequest request,
    HttpServletResponse response,
    HandlerMethod handlerMethod ) {

    // 构造调用所需要的参数
    Method method = handlerMethod.getMethod();
    Parameter[] parameters = handlerMethod.getParameters();
    Object[] arguments = new Object[parameters.length];
    for (int i = 0; i < parameters.length; i++) {
      MethodParameter methodParameter = new MethodParameter(method, i);
      for (HandlerMethodArgumentResolver argumentResolver : this.argumentResolverList) {
        if ( argumentResolver.supportsParameter(methodParameter) ) {
          arguments[i] = argumentResolver.resolveArgument(request, response, handlerMethod, methodParameter);
          break;
        }
      }
    }

    // 调用
    Object returnObject = null;
    Object controller = handlerMethod.getController();
    try {
      returnObject = method.invoke(controller, arguments);
    }
    catch (IllegalAccessException | InvocationTargetException e) {
      e.printStackTrace();
    }

    // 若 returnObject 不是 ModelAndView 或者 String, return null
    if (returnObject == null) {
      return null;
    }
    else if (returnObject.getClass() != ModelAndView.class && returnObject.getClass() != String.class) {
      return null;
    }
    else {
      return returnObject;
    }

  }

  // 向 this.argumentResolverList 添加 resolver
  public void registerArgumentResolver(HandlerMethodArgumentResolver resolver) {
    this.argumentResolverList.add(resolver);
  }

}
