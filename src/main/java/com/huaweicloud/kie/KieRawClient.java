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

import com.huaweicloud.kie.http.HttpRequest;
import com.huaweicloud.kie.http.HttpResponse;
import com.huaweicloud.kie.http.HttpTransport;
import com.huaweicloud.kie.http.HttpTransportFactory;
import com.huaweicloud.kie.http.IpPort;
import com.huaweicloud.kie.http.IpPortManager;
import com.huaweicloud.kie.http.TLSConfig;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KieRawClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(KieRawClient.class);

  private static final String DEFAULT_HOST = "localhost";

  private static final int DEFAULT_PORT = 30110;

  private static final String PROJECT_NAME = "default";

  private static final String V1_PREFIX = "v1";

  private String basePath;

  private IpPortManager ipPortMgr;

  private HttpTransport httpTransport;

  public KieRawClient() {
    this(Collections.singletonList(new IpPort(DEFAULT_HOST, DEFAULT_PORT)),
        HttpTransportFactory.getHttpTransport());
  }

  public KieRawClient(TLSConfig tlsConfig) {
    this(Collections.singletonList(new IpPort(DEFAULT_HOST, DEFAULT_PORT)),
        HttpTransportFactory.getHttpTransport(tlsConfig));
  }

  private KieRawClient(List<IpPort> ipPort, HttpTransport httpTransport) {
    this.httpTransport = httpTransport;

    ipPort.forEach(item -> {
      String hostLowercase = item.getHost().toLowerCase();
      if (!hostLowercase.startsWith("https://") && !hostLowercase.startsWith("http://")) {
        // no protocol in host, use default 'http'
        item.setHost("http://" + item.getHost());
      }
    });

    this.ipPortMgr = new IpPortManager(ipPort);

    this.basePath = ipPortMgr.getHost() + ":" + ipPortMgr.getPort() + "/" + V1_PREFIX;
  }

  public HttpResponse getHttpRequest(String url, Map<String, String> headers, String content) {

    if (headers == null) {
      headers = new HashMap<String, String>();
    }

    HttpRequest httpRequest = new HttpRequest(basePath + url, headers, content);

    try {
      return httpTransport.get(httpRequest);
    } catch (IOException e) {
      toggle();
      LOGGER.error("kie unavailable.error message= : " + e.getMessage());
    }
    return null;
  }

  public HttpResponse postHttpRequest(String url, Map<String, String> headers, String content) {

    if (headers == null) {
      headers = new HashMap<String, String>();
    }

    HttpRequest httpRequest = new HttpRequest(basePath + url, headers, content);

    try {
      return httpTransport.post(httpRequest);
    } catch (IOException e) {
      toggle();
      LOGGER.error("kie unavailable.error message= : " + e.getMessage());
    }
    return null;
  }

  public HttpResponse putHttpRequest(String url, Map<String, String> headers, String content) {

    if (headers == null) {
      headers = new HashMap<String, String>();
    }

    HttpRequest httpRequest = new HttpRequest(basePath + url, headers, content);

    try {
      return httpTransport.put(httpRequest);
    } catch (IOException e) {
      toggle();
      LOGGER.error("kie unavailable.error message= : " + e.getMessage());
    }
    return null;
  }

  public HttpResponse deleteHttpRequest(String url, Map<String, String> headers, String content) {

    if (headers == null) {
      headers = new HashMap<>();
    }

    HttpRequest httpRequest = new HttpRequest(basePath + url, headers, content);

    try {
      return httpTransport.delete(httpRequest);
    } catch (IOException e) {
      toggle();
      LOGGER.error("kie unavailable.error message= : " + e.getMessage());
    }
    return null;
  }

  private void toggle() {
    ipPortMgr.toggle();
    this.basePath = ipPortMgr.getHost() + ":" + ipPortMgr.getPort() + "/" + V1_PREFIX;
  }

  public static class Builder {

    private List<IpPort> ipPort;

    private TLSConfig tlsConfig;

    public Builder() {
      ipPort = Collections.singletonList(new IpPort(DEFAULT_HOST, DEFAULT_PORT));
    }

    public List<IpPort> getIpPort() {
      return ipPort;
    }

    public Builder setIpPort(List<IpPort> ipPort) {
      this.ipPort = ipPort;
      return this;
    }

    public TLSConfig getTlsConfig() {
      return tlsConfig;
    }

    public Builder setTlsConfig(TLSConfig tlsConfig) {
      this.tlsConfig = tlsConfig;
      return this;
    }

    public KieRawClient build() {
      if (tlsConfig == null) {
        return new KieRawClient(ipPort, HttpTransportFactory.getHttpTransport());
      }
      return new KieRawClient(ipPort, HttpTransportFactory.getHttpTransport(tlsConfig));
    }
  }
}
