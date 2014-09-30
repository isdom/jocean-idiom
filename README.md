jocean-idiom
============

jocean's 通用基础库，适用于 Android & J2SE.

2014-09-30:  release 0.1.2 版本
  1、在InterfaceUtils.combineImpls 和 InterfaceUtils.genAsyncImpl 实现中，处理 Thread.currentThread().getContextClassLoader() 返回为 null 的情况，直接使用 cls.getClassLoader()。

2014-08-19:  release 0.1.1 版本
  1、实现 RefcountedArgsGuard 工具支持类，用于 ReferenceCounted 实例用于异步传递时的retain/release 保护
  2、新增 Md5 工具类，获取 特定字符串的md5计算结果，并转换为固定32个字符长度的String
  3、Blob 接口实现 Closeable 接口， BlobImpl 实现 close 的方法是调用一次 release。 在JDK 1.7兼容环境下 可基于 try-with-resources 特性，自动释放作用域开头区域创建的 Blob 实例。
  4、BlockUtils静态工具类新增 静态方法: public static long blob2DataOutput和 public static long inputStream2DataOutput  

2014-06-11:  release 0.1.0 版本
  1、增加ValidationId，用于event-api based 业务逻辑(flow)实现中的有效实例验证

2014-05-22： release 0.0.9 版本：
  1、升级到 0.0.9-SNAPSHOT，新增 AnnotationWrapper，包装不能直接标注的annotation
  2、AnnotationWrapper 增加 @Retention(RetentionPolicy.RUNTIME)
  3、在 ExceptionUtils.exception2detail 中对InvocationTargetException进行识别，并提取出所包装的 TargetException，显示异常堆栈
  4、对 idiom 中的Detachable增加静态常量 DoNothing，在其detach方法中，不做任何动作
  5、将通用 FetchAgent 定义加入到 jocean-idiom子模块中，将 KEY & VALUE 的泛型限制在FetchAgent 类上、onFetchResult 方法更名为 onFetchComplete
  6、inputStream2BytesListInputStream：将通用InputStream内容读取到BytesList作为后端存储的InputStream(该InputStream 支持 mark &
  reset操作)
  7、添加BlockUtils.blob2OutputStream 方法
