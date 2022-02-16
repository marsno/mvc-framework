package mvc.web.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import mvc.beans.ApplicationContext;
import mvc.beans.BeanDefinition;
import mvc.beans.config.BeanScope;
import mvc.web.servlet.config.RequestMapping;
import mvc.web.servlet.context.WebApplicationContext;
import mvc.web.servlet.handler.HandlerMethod;
import mvc.web.servlet.handler.support.NormalMethodArgumentResolver;
import mvc.web.servlet.handler.support.PathVariableMethodArgumentResolver;
import mvc.web.servlet.handler.support.RequestParamMethodArgumentResolver;
import mvc.web.servlet.support.InternalResourceViewResolver;
import mvc.web.servlet.support.RequestMappingHandlerAdapter;
import mvc.web.servlet.support.RequestMappingHandlerMapping;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// spring mvc 唯一的 HttpServlet, 也是整个框架最顶层的类
public class DispatcherServlet extends HttpServlet {

  // HttpServlet 被创建时初始化的扩展点
  @Override
  public void init() throws ServletException {
    this.initContext();
    this.initHandlerMappingList(this.context);
    this.initHandlerAdapter(this.context);
    this.initViewResolver(this.context);
  }

  // 接受 request 后, 调用的方法
  @Override
  protected void service(HttpServletRequest request, HttpServletResponse response) {
    this.doDispatch(request, response);
  }

  // spring mvc 的内部 beans
  private List<Class<?>> internalBeans = new ArrayList<Class<?>>();

  // servlet context
  private WebApplicationContext context = null;

  // HandlerMapping list
  private List<HandlerMapping> handlerMappingList
      = new ArrayList<HandlerMapping>();

  // 处理 HandlerMethod, 生成 ModelAndView
  private RequestMappingHandlerAdapter handlerAdapter = null;

  // view resolver list
  private ViewResolver viewResolver = null;

  /**
   * 初始化 ioc 容器
   * 若 ServletContext 中有 name 为 "context" 的属性,
   * 将其设为 ioc 容器的父容器
   */
  private void initContext() {

    // 初始化 spring mvc ioc 容器, 添加内部 beans
    this.context = new WebApplicationContext("");
    this.internalBeans.add(RequestMappingHandlerMapping.class);
    this.internalBeans.add(RequestMappingHandlerAdapter.class);
    this.internalBeans.add(InternalResourceViewResolver.class);
    this.internalBeans.add(NormalMethodArgumentResolver.class);
    this.internalBeans.add(RequestParamMethodArgumentResolver.class);
    this.internalBeans.add(PathVariableMethodArgumentResolver.class);
    for (Class<?> type : this.internalBeans) {
      String beanId = type.getSimpleName().toLowerCase().charAt(0)
          + type.getSimpleName().substring(1);
      this.context.registerBean(beanId, BeanScope.SINGLETON, type);
    }

    // 添加父容器
    ApplicationContext parentContext =
        (ApplicationContext) this.getServletContext().getAttribute("context");
    if (context == null) return;
    this.context.setParentContext(parentContext);
  }

  // init this.handlerMapping
  private void initHandlerMappingList(WebApplicationContext context) {

    RequestMappingHandlerMapping handlerMapping =
        (RequestMappingHandlerMapping) context.getBean("requestMappingHandlerMapping");

    this.initializingRequestMappingHandlerMapping(handlerMapping, context);
    this.handlerMappingList.add(handlerMapping);

  }

  // init this.handlerAdapter
  private void initHandlerAdapter(WebApplicationContext context) {
    this.handlerAdapter = (RequestMappingHandlerAdapter) context.getBean("requestMappingHandlerAdapter");

    PathVariableMethodArgumentResolver pathVariableMethodArgumentResolver
        = (PathVariableMethodArgumentResolver) context.getBean("pathVariableMethodArgumentResolver");
    NormalMethodArgumentResolver normalMethodArgumentResolver
        = (NormalMethodArgumentResolver) context.getBean("normalMethodArgumentResolver");
    RequestParamMethodArgumentResolver requestParamMethodArgumentResolver
        = (RequestParamMethodArgumentResolver) context.getBean("requestParamMethodArgumentResolver");

    this.handlerAdapter.registerArgumentResolver(pathVariableMethodArgumentResolver);
    this.handlerAdapter.registerArgumentResolver(normalMethodArgumentResolver);
    this.handlerAdapter.registerArgumentResolver(requestParamMethodArgumentResolver);
  }

  // init this.viewResolverList
  private void initViewResolver(WebApplicationContext context) {
    this.viewResolver =
    (ViewResolver) context.getBean("internalResourceViewResolver");
  }

  // this.service 调用
  private void doDispatch( HttpServletRequest request,
                           HttpServletResponse response ) {

    // 根据 request, 获取 HandlerExecutionChain
    HandlerExecutionChain chain = null;
    for (HandlerMapping handlerMapping : this.handlerMappingList) {
      chain = handlerMapping.getHandler(request);
      if (chain != null) break;
    }
    if (chain == null) {
      response.setStatus(404);
      return;
    }

    ModelAndView modelAndView = this.handlerAdapter.handle( request, response, chain.getHandler() );
    if (modelAndView.getViewName() == null) {
      try {
        response.getWriter().write( (String) modelAndView.getModelMap().get("string") );
        return;
      }
      catch (IOException exception) {
        exception.printStackTrace();
      }
    }
    else {
      this.render( request, response, modelAndView );
      return;
    }

  }

  /**
   * 渲染 view
   * @param request 当前 http request
   * @param response 当前 http response
   * @param modelAndView handler 返回的 ModelAndView
   */
  protected void render( HttpServletRequest request,
                         HttpServletResponse response,
                         ModelAndView modelAndView ) {

    // 通过 ViewResolver 获取 View 对象
    View view = this.viewResolver.resolveViewName( modelAndView.getViewName() );

    // 从 modelAndView 中获取 model
    Map<String,Object> model = modelAndView.getModelMap();

    // 渲染
    view.render( request, response, model );

  }

  private void initializingRequestMappingHandlerMapping( RequestMappingHandlerMapping handlerMapping,
                                                         WebApplicationContext context ) {
    // 获取所有 controller 的 BeanDefinition
    List<BeanDefinition> bds = context.getControllers();
    if (bds == null) return;

    // 根据 controller 的 BeanDefinition 初始化 handlerMapping
    for (BeanDefinition bd : bds) {
      for ( Method method : bd.getBeanClass().getDeclaredMethods() ) {
        if ( ! method.isAnnotationPresent(RequestMapping.class) )
          continue;
        String URI = "";
        if ( bd.getBeanClass().isAnnotationPresent(RequestMapping.class) )
          URI = bd.getBeanClass().getAnnotation(RequestMapping.class).value();
        URI += method.getAnnotation(RequestMapping.class).value();
        Object controller = context.getBean( bd.getId() );
        HandlerMethod handlerMethod = new HandlerMethod();
        handlerMethod.setRequestMapping(URI);
        handlerMethod.setController(controller);
        handlerMethod.setMethod(method);
        handlerMapping.registerHandlerMethod(handlerMethod.getRegexURI(), handlerMethod);
      }
    }

  }

}
