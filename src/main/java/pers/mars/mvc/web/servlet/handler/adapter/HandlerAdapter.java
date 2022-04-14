package pers.mars.mvc.web.servlet.handler.adapter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pers.mars.mvc.web.servlet.ModelAndView;

public interface HandlerAdapter {

  /**
   * 处理请求, 返回 ModelAndView.
   * @param request 请求.
   * @param response 响应.
   * @param handler 实际的处理对象.
   * @return 处理后的 ModelAndView 对象.
   */
  ModelAndView handle(
    HttpServletRequest request,
    HttpServletResponse response,
    Object handler
  );

}
