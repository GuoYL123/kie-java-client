package com.huaweicloud.kie.model;

import com.huaweicloud.kie.event.KieConfigEventBus;
import java.util.Map;
import java.util.function.Function;

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

  public void addListener(Function<Config, Void> callback) {
    KieConfigEventBus.getInstance()
        .register(event -> {
          if (event.getMessage().equals(key)) {
            callback.apply(this);
          }
        });

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
}
