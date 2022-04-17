package pers.mars.mvc.servlet.multipart.support;

import jakarta.servlet.http.HttpServletRequest;
import pers.mars.mvc.servlet.multipart.MultipartHttpServletRequest;
import pers.mars.mvc.servlet.multipart.MultipartResolver;

public class StandardServletMultipartResolver
  implements MultipartResolver {

  /**
   * 判断 request 是否是 multipart
   * @param request current http request
   */
  @Override
  public boolean isMultipart(HttpServletRequest request) {

    if ( !request.getMethod().toLowerCase().equals("post") )
      return false;

    String contentType = request.getContentType();
    return contentType != null && contentType.toLowerCase().startsWith("multipart/");

  }

  /**
   * 将 HttpServletRequest 转化为 MultipartHttpServletRequest
   *
   * @param     request 要转化的 request
   * @return    转化后的 request
   */
  @Override
  public MultipartHttpServletRequest resolveMultipart(HttpServletRequest request) {
    return new StandardMultipartHttpServletRequest(request);
  }

}