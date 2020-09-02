package com.huaweicloud.kie;

import com.huaweicloud.kie.model.Config;
import java.util.TreeMap;

/**
 * @Author GuoYl123
 * @Date 2020/8/29
 **/
public interface ConfigFactory {

  Config getConfig(TreeMap<String, String> priorityLabels, String key);
}
