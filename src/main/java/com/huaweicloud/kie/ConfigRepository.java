package com.huaweicloud.kie;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huaweicloud.kie.event.ConfigChangeEvent;
import com.huaweicloud.kie.event.KieConfigEventBus;
import com.huaweicloud.kie.http.IpPort;
import com.huaweicloud.kie.http.TLSConfig;
import com.huaweicloud.kie.model.KVResponse;
import com.huaweicloud.kie.model.KVStatus;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author GuoYl123
 * @Date 2020/8/29
 **/
public class ConfigRepository {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConfigRepository.class);

  private KieClient kieClient;

  private KieConfigEventBus kieConfigEventBus = KieConfigEventBus.getInstance();

  private Map<String, String> queryLabel;

  private KVResponse remoteConfig;

  private boolean enableFile = true;

  private String fileDir = "./";

  private Future<Integer> lastWrite;

  private int currentTime = 0;

  private int errTime;

  private AsynchronousFileChannel fileChannel;

  private static ObjectMapper mapper = new ObjectMapper();

  private boolean fileExist;

  private ScheduledExecutorService EXECUTOR = Executors.newScheduledThreadPool(1, (r) -> {
    Thread thread = new Thread(r);
    thread.setName("com.huaweicloud.kie.longPolling");
    thread.setDaemon(true);
    return thread;
  });

  public ConfigRepository(Map<String, String> queryLabel, List<IpPort> list, TLSConfig tlsConfig,
      boolean enableFile, String fileDir) {
    this.enableFile = enableFile;
    this.fileDir = fileDir;
    init(queryLabel, list, tlsConfig);
  }

  public ConfigRepository(Map<String, String> queryLabel, List<IpPort> list, TLSConfig tlsConfig) {
    init(queryLabel, list, tlsConfig);
  }

  private void init(Map<String, String> queryLabel, List<IpPort> list, TLSConfig tlsConfig) {
    kieClient = new KieClient(list, tlsConfig);
    this.errTime = list.size() * 2;
    this.queryLabel = queryLabel;

    File file = new File(fileDir);
    if (!file.exists()) {
      fileExist = file.mkdirs();
    } else {
      fileExist = true;
    }
    Path path = Paths.get(fileDir);
    try {
      fileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.WRITE);
    } catch (IOException e) {
      LOGGER.error("open fileChannel failed.");
    }
    watch();
  }


  private void watch() {
    KVResponse remoteConfig = getConfig();
    //如果数据没变更
    if (remoteConfig == null) {
      EXECUTOR.execute(this::watch);
      return;
    }
    this.remoteConfig = remoteConfig;
    //同一label下的config都会接受到event
    //所以会出现接收到event但是配置没有发生变化的情况，业务要自己评估影响。
    kieConfigEventBus.fire(new ConfigChangeEvent());
    writeFile(remoteConfig);
    //继续监听
    EXECUTOR.execute(this::watch);
  }

  public KVResponse getSourceData() {
    return remoteConfig;
  }

  /**
   * 读取优先级
   *    kie server > 内存 > 磁盘
   *
   *  1. 失败(db挂了/kie挂了/网络挂了)首先进行faile over
   *     访问其他客户端，所有客户端失败遍历2次后，开始尝试从磁盘读取
   *     watch开始监听磁盘文件内容变化作为动态配置
   *     同时保留进行远程服务端进行请求，为网络恢复做准备
   * 2. 如果网络恢复，停止从磁盘中读取
   *
   * @return
   */
  private KVResponse getConfig() {
    try {
      KVResponse kvResponse = kieClient.queryKV(null, queryLabel, null, null,
          null, "", KVStatus.enabled.name(), "default", "30s", true);
      currentTime = 0;
      return kvResponse;
    } catch (IOException e) {
      //todo: 限流取代
      try {
        Thread.sleep(3000);
      } catch (InterruptedException ex) {
      }
      if (currentTime >= errTime) {
        return readFile();
      }
      currentTime++;
      return null;
    }
  }

  private KVResponse readFile() {
    if (!fileExist || !enableFile) {
      return null;
    }
    try {
      Thread.sleep(10000);
    } catch (InterruptedException ex) {
    }
    try {
      return mapper.readValue(new File(fileDir), KVResponse.class);
    } catch (IOException e) {
      LOGGER.error("read kie-conf file failed.");
    }
    return null;
  }

  /**
   * 优先异步写入，失败则同步写入
   *
   * @param kvResponse
   */
  private void writeFile(KVResponse kvResponse) {
    if (!fileExist || !enableFile) {
      return;
    }
    if (lastWrite != null && !lastWrite.isDone()) {
      LOGGER.warn("last write file not done.");
    }
    try {
      ByteBuffer buffer = transform(mapper.writeValueAsBytes(kvResponse));
      if (fileChannel == null) {
        mapper.writeValue(new File(fileDir), kvResponse);
      } else {
        lastWrite = fileChannel.write(buffer, 0);
      }
    } catch (IOException e) {
      LOGGER.error("read kie-conf file failed.");
    }
  }

  private ByteBuffer transform(byte[] value) {
    ByteBuffer byteBuffer = ByteBuffer.allocate(value.length);
    byteBuffer.clear();
    byteBuffer.get(value, 0, value.length);
    return byteBuffer;
  }

}
