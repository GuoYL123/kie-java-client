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

package com.huaweicloud.kie.http;

import java.io.IOException;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

public class HttpTransportImpl implements HttpTransport {

  protected static final int DEFAULT_MAX_CONNECTIONS = 1000;

  protected static final int DEFAULT_MAX_PER_ROUTE = 500;

  protected static final int DEFAULT_SOCKET_TIMEOUT = 50000;

  protected static final int DEFAULT_CONNECTION_TIMEOUT = 5000;

  private static final String HEADER_CONTENT_TYPE = "Content-Type";

  private static final String HEADER_USER_AGENT = "User-Agent";

  private static final String HEADER_TENANT_NAME = "x-domain-name";

  protected HttpClient httpClient;

  public HttpClient getHttpClient() {
    return httpClient;
  }

  public void setHttpClient(HttpClient httpClient) {
    this.httpClient = httpClient;
  }

  public HttpTransportImpl() {
    //register http/https socket factory
    Registry<ConnectionSocketFactory> connectionSocketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
        .register("http", PlainConnectionSocketFactory.INSTANCE)
        .build();

    //connection pool management
    PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(
        connectionSocketFactoryRegistry);
    connectionManager.setMaxTotal(DEFAULT_MAX_CONNECTIONS);
    connectionManager.setDefaultMaxPerRoute(DEFAULT_MAX_PER_ROUTE);

    //request parameter configuration
    RequestConfig config = RequestConfig.custom().
        setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT).
        setConnectionRequestTimeout(DEFAULT_CONNECTION_TIMEOUT).
        setSocketTimeout(DEFAULT_SOCKET_TIMEOUT).
        build();

    // construct httpClient
    HttpClientBuilder httpClientBuilder = HttpClientBuilder.create().
        setConnectionManager(connectionManager).
        setDefaultRequestConfig(config);

    httpClient = httpClientBuilder.build();
  }

  @Override
  public HttpResponse get(HttpRequest request) throws IOException {

    HttpGet httpGet = new HttpGet(request.getUrl());

    return doRequest(httpGet);
  }

  @Override
  public HttpResponse post(HttpRequest request) throws IOException {

    HttpPost httpPost = new HttpPost(request.getUrl());

    if (request.getContent() != null) {
      httpPost.setEntity(new StringEntity(request.getContent(), "UTF-8"));
    }
    return doRequest(httpPost);
  }

  @Override
  public HttpResponse put(HttpRequest request) throws IOException {

    HttpPut httpPut = new HttpPut(request.getUrl());

    if (request.getContent() != null) {
      httpPut.setEntity(new StringEntity(request.getContent(), "UTF-8"));
    }
    return doRequest(httpPut);
  }

  @Override
  public HttpResponse delete(HttpRequest request) throws IOException {

    HttpDelete httpDelete = new HttpDelete(request.getUrl());

    return doRequest(httpDelete);
  }

  /**
   * handle httpRequest
   *
   * @param httpRequest
   * @return
   * @throws IOException
   */
  private HttpResponse doRequest(HttpUriRequest httpRequest) throws IOException {
    //add header
    httpRequest.addHeader(HEADER_CONTENT_TYPE, "application/json");
    httpRequest.addHeader(HEADER_USER_AGENT, "kie-client/1.0.0");
    httpRequest.addHeader(HEADER_TENANT_NAME, "default");

    //get Http response
    org.apache.http.HttpResponse response = httpClient.execute(httpRequest);

    int statusCode = response.getStatusLine().getStatusCode();
    String messgae = response.getStatusLine().getReasonPhrase();
    String context = null;
    if (response.getEntity() != null) {
      context = EntityUtils.toString(response.getEntity(), "UTF-8");
    }

    return new HttpResponse(statusCode, messgae, context, response.getAllHeaders());
  }
}