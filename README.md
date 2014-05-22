jocean-idiom
============

jocean's 通用基础库，适用于 Android & J2SE.

2014-05-22： release 0.0.9 版本：
  1、升级到 0.0.9-SNAPSHOT，新增 AnnotationWrapper，包装不能直接标注的annotation
  2、AnnotationWrapper 增加 @Retention(RetentionPolicy.RUNTIME)
  3、在 ExceptionUtils.exception2detail 中对InvocationTargetException进行识别，并提取出所包装的 TargetException，显示异常堆栈
  4、对 idiom 中的Detachable增加静态常量 DoNothing，在其detach方法中，不做任何动作
  5、将通用 FetchAgent 定义加入到 jocean-idiom子模块中，将 KEY & VALUE 的泛型限制在FetchAgent 类上、onFetchResult 方法更名为 onFetchComplete
  6、inputStream2BytesListInputStream：将通用InputStream内容读取到BytesList作为后端存储的InputStream(该InputStream 支持 mark &
  reset操作)
  7、添加BlockUtils.blob2OutputStream 方法
