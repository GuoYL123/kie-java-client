package com.huaweicloud.kie;

import static com.huaweicloud.kie.StaticConfig.DEFAULT_KEY;
import static com.huaweicloud.kie.StaticConfig.FILE_PREFIX;

import com.huaweicloud.kie.http.IpPort;
import com.huaweicloud.kie.http.TLSConfig;
import com.huaweicloud.kie.model.Config;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * @Author GuoYl123
 * @Date 2020/8/29
 **/
public class ConfigService {

  private TreeMap<String, String> priorityLabels;

  private ConfigRepository configRepository;

  public void init(TreeMap<String, String> priorityLabels, List<IpPort> list, TLSConfig tlsConfig) {
    this.priorityLabels = priorityLabels;
    Map<String, String> queryLabel = new HashMap<>();
    if (priorityLabels != null && priorityLabels.size() > 0) {
      Entry<String, String> entry = priorityLabels.firstEntry();
      queryLabel.put(entry.getKey(), entry.getValue());
    }
    configRepository = new ConfigRepository(queryLabel, list, tlsConfig);
  }


  private Map<String, AbstractConfigFactory> factoryMap = new HashMap<>();


  /**
   *  根据 "KIEFILE." + fileName 的形式读取指定KV文件，并进行解析
   *  文件根据app、service、env进行分区, 不做分层拉取,直接拉最底层  只做解析不做聚合
   *
   * @param fileName
   * @return
   */
  public Config getFileConfig(String fileName) {
    fileName = FILE_PREFIX + fileName;
    factoryMap.putIfAbsent(fileName, new FileConfigFactory(configRepository));
    AbstractConfigFactory configFactory = factoryMap.get(fileName);
    return configFactory.getConfig(priorityLabels, fileName);
  }


  /**
   *
   * 根据 预设的 app、service、env 进行全量配置的 分层拉取 和 聚合
   *
   * @return
   */
  public Config getAggregationConfig() {
    factoryMap.putIfAbsent(DEFAULT_KEY, new MarshalKVConfigFactory(configRepository));
    AbstractConfigFactory configFactory = factoryMap.get(DEFAULT_KEY);
    return configFactory.getConfig(priorityLabels, DEFAULT_KEY);
  }

  /**
   * 直接根据key和对应label返回指定配置
   *
   * @param key
   * @return
   */
  public Config getRawConfig(String key, Map<String, String> labels) {
    factoryMap.putIfAbsent(key, new DefaultConfigFactory(configRepository, labels));
    AbstractConfigFactory configFactory = factoryMap.get(key);
    return configFactory.getConfig(priorityLabels, key);
  }
}
