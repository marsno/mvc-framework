package mvc.web.multipart.support;

import jakarta.servlet.http.HttpServletRequest;
import mvc.web.multipart.MultipartFile;
import mvc.web.multipart.MultipartResolver;

public class StandardServletMultipartResolver
    implements MultipartResolver {

  @Override
  public boolean isMultipart(HttpServletRequest request) {

    if ( !request.getMethod().toLowerCase().equals("post") ) {
      return false;
    }

    String contentType = request.getContentType();
    return contentType != null && contentType.toLowerCase().startsWith("multipart/");
  }

}
