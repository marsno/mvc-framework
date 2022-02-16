package mvc.web.servlet.support;

import jakarta.servlet.http.HttpServletRequest;
import mvc.web.servlet.HandlerExecutionChain;
import mvc.web.servlet.HandlerMapping;

public class SimpleUrlHandlerMapping implements HandlerMapping {

  @Override
  public HandlerExecutionChain getHandler(HttpServletRequest request) {
    return null;
  }

}
