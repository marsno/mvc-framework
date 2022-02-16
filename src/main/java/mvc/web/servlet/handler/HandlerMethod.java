package mvc.web.servlet.handler;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public class HandlerMethod {

  public HandlerMethod() {
    super();
  }

  /**
   * @param controller handler method 所在的 controller 对象
   * @param method handler method 所在的 method
   */
  public HandlerMethod(Object controller, Method method) {
    super();
    this.controller = controller;
    this.method = method;
    this.returnType = method.getReturnType();
    this.parameters = method.getParameters();
  }

  // <code>RequestMapping</code> 的值
  private String requestMapping = null;

  // handler method 所在的对象实例 controller, 默认是单例
  private Object controller;

  // handler 方法
  private Method method;

  // handler 方法的所有参数;
  private Parameter[] parameters;

  private Class<?> returnType;

  // getter
  public Parameter[] getParameters() {
    return this.parameters;
  }

  // setter
  public void setController(Object controller) {
    this.controller = controller;
  }

  // getter
  public Object getController() {
    return this.controller;
  }

  // setter
  public void setMethod(Method method) {
    this.method = method;
    this.returnType = method.getReturnType();
    this.parameters = method.getParameters();
  }

  // getter
  public Method getMethod() {
    return this.method;
  }

  // setter
  public void setRequestMapping(String mapping) {
    this.requestMapping = mapping;
  }

  // getter
  public String getRequestMapping() {
    return this.requestMapping;
  }

  // 获取 regex uri
  public String getRegexURI() {
    String processingString = String.copyValueOf( this.requestMapping.toCharArray() );
    String regexURI = null;
    while (true) {
      regexURI = String.copyValueOf( processingString.toCharArray() );
      processingString = this.toRegexString(processingString);
      if (processingString == null) {
        return regexURI;
      }
    }
  }

  /**
   * 将 String 其中的一个 "{*}" 替换为 "(.*)"
   * @param value 想要修改的 String
   * @return 修改过后的 String, 若找不到, 返回 <code>null</code>
   */
  public String toRegexString(String value) {
    int front = 0;
    int behind = 0;
    boolean isFrontExisting = false;
    boolean isBehindExisting = false;
    for (int i = 0; i < value.length(); i++) {
      if ( !isFrontExisting && value.charAt(i) == '{' ) {
        front = i;
        isFrontExisting = true;
      }
      if ( isFrontExisting && value.charAt(i) == '}' ) {
        behind = i;
        isBehindExisting = true;
        break;
      }
    }
    if ( !isFrontExisting || !isBehindExisting ) {
      return null;
    }
    else {
      return value.substring( 0, front ) + "(.*)" + value.substring( behind + 1 );
    }
  }

  /**
   * 获取 this.requestMapping 中的 argument name
   *
   * @return argument name array, or <code>null</code>,
   *         如果不是 PathVariable
   */
  public String[] getArgumentNames() {
    String rm = this.requestMapping;
    List<Integer> indexList = new ArrayList<Integer>();
    boolean isMatchingLeft = true;
    for (int i = 0; i < rm.length(); i++) {
      if (isMatchingLeft && rm.charAt(i) == '{') {
        indexList.add(i);
        isMatchingLeft = false;
      }
      else if (!isMatchingLeft && rm.charAt(i) == '}') {
        indexList.add(i);
        isMatchingLeft = true;
      }
    }
    if (indexList.size() % 2 != 0) return null;
    String[] argumentNames = new String[ indexList.size() / 2 ];
    for (int i = 0; i < indexList.size() / 2; i++) {
      int left = indexList.get( i * 2 );
      int right = indexList.get( (i*2) + 1 );
      argumentNames[i] = rm.substring( left+1, right );
    }
    return argumentNames;
  }

}
