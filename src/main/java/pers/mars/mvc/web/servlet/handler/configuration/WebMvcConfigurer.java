package pers.mars.mvc.web.servlet.handler.configuration;

import pers.mars.mvc.web.servlet.ViewResolver;
import pers.mars.mvc.web.servlet.handler.InterceptorRegistry;
import pers.mars.mvc.web.servlet.handler.adapter.HandlerMethodArgumentResolver;
import java.util.List;

public interface WebMvcConfigurer {

  default void addInterceptors(InterceptorRegistry registry) {
  }

  default void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
  }

  default void addViewResolvers(List<ViewResolver> resolvers) {
  }

}
