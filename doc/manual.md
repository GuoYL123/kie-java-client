

### 设计思想

使用kie的客户端首先需要理解kie的模型和设计思想，在kie中，一个基本的数据结构如下：
```json
{
    "id": "ba566f77-b311-4f0d-b00c-7b0fca36e571",
    "key": "KIEFILE.xxx",
    "value": "xxxx",
    "value_type": "text",
    "create_revision": 52,
    "update_revision": 53,
    "status": "enabled",
    "create_time": 1598926881,
    "update_time": 1598928701,
    "labels": {
        "app": "default",
        "environment": "",
        "service": "provider",
        "version": "0.0.1"
    }
}
```
一对kie的kv对的主要信息包括了key、value的名字，value_type代表kv的类型，可取值包括: yaml, ini, text, json, properties，
label是一系列标签用来标记这个kv对，可以根据label来查找指定的kv对集合。


### Java 客户端的使用

```java
@RestController
public class ConfigController {

  private ConfigService configService;

  private int dataChange = 0;

  {
    TreeMap<String, String> tt = new TreeMap<String, String>();
    tt.put("app", "default");
    tt.put("service", "provider");
    tt.put("environment", "");
    tt.put("version", "0.0.1");
    IpPort ipPort = new IpPort("127.0.0.1", 30110);
    configService = new ConfigService();
    configService.init(tt, Collections.singletonList(ipPort), null);
  }

  @RequestMapping("/config")
  public String config() {
    Config config = configService.getAggregationConfig();
    config.addListener(event -> dataChange++);
    return config.toString() + ",changeTime: " + dataChange;
  }


  @RequestMapping("/file")
  public String file() {
    return configService.getFileConfig("application").toString();
  }
}
```

1. 新建一个ConfigService对象。
    ```java
    ConfigService configService = new ConfigService(treeMap, Collections.singletonList(ipPort), null);
    ```
   参数说明：  
   ConfigService构造方法接受一个treeMap，客户端会根据treeMap中的 **第一个元素** 从kie中拉取label中包含该label的k-v对，并缓存到本地。
   缓存到本地的数据会按照treeMap中的label匹配情况进行优先级排序和聚合，这里理解起来可能比较复杂，可以结合[例子](./example.md)理解。
   ConfigService构造方法的第二个第三个参数分别代表kie的ip-port组合、ssl配置（没有传null）。
   
    ```java
    ConfigService configService = new ConfigService(treeMap, Collections.singletonList(ipPort), null, true, "./");
    ```
   参数说明：  
   前三个参数同上，后面两个参数本别代表是否开启客户端落盘，以及客户端落盘路径。

3. 配置获取：
    1. 使用getAggregationConfig获取配置
        ```java
        public Config configService.getAggregationConfig()
        ```
       使用该方法获取配置会把kie中的key作为前缀，根据value_type中的内容进行解析，对内容根据设定的labels进行优先级聚合。
    2. 使用getFileConfig获取配置
        ```java
        public Config configService.getFileConfig(String key)
        ```
       使用该方法获取配置会把key加上前缀'KIEFILE.'，查找指定label下面的配置，直接对value的内容按照value_type进行解析，对内容根据设定的labels进行优先级聚合。
    3. 使用getRawConfig获取配置
        ```java
        public Config configService.getRawConfig(String key, Map<String, String> labels)
        ```
       使用该方法获取指定key和包含指定labels的配置，如果有多个，则取第一个。直接对value的内容按照value type进行解析。
4. 注册监听：
  获取到的Config对象可以利用config.addListener(()->dosomething())添加监听方法，在配置变更时进行业务处理。
  
       
       
### 与Spring Cloud结合

使用[spring cloud huawei](https://github.com/huaweicloud/spring-cloud-huawei), 会把配置注入spring数据源，可以支持
spring原生的各种注解如@Value、@ConfigurationProperties等，支持使用netflix archaius框架获取配置。

### ServiceComb JavaChassis框架的使用

移步[文档](https://docs.servicecomb.io/java-chassis/zh_CN/config/general-config/)。