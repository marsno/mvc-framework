package test.configuration;

import pers.mars.mvc.context.annotation.ComponentScan;
import pers.mars.mvc.context.annotation.Configuration;
import pers.mars.mvc.servlet.handler.configuration.WebMvcConfigurer;
import pers.mars.mvc.servlet.interceptor.InterceptorRegistry;
import test.interceptor.FirstInterceptor;
import test.interceptor.SecondInterceptor;

@Configuration
@ComponentScan("test")
public class TestConfiguration implements WebMvcConfigurer {

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    // registry.addInterceptor(new FirstInterceptor()).addPathPatterns(".*");
    // registry.addInterceptor(new SecondInterceptor()).addPathPatterns(".*");
  }

}
