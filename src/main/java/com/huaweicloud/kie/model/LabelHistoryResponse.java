package com.huaweicloud.kie.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author GuoYl123
 * @Date 2020/2/13
 **/
public class LabelHistoryResponse {

  @JsonAlias("label_id")
  private String labelId;

  private Map<String, String> labels = new HashMap<String, String>();

  private List<KVDoc> data;

  private Integer revision;

  public String getLabelId() {
    return labelId;
  }

  public void setLabelId(String labelId) {
    this.labelId = labelId;
  }

  public Map<String, String> getLabels() {
    return labels;
  }

  public void setLabels(Map<String, String> labels) {
    this.labels = labels;
  }

  public List<KVDoc> getData() {
    return data;
  }

  public void setData(List<KVDoc> data) {
    this.data = data;
  }

  public Integer getRevision() {
    return revision;
  }

  public void setRevision(Integer revision) {
    this.revision = revision;
  }
}
