package com.huaweicloud.kie;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huaweicloud.kie.model.Config;
import com.huaweicloud.kie.model.KVDoc;
import com.huaweicloud.kie.model.ValueType;
import java.io.StringReader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.util.StringUtils;

/**
 * @Author GuoYl123
 * @Date 2020/8/29
 **/
public abstract class AbstractConfigFactory implements ConfigFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractConfigFactory.class);

  private String md5;

  private Config stageConfig;

  protected ConfigRepository dataSource;

  public AbstractConfigFactory(ConfigRepository dataSource) {
    this.dataSource = dataSource;
  }

  private static final ObjectMapper objectMapper = new ObjectMapper()
      .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

  protected Map<String, Object> processValueType(KVDoc kvDoc, String prefix) {
    ValueType vtype;
    try {
      vtype = ValueType.valueOf(kvDoc.getValueType());
    } catch (IllegalArgumentException e) {
      throw new RuntimeException("value type not support");
    }
    Properties properties = new Properties();
    Map<String, Object> kvMap = new HashMap<>();
    try {
      //todo: 支持ini、json格式
      switch (vtype) {
        case yaml:
          YamlPropertiesFactoryBean yamlFactory = new YamlPropertiesFactoryBean();
          yamlFactory.setResources(new ByteArrayResource(kvDoc.getValue().getBytes()));
          return toMap(prefix, yamlFactory.getObject());
        case properties:
          properties.load(new StringReader(kvDoc.getValue()));
          return toMap(prefix, properties);
        case text:
        case ini:
        case json:
        default:
          kvMap.put(prefix, kvDoc.getValue());
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


  protected Config stageConfig(List<KVDoc> kvList, Supplier<Config> function) {
    try {
      String md5 = objectMapper.writeValueAsString(kvList);
      if (md5.equals(this.md5)) {
        return stageConfig;
      }
      this.md5 = md5;
    } catch (JsonProcessingException e) {
      LOGGER.error("parse json failed");
    }
    stageConfig = function.get();
    return stageConfig;
  }


  protected LinkedList<List<KVDoc>> sortByProority(List<KVDoc> kvList,
      TreeMap<String, String> priorityLabels) {
    LinkedList<List<KVDoc>> priorityKVList = new LinkedList<>();
    for (Entry<String, String> entry : priorityLabels.entrySet()) {
      List<KVDoc> tempList;
      if (priorityKVList.isEmpty()) {
        priorityKVList.add(filterByLabel(kvList, entry));
        continue;
      } else {
        tempList = priorityKVList.getLast();
      }
      priorityKVList.add(filterByLabel(tempList, entry));
    }
    return priorityKVList;
  }

  private List<KVDoc> filterByLabel(List<KVDoc> tempList, Entry<String, String> label) {
    return tempList.stream().filter(
        kv -> kv.getLabels().containsKey(label.getKey()) &&
            kv.getLabels().get(label.getKey()).equals(label.getValue()))
        .collect(Collectors.toList());
  }
}
