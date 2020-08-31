package com.huaweicloud.kie;

import com.huaweicloud.kie.model.Config;
import com.huaweicloud.kie.model.KVDoc;
import com.huaweicloud.kie.model.KVResponse;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * @Author GuoYl123
 * @Date 2020/8/29
 **/
public class FileConfigFactory extends AbstractConfigFactory {

  private ConfigRepository dataSource;

  public FileConfigFactory(ConfigRepository dataSource) {
    this.dataSource = dataSource;
  }


  /**
   * File start with .
   *
   * @param priorityLabels
   * @return
   */
  public Config getConfig(TreeMap<String, String> priorityLabels, String key) {
    KVResponse resp = dataSource.getSourceData();
    Config config = null;
    for (KVDoc kvDoc : resp.getData()) {
      if (!kvDoc.getKey().startsWith("." + key)) {
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
        config = new Config(kvDoc.getKey(), processValueType(kvDoc));
        break;
      }
    }
    return config;
  }
}
