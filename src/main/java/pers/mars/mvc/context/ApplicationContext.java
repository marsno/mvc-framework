package pers.mars.mvc.context;

import pers.mars.mvc.context.config.BeanScope;

public interface ApplicationContext extends BeanFactory {

  boolean registerBean(String id, BeanScope scope, Class<?> type );

}