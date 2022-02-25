package mvc.web.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import mvc.beans.ApplicationContext;
import mvc.beans.BeanDefinition;
import mvc.beans.config.BeanScope;
import mvc.web.multipart.MultipartResolver;
import mvc.web.multipart.support.StandardServletMultipartResolver;
import mvc.web.servlet.config.RequestMapping;
import mvc.web.servlet.context.ServletBeanDefinitionStrategy;
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

/** spring mvc 唯一的 HttpServlet, 也是整个框架最顶层的类 */
public class DispatcherServlet extends HttpServlet {

  /** spring mvc 的内部 beans */
  private List<Class<?>> internalBeans = new ArrayList<Class<?>>();

  /** servlet context */
  private WebApplicationContext context = null;

  /**
   * handler mapping list 里面有:
   * - RequestMappingHandlerMapping
   * - SimpleUrlHandlerMapping
   */
  private List<HandlerMapping> handlerMappingList  = new ArrayList<>();

  /** 处理 HandlerMethod, 生成 ModelAndView */
  private RequestMappingHandlerAdapter handlerAdapter = null;

  /** view resolver list */
  private ViewResolver viewResolver = null;

  private MultipartResolver multipartResolver = null;

  /**
   * 初始化 ioc 容器
   * 若 ServletContext 中有 name 为 "context" 的属性,
   * 将其设为 ioc 容器的父容器
   */
  private void initContext() {

    this.internalBeans.add(RequestMappingHandlerMapping.class);
    this.internalBeans.add(RequestMappingHandlerAdapter.class);
    this.internalBeans.add(InternalResourceViewResolver.class);
    this.internalBeans.add(NormalMethodArgumentResolver.class);
    this.internalBeans.add(RequestParamMethodArgumentResolver.class);
    this.internalBeans.add(PathVariableMethodArgumentResolver.class);
    this.internalBeans.add(StandardServletMultipartResolver.class);

    // 创建 WebApplicationContext, 并指定一个 strategy
    this.context = new WebApplicationContext( new ServletBeanDefinitionStrategy() );

    // 初始化, "" 意味着扫描 classpath 下的所有类
    this.context.init("");

    // 在 this.context 中注册所有 framework 使用的所有 bean
    for (Class<?> type : this.internalBeans) {
      String beanId = type.getSimpleName().toLowerCase().charAt(0) + type.getSimpleName().substring(1);
      this.context.registerBean(beanId, BeanScope.SINGLETON, type);
    }

    // 如果在 ServletContext 中有父容器, 添加父容器
    ApplicationContext parentContext = (ApplicationContext) this.getServletContext().getAttribute("context");
    if (context == null) return;
    this.context.setParentContext(parentContext);

  }

  // init this.handlerMapping
  private void initHandlerMappingList(WebApplicationContext context) {

    RequestMappingHandlerMapping handlerMapping =
      (RequestMappingHandlerMapping) context.getBean("requestMappingHandlerMapping");

    this.initRequestMappingHandlerMapping(handlerMapping, context);

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
    HandlerExecutionChain handlerExecutionChain = null;

    for (HandlerMapping handlerMapping : this.handlerMappingList) {
      handlerExecutionChain = handlerMapping.getHandler(request);
      if (handlerExecutionChain != null) break;
    }

    if (handlerExecutionChain == null) {
      response.setStatus(404);
      return;
    }

    ModelAndView modelAndView = this.handlerAdapter.handle(
      request, response, handlerExecutionChain.getHandler()
    );

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

  /**
   * @param handlerMapping 需要初始化的 handler mapping
   * @param context ioc 容器
   * @return 初始化成功返回 {@code true}, 失败返回 {@code false}
   */
  private boolean initRequestMappingHandlerMapping( RequestMappingHandlerMapping handlerMapping,
                                                    WebApplicationContext context ) {

    // 获取所有 controller 的 BeanDefinition
    List<BeanDefinition> bds = context.getControllers();
    if (bds == null) return false;

    // 根据 controller 的 BeanDefinition 初始化 handlerMapping
    for (BeanDefinition bd : bds) {
      for ( Method method : bd.getBeanClass().getDeclaredMethods() ) {
        if ( !method.isAnnotationPresent(RequestMapping.class) )
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

    return true;

  }

  /**
   * 初始化 {@code this.multipartResolver}
   *
   * @param    context spring mvc 的 ioc 容器, 用于获取 bean
   */
  public void initMultipartResolver(WebApplicationContext context) {

    StandardServletMultipartResolver multipartResolver = (StandardServletMultipartResolver)
      context.getBean("standardServletMultipartResolver");

    this.multipartResolver = multipartResolver;

  }

  // HttpServlet 被创建时初始化的扩展点
  @Override
  public void init() throws ServletException {
    this.initContext();
    this.initMultipartResolver(this.context);
    this.initHandlerMappingList(this.context);
    this.initHandlerAdapter(this.context);
    this.initViewResolver(this.context);
  }

  // 接受 request 后, 调用的方法
  @Override
  protected void service(HttpServletRequest request, HttpServletResponse response) {
    this.doDispatch(request, response);
  }

}