package test;

import jakarta.servlet.http.HttpServletRequest;
import pers.mars.mvc.servlet.handler.configuration.RequestMethod;
import pers.mars.mvc.servlet.ModelAndView;
import pers.mars.mvc.context.annotation.Controller;
import pers.mars.mvc.servlet.handler.configuration.RequestMapping;

@Controller
public class IndexController {

  @RequestMapping(value = "/test", method = RequestMethod.GET)
  public ModelAndView index(HttpServletRequest request) {
    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("/index.jsp");
    return modelAndView;
  }

}
