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
import com.huaweicloud.kie.model.KVDoc;
import com.huaweicloud.kie.model.KVResponse;
import com.huaweicloud.kie.model.LabelHistoryResponse;
import java.io.IOException;
import java.util.List;
import org.apache.http.HttpStatus;
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
      ObjectMapper mapper = new ObjectMapper();
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
  public List<KVResponse> getValueOfKey(String key) {
    try {
      HttpResponse response = httpClient.getHttpRequest("/kie/kv/" + key, null, null);
      if (response.getStatusCode() == HttpStatus.SC_OK) {
        ObjectMapper mapper = new ObjectMapper();
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
  public List<KVResponse> listKeyValue() {
    try {
      HttpResponse response = httpClient.getHttpRequest("/kie/kv", null, null);
      if (response.getStatusCode() == HttpStatus.SC_OK) {
        ObjectMapper mapper = new ObjectMapper();
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
   * @param kvDoc
   * @return void
   */
  public void deleteKeyValue(KVDoc kvDoc) {
    try {
      HttpResponse response = httpClient.deleteHttpRequest("/kie/kv/?kvID=" + kvDoc.getId(), null, null);
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
   * todo : with key
   * @return void
   */
  public List<LabelHistoryResponse> getRevisionByLabelId(String labelId) {
    try {
      HttpResponse response = httpClient.getHttpRequest("/kie/revision/" + labelId, null, null);
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
