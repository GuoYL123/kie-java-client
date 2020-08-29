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

  private String check;

  private String domain;

  private String key;

  @JsonAlias("label_id")
  private String labelId;

  private Map<String, String> labels = new HashMap<String, String>();

  private Integer revision;

  private String value;

  @JsonAlias("value_type")
  private String valueType;

  private String status;

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

  public String getCheck() {
    return check;
  }

  public String getDomain() {
    return domain;
  }

  public String getLabelId() {
    return labelId;
  }

  public Map<String, String> getLabels() {
    return labels;
  }

  public Integer getRevision() {
    return revision;
  }

  public String getValue() {
    return value;
  }

  public void setCheck(String check) {
    this.check = check;
  }

  public void setDomain(String domain) {
    this.domain = domain;
  }

  public void setLabelId(String labelId) {
    this.labelId = labelId;
  }

  public void setLabels(Map<String, String> labels) {
    this.labels = labels;
  }

  public void setRevision(Integer revision) {
    this.revision = revision;
  }

  public void setValueType(String valueType) {
    this.valueType = valueType;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getValueType() {
    return valueType;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  @Override
  public String toString() {
    return "KVDoc{" +
        "id='" + id + '\'' +
        ", check='" + check + '\'' +
        ", domain='" + domain + '\'' +
        ", key='" + key + '\'' +
        ", labelId='" + labelId + '\'' +
        ", labels=" + labels +
        ", revision=" + revision +
        ", value='" + value + '\'' +
        ", valueType='" + valueType + '\'' +
        ", status='" + status + '\'' +
        '}';
  }
}
