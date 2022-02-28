package test;

import jakarta.servlet.http.HttpServletRequest;
import mvc.web.servlet.ModelAndView;
import mvc.web.bind.annotation.Controller;
import mvc.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {

  @RequestMapping("/test")
  public ModelAndView index(HttpServletRequest request) {
    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("/index.jsp");
    return modelAndView;
  }

}