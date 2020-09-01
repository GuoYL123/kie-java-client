/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huaweicloud.kie.model;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAlias;

public class KVDoc {

  private String id;

  private String key;

  private String value;

  @JsonAlias("value_type")
  private String valueType;

  @JsonAlias("create_revision")
  private String createRevision;

  @JsonAlias("update_revision")
  private String updateRevision;

  private String status;

  @JsonAlias("create_time")
  private String createTime;

  @JsonAlias("update_time")
  private String updateTime;

  private Map<String, String> labels = new HashMap<String, String>();

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getValueType() {
    return valueType;
  }

  public void setValueType(String valueType) {
    this.valueType = valueType;
  }

  public String getCreateRevision() {
    return createRevision;
  }

  public void setCreateRevision(String createRevision) {
    this.createRevision = createRevision;
  }

  public String getUpdateRevision() {
    return updateRevision;
  }

  public void setUpdateRevision(String updateRevision) {
    this.updateRevision = updateRevision;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getCreateTime() {
    return createTime;
  }

  public void setCreateTime(String createTime) {
    this.createTime = createTime;
  }

  public String getUpdateTime() {
    return updateTime;
  }

  public void setUpdateTime(String updateTime) {
    this.updateTime = updateTime;
  }

  public Map<String, String> getLabels() {
    return labels;
  }

  public void setLabels(Map<String, String> labels) {
    this.labels = labels;
  }

  @Override
  public String toString() {
    return "KVDoc{" +
        "id='" + id + '\'' +
        ", key='" + key + '\'' +
        ", value='" + value + '\'' +
        ", valueType='" + valueType + '\'' +
        ", createRevision='" + createRevision + '\'' +
        ", updateRevision='" + updateRevision + '\'' +
        ", status='" + status + '\'' +
        ", createTime='" + createTime + '\'' +
        ", updateTime='" + updateTime + '\'' +
        ", labels=" + labels +
        '}';
  }
}
