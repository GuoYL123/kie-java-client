package com.huaweicloud.kie.http;

/**
 * @Author GuoYl123
 * @Date 2020/2/14
 **/
public class IpPort {

  String host;

  Integer port;

  public IpPort(String host, Integer port) {
    this.host = host;
    this.port = port;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public Integer getPort() {
    return port;
  }

  public void setPort(Integer port) {
    this.port = port;
  }
}
