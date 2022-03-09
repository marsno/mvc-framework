package pers.mars.mvc.web.servlet.config.annotation;

/**
 * 配置类实现此接口, 可以配置 HandlerInterceptor
 */
public interface WebMvcConfigurer {

  void addInterceptors(InterceptorRegistry registry);

}