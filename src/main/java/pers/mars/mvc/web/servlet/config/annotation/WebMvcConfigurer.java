package pers.mars.mvc.web.servlet.config.annotation;

import pers.mars.mvc.web.servlet.ViewResolver;
import pers.mars.mvc.web.servlet.handler.HandlerMethodArgumentResolver;
import java.util.List;

public interface WebMvcConfigurer {

  default void addInterceptors(InterceptorRegistry registry) {
  }

  default void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
  }

  default void addViewResolvers(List<ViewResolver> resolvers) {
  }

}