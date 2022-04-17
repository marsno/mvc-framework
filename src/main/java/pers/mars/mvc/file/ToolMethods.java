package pers.mars.mvc.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ToolMethods {

  /**
   * 获取在这个文件夹下的所有文件的绝对路径
   * @param directory 文件夹
   * @return 所有文件的绝对路径, 或者 <code>null</code>, 如果文件夹下无文件
   */
  public static List<String> getPathsUnderDirectory(File directory) {
    List<String> paths = new ArrayList<>();
    File[] files = directory.listFiles();
    if (files == null) return null;
    for (File file : files) {
      if (!file.isDirectory()) {
        paths.add(file.getPath());
      }
      else {
        List<String> subPaths = ToolMethods.getPathsUnderDirectory(file);
        if (subPaths != null) paths.addAll( subPaths );
      }
    }
    return paths;
  }

}
