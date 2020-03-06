package com.itisacat.basic.framework.consts;

/**
 * @ClassName: PropertiesConsts
 * @Description: 所有BaseProperties所有key常量
 */
public class PropConsts {

    private PropConsts() {
    }

    public static class Core {

        private Core() {
        }

        /**
         * CORE 应用系统名称
         */
        public static final String DEFAULT_APPLICATION_NAME = "default.application.name";

        /**
         * CORE 是否开启配置中心
         */
        public static final String HJCONFIG_ENABLED = "hjconfig.enabled";

        /**
         * 未知
         */
        public static final String UNKNOWN = "unknown";

    }

    public static class Dao {

        private Dao() {
        }

        /**
         * DAO 系统是否加载dao层 默认:true 如果系统不需要连接数据库 需要设置为false
         */
        public static final String SYS_DAO_ISLOAD = "sys.dao.isload";

        /**
         * DAO sharding 分片规则配置文件 默认为sharding.json
         */
        public static final String JDBC_SHARDING_FILE = "jdbc.sharding.file";

        /**
         * DAO druid 是否做空闲连接检查 默认:true
         */
        public static final String JDBC_TESTWHILEIDLE = "jdbc.testWhileIdle";

        /**
         * DAO druid 从数据库连接池中获取连接时是否需要做有效检查 默认:false
         */
        public static final String JDBC_TESTONBORROW = "jdbc.testOnBorrow";

        /**
         * DAO druid 返回数据库连接池时是否需要做有效检查 默认:false
         */
        public static final String JDBC_TESTONRETURN = "jdbc.testOnReturn";

        /**
         * DAO druid 超过时间限制是否回收 默认:true
         */
        public static final String JDBC_REMOVEABANDONED = "jdbc.removeAbandoned";

        /**
         * DAO druid 验证查询超时 default:5s
         */
        public static final String JDBC_VALIDATIONQUERYTIMEOUT = "jdbc.validationQueryTimeout";

        /**
         * DAO druid 关闭abanded连接时输出错误日志 默认:true
         */
        public static final String JDBC_LOGABANDONED = "jdbc.logAbandoned";

        /**
         * DAO druid 超时时间；单位为秒。 默认:30秒 公司的HAProxy 默认设置为60S 应用设置应该比这个值小
         */
        public static final String JDBC_REMOVEABANDONEDTIMEOUT = "jdbc.removeAbandonedTimeout";

        /**
         * DAO druid 是否对SQL语句进行检查 默认:true
         */
        public static final String JDBC_CHECK_SQL = "jdbc.check.sql";

        /**
         * DAO druid 是否显示慢SQL 默认：true
         */
        public static final String DRUID_SHOW_SQL = "druid.show.sql";

        /**
         * DAO druid 慢SQL 监控阈值 默认:3000ms
         */
        public static final String DRUID_SHOW_TIMEOUT = "druid.sql.timeout";

        /**
         * DAO druid 是否合并SQL 默认:false
         */
        public static final String DRUID_MERGE_SQL = "druid.merge.sql";

        /**
         * DAO druid 是否对union sql检查 默认:false
         */
        public static final String DRUID_SELECT_UNIONCHECK = "druid.select.unioncheck";

        /**
         * DAO 数据加密 公钥设置
         */
        public static final String JDBC_RSA_PUBLICKEY = "jdbc.password.publickey";

        /**
         * DAO mybatis typeAliasesPackage设置
         */
        public static final String MYBATIS_MODEL = "mybatis.model";

        /**
         * DAO mybatis typeHandlersPackage设置
         */
        public static final String MYBATIS_TYPEHANDLERSPACKAGE = "mybatis.typeHandlersPackage";

        /**
         * DAO mybatis 定义dao interface 并加入@MyBatisRepository 扫描包路径设置
         */
        public static final String MYBATIS_BASEPACKAGE = "mybatis.basePackage";
        /**
         * DAO mybatis 定义 Configuration 中解决执行sql的响应超时设置 default:300秒
         */
        public static final String MYBATIS_STATEMENT_TIMEOUT = "mybatis.statement.timeout";

        /**
         * DAO datasouce 定义事物后缀名称 默认:tx
         */
        public static final String TX_SUFFIX = "datasouce.tx.suffix";

        /**
         * DAO mybatis 是否显示慢SQL 默认:false
         */
        public static final String MYBATIS_SHOW_SQL = "mybatis.show.sql";

        /**
         * DAO mybatis 如果查询参数为空 防止数据全部加入jvm 默认增加limit 500, 默认:true
         */
        public static final String MYBATIS_MAXLIMIT_ENABLE = "mybatis.maxLimit.enable";
        /**
         * DAO mybatis 慢SQL的阈值设置 默认:3000ms
         */
        public static final String MYBATIS_SHOW_TIMEOUT = "mybatis.sql.timeout";
        /**
         * DAO mybatis 如果执行select时，无where条件 为防止所有数据加载到java内存 引发oom，加入获取最大条数 默认:500
         */
        public static final String MYBATIS_MAX_LIMIT = "mybatis.max.limit";

        /**
         * DAO page 设置为true时，会将RowBounds第一个参数offset当成pageNum页码使用和startPage中的pageNum效果一样 默认:true
         */
        public static final String MYBATIS_PAGE_OFFSETASPAGENUM = "mybatis.page.offsetAsPageNum";

        /**
         * DAO page 该参数默认为false 设置为true时，使用RowBounds分页会进行count查询 默认:true
         */
        public static final String MYBATIS_PAGE_ROWBOUNDSWITHCOUNT = "mybatis.page.rowBoundsWithCount";

        /**
         * 设置为true时，如果pageSize=0或者RowBounds.limit = 0就会查询出全部的结果 (相当于没有执行分页查询，但是返回结果仍然是Page类型) 默认:true
         */
        public static final String MYBATIS_PAGE_PAGESIZEZERO = "mybatis.page.pageSizeZero";

        /**
         * 3.3.0版本可用 - 分页参数合理化，默认false禁用 ~ 实测3.2.8也可用 启用合理化时，如果pageNum<1会查询第一页，如果pageNum>pages会查询最后一页
         * 禁用合理化时，如果pageNum<1或pageNum>pages会返回空数据 启用会引起一些通过分页来判断的逻辑异常，如Spring-Batch的分页reader 默认:false
         */
        public static final String MYBATIS_PAGE_REASONABLE = "mybatis.page.reasonable";

        /**
         * 3.5.0版本可用 - 为了支持startPage(Object params)方法 增加了一个`params`参数来配置参数映射，用于从Map或ServletRequest中取值
         * 可以配置pageNum,pageSize,count,pageSizeZero,reasonable,orderBy,不配置映射的用默认值 不理解该含义的前提下，不要随便复制该配置
         * 默认:pageNum=pageHelperStart;pageSize=pageHelperRows;
         */
        public static final String MYBATIS_PAGE_PARAMS = "mybatis.page.params";

        /**
         * 支持通过Mapper接口参数来传递分页参数 默认:false
         */
        public static final String MYBATIS_PAGE_SUPPORTMETHODSARGUMENTS = "mybatis.page.supportMethodsArguments";

        /**
         * always总是返回PageInfo类型,check检查返回类型是否为PageInfo,none返回Page 默认:none
         */
        public static final String MYBATIS_PAGE_RETURNPAGEINFO = "mybatis.page.returnPageInfo";
    }

    /**
     * websocket相关配置
     */
    public static class WebSocket {

        private WebSocket() {
        }

        /**
         * websocket跨域允许域名url
         */
        public static final String WEBSOCKET_ALLOW_ORIGINS = "websocket.allow.origins";

    }

    /**
     * websocket相关配置
     */
    public static class Zookeeper {

        private Zookeeper() {
        }

        /**
         * Zookeeper url
         */
        public static final String ZK_URL = "zk.url";
        /**
         * Zookeeper url
         */
        public static final String ZK_CONNECTION_TIMEOUT = "zk.connection.timeout";
        /**
         * Zookeeper url
         */
        public static final String ZK_SESSION_TIMEOUT = "zk.session.timeout";

    }

    /**
     * 用户相关配置
     */
    public static class User {

        private User() {
        }

        /**
         * pass分配的appkey
         */
        public static final String AUTH_HEADER_HJAPPKEY = "auth.header.hjappkey";
        /**
         * pass分配的签名
         */
        public static final String AUTH_HEADER_HJAPPSIGN = "auth.header.hjappsign";

        /**
         * 签名算法
         */
        public static final String AUTH_HEADER_HJSIGNMETHOD = "auth.header.hjsignmethod";
        /**
         * 设备id
         */
        public static final String AUTH_HEADER_HJDEVICEID = "auth.header.hjdeviceId";
        /**
         * http请求pass头key
         */
        public static final String HJ_APPKEY = "hj_appkey";
        /**
         * http请求pass头key
         */
        public static final String HJ_APPSIGN = "hj_appsign";
        /**
         * http请求pass头key
         */
        public static final String HJ_SIGNMETHOD = "hj_signmethod";
        /**
         * http请求pass头key
         */
        public static final String HJ_DEVICEID = "hj_deviceId";

        public static final String HJ_CLUBAUTH = "hj_clubauth";
        /**
         * http请求pass头key
         */
        public static final String X_USER_DOMAIN = "X-USER-DOMAIN";
        /**
         * 请求沪江各域名的ua
         */
        public static final String AUTH_HEADER_HJUSERAGENT = "auth.header.hjuseragent";
        /**
         * 请求pass的用户域
         */
        public static final String AUTH_HEADER_USER_DOMAIN = "auth.header.userdomain";
        /**
         * http请求pass url
         */
        public static final String AUTH_URL = "auth.url";
        /**
         * 是否启用缓存
         */
        public static final String AUTH_CACHE = "auth.cache";
        /**
         * 缓存过期时间
         */
        public static final String AUTH_CACHE_EXPIRE = "auth.cache.expire";

        /**
         * 用户id
         */
        public static final String USER_ID = "user_id";

        /**
         * 请求pass cookie
         */
        public static final String AUTH_COOKIE_KEY = "auth.cookie.key";

        /**
         * 请求pass token
         */
        public static final String ACCESS_TOKEN = "access_token";

        /**
         * 请求pass club auth
         */
        public static final String CLUB_AUTH_COOKIE = "club_auth_cookie";

        /**
         * http头的headers
         */
        public static final String FILTER_REQUEST_HEADERS = "filter.request.headers";

        /**
         * pass的web域名
         */
        public static final String AUTH_URL_HUJIANG = "auth.url.hujiang";

        public static final String IMPORT_CLUBAUTH="auth.import.clubauth";
    }

    public static class Rest {

        private Rest() {
        }

        /**
         * 异步处理 线程池 命名
         */
        public static final String ASYNC_THREAD_NAME = "async.thread.name";

        /**
         * 异步处理 线程池 最大线程数 默认:20
         */
        public static final String ASYNC_THREAD_MAX = "async.thread.max";

        /**
         * 异步处理 线程池 缓存队列深度 默认:0
         */
        public static final String ASYNC_THREAD_QUEUES = "async.thread.queues";

        /**
         * 异步处理 线程池 空闲线程数 默认:4
         */
        public static final String ASYNC_THREAD_ALIVE = "async.thread.alive";

        /**
         * RestClientWapper 使用的json配置文件名称
         */
        public static final String REST_CONFIG_FILENAME = "rest.config.filename";

        /**
         * RestClientWapper 使用的json配置文件名称
         */
        public static final String REST_REQUESTILTER_IINCLUDE_URL = "rest.requestfilter.include.url";

        /**
         * RestClientWapper 使用的json配置文件名称
         */
        public static final String REST_REQUESTILTER_EXCLUSIONS_URL = "rest.requestfilter.exclusions.url";

        /**
         * druid 管理控制台 登录用户名
         */
        public static final String DRUID_MANAGER_USERNAME = "druid.manager.username";

        /**
         * druid 管理控制台 登录用户名
         */
        public static final String DRUID_MANAGER_PASSWORD = "druid.manager.password";

        /**
         * druid 管理控制台 是否能够重置数据 默认:true
         */
        public static final String DRUID_MANAGER_RESETENABLE = "druid.manager.resetEnable";

        /**
         * Rest 返回前端序列化为fastjson 此参数设置SerializerFeature属性 例如:WriteMapNullValue
         */
        public static final String MVC_SERIALIZERFEATURE = "mvc.serializerFeature";

        /**
         * undertow 允许的httpBody最大值 默认:32M
         */
        public static final String SYS_UNDERTOW_MAX_ENTITY_SIZE = "sys.undertow.max_entity_size";

        /**
         * undertow 允许的multiport 单个httpBody最大值 默认:32M
         */
        public static final String SYS_UNDERTOW_MULTIPART_MAX_ENTITY_SIZE = "sys.undertow.multipart_max_entity_size";

        /**
         * http接收请求时，与发送时间差值的阈值 默认:1000ms
         */
        public static final String HTTP_RECEIVE_TIMEOUT_THRESHOLD = "http.receive.timeout.threshold";

        /**
         * 远程IP地址在header中为x-forwarded-for中 多IP分割符  默认:,
         */
        public static final String SYSTEM_REMOTEIP_SPLIT_FLAG = "system.remoteip.split.flag";

        /**
         * druid filter 加载顺序 默认为 101 值越小越先执行
         */
        public static final String FILTER_ORDER_DRUID = "filter.order.druid";

        /**
         * request filter 加载顺序 默认为 100 值越小越先执行
         */
        public static final String FILTER_ORDER_REQUEST = "filter.order.request";

        /**
         * http请求（客户端、服务端）接收请求出入参日志，排除前缀url
         */
        public static final String HTTP_REQUEST_LOG_EXCLUDEDPREFIXES = "http.request_log.excludedPrefixes";
        /**
         * http请求（客户端、服务端）接收请求出入参日志，排除后缀url
         */
        public static final String HTTP_REQUEST_LOG_EXCLUDEDSUFFIXES = "http.request_log.excludedSuffixes";
        /**
         * http服务端接收请求出入参日志，是否启用
         */
        public static final String HTTP_SERVER_LOG_ENABLE = "http.server_log.enable";
        /**
         * http客户端接收请求出入参日志，是否启用
         */
        public static final String HTTP_CLIENT_LOG_ENABLE = "http.client_log.enable";
        /**
         * http请求异常（客户端、服务端）是否输出body内容
         */
        public static final String HTTP_BODY_SHOWLOG_ENABLE = "http.body.showlog.enable";

    }

    public static class Jsonp {

        private Jsonp() {
        }

        /**
         * 发送jsonp请求时 参数key标记 默认:callback
         */
        public static final String JSONP_REQ_KEY = "jsonp.req.key";
    }

    public static class SelfCheck {

        private SelfCheck() {
        }
//        public static final String REDIS_PROPERTY_NAME = "hj.selfcheck.redis";
//
//        public static final String DATASOURCE_PROPERTY_NAME = "hj.selfcheck.datasource";
//
//        public static final String RABBITMQ_PROPERTY_NAME = "hj.selfcheck.rabbitmq";

        public static final String MAIL_PROPERTY_KEY = "hj.selfcheck.key";

        public static final String MAIL_PROPERTY_NAME = "hj.selfcheck.mail";

        public static final String PHONE_PROPERTY_NAME = "hj.selfcheck.phone";
    }

    /**
     * common某块相关配置
     */
    public static class Common {

        private Common() {
        }

        /**
         * 额外添加rest请求的日期模板 默认:空 eg: rest.request.dateformat={"[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}.[0-9]{0,7}":"yyyy-MM-dd'T'HH:mm:ss.SSSSSSS"}
         */
        public static final String REST_REQUEST_DATAFORMAT = "rest.request.dateformat";

        /**
         * 用于并发发送HTTP请求工具类ConCurrentHttpClientUtil ConCurrentHttpClientUtil内部是通过线程池来实现,该参数用设置内部线程池的最大线程数 默认: 1000
         */
        public static final String SYS_CONCURRENT_HTTP_CLIENT_THREAD_MAX = "sys.concurrenthttpClient.thread.max";

        /**
         * 用于并发发送HTTP请求工具类ConCurrentHttpClientUtil ConCurrentHttpClientUtil内部是通过线程池来实现,该参数用设置内部线程池的有效线程数 默认: 20
         */
        public static final String SYS_CONCURRENT_HTTP_CLIENT_THREAD_IDLE = "sys.concurrenthttpClient.thread.idle";

        /**
         * 用于监控HttpClientUtil来发送http请求时长超过这个配置时会记录下并监控 默认:1000
         */
        public static final String HTTP_RECEIVE_TIMEOUT_THRESHOLD = "http.receive.timeout.threshold";

        /**
         * HttpClientUtil socket 超时时长 默认:10000
         */
        public static final String HTTPCLIENT_SOCKET_TIMEOUT = "httpclient.socketTimeout";
        /**
         * HttpClientUtil Connection timeout retry 重试开关 默认:false
         */
        public static final String HTTPCLIENT_CONNTIMEOUTRETRY_ENABLE = "httpclient.connTimeoutRetry.enable";
        /**
         * HttpClientUtil Connection reset retry 重试开关 默认:true
         */
        public static final String HTTPCLIENT_CONNRESETRETRY_ENABLE = "httpclient.connResetRetry.enable";

        /**
         * HttpClientUtil connect 超时时长 默认:10000
         */
        public static final String HTTPCLIENT_CONNECT_TIMEOUT = "httpclient.connectTimeout";

        /**
         * HttpClientUtil connect request 超时时长 默认:10000
         */
        public static final String HTTPCLIENT_CONNECTION_REQUEST_TIMEOUT = "httpclient.connectionRequestTimeout";

        /**
         * JsonUtil中设置序列化时设置日期格式 默认为 yyyy-MM-dd'T'HH:mm:ssZ
         */
        public static final String JSON_DATE_FORMAT = "json.date.format";

    }

    public static class Cache {

        private Cache() {
        }

        public static final int REDIS_ERROR_CODE = 6379;

        public static final String IS_OK = "OK";

        public static final String REDIS_MAXRETRY = "redis.maxRetry";

        public static final String REDIS_PREFIX = "redis.";

    }

    public static class MybatisHotLoad {

        private MybatisHotLoad() {
        }

        public static final String MYBATIS_HOTLOAD_DELAYSECONDS = "mybatis.hotload.delaySeconds";

        public static final String MYBATIS_HOTLOAD_SLEEPSECONDS = "mybatis.hotload.sleepSeconds";

        public static final String MYBATIS_MAPPER_LOCATIONS = "mybatis.mapper-locations";

    }

    /**
     * Codis插件相关配置
     */
    public static class Codis {

        private Codis() {
        }

        /**
         * codis操作失败后重试次数 默认:2
         */
        public static final String CODIS_MAX_RETRY = "codis.maxRetry";

        /**
         * codis链接zookeeper地址 必填
         */
        public static final String CODIS_ZOOKEEPER_ADDR = "codis.zookeeper.addr";

        /**
         * codis密码 必填
         */
        public static final String CODIS_PRODUCT_AUTH = "codis.product.auth";

        /**
         * codis链接池最大空闲线程数 必填
         */
        public static final String CODIS_REDIS_POOL_MAX_IDLE = "codis.redis.pool.max.idle";

        /**
         * codis链接池最大数 必填
         */
        public static final String CODIS_REDIS_POOL_MAX_TOTAL = "codis.redis.pool.max.total";

        /**
         * codis链接池最大等待时间 必填
         */
        public static final String CODIS_REDIS_POOL_MAX_WAIT = "codis.redis.pool.max.wait";

        /**
         * codis和zookeepersession超时配置 默认: 600000
         */
        public static final String CODIS_ZOOKEEPER_TIMEOUT = "codis.zookeeper.timeout";
        
        public static final String CODIS_CONNECT_TIMEOUT = "codis.connect.timeout";
        
        public static final String CODIS_ZOOKEEPER_RETRY_TIMES = "codis.zookeeper.retry.times";

        /**
         * codis名字
         */
        public static final String CODIS_PRODUCT_NAME = "codis.product.name";
    }

    public static class RPC {

        private RPC() {
        }

        /**
         * rpc应用名
         */
        public static final String RPC_DUBBO_NAME = "rpc.dubbo.name";

        /**
         * rpc应用日志输出方式 默认:slf4j
         */
        public static final String RPC_DUBBO_LOGGER = "rpc.dubbo.logger";

        /**
         * consumer调用provider时超时时间
         */
        public static final String RPC_DUBBO_PROVIDER_TIMEOUT = "rpc.dubbo.provider.timeout";

        /**
         * consumer调用provider时超时后重试次数
         */
        public static final String RPC_DUBBO_PROVIDER_RETRIES = "rpc.dubbo.provider.retries";

        /**
         * rpc注册中心协议 默认:zookeeper
         */
        public static final String RPC_DUBBO_REGISTRY_PROTOCOL = "rpc.dubbo.registry.protocol";

        /**
         * rpc注册中心地址
         */
        public static final String RPC_DUBBO_REGISTRY_ADDRESS = "rpc.dubbo.registry.address";

        /**
         * rpc注册中心用户名
         */
        public static final String RPC_DUBBO_REGISTRY_USERNAME = "rpc.dubbo.registry.username";

        /**
         * rpc注册中心密码
         */
        public static final String RPC_DUBBO_REGISTRY_PASSWORD = "rpc.dubbo.registry.password";

        /**
         * rpc注册中心列表存储文件
         */
        public static final String RPC_DUBBO_REGISTRY_FILE = "rpc.dubbo.registry.file";

        /**
         * 服务协议 默认:dubbo
         */
        public static final String RPC_DUBBO_PROTOCOL_NAME = "rpc.dubbo.protocol.name";

        /**
         * 线程池类型 默认: fixed
         */
        public static final String RPC_DUBBO_PROTOCOL_THREADPOOL = "rpc.dubbo.protocol.threadpool";

        /**
         * 信息线程模型派发方式 默认: all
         */
        public static final String RPC_DUBBO_PROTOCOL_DISPATCHER = "rpc.dubbo.protocol.dispatcher";

        /**
         * 服务端口 必填
         */
        public static final String RPC_DUBBO_PROTOCOL_PORT = "rpc.dubbo.protocol.port";

        /**
         * 线程池大小(固定大小) 默认: 200
         */
        public static final String RPC_DUBBO_PROTOCOL_THREADS = "rpc.dubbo.protocol.threads";

    }

    public static class MQ {

        private MQ() {
        }

        public static final String DEFAULT_NAMESPACE = "default";
        public static final String CFG_HOST = "mq.host";
        public static final String CFG_PORT = "mq.port";
        public static final String CFG_USER = "mq.user";
        public static final String CFG_PWD = "mq.pwd";
        public static final String CFG_VHOST = "mq.vhost";
        public static final String CFG_TIMEOUT = "mq.timeout";

    }

    public static class RateLimit {

        private RateLimit() {
        }

        /**
         * 限流所选用redis的名字 default:simple
         */
        public static final String RATELIMIT_REDIS_NAME = "rateLimit.redis.name";
        /**
         * 限流的时间间隔
         */
        public static final String RATELIMIT_TIME_INTERVAL = "rateLimit.time.interval";
        /**
         * 限流次数
         */
        public static final String RATELIMIT_TIMES = "rateLimit.times";

    }

    public static class SoaGovernance {

        private SoaGovernance() {
        }

        /**
         * 应用Id
         */
        public static final String SYS_SOA_APPID = "sys.soa.appid";

        /**
         * 应用版本号 default:v1
         */
        public static final String SYS_SOA_APPVERSION = "sys.soa.appversion";

        /**
         * SOA 协议 default:http
         */
        public static final String SYS_SOA_PROTOCOL = "sys.soa.protocol";

        /**
         * 应用所在集群 default:default
         */
        public static final String SYS_SOA_CLUSTER = "sys.soa.cluster";

        /**
         * 应用所属域名 default:default
         */
        public static final String SYS_SOA_DOMAIN = "sys.soa.domain";

        /**
         * SOA Gateway服务地址 default: qa:http://qa.soagateway.intra.yeshj.com prod:http://soagateway.intra.yeshj.com
         */
        public static final String SYS_SOA_GATEWAY_URL = "sys.soa.gateway.url";

        /**
         * 应用中心地址 default: qa:http://qa.appcenter.yeshj.com prod:http://appcenter.yeshj.com
         */
        public static final String SYS_SOA_APPCENTER_URL = "sys.soa.appcenter.url";

        /**
         * SOA Manager地址 default: qa:http://qa.soamanager.yeshj.com prod:http://soamanager.yeshj.com
         */
        public static final String SYS_SOA_SOAMANAGER_URL = "sys.soa.soamanager.url";

        /**
         * SOA 注册中心地址
         */
        public static final String SYS_SOA_ZK_URL = "sys.soa.zk.url";

        /**
         * SOA 注册中心连接超时 default:3000ms
         */
        public static final String SYS_SOA_ZK_CONNECTIONTIMEOUT = "sys.soa.zk.connectionTimeout";

        /**
         * SOA 注册中心会话超时 default:2000ms
         */
        public static final String SYS_SOA_ZK_SESSIONTIMEOUT = "sys.soa.zk.sessionTimeout";

        /**
         * SOA Interface 本地缓存过期，最后访问有效时间，单位：分钟 default:30分钟
         */
        public static final String SYS_SOA_CLASS_CACHE_EXPIRES = "sys.soa.class.cache.expries";

        /**
         * SOA Interface 中获取到的方法 本地缓存过期，最后访问有效时间，单位：分钟 default:30分钟
         */
        public static final String SYS_SOA_METHOD_CACHE_EXPIRES = "sys.soa.method.cache.expries";

        /**
         * SOA swagger 服务接口，设置指定的package路径
         */
        public static final String SYS_SOA_SWAGGER_PACKAGE = "sys.soa.swagger.package";

        /**
         * SOA async callback timeout default:1800000ms
         */
        public static final String SYS_SOA_ASYNC_CALLBACK_TIMEOUT = "sys.soa.async.callback.timeout";

        /**
         * SOA async threadpool max threads default:200
         */
        public static final String SYS_SOA_ASYNC_THREADPOOL_MAXTHREADS = "sys.soa.async.threadpool.maxthreads";

        /**
         * SOA async threadpool queues default:0
         */
        public static final String SYS_SOA_ASYNC_THREADPOOL_QUEUES = "sys.soa.async.threadpool.queues";

        /**
         * SOA async threadpool timeout default:6000000ms
         */
        public static final String SYS_SOA_ASYNC_THREADPOOL_TIMEOUT = "sys.soa.async.threadpool.timeout";


        /**
         * SOA CircuitBreaker ErrorThreshold Percentage default:50 错误请求超过50% 触发熔断
         */
        public static final String SYS_SOA_CIRCUITBREAKER_ERRORTHRESHOLDPERCENTAGE = "sys.soa.circuitbreaker.errorThresholdPercentage";

        /**
         * SOA CircuitBreaker Sleep Window InMilliseconds default:5000 熔断休眠5秒后，重新开始接受请求
         */
        public static final String SYS_SOA_CIRCUITBREAKER_SLEEPWINDOWINMILLISECONDS = "sys.soa.circuitbreaker.sleepWindowInMilliseconds";

        /**
         * SOA CircuitBreaker queueSize rejection Threshold default:200 为隔离请求，设置的线程池中缓冲队列大小,超过此值时拒绝请求
         */
        public static final String SYS_SOA_CIRCUITBREAKER_QUEUESIZEREJECTIONTHRESHOLD = "sys.soa.circuitbreaker.queueSizeRejectionThreshold";

        /**
         * SOA CircuitBreaker 线程池 coresize 大小,default:10 请求不同的SOA服务，都是基于线程池隔离，此值为单个线程池的coresize
         */
        public static final String SYS_SOA_CIRCUITBREAKER_THREADPOOL_CORESIZE = "sys.soa.circuitbreaker.threadpool.coreSize";

        /**
         * SOA CircuitBreaker 线程池 对于线程回收时，最大空闲时间 大小,default:10 单位为分钟. 请求不同的SOA服务，都是基于线程池隔离，此值为单个线程池的 timeout
         */
        public static final String SYS_SOA_CIRCUITBREAKER_THREADPOOL_KEEPALIVETIMEMINUTES = "sys.soa.circuitbreaker.threadpool.keepAliveTimeMinutes";

        /**
         * SOA CircuitBreaker 线程池最大线程数 大小,default:50  请求不同的SOA服务，都是基于线程池隔离，此值为单个线程池的 maxsize
         */
        public static final String SYS_SOA_CIRCUITBREAKER_THREADPOOL_MAXIMUMSIZE = "sys.soa.circuitbreaker.threadpool.maximumSize";

        /**
         * SOA CircuitBreaker 线程池中队列深度 大小,default:200  用于请求并发大时，线程资源不够，起到缓冲作用
         */
        public static final String SYS_SOA_CIRCUITBREAKER_THREADPOOL_QUEUESIZE = "sys.soa.circuitbreaker.threadpool.queuesize";

        /**
         * SOA soa功能的全局开关, default:true
         */
        public static final String SYS_SOA_ENABLE = "sys.soa.enable";

        /**
         * SOA soa服务注册开关, default:true
         */
        public static final String SYS_SOA_REGISTER_ENABLE = "sys.soa.register.enable";

    }
}
