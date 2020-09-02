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

package com.huaweicloud.kie;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.huaweicloud.kie.http.HttpResponse;
import com.huaweicloud.kie.http.IpPort;
import com.huaweicloud.kie.http.TLSConfig;
import com.huaweicloud.kie.model.KVBody;
import com.huaweicloud.kie.model.KVDoc;
import com.huaweicloud.kie.model.KVResponse;
import com.huaweicloud.kie.model.LabelHistoryResponse;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpStatus;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class KieClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(KieClient.class);

  private static ObjectMapper mapper = new ObjectMapper();

  private String revison = "0";

  private KieRawClient httpClient;

  public KieClient() {
    this(new KieRawClient());
  }

  public KieClient(TLSConfig tlsConfig) {
    this(new KieRawClient(tlsConfig));
  }

  public KieClient(String host, int port) {
    IpPort temp = new IpPort(host, port);
    this.httpClient = new KieRawClient.Builder().setIpPort(Collections.singletonList(temp)).build();
  }

  public KieClient(List<IpPort> list) {
    this.httpClient = new KieRawClient.Builder().setIpPort(list).build();
  }


  public KieClient(String host, int port, TLSConfig tlsConfig) {
    IpPort temp = new IpPort(host, port);
    this.httpClient = new KieRawClient.Builder().setIpPort(Collections.singletonList(temp))
        .setTlsConfig(tlsConfig).build();
  }

  public KieClient(List<IpPort> list, TLSConfig tlsConfig) {
    this.httpClient = new KieRawClient.Builder().setIpPort(list).setTlsConfig(tlsConfig).build();
  }

  public KieClient(KieRawClient serviceCenterRawClient) {
    this.httpClient = serviceCenterRawClient;
  }

  /**
   * Create value of a key
   *
   * @param kvBody
   * @param project
   * @return
   */
  public String postKeyValue(KVBody kvBody, String project) throws IOException {
    try {
      HttpResponse response = httpClient.putHttpRequest("/" + project + "/kie/kv/", null,
          mapper.writeValueAsString(kvBody));
      if (response.getStatusCode() == HttpStatus.SC_OK) {
        return response.getContent();
      } else {
        LOGGER.error(
            "create keyValue fails, responseStatusCode={}, responseMessage={}, responseContent{}",
            response.getStatusCode(), response.getMessage(), response.getContent());
      }
    } catch (JsonProcessingException e) {
      LOGGER.error("resp parse error , ", e);
    }
    return null;
  }

  /**
   * Modify value of a key
   *
   * @param kvID
   * @param kvBody
   * @return key-value json string; when some error happens, return null
   */
  public String putKeyValue(String kvID, KVBody kvBody, String project) throws IOException {
    try {
      HttpResponse response = httpClient.putHttpRequest("/" + project + "/kie/kv/" + kvID, null,
          mapper.writeValueAsString(kvBody));
      if (response.getStatusCode() == HttpStatus.SC_OK) {
        return response.getContent();
      } else {
        LOGGER.error(
            "mdoify keyValue fails, responseStatusCode={}, responseMessage={}, responseContent{}",
            response.getStatusCode(), response.getMessage(), response.getContent());
      }
    } catch (JsonProcessingException e) {
      LOGGER.error("resp parse error , ", e);
    }
    return null;
  }

  /**
   * Get value of a key
   * todo: 如果获取增量数据？也许kie可以实现增量数据的推送
   *
   * @param key
   * @return List<KVResponse>; when some error happens, return null
   */
  public KVResponse queryKV(String key, Map<String, String> labels, String match,
      String pageNum, String pageSize, String sessionID, String status, String project,
      String wait, boolean isWatch) throws IOException {
    try {
      URIBuilder uri = new URIBuilder("/" + project + "/kie/kv");
      if (labels != null && labels.size() > 0) {
        labels.forEach((k, v) -> uri.addParameter("label", k + ":" + v));
      }
      if (key != null && !key.equals("")) {
        uri.addParameter("key", key);
      }
      if (match != null && !match.equals("")) {
        uri.addParameter("match", match);
      }
      if (pageNum != null && !pageNum.equals("")) {
        uri.addParameter("pageNum", pageNum);
      }
      if (pageSize != null && !pageSize.equals("")) {
        uri.addParameter("pageSize", pageSize);
      }
      if (status != null && !status.equals("")) {
        uri.addParameter("status", status);
      }
      if (wait != null && !wait.equals("")) {
        uri.addParameter("wait", wait);
      }
      if (isWatch) {
        uri.addParameter("revision", revison);
      }
      Map<String, String> header = new HashMap<>();
      header.put("sessionID", sessionID);
      HttpResponse response = httpClient.getHttpRequest(uri.build().toString(), header, null);
      if (response.getStatusCode() == HttpStatus.SC_OK) {
        revison = response.getHeader("X-Kie-Revision");
        return mapper.readValue(response.getContent(), KVResponse.class);
      } else if (response.getStatusCode() == HttpStatus.SC_NOT_MODIFIED) {
        return null;
      } else {
        LOGGER.error("get value of key fails, responseStatusCode={}, responseMessage={}, responseContent{}",
            response.getStatusCode(), response.getMessage(), response.getContent());
        throw new IOException("backend node is abnormal.");
      }
    } catch (JsonProcessingException e) {
      LOGGER.error("resp parse error , ", e);
    } catch (URISyntaxException e) {
      LOGGER.error("parse object failed ,", e);
    }
    return null;
  }

  public KVDoc getKVByID(String kvID, String project) {
    try {
      URIBuilder uri = new URIBuilder("/" + project + "/kie/kv/" + kvID);
      HttpResponse response = httpClient.getHttpRequest(uri.build().toString(), null, null);
      if (response.getStatusCode() == HttpStatus.SC_OK) {
        return mapper.readValue(response.getContent(), KVDoc.class);
      } else {
        LOGGER.error(
            "list key value failed, responseStatusCode={}, responseMessage={}, responseContent{}",
            response.getStatusCode(), response.getMessage(), response.getContent());
      }
    } catch (IOException e) {
      LOGGER.error("io exception , ", e);
    } catch (URISyntaxException e) {
      LOGGER.error("parse object failed ,", e);
    }
    return null;
  }

  /**
   * Delete remove kv
   *
   * @return void
   */
  public String deleteKeyValue(String kvID, String labelId, String project) throws IOException {
    try {
      URIBuilder uri = new URIBuilder("/" + project + "/kie/kv/" + kvID);
      if (labelId != null && !labelId.equals("")) {
        uri.addParameter("labelId", labelId);
      }
      HttpResponse response = httpClient.deleteHttpRequest(uri.build().toString(), null, null);
      if (response.getStatusCode() == HttpStatus.SC_NO_CONTENT) {
        return "Delete keyValue success";
      } else {
        LOGGER.error(
            "delete keyValue fails, responseStatusCode={}, responseMessage={}, responseContent{}",
            response.getStatusCode(), response.getMessage(), response.getContent());
      }
    } catch (URISyntaxException e) {
      LOGGER.error("parse object failed ,", e);
    }
    return null;
  }


  /**
   * Get revision
   *
   * @return void
   */
  public List<LabelHistoryResponse> getRevisionByLabelId(String labelId, String key, String pageNum,
      String pageSize, String project) throws IOException {
    try {
      URIBuilder uri = new URIBuilder("/" + project + "/kie/revision/" + labelId);
      if (key != null && !key.equals("")) {
        uri.addParameter("key", key);
      }
      if (pageNum != null && !pageNum.equals("")) {
        uri.addParameter("pageNum", pageNum);
      }
      if (pageSize != null && !pageSize.equals("")) {
        uri.addParameter("pageSize", pageSize);
      }
      HttpResponse response = httpClient.getHttpRequest(uri.build().toString(), null, null);
      if (response.getStatusCode() == HttpStatus.SC_OK) {
        return mapper
            .readValue(response.getContent(), new TypeReference<List<LabelHistoryResponse>>() {
            });
      } else {
        LOGGER.error(
            "get revision failed, responseStatusCode={}, responseMessage={}, responseContent{}",
            response.getStatusCode(), response.getMessage(), response.getContent());
      }
    } catch (JsonProcessingException e) {
      LOGGER.error("resp parse error , ", e);
    } catch (URISyntaxException e) {
      LOGGER.error("parse object failed ,", e);
    }
    return null;
  }
}
