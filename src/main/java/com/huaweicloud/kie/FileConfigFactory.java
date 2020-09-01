package com.huaweicloud.kie;

import com.huaweicloud.kie.model.Config;
import com.huaweicloud.kie.model.KVDoc;
import com.huaweicloud.kie.model.KVResponse;
import com.huaweicloud.kie.model.ValueType;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * @Author GuoYl123
 * @Date 2020/8/29
 **/
public class FileConfigFactory extends AbstractConfigFactory {

  FileConfigFactory(ConfigRepository dataSource) {
    super(dataSource);
  }


  /**
   * File start with .
   *
   * @param priorityLabels
   * @return
   */
  public Config getConfig(TreeMap<String, String> priorityLabels, String key) {
    KVResponse resp = dataSource.getSourceData();
    //todo: 返回null, option优化
    return stageConfig(resp.getData(),
        () -> marshal(resp.getData(), priorityLabels, key));
  }

  private Config marshal(List<KVDoc> kvList, TreeMap<String, String> priorityLabels,
      String key) {
    kvList = kvList.stream().filter(kv -> kv.getKey().equals(key))
        .collect(Collectors.toList());
    LinkedList<List<KVDoc>> priorityKVList = sortByProority(kvList, priorityLabels);
    Map<String, Object> configsMap = new HashMap<>();
    for (List<KVDoc> kvDocs : priorityKVList) {
      if (kvDocs.isEmpty()) {
        continue;
      }
      KVDoc kvDoc = kvDocs.get(0);
      if (!kvDoc.getValueType().equals(ValueType.text.name())) {
        kvDoc.setKey("");
      }
      configsMap.putAll(processValueType(kvDoc));
    }
    return new Config(key, configsMap);
  }

}
