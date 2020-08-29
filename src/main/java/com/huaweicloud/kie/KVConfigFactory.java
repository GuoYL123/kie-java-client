package com.huaweicloud.kie;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huaweicloud.kie.model.Config;
import com.huaweicloud.kie.model.KVDoc;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author GuoYl123
 * @Date 2020/8/29
 **/
public class KVConfigFactory extends AbstractConfigFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(KVConfigFactory.class);

  private ConfigRepository dataSource;

  private String md5;

  private static final ObjectMapper objectMapper = new ObjectMapper()
      .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

  private Config stageConfig;

  public KVConfigFactory(ConfigRepository dataSource) {
    this.dataSource = dataSource;
  }

  public Config getConfig(TreeMap<String, String> priorityLabels) {
    List<KVDoc> kvList = dataSource.getSourceData().getData().stream()
        .filter(data -> !data.getKey().startsWith(".")).collect(Collectors.toList());
    try {
      String md5 = objectMapper.writeValueAsString(kvList);
      if (md5.equals(this.md5)) {
        return stageConfig;
      }
      this.md5 = md5;
    } catch (JsonProcessingException e) {
      LOGGER.error("parse json failed");
    }
    marshal(kvList, priorityLabels);
    return null;
  }

  //todo : 根据 priorityLabels 聚合配置
  private Map<String, Object> marshal(List<KVDoc> kvList, TreeMap<String, String> priorityLabels) {
    return null;
  }
}
