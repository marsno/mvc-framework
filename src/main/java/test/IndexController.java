package test;

import jakarta.servlet.http.HttpServletRequest;
import pers.mars.mvc.web.bind.annotation.RequestMethod;
import pers.mars.mvc.web.servlet.ModelAndView;
import pers.mars.mvc.web.bind.annotation.Controller;
import pers.mars.mvc.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {

  @RequestMapping(method = RequestMethod.GET, value = "/test")
  public ModelAndView index(HttpServletRequest request) {
    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("/index.jsp");
    return modelAndView;
  }

}