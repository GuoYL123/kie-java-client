package com.huaweicloud.kie.http;

import java.util.List;

/**
 * @Author GuoYl123
 * @Date 2020/2/14
 **/
public class IpPortManager {

  List<IpPort> address;

  int index = 0;

  public IpPortManager(List<IpPort> address) {
    this.address = address;
  }

  public List<IpPort> getAddress() {
    return address;
  }

  public void setAddress(List<IpPort> address) {
    this.address = address;
  }

  public String getHost() {
    return address.get(index).getHost();
  }

  public Integer getPort() {
    return address.get(index).getPort();
  }

  public void toggle() {
    index = (index + 1) % address.size();
  }
}
