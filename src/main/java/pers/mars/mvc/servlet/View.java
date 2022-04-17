package pers.mars.mvc.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

public interface View {

  void render(HttpServletRequest request,
              HttpServletResponse response,
              Map<String,Object> model);

}
