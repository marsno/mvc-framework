package test.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pers.mars.mvc.servlet.handler.configuration.PathVariable;
import pers.mars.mvc.servlet.handler.configuration.RequestMethod;
import pers.mars.mvc.servlet.ModelAndView;
import pers.mars.mvc.context.annotation.Controller;
import pers.mars.mvc.servlet.handler.configuration.RequestMapping;
import pers.mars.mvc.servlet.handler.configuration.RequestParam;

@Controller
public class IndexController {

  @RequestMapping(value = "/test1", method = RequestMethod.GET)
  public ModelAndView index1() {

    System.out.println("[/test1] 执行中...");

    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("/index.jsp");
    return modelAndView;
  }

  @RequestMapping(value = "/test4", method = RequestMethod.GET)
  public ModelAndView index4(
    ModelAndView modelAndView
  ) {

    System.out.println("[/test4] 执行中...");

    modelAndView.setViewName("/index.jsp");
    return modelAndView;
  }

  @RequestMapping(value = "/test5", method = RequestMethod.GET)
  public ModelAndView index5(
    ModelAndView modelAndView,
    HttpServletRequest request,
    HttpServletResponse response
  ) {

    System.out.println("[/test5] -----------------");
    if (request != null) System.out.println("request 对象成功注入");
    if (response != null) System.out.println("response 对象成功注入");

    modelAndView.setViewName("/index.jsp");
    return modelAndView;
  }

  @RequestMapping(value = "/test2", method = RequestMethod.GET)
  public ModelAndView index2(
    @RequestParam("questionId") String questionId,
    @RequestParam("answerId") String answerId
  ) {

    System.out.println("[/test2] RequestParam ------------");
    System.out.println(questionId);
    System.out.println(answerId);

    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("/index.jsp");
    return modelAndView;
  }

  @RequestMapping(value = "/test3/question/{questionId}/answer/{answerId}", method = RequestMethod.GET)
  public ModelAndView index3(
    @PathVariable("questionId") String questionId,
    @PathVariable("answerId") String answerId
  ) {

    System.out.println("[/test3] PathVariable ------------");
    System.out.println(questionId);
    System.out.println(answerId);

    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("/index.jsp");
    return modelAndView;
  }

}
