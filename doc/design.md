### 设计理念

#### 拉取配置
客户端每次拉取到到配置，会进行缓存和落盘处理，落盘为异步进行。

#### 读取配置
读取配置时优先级: kie server >  磁盘 > 内存

优先从服务端拉配置，采用长轮训的方式可以响应服务端配置的秒级变更。

如果服务端不可访问(db挂了/kie挂了/网络挂了)，首先进行fail over，访问其他客户端，所有客户端失败遍历2次后，
开始尝试从磁盘读取，每10s会读取一次本地文件维护缓存数据；同时保留进行远程服务端进行请求，为kie server恢复做准备。
如果kie server恢复，停止从磁盘中读取。

考虑到业务方使用kie-client的业务服务实例可能是在k8s中部署的无状态服务，业务要自行考虑如何处理落盘文件在pod发生漂移时的一些问题
，如果处理不当极有可能发生错误，所以也提供了关闭落盘机制的选项(默认打开)。

#### [客户端使用](./manual.md)