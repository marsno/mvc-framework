package mvc.web.multipart.support;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.Part;
import mvc.web.multipart.MultipartFile;
import mvc.web.multipart.MultipartHttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class StandardMultipartHttpServletRequest
  extends HttpServletRequestWrapper
  implements MultipartHttpServletRequest {

  protected List<MultipartFile> multipartFileList = new ArrayList<>();

  protected void parseRequest(HttpServletRequest request) throws ServletException, IOException {
    Collection<Part> parts = request.getParts();
    for (Part part : parts) {
      MultipartFile multipartFile = new StandardMultipartFile(part, part.getName());
      this.multipartFileList.add(multipartFile);
    }
  }

  /**
   * 将 request 的形式由 HttpServletRequest 转化为 StandardMultipartHttpServletRequest
   *
   * @param   request 要转化的 request
   */
  public StandardMultipartHttpServletRequest(HttpServletRequest request) {
    super(request);
    try {
      parseRequest(request);
    } catch (ServletException | IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public HttpMethod getRequestMethod() {
    return null;
  }

  private static class StandardMultipartFile implements MultipartFile {

    private final Part part;

    // original file name
    private final String filename;

    @Override
    public String getName() {
      return this.part.getName();
    }

    @Override
    public String getOriginalFilename() {
      return this.filename;
    }

    @Override
    public String getContentType() {
      return this.part.getContentType();
    }

    @Override
    public void transferTo(File destination) throws IOException {
      this.part.write( destination.getPath() );
    }

    public StandardMultipartFile(Part part, String filename) {
      this.part = part;
      this.filename = filename;
    }

  }

}