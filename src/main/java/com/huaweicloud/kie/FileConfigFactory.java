package com.huaweicloud.kie;

import static com.huaweicloud.kie.StaticConfig.FILE_PREFIX;

import com.huaweicloud.kie.model.Config;
import com.huaweicloud.kie.model.KVDoc;
import com.huaweicloud.kie.model.KVResponse;
import com.huaweicloud.kie.model.ValueType;
import java.util.Map.Entry;
import java.util.TreeMap;

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
    return stageConfig(resp.getData(), () -> getFileConfig(resp, priorityLabels, key));
  }

  private Config getFileConfig(KVResponse resp, TreeMap<String, String> priorityLabels,
      String key) {
    Config config = null;
    for (KVDoc kvDoc : resp.getData()) {
      if (!kvDoc.getKey().startsWith(FILE_PREFIX + key)) {
        continue;
      }
      boolean ok = true;
      //筛选出有所有label的kvDoc
      for (Entry<String, String> label : priorityLabels.entrySet()) {
        if (!(kvDoc.getLabels().containsKey(label.getKey())
            && kvDoc.getLabels().get(label.getKey()).equals(label.getValue()))) {
          ok = false;
          break;
        }
      }
      if (ok) {
        String kvKey = kvDoc.getKey();
        if (!kvDoc.getValueType().equals(ValueType.text.name())) {
          kvDoc.setKey("");
        }
        config = new Config(kvKey, processValueType(kvDoc));
        break;
      }
    }
    return config;
  }
}
