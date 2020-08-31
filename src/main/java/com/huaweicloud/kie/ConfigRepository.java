package com.huaweicloud.kie;

import com.huaweicloud.kie.event.ConfigChangeEvent;
import com.huaweicloud.kie.event.KieConfigEventBus;
import com.huaweicloud.kie.model.KVResponse;
import com.huaweicloud.kie.model.KVStatus;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @Author GuoYl123
 * @Date 2020/8/29
 **/
public class ConfigRepository {

  private KieClient kieClient = new KieClient();

  private KieConfigEventBus kieConfigEventBus = KieConfigEventBus.getInstance();

  private Map<String, String> queryLabel;

  private KVResponse remoteConfig;

  private ScheduledExecutorService EXECUTOR = Executors.newScheduledThreadPool(1, (r) -> {
    Thread thread = new Thread(r);
    thread.setName("com.huaweicloud.kie.longPolling");
    thread.setDaemon(true);
    return thread;
  });

  public ConfigRepository(Map<String, String> queryLabel) {
    this.queryLabel = queryLabel;
    EXECUTOR.execute(this::watch);
  }


  private void watch() {
    KVResponse remoteConfig = getConfig();
    //如果数据没变更
    if (remoteConfig == null) {
      EXECUTOR.execute(this::watch);
      return;
    }
    this.remoteConfig = remoteConfig;
    kieConfigEventBus.fire(new ConfigChangeEvent());
    //继续监听
    EXECUTOR.execute(this::watch);
  }

  public KVResponse getSourceData() {
    return remoteConfig;
  }

  /**
   * todo: 落盘实现
   * @return
   */
  private KVResponse getConfig() {
    return kieClient.queryKV(null, queryLabel, null, null,
        null, "", KVStatus.enabled.name(), "default", "30s", true);
  }
}
