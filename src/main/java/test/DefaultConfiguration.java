package test;

import pers.mars.mvc.context.annotation.ComponentScan;
import pers.mars.mvc.context.annotation.Configuration;
import pers.mars.mvc.web.servlet.config.annotation.InterceptorRegistry;
import pers.mars.mvc.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ComponentScan(value = "test")
public class DefaultConfiguration implements WebMvcConfigurer {

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new FirstInterceptor()).addPathPatterns(".*");
    registry.addInterceptor(new SecondInterceptor()).addPathPatterns(".*");
  }

}