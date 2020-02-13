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

import com.huaweicloud.kie.http.HttpResponse;
import com.huaweicloud.kie.http.TLSConfig;
import com.huaweicloud.kie.model.KVBody;
import com.huaweicloud.kie.model.KVResponse;
import com.huaweicloud.kie.model.LabelHistoryResponse;
import java.io.IOException;
import java.net.URISyntaxException;
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

  private KieRawClient httpClient;

  public KieClient() {
    this(new KieRawClient());
  }

  public KieClient(TLSConfig tlsConfig) {
    this(new KieRawClient(tlsConfig));
  }

  /**
   * Customized host,port,projectName and if any one parameter is null, it will be defaults
   *
   * @param host
   * @param port
   * @param projectName
   */
  public KieClient(String host, int port, String projectName) {
    this.httpClient = new KieRawClient.Builder().setHost(host).setPort(port).setProjectName(projectName).build();
  }

  public KieClient(KieRawClient serviceCenterRawClient) {
    this.httpClient = serviceCenterRawClient;
  }

  /**
   * Create value of a key
   *
   * @param key
   * @param kvBody
   * @return key-value json string; when some error happens, return null
   */
  public String putKeyValue(String key, KVBody kvBody) {
    try {
      HttpResponse response = httpClient.putHttpRequest("/kie/kv/" + key, null, mapper.writeValueAsString(kvBody));
      if (response.getStatusCode() == HttpStatus.SC_OK) {
        return response.getContent();
      } else {
        LOGGER.error("create keyValue fails, responseStatusCode={}, responseMessage={}, responseContent{}",
            response.getStatusCode(), response.getMessage(), response.getContent());
      }
    } catch (IOException e) {
      LOGGER.error("create keyValue fails", e);
    }
    return null;
  }

  /**
   * Get value of a key
   *
   * @param key
   * @return List<KVResponse>; when some error happens, return null
   */
  public List<KVResponse> getValueOfKey(String key, List<String> labels, String match,
      String pageNum, String pageSize, String insId, String status) throws URISyntaxException {
    try {
      URIBuilder uri = new URIBuilder("/kie/kv/" + key);
      if (labels != null && labels.size() > 0) {
        labels.forEach(a -> uri.addParameter("label", a));
      }
      String str = uri
          .addParameter("match", match)
          .addParameter("pageNum", pageNum)
          .addParameter("pageSize", pageSize)
          .addParameter("status", status)
          .build()
          .toString();
      Map<String, String> header = new HashMap<>();
      header.put("insId", insId);
      HttpResponse response = httpClient.getHttpRequest(str, header, null);
      if (response.getStatusCode() == HttpStatus.SC_OK) {
        return mapper.readValue(response.getContent(), new TypeReference<List<KVResponse>>() {
        });
      } else {
        LOGGER.error("get value of key fails, responseStatusCode={}, responseMessage={}, responseContent{}",
            response.getStatusCode(), response.getMessage(), response.getContent());
      }
    } catch (IOException e) {
      LOGGER.error("get value of key fails", e);
    }
    return null;
  }

  /**
   * List value and key
   *
   * @return List<KVResponse>; when some error happens, return null
   */
  public List<KVResponse> listKeyValue(List<String> labels, String match, String wait,
      String pageNum, String pageSize, String insId, String status) throws URISyntaxException {
    try {
      URIBuilder uri = new URIBuilder("/kie/kv");
      String str = uri
          .addParameter("match", match)
          .addParameter("pageNum", pageNum)
          .addParameter("pageSize", pageSize)
          .addParameter("wait", wait)
          .addParameter("status", status)
          .build()
          .toString();
      Map<String, String> header = new HashMap<>();
      header.put("insId", insId);
      HttpResponse response = httpClient.getHttpRequest(str, header, null);
      if (response.getStatusCode() == HttpStatus.SC_OK) {
        return mapper.readValue(response.getContent(), new TypeReference<List<KVResponse>>() {
        });
      } else {
        LOGGER.error(
            "list key value failed, responseStatusCode={}, responseMessage={}, responseContent{}",
            response.getStatusCode(), response.getMessage(), response.getContent());
      }
    } catch (IOException e) {
      LOGGER.error("list key value failed", e);
    }
    return null;
  }

  /**
   * Delete remove kv
   *
   * @return void
   */
  public void deleteKeyValue(String kvID, String labelId) throws URISyntaxException {
    try {
      URIBuilder uri = new URIBuilder("/kie/kv/?kvID=" + kvID);
      String str = uri
          .addParameter("labelID", labelId).toString();
      HttpResponse response = httpClient.deleteHttpRequest(str, null, null);
      if (response.getStatusCode() == HttpStatus.SC_NO_CONTENT) {
        LOGGER.info("Delete keyValue success");
      } else {
        LOGGER.error("delete keyValue fails, responseStatusCode={}, responseMessage={}, responseContent{}",
            response.getStatusCode(), response.getMessage(), response.getContent());
      }
    } catch (IOException e) {
      LOGGER.error("delete keyValue fails", e);
    }
  }


  /**
   * Get revision
   *
   * @return void
   */
  public List<LabelHistoryResponse> getRevisionByLabelId(String labelId, String key, String pageNum,
      String pageSize) throws URISyntaxException {
    try {
      URIBuilder uri = new URIBuilder("/kie/revision/" + labelId);
      String str = uri
          .addParameter("key", key)
          .addParameter("pageNum", pageNum)
          .addParameter("pageSize", pageSize)
          .toString();
      HttpResponse response = httpClient.getHttpRequest(str, null, null);
      if (response.getStatusCode() == HttpStatus.SC_OK) {
        return mapper
            .readValue(response.getContent(), new TypeReference<List<LabelHistoryResponse>>() {
            });
      } else {
        LOGGER.error(
            "get revision failed, responseStatusCode={}, responseMessage={}, responseContent{}",
            response.getStatusCode(), response.getMessage(), response.getContent());
      }
    } catch (IOException e) {
      LOGGER.error("get revision failed", e);
    }
    return null;
  }
}
