package mvc.beans;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Scanner {

  /**
   * 扫描包, 返回所有 Class.
   * @param packageName 只扫描哪个包, 如果等于 <code>""</code> 扫描所有包.
   * @return Class 列表, 或者 <code>null</code>, 如果此包下无文件.
   */
  public List<Class<?>> scan(String packageName) {
    // classpath = class文件的根目录的绝对路径
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    String classpath = loader.getResource("").getPath();
    classpath = classpath.substring( 1, classpath.length()-1 );

    // paths = 所有class文件的绝对路径
    File file = null;
    if (packageName.equals(""))
      file = new File(classpath);
    else
      file = new File(classpath + "/" + packageName.replace(".", "/"));
    List<String> paths = ToolMethods.getPathsUnderDirectory(file);
    if (paths == null) return null;

    // 处理 paths, 绝对路径 => 全类名
    String front = file.getPath() + "\\";
    if (!packageName.equals(""))
      front = file.getPath().substring(
        0,
        file.getPath().length() - packageName.length()
      );
    for ( int i = 0; i < paths.size(); i++ ) {
      String classname = paths.get(i)
              .replace( front, "" )
              .replace( ".class", "" )
              .replace( "\\","." );
      paths.set( i, classname );
    }
    List<String> classnames = paths;

    // 获取所有类
    List<Class<?>> classObjects = new ArrayList<Class<?>>();
    for (String classname : classnames) {
      try {
        Class<?> classObject = Class.forName(classname);
        classObjects.add( classObject );
      }
      catch (ClassNotFoundException e) {
        e.printStackTrace();
      }
    }

    return classObjects;
  }

}