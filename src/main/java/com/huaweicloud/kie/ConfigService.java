package com.huaweicloud.kie;

import com.huaweicloud.kie.model.Config;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * @Author GuoYl123
 * @Date 2020/8/29
 **/
public class ConfigService {

  private TreeMap<String, String> priorityLabels;

  //todo: 单例bean管理
  private ConfigRepository configRepository;

  private void init(TreeMap<String, String> priorityLabels) {
    this.priorityLabels = priorityLabels;
    Map<String, String> queryLabel = new HashMap<>();
    if (priorityLabels != null && priorityLabels.size() > 0) {
      Entry<String, String> entry = priorityLabels.firstEntry();
      queryLabel.put(entry.getKey(), entry.getValue());
    }
    configRepository = new ConfigRepository(queryLabel);
  }

  private static final String DEFAULT_KEY = "";

  private Map<String, AbstractConfigFactory> factoryMap = new HashMap<>();

  /**
   * FileConfigFactory
   * 根据 "." + key 的形式读取指定KV文件，并进行解析
   * 文件根据app、service、env进行分区？
   * 不做分层拉取,直接拉最底层  只做解析不做聚合
   *
   * @param key
   * @return
   */
  public Config getConfig(String key) {
    factoryMap.putIfAbsent(key, new FileConfigFactory(configRepository));
    AbstractConfigFactory configFactory = factoryMap.get(key);
    return configFactory.getConfig(priorityLabels, key);
  }


  /**
   * KVConfigFactory
   * 根据 预设的 app、service、env 进行全量配置的 分层拉取 和 聚合
   *
   * @return
   */
  public Config getConfig() {
    factoryMap.putIfAbsent(DEFAULT_KEY, new MarshalKVConfigFactory(configRepository));
    AbstractConfigFactory configFactory = factoryMap.get(DEFAULT_KEY);
    return configFactory.getConfig(priorityLabels, DEFAULT_KEY);
  }
}
