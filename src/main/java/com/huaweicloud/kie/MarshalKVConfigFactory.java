package com.huaweicloud.kie;

import static com.huaweicloud.kie.StaticConfig.FILE_PREFIX;

import com.huaweicloud.kie.model.Config;
import com.huaweicloud.kie.model.KVDoc;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * @Author GuoYl123
 * @Date 2020/8/29
 **/
public class MarshalKVConfigFactory extends AbstractConfigFactory {

  MarshalKVConfigFactory(ConfigRepository dataSource) {
    super(dataSource);
  }

  public Config getConfig(TreeMap<String, String> priorityLabels, String key) {
    List<KVDoc> kvList = dataSource.getSourceData().getData().stream()
        .filter(data -> !data.getKey().startsWith(FILE_PREFIX)).collect(Collectors.toList());
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
