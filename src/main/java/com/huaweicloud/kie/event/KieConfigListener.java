package com.huaweicloud.kie.event;

/**
 * @Author GuoYl123
 * @Date 2020/8/27
 **/
public interface KieConfigListener {

  void onEvent(ConfigChangeEvent event);
}
