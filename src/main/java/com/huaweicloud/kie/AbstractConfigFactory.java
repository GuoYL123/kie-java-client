package com.huaweicloud.kie;

import com.huaweicloud.kie.model.Config;
import com.huaweicloud.kie.model.KVDoc;
import com.huaweicloud.kie.model.ValueType;
import java.io.StringReader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.util.StringUtils;

/**
 * @Author GuoYl123
 * @Date 2020/8/29
 **/
public abstract class AbstractConfigFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractConfigFactory.class);

  abstract Config getConfig(TreeMap<String, String> priorityLabels, String key);

  protected Map<String, Object> processValueType(KVDoc kvDoc) {
    ValueType vtype;
    try {
      vtype = ValueType.valueOf(kvDoc.getValueType());
    } catch (IllegalArgumentException e) {
      throw new RuntimeException("value type not support");
    }
    Properties properties = new Properties();
    Map<String, Object> kvMap = new HashMap<>();
    try {
      switch (vtype) {
        case yml:
        case yaml:
          YamlPropertiesFactoryBean yamlFactory = new YamlPropertiesFactoryBean();
          yamlFactory.setResources(new ByteArrayResource(kvDoc.getValue().getBytes()));
          return toMap(kvDoc.getKey(), yamlFactory.getObject());
        case properties:
          properties.load(new StringReader(kvDoc.getValue()));
          return toMap(kvDoc.getKey(), properties);
        case text:
        case string:
        default:
          kvMap.put(kvDoc.getKey(), kvDoc.getValue());
          return kvMap;
      }
    } catch (Exception e) {
      LOGGER.error("read config failed");
    }
    return Collections.emptyMap();
  }

  private Map<String, Object> toMap(String prefix, Properties properties) {
    if (properties == null) {
      return Collections.emptyMap();
    }
    Map<String, Object> result = new HashMap<>();
    Enumeration<String> keys = (Enumeration<String>) properties.propertyNames();
    while (keys.hasMoreElements()) {
      String key = keys.nextElement();
      Object value = properties.getProperty(key);
      if (!StringUtils.isEmpty(prefix)) {
        key = prefix + "." + key;
      }
      if (value != null) {
        result.put(key, value);
      } else {
        result.put(key, null);
      }
    }
    return result;
  }

}
