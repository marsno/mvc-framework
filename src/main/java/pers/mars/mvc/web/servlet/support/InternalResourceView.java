package pers.mars.mvc.web.servlet.support;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pers.mars.mvc.web.servlet.View;

import java.io.IOException;
import java.util.Map;

public class InternalResourceView implements View {

  public InternalResourceView() {
    super();
  }

  // constructor
  public InternalResourceView(String url) {
    super();
    this.URL = url;
  }

  private String URL = null;

  @Override
  public void render(HttpServletRequest request,
                     HttpServletResponse response,
                     Map<String,Object> model) {

    // 将 model 作为 request 的属性传入
    for (Map.Entry<String,Object> entry : model.entrySet()) {
      request.setAttribute(entry.getKey(), entry.getValue());
    }

    // 转发
    try {
      request.getRequestDispatcher(this.URL).forward(request, response);
    }
    catch (ServletException | IOException e) {
      e.printStackTrace();
    }

  }

  // setter
  public void setUrl(String URL) {
    this.URL = URL;
  }

}
