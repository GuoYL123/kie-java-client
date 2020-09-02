package com.huaweicloud.kie;

import static com.huaweicloud.kie.StaticConfig.FILE_PREFIX;

import com.huaweicloud.kie.model.Config;
import com.huaweicloud.kie.model.KVDoc;
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
public class MarshalKVConfigFactory extends AbstractConfigFactory {

  MarshalKVConfigFactory(ConfigRepository dataSource) {
    super(dataSource);
  }

  public Config getConfig(TreeMap<String, String> priorityLabels, String key) {
    List<KVDoc> kvList = dataSource.getSourceData().getData().stream()
        .filter(data -> !data.getKey().startsWith(FILE_PREFIX)).collect(Collectors.toList());
    return stageConfig(kvList, () -> marshal(kvList, priorityLabels, key));
  }

  private Config marshal(List<KVDoc> kvList, TreeMap<String, String> priorityLabels, String key) {
    LinkedList<List<KVDoc>> priorityKVList = sortByProority(kvList, priorityLabels);
    Map<String, Object> configsMap = new HashMap<>();
    //todo: 不同value Type的优先级
    for (List<KVDoc> kvDocs : priorityKVList) {
      for (KVDoc kvDoc : kvDocs) {
        configsMap.putAll(processValueType(kvDoc, kvDoc.getKey()));
      }
    }
    return new Config(key, configsMap);
  }
}
