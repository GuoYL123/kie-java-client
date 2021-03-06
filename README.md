# kie-java-client

The java client for [kie](https://github.com/apache/servicecomb-kie)

### The way to get the ApiDoc:
After you launch kie server, you can browse API doc in http://127.0.0.1:30110/apidocs.json, copy this doc to http://editor.swagger.io/

### 客户端功能特性

1. 提供kie原生的 open api客户端。
2. 支持获取单文件级别的配置获取。
3. 支持多层次、层级的配置获取以及优先级聚合。
3. 对外开放扩展，用户自定义label以及其含义。
5. 通过长轮训(long polling)+全量拉取机制获取配置。
6. 支持客户端落盘机制，以适应在极端环境下后端节点故障/网络故障、业务服务重启的发生。

### 功能路标

1. 支持增量配置的拉取，减少网络消耗以及服务端压力。
2. 支持客户端限流的实现，避免网络发生错误时频繁请求。
3. 支持解析ini和json格式的配置内容。
4. 在进行marshal时要区分不同value type的优先级，目前没有区分，优先级随机，可能发生覆盖。
5. 同一label下的config都会接受到event，所以会出现接收到event但是配置没有发生变化的情况，业务要自己评估影响。

### [使用指南](./doc/manual.md)

### [客户端设计](./doc/design.md)