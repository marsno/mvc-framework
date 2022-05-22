package test.context;

import pers.mars.mvc.context.annotation.*;

@Component
// @Scope(BeanScope.PROTOTYPE)
// @Lazy
// @Controller
// @Service
// @Repository
public class Phone {

  @Autowired("battery")
  private Battery battery;

  public Phone() {
    System.out.println("Phone 被创建");
  }

}
