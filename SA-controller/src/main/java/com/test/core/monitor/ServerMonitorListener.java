package com.test.core.monitor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class ServerMonitorListener implements Watcher, ServletContextListener {

  private ZooKeeper zk;
  public static final int connectTimeOut = 10 * 1000;// 毫秒
  // 服务器信息列表，用于websocket推送
  public static List<ServerData> serverDatas = new ArrayList<ServerData>();

  /**
   * 建立和zookeeper的连接，并注册监听
   * 
   * @throws IOException
   */
  public void createMonitorClientToZK() throws Exception {

    zk = new ZooKeeper("127.0.0.1:2181", connectTimeOut, this);
    System.out.println("连接到zk," + zk.toString());

  }

  /**
   * 获取服务端列表
   * 
   * @return
   * @throws InterruptedException
   * @throws KeeperException
   */
  public void getServers() {
    try {
      List<String> servers = zk.getChildren(ServerData.serverRootNode, true);
      if (servers != null) {
        System.out.println("在线服务列表：");
        serverDatas.clear();
        for (Iterator<String> iterator = servers.iterator(); iterator.hasNext();) {
          String serverName = (String) iterator.next();
          getServer(ServerData.serverRootNode + "/" + serverName);
        }
        /**
         * 将实时的状态推送到web端
         */
        if (!serverDatas.isEmpty()) {
          pushToWeb();
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 通过websocket推送数据到浏览器。
   */
  private static void pushToWeb() {
    WebSocketServer.sentDataToWeb(serverDatas);
  }

  /**
   * 获取待监控的服务节点数据列表
   * 
   * @return
   * @throws InterruptedException
   * @throws KeeperException
   */
  public void getServer(String serverName) {
    try {
      System.out.println(serverName);
      byte[] serverdata = zk.getData(serverName, true, new Stat());
      ServerData data = ServerData.toServerData(serverdata);
      data.setServerNodeName(serverName);
      if (data != null) {
        serverDatas.add(data);
        System.out.println("服务器状态：" + data);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  /**
   * 处理watch
   */
  public void process(WatchedEvent event) {
    System.out.println("*************************************");
    System.out.println("监听到事件：" + event);
    getServers();
  }

  // 关闭Zookeeper实例
  public void closeZkConnect() throws InterruptedException {
    zk.close();
  }

  /**
   * 容器启动时连接到zk
   */
  public void contextInitialized(ServletContextEvent sce) {
    try {
      createMonitorClientToZK();
    } catch (Exception e) {
      System.out.println("连接到zk失败...");
      e.printStackTrace();
    }
  }

  /**
   * 容器关闭时主动断开
   */
  public void contextDestroyed(ServletContextEvent sce) {
    try {
      closeZkConnect();
    } catch (Exception e) {
      System.out.println("关闭zk连接失败...");
      e.printStackTrace();
    }
  }
  
  /**
   * 通过nodeName获得
   */
  public static ServerData getByName(String name) {
	for(ServerData data : serverDatas) {
		if (data.getServerNodeName().equals(name)) {
			return data;
		}
	}
	return null;
  }
}
