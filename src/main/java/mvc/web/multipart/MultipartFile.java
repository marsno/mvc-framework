package mvc.web.multipart;

import java.io.File;
import java.io.IOException;

public interface MultipartFile {

  /**
   * Return the name of the parameter.
   * @return the name of the parameter
   */
  String getName();

  /**
   * 返回客户端文件系统的文件名.
   * @return 文件名 String
   */
  String getOriginalFilename();

  /**
   * 返回文件的 content type.
   * @return content type, {@code null} otherwise
   */
  String getContentType();

  /**
   * 将文件存储在本地.
   * @param destination destination file
   */
  void transferTo(File destination) throws IOException;

}