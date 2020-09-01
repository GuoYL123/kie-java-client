package com.huaweicloud.kie;

import com.huaweicloud.kie.event.ConfigChangeEvent;
import com.huaweicloud.kie.event.KieConfigEventBus;
import com.huaweicloud.kie.http.IpPort;
import com.huaweicloud.kie.http.TLSConfig;
import com.huaweicloud.kie.model.KVResponse;
import com.huaweicloud.kie.model.KVStatus;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @Author GuoYl123
 * @Date 2020/8/29
 **/
public class ConfigRepository {

  private KieClient kieClient;

  private KieConfigEventBus kieConfigEventBus = KieConfigEventBus.getInstance();

  private Map<String, String> queryLabel;

  private KVResponse remoteConfig;

  private ScheduledExecutorService EXECUTOR = Executors.newScheduledThreadPool(1, (r) -> {
    Thread thread = new Thread(r);
    thread.setName("com.huaweicloud.kie.longPolling");
    thread.setDaemon(true);
    return thread;
  });

  public ConfigRepository(Map<String, String> queryLabel, List<IpPort> list, TLSConfig tlsConfig) {
    kieClient = new KieClient(list, tlsConfig);
    this.queryLabel = queryLabel;
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
    //继续监听
    EXECUTOR.execute(this::watch);
  }

  public KVResponse getSourceData() {
    return remoteConfig;
  }

  /**
   * todo: 限流实现
   * todo: 落盘实现
   * @return
   */
  private KVResponse getConfig() {
    try {
      return kieClient.queryKV(null, queryLabel, null, null,
          null, "", KVStatus.enabled.name(), "default", "30s", true);
    } catch (IOException e) {
      try {
        Thread.sleep(3000);
      } catch (InterruptedException ex) {
        ex.printStackTrace();
      }
      return null;
    }
  }
}
