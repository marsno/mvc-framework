package pers.mars.mvc.web.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pers.mars.mvc.context.BeanDefinition;
import pers.mars.mvc.context.annotation.BeanScope;
import pers.mars.mvc.web.multipart.MultipartResolver;
import pers.mars.mvc.web.multipart.support.StandardServletMultipartResolver;
import pers.mars.mvc.web.bind.annotation.RequestMapping;
import pers.mars.mvc.web.servlet.config.annotation.InterceptorRegistry;
import pers.mars.mvc.web.servlet.config.annotation.WebMvcConfigurer;
import pers.mars.mvc.web.servlet.context.WebApplicationContext;
import pers.mars.mvc.web.servlet.handler.HandlerMethod;
import pers.mars.mvc.web.servlet.handler.support.NormalMethodArgumentResolver;
import pers.mars.mvc.web.servlet.handler.support.PathVariableMethodArgumentResolver;
import pers.mars.mvc.web.servlet.handler.support.RequestParamMethodArgumentResolver;
import pers.mars.mvc.web.servlet.support.InternalResourceViewResolver;
import pers.mars.mvc.web.servlet.support.RequestMappingHandlerAdapter;
import pers.mars.mvc.web.servlet.support.RequestMappingHandlerMapping;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** spring mvc 唯一的 HttpServlet, 也是整个框架最顶层的类 */
public class DispatcherServlet extends HttpServlet {

  /** DispatcherServlet 是否准备好接受请求 */
  protected boolean isReady = false;

  /** spring mvc 的内部 beans */
  protected List<Class<?>> internalBeans = new ArrayList<Class<?>>();

  /** servlet context */
  protected WebApplicationContext context = null;

  /**
   * handler mapping list 里面有:
   * - RequestMappingHandlerMapping
   * - SimpleUrlHandlerMapping
   */
  protected List<HandlerMapping> handlerMappingList  = new ArrayList<>();

  /** 处理 HandlerMethod, 生成 ModelAndView */
  protected RequestMappingHandlerAdapter handlerAdapter = null;

  /** view resolver list */
  protected ViewResolver viewResolver = null;

  protected MultipartResolver multipartResolver = null;

  /**
   * 初始化 ioc 容器
   * 若 ServletContext 中有 name 为 "context" 的属性,
   * 将其设为 ioc 容器的父容器
   */
  protected void initContext() {

    String configurationClassName = this.getServletConfig().getInitParameter("defaultConfigurationClass");
    Class<?> configuration = null;
    try {
      configuration = Class.forName(configurationClassName);
    }
    catch (ClassNotFoundException exception) {
      exception.printStackTrace();
    }
    this.context = new WebApplicationContext(configuration);

    this.internalBeans.add(RequestMappingHandlerMapping.class);
    this.internalBeans.add(RequestMappingHandlerAdapter.class);
    this.internalBeans.add(InternalResourceViewResolver.class);
    this.internalBeans.add(NormalMethodArgumentResolver.class);
    this.internalBeans.add(RequestParamMethodArgumentResolver.class);
    this.internalBeans.add(PathVariableMethodArgumentResolver.class);
    this.internalBeans.add(StandardServletMultipartResolver.class);
    this.internalBeans.add(InterceptorRegistry.class);

    // 在 this.context 中注册所有 framework 使用的所有 bean
    for (Class<?> type : this.internalBeans) {
      String beanId = type.getSimpleName().toLowerCase().charAt(0) + type.getSimpleName().substring(1);
      this.context.registerBean(beanId, BeanScope.SINGLETON, type);
    }

  }

  /**
   * 初始化 {@code this.handlerMappingList}
   *
   * @param    context 用于获取 bean
   */
  protected void initHandlerMappings(WebApplicationContext context) {

    RequestMappingHandlerMapping handlerMapping
      = (RequestMappingHandlerMapping) context.getBean("requestMappingHandlerMapping");

    this.initRequestMappingHandlerMapping(handlerMapping, context);
    this.handlerMappingList.add(handlerMapping);

  }

  // init this.handlerAdapter
  protected void initHandlerAdapter(WebApplicationContext context) {

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

  /** init this.viewResolverList */
  protected void initViewResolver(WebApplicationContext context) {
    this.viewResolver = (ViewResolver) context.getBean("internalResourceViewResolver");
  }

  /** this.service 调用 */
  protected void doDispatch( HttpServletRequest request,
                           HttpServletResponse response ) {

    HttpServletRequest processedRequest = checkMultipart(request);

    HandlerExecutionChain handlerExecutionChain = getHandler(request);

    // handlerExecutionChain 为 null
    if (handlerExecutionChain == null) {

      // 是否请求静态资源
      try {
        if (this.tryFindStaticResource(request, response)) {
          return;
        }
      }
      catch (IOException exception) {
        exception.printStackTrace();
      }

      // 运行到此, 没有找到 handler, 也不是请求静态资源, return 404
      response.setStatus(404);
      return;

    }

    if (!handlerExecutionChain.applyPreHandle(request, response)) {
      return;
    }

    ModelAndView modelAndView
      = this.handlerAdapter.handle( request, response, handlerExecutionChain.getHandler() );

    handlerExecutionChain.applyPostHandle(request, response, modelAndView);

    // modelAndView 可能并没有设置 view, 只能 return 404, 若设置了, 渲染
    if (modelAndView.getViewName() != null) {
      this.render(request, response, modelAndView);
    }
    else {
      response.setStatus(404);
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

    // 从 modelAndView 中获取 model
    Map<String,Object> model = modelAndView.getModelMap();

    // 通过 ViewResolver 获取 View 对象
    View view = this.viewResolver.resolveViewName( modelAndView.getViewName() );

    // 渲染
    view.render( request, response, model );

  }

  /**
   * @param handlerMapping 需要初始化的 handler mapping
   * @param context ioc 容器
   */
  protected void initRequestMappingHandlerMapping(RequestMappingHandlerMapping handlerMapping,
                                                  WebApplicationContext context ) {

    // 注入 InterceptorRegistry
    InterceptorRegistry registry = (InterceptorRegistry) context.getBean("interceptorRegistry");
    WebMvcConfigurer webMvcConfigurer = context.getBean(WebMvcConfigurer.class);
    if (webMvcConfigurer != null) {
      webMvcConfigurer.addInterceptors(registry);
    }
    handlerMapping.setInterceptorRegistry(registry);

    // 获取所有 controller 的 BeanDefinition
    List<BeanDefinition> bds = context.getControllers();
    if (bds == null) {
      System.out.println("没有任何 controller 被找到");
      return;
    }

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
        handlerMethod.setRequestMethods( method.getAnnotation(RequestMapping.class).method() );
        handlerMethod.setController(controller);
        handlerMethod.setMethod(method);
        handlerMapping.registerHandlerMethod(handlerMethod.getRegexURI(), handlerMethod);
      }
    }

  }

  /**
   * 初始化 {@code this.multipartResolver}
   *
   * @param context spring mvc 的 ioc 容器, 用于获取 bean
   */
  protected void initMultipartResolver(WebApplicationContext context) {

    StandardServletMultipartResolver multipartResolver = (StandardServletMultipartResolver)
      context.getBean("standardServletMultipartResolver");
    if (multipartResolver == null) return;

    this.multipartResolver = multipartResolver;

  }

  /**
   * 将 request 从 HttpServletRequest 转化为 MultipartHttpServletRequest
   * 如果请求是 multipart 的情况下.
   *
   * @param     request 当前要检查的 request
   * @return    如果是 multipart, 则是 MultipartHttpServletRequest
   */
  protected HttpServletRequest checkMultipart(HttpServletRequest request) {

    // 如果 request is multipart, 将其转化为 MultipartHttpServletRequest
    if ( this.multipartResolver != null && this.multipartResolver.isMultipart(request) )
      return this.multipartResolver.resolveMultipart(request);

    // 前面没有 return, 则 argument request 不是 multipart, 不做任何改变 return
    return request;

  }

  /**
   * 获取 HandlerExecutionChain 为了当前的 request
   *
   * @return    一个 HandlerExecutionChain instance,
   *            or {@code null} 如果没有 handler 被找到
   * @param     request 当前的 request
   */
  protected HandlerExecutionChain getHandler(HttpServletRequest request) {

    // 如果一个 handler mapping 也没有, return null
    if ( this.handlerMappingList.isEmpty() ) {
      return null;
    }

    // 遍历 this.handlerMappingList, 获取 handler
    for (HandlerMapping handlerMapping : this.handlerMappingList) {
      HandlerExecutionChain handlerExecutionChain = handlerMapping.getHandler(request);
      if (handlerExecutionChain != null) {
        return handlerExecutionChain;
      }
    }

    // 前面没有 return, 说明没有 handler 被找到
    return null;

  }

  /**
   * 尝试将静态资源作为 response
   *
   * @return    {@code true} 如果找到并返回, or {@code false} 如果没有找到
   * @param     request 当前的 http request
   * @param     response 当前的 http response
   * @throws    FileNotFoundException 资源不存在
   */
  protected boolean tryFindStaticResource(HttpServletRequest request, HttpServletResponse response)
    throws IOException {

    String webRoot = request.getSession().getServletContext().getRealPath("");

    // 获取请求的 static file path
    String[] nodes = request.getRequestURI().split("/");
    String filePath = webRoot.substring(0, webRoot.length() - 1);;
    for (String node : nodes) {
      if ( node.equals("") ) continue;
      filePath += File.separator + node;
    }

    // 判断文件是否存在
    File file = new File(filePath);
    if ( !file.exists() ) {
      return false;
    }

    FileInputStream fileInputStream = new FileInputStream(file);
    ServletOutputStream outputStream = response.getOutputStream();

    byte[] buffer = new byte[1024];
    while (fileInputStream.read(buffer) != -1) {
      outputStream.write(buffer);
    }

    fileInputStream.close();
    outputStream.flush();
    outputStream.close();

    return true;

  }

  /**
   * 接受请求后调用,
   */
  @Override
  protected void service(HttpServletRequest request, HttpServletResponse response) {

    if (!this.isReady) {
      System.out.println("DispatcherServlet 初始化失败, 无法接受请求");
      response.setStatus(404);
      return;
    }

    this.doDispatch(request, response);

  }

  /**
   * Servlet 容器提供的 HttpServlet 扩展节点,
   * 用于初始化 HttpServlet, 由 Servlet 容器调用.
   */
  @Override
  public void init() throws ServletException {

    this.initContext();
    this.initMultipartResolver(this.context);
    this.initHandlerMappings(this.context);
    this.initHandlerAdapter(this.context);
    this.initViewResolver(this.context);

    // 运行到这里, 初始化完成, DispatcherServlet 准备好接受 request
    this.isReady = true;

  }

}