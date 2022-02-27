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

    // class 文件的根目录的绝对路径
    String classpath = Thread.currentThread().getContextClassLoader().getResource("").getPath();

    // 创建要扫描的包的 File 对象
    File file = null;
    if ( packageName.equals("") ) {
      file = new File(classpath);
    } else {
      String packagePath = classpath + File.separator + packageName.replace(".", File.separator);
      file = new File(packagePath);
    }

    // 获取所有类对应的文件的绝对路径
    List<String> processingClassNameList = ToolMethods.getPathsUnderDirectory(file);
    if (processingClassNameList == null) return null;

    // 只保留以 ".class" 为后缀的 java 文件, 剔除其他文件, 例如 ".yaml"
    for (int index = processingClassNameList.size() - 1; index >= 0; index--) {
      if ( !processingClassNameList.get(index).endsWith(".class") ) {
        processingClassNameList.remove(index);
      }
    }

    // 将所有的类对应的绝对路径转化为全类名
    for ( int i = 0; i < processingClassNameList.size(); i++ ) {
      String classname = processingClassNameList.get(i)
        .substring(classpath.length())
        .replace(".class", "").replace(File.separator, ".");
      processingClassNameList.set(i, classname);
    }

    // 获取所有类
    List<Class<?>> classObjects = new ArrayList<Class<?>>();
    for (String classname : processingClassNameList) {
      try {
        Class<?> classObject = Class.forName(classname);
        classObjects.add( classObject );
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }
    }

    return classObjects;

  }

}