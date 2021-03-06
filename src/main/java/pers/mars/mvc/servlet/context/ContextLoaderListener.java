package pers.mars.mvc.servlet.context;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import pers.mars.mvc.context.annotation.AnnotationConfigApplicationContext;

/**
 * 实现了 ServletContextListener 接口, 用于在 ServletContext 被创建时,
 * 创建 ioc 容器, 并将其作为 ServletContext 的属性, 便于 DispatcherServlet
 * 获取作为 spring mvc ioc 容器的父容器.
 */
public class ContextLoaderListener implements ServletContextListener {

  /** 初始化 ioc 容器, 将 ioc 容器设置为 ServletContext 的属性 */
  @Override
  public void contextInitialized(ServletContextEvent servletContextEvent) {

    String configurationClassName = servletContextEvent.getServletContext().getInitParameter("configurationClassName");
    Class<?> configurationClass = null;
    try {
      configurationClass = Class.forName(configurationClassName);
    } catch (ClassNotFoundException exception) {
      exception.printStackTrace();
    }
    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(configurationClass);

    // 将 ioc 容器添加到 ServletContext 中
    ServletContext servletContext = servletContextEvent.getServletContext();
    servletContext.setAttribute("context", context);

  }

  @Override
  public void contextDestroyed(ServletContextEvent servletContextEvent) {}

}
