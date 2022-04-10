package test;

import jakarta.servlet.http.HttpServletRequest;
import pers.mars.mvc.context.annotation.BeanScope;
import pers.mars.mvc.context.annotation.Lazy;
import pers.mars.mvc.context.annotation.Scope;
import pers.mars.mvc.web.bind.annotation.RequestMethod;
import pers.mars.mvc.web.servlet.ModelAndView;
import pers.mars.mvc.context.annotation.Controller;
import pers.mars.mvc.web.bind.annotation.RequestMapping;

@Controller
@Scope(BeanScope.PROTOTYPE)
@Lazy
public class IndexController {

  @RequestMapping(value = "/test", method = RequestMethod.GET)
  public ModelAndView index(HttpServletRequest request) {
    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("/index.jsp");
    return modelAndView;
  }

}
