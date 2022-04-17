package test;

import pers.mars.mvc.context.annotation.ComponentScan;
import pers.mars.mvc.context.annotation.Configuration;
import pers.mars.mvc.servlet.interceptor.InterceptorRegistry;
import pers.mars.mvc.servlet.handler.configuration.WebMvcConfigurer;

@Configuration
@ComponentScan("test")
public class DefaultConfiguration implements WebMvcConfigurer {

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new FirstInterceptor()).addPathPatterns(".*");
    registry.addInterceptor(new SecondInterceptor()).addPathPatterns(".*");
  }

}
