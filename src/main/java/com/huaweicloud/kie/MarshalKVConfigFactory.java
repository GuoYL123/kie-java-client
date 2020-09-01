package com.huaweicloud.kie;

import com.huaweicloud.kie.model.Config;
import com.huaweicloud.kie.model.KVDoc;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author GuoYl123
 * @Date 2020/8/29
 **/
public class MarshalKVConfigFactory extends AbstractConfigFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(MarshalKVConfigFactory.class);

  private ConfigRepository dataSource;



  public MarshalKVConfigFactory(ConfigRepository dataSource) {
    this.dataSource = dataSource;
  }

  public Config getConfig(TreeMap<String, String> priorityLabels, String key) {
    List<KVDoc> kvList = dataSource.getSourceData().getData().stream()
        .filter(data -> !data.getKey().startsWith(FILE_PREFIX)).collect(Collectors.toList());
//    try {
//      String md5 = objectMapper.writeValueAsString(kvList);
//      if (md5.equals(this.md5)) {
//        return stageConfig;
//      }
//      this.md5 = md5;
//    } catch (JsonProcessingException e) {
//      LOGGER.error("parse json failed");
//    }
//    stageConfig = new Config(key, marshal(kvList, priorityLabels));
    return stageConfig(kvList, () -> new Config(key, marshal(kvList, priorityLabels)));
  }

  private Map<String, Object> marshal(List<KVDoc> kvList, TreeMap<String, String> priorityLabels) {
    LinkedList<List<KVDoc>> priorityKVList = new LinkedList<>();
    for (Entry<String, String> entry : priorityLabels.entrySet()) {
      List<KVDoc> tempList;
      if (priorityKVList.isEmpty()) {
        priorityKVList.add(filterByLabel(kvList, entry));
        continue;
      } else {
        tempList = priorityKVList.getLast();
      }
      priorityKVList.add(filterByLabel(tempList, entry));
    }
    Map<String, Object> configs = new HashMap<>();
    //todo: 不同value Type的优先级
    for (List<KVDoc> kvDocs : priorityKVList) {
      for (KVDoc kvDoc : kvDocs) {
        configs.putAll(processValueType(kvDoc));
      }
    }
    return configs;
  }

  private List<KVDoc> filterByLabel(List<KVDoc> tempList, Entry<String, String> label) {
    return tempList.stream().filter(
        kv -> kv.getLabels().containsKey(label.getKey()) &&
            kv.getLabels().get(label.getKey()).equals(label.getValue()))
        .collect(Collectors.toList());
  }
}
