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

import java.util.HashMap;
import java.util.Map;
import org.apache.http.Header;

public class HttpResponse {

  private int statusCode;

  private String message;

  private String content;

  private Map<String, String> headers = new HashMap<>();

  public HttpResponse() {

  }

  HttpResponse(int statusCode, String message, String content, Header[] headers) {
    for (Header header : headers) {
      this.headers.put(header.getName(), header.getValue());
    }
    this.statusCode = statusCode;
    this.content = content;
    this.message = message;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(int statusCode) {
    this.statusCode = statusCode;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public Map<String, String> getHeaders() {
    return headers;
  }

  public String getHeader(String key) {
    return headers.get(key);
  }

  public void setHeaders(Map<String, String> headers) {
    this.headers = headers;
  }
}
