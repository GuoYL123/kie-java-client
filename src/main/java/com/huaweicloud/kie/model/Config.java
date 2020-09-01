package com.huaweicloud.kie.model;

import com.huaweicloud.kie.event.ConfigChangeEvent;
import com.huaweicloud.kie.event.KieConfigEventBus;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @Author GuoYl123
 * @Date 2020/8/27
 **/
public class Config {

  public String key;

  public Map<String, Object> content;

  public Config(String key, Map<String, Object> content) {
    this.key = key;
    this.content = content;
  }

  /**
   * 当前事件通知可能不准确
   * 不会少通知，但会多通知
   *
   * @param callback
   */
  public void addListener(Consumer<ConfigChangeEvent> callback) {
    KieConfigEventBus.getInstance().register(callback::accept);
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public Map<String, Object> getContent() {
    return content;
  }

  public void setContent(Map<String, Object> content) {
    this.content = content;
  }

  @Override
  public String toString() {
    return "Config{" +
        "key='" + key + '\'' +
        ", content=" + content +
        '}';
  }
}
