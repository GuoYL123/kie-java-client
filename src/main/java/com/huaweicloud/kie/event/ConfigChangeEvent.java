package com.huaweicloud.kie.event;

/**
 * @Author GuoYl123
 * @Date 2020/8/27
 **/
public class ConfigChangeEvent {

  String message;

  public ConfigChangeEvent() {
  }

  public ConfigChangeEvent(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
