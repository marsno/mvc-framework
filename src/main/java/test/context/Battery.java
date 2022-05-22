package test.context;

import pers.mars.mvc.context.annotation.BeanScope;
import pers.mars.mvc.context.annotation.Component;
import pers.mars.mvc.context.annotation.Lazy;
import pers.mars.mvc.context.annotation.Scope;

@Component
// @Scope(BeanScope.PROTOTYPE)
// @Scope(BeanScope.SINGLETON)
// @Scope(BeanScope.PROTOTYPE)
// @Lazy
public class Battery {

  public Battery() {
    System.out.println("Battery 被创建");
  }

}
