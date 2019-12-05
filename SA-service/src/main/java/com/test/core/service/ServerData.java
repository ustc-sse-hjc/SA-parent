package com.test.core.service;

import java.io.UnsupportedEncodingException;

/**
 * 服务器监控数据
 * 
 */
public class ServerData {

  public ServerData(double cpuPercent, double memoryPercent,
      double diskPercent) {
    super();
    this.cpuPercent = cpuPercent;
    this.memoryPercent = memoryPercent;
    this.diskPercent = diskPercent;
  }

  /**
   * zk 服务器根节点
   */
  public static final String serverRootNode = "/server";
  /**
   * 服务器节点前缀
   */
  public static final String serverNodePreFix = "/server/server";
  /**
   * 告警阈值
   */
  public static final double threshold = 90d;
  /**
   * 服务器节点名称，也是zk node节点的名称
   */
  private String  serverNodeName;
  /**
   * cpu占用百分比
   */
  private double cpuPercent;
  /**
   * 内存占用百分比
   */
  private double memoryPercent;
  /**
   * 磁盘 占用百分比
   */
  private double diskPercent;
  public double getCpuPercent() {
    return cpuPercent;
  }
  public void setCpuPercent(double cpuPercent) {
    this.cpuPercent = cpuPercent;
  }
  public double getMemoryPercent() {
    return memoryPercent;
  }
  public void setMemoryPercent(double memoryPercent) {
    this.memoryPercent = memoryPercent;
  }
  public double getDiskPercent() {
    return diskPercent;
  }
  public void setDiskPercent(double diskPercent) {
    this.diskPercent = diskPercent;
  }
  
  public String getServerNodeName() {
    return serverNodeName;
  }
  public void setServerNodeName(String serverNodeName) {
    this.serverNodeName = serverNodeName;
  }
  @Override
  public String toString() {
    return "ServerData [cpuPercent=" + cpuPercent + ", memoryPercent=" + memoryPercent
        + ", diskPercent=" + diskPercent + "]";
  }

  /**
   * to byte
   * @return
   */
  public static byte[] toBytes(ServerData serverData) {
    try {
      return (serverData.getCpuPercent()+"|"+serverData.getMemoryPercent()+"|"+serverData.getDiskPercent()).getBytes("utf-8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return null;
  }
  /**
   * to serverdata
   * @return
   */
  public static ServerData toServerData(byte[] serverData) {
    try {
      if(serverData!=null) {
        String serverDataStr = new String(serverData, "utf-8");
        String[] datas = serverDataStr.split("\\|");
        return new  ServerData(Double.valueOf(datas[0]), Double.valueOf(datas[1]), Double.valueOf(datas[2]));
      }
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return null;
  }
  
  public static void main(String[] args) {
    ServerData serverData = new ServerData(80, 40, 90);
    System.out.println(toServerData(toBytes(serverData)));
    //20|24|35
  }
  
}
