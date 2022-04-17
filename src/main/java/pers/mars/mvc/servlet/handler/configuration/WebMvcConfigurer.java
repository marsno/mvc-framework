package pers.mars.mvc.servlet.handler.configuration;

import pers.mars.mvc.servlet.ViewResolver;
import pers.mars.mvc.servlet.interceptor.InterceptorRegistry;
import pers.mars.mvc.servlet.handler.adapter.HandlerMethodArgumentResolver;
import java.util.List;

public interface WebMvcConfigurer {

  default void addInterceptors(InterceptorRegistry registry) {
  }

  default void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
  }

  default void addViewResolvers(List<ViewResolver> resolvers) {
  }

}
