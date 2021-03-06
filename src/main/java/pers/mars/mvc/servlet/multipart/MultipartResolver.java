package pers.mars.mvc.servlet.multipart;

import jakarta.servlet.http.HttpServletRequest;

public interface MultipartResolver {

  // 决定 request 是否是 multipart
  boolean isMultipart(HttpServletRequest request);

  MultipartHttpServletRequest resolveMultipart(HttpServletRequest request);

}