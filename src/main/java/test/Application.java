package test;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.startup.Tomcat;
import pers.mars.mvc.servlet.DispatcherServlet;

public class Application {

  public static int PORT = 8080;
  public static String HOSTNAME = "127.0.0.1";
  public static String WEBAPP_PATH = "src/main";
  public static String CLASSES = "/WEB-INF/classes";
  public static String CLASS_PATH = "target/classes";
  public static String INTERNAL_PATH = "/";

  public static void main(String[] args) {

    Tomcat tomcat = new Tomcat();
    tomcat.setPort(Application.PORT);
    tomcat.setHostname(Application.HOSTNAME);
    tomcat.setBaseDir(".");

    DispatcherServlet servlet = new DispatcherServlet();
    Wrapper servletWrapper = tomcat.addServlet("", "dispatcherServlet", servlet);
    servletWrapper.setLoadOnStartup(1);
    servletWrapper.addMapping("/");
    servletWrapper.addInitParameter("configurationClassName", "test.DefaultConfiguration");
    try {
      tomcat.start();
      tomcat.getServer().await();
    } catch (LifecycleException e) {
      e.printStackTrace();
    }

  }
}
