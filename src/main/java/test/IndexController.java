package test;

import mvc.web.servlet.ModelAndView;
import mvc.web.servlet.config.Controller;
import mvc.web.servlet.config.RequestMapping;

@Controller
public class IndexController {

  @RequestMapping("/index")
  public ModelAndView index() {
    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("/index.jsp");
    return modelAndView;
  }

}