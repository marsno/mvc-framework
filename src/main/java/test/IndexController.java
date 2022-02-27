package test;

import jakarta.servlet.http.HttpServletRequest;
import mvc.web.servlet.ModelAndView;
import mvc.web.servlet.config.Controller;
import mvc.web.servlet.config.RequestMapping;

@Controller
public class IndexController {

  @RequestMapping("/test")
  public ModelAndView index(HttpServletRequest request) {
    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("/index.jsp");
    return modelAndView;
  }

}