package com.huaweicloud.kie;

import com.huaweicloud.kie.model.Config;
import com.huaweicloud.kie.model.KVDoc;
import com.huaweicloud.kie.model.ValueType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * @Author GuoYl123
 * @Date 2020/9/1
 **/
public class DefaultConfigFactory extends AbstractConfigFactory {

  private Map<String, String> labels;

  public DefaultConfigFactory(ConfigRepository dataSource, Map<String, String> labels) {
    super(dataSource);
    this.labels = labels;
  }

  @Override
  public Config getConfig(TreeMap<String, String> priorityLabels, String key) {
    List<KVDoc> kvList = dataSource.getSourceData().getData();
    return stageConfig(kvList, () -> parse(kvList, key));
  }

  private Config parse(List<KVDoc> data, String key) {
    Map<String, Object> configsMap = new HashMap<>();
    for (KVDoc datum : data) {
      if (!datum.getKey().equals(key)) {
        continue;
      }
      boolean ok = true;
      //check label
      for (Entry<String, String> entry : labels.entrySet()) {
        if (!(datum.getLabels().containsKey(entry.getKey()) &&
            datum.getLabels().get(entry.getKey()).equals(entry.getValue()))) {
          ok = false;
          break;
        }
      }
      if (ok) {
        String prefix = datum.getKey();
        if (!datum.getValueType().equals(ValueType.text.name())) {
          prefix = "";
        }
        configsMap.putAll(processValueType(datum, prefix));
      }
    }
    return new Config(key, configsMap);
  }
}
