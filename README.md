jocean-idiom
============

jocean's 通用基础库，适用于 Android & J2SE.

2015-11-18:  release 0.1.3 版本

    1、增加 getMethodOf，获取指定 Class/MethodName/参数类型 的Method
    2、增加 Emitter 接口定义，向特定 Action1<T> 实例发射 T 实例
    3、解决 Android 下的 combineImpls 问题: java.lang.IllegalArgumentException:interface org.jocean.http.client.ApplyToRequest is not visible from class loader
    4、新增通用工具类：Regexs —— 正则表达式工具类
    5、增加 BeanHolder 接口，定义获取 Bean 实例的标准方式; 扩展 BeanHolder 接口的接口方法，getBean(name, requiredType); 添加 BeanHolder 感知类: BeanHolderAware
    6、添加idiom.io子包，包含从common-io-2.4移植得来的FilenameUtils & IOCase工具类
    7、新增 Ordered 接口，表示可排序类型
    8、新增JOArrays静态工具类，addFirst用于在已有的数组(可为null)前面添加元素(toAdd)，并返回新产生的数组实例
    9、add RxJava 1.0.15 as dependency；
        新增 idiom.rx 子包；包括 OneshotSubscription 抽象基类
        and add RxSubscribers as Rxutil,guardUnsubscribed method using 
        GuardUnsubscribedSubscriber wrap Subscriber instance, which never call onNext、onCompleted or onError when isUnsubscribed return true；
        使用 FuncX & ActionX 代替 Function & VisitorX
        添加 delaySubscriptionUntilCompleted 静态方法，实现对 source Observable 的包装, 使得 subscription 被延迟到 selector's onCompleted 发生时
        新增工具类 rx.Functions 实现了基于RxJava Function 接口的静态工具方法。fromConstant 实现从单个R实例产生 FuncN<R>的实例
        OnNextSensor & TestSubscription 迁移到 jocean-idiom 模块中
        新增静态工具类:RxObservables,静态方法ignoreCompleted()产生忽略onCompleted的Transformer，可用于Observable<T>.compose.
    10、增加Features工具类，用于配置及检测通过enum表达启动或禁止特定功能的特性,从 fastjson 代码参考
    11、InterfaceUtils新增 InterfaceUtils.selectIncludeType 方法，从Object数组中过滤出实现了特定接口的N个实例，若没有满足要求的实例，返回值为null; 
        新增方法InterfaceUtils.compositeIncludeType, 根据特定接口类，从候选对象实例数组中，构建出单个的接口实例; 
        ReflectUtils 工具类添加 invokeClone 方法，对任意类型通过反射调用其 clone 方法
    12、ExectionLoop中新增immediateLoop，为即时执行ExectionLoop实例，一般用于测试
    13、COWCompositeSupport工具类新增 isEmpty 测试方法，返回是否不包含任何component
    14、新建子包stats，将业务统计和时间间隔统计接口及部分实现类 (BizMemo、BizMemoImpl & TimeIntervalMemo)从xharbor工程迁移到 jocean-idiom模块;
        在BizMemo中增加StepMemo接口定义，并定义静态方法buildStepMemo通过BizMemo实例和StopWatch实例产生StepMemo实例
    15、新增 Tuple 工具类，表示个数不定的多元组，Pair 等价于 Tuple.of(first, second)，而 Triple 等价于 Tuple.of(first, second, third)
    16、新增 PairedVisitor 接口，用于表示更为广泛的具备成对调用要求的接口/类，例如netty
    17、新增 计时工具类 StopWatch
    18、SimpleCache 添加 clear 方法: 移除所有缓存项, 添加 接受 Visitor2<K,V> 参数的新构造函数，当特定的value 和 key首次关联时，该回调将会被调用 ， 增加 snapshot 方法，获取当前缓存内容的快照
    19、切换用 gradle 构建 
    
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
    6、inputStream2BytesListInputStream：将通用InputStream内容读取到BytesList作为后端存储的InputStream(该InputStream 支持 mark & reset操作)
    7、添加BlockUtils.blob2OutputStream 方法
