package com.huaweicloud.kie;

import com.huaweicloud.kie.model.Config;
import java.util.Map;
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

  /**
   * 直接获取配置
   *
   * @param priorityLabels
   * @param key
   * @return
   */
  @Override
  Config getConfig(TreeMap<String, String> priorityLabels, String key) {
    //todo:
    return null;
  }
}
