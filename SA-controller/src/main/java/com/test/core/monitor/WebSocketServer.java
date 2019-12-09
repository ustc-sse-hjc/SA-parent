package com.test.core.monitor;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.mysql.jdbc.PreparedStatement;

/**
 * websocket 推送程序
 * @author hjc
 */

@ServerEndpoint("/monitor")
public class WebSocketServer {

  private static Map<String, Session> clients = new HashMap<String, Session>();

  @OnOpen
  public synchronized void onOpen(Session session) throws IOException {

    clients.put(session.getId(), session);

    initDataToWeb();
  }

  /**
   * 初始化
   */
  public static void initDataToWeb() {
	System.out.println("hjc");
    sentDataToWeb(ServerMonitorListener.serverDatas);
  }

  static DecimalFormat df = new DecimalFormat("#.##");

  /**
   * 推送数据到浏览器
   * @param datas
   */
  public synchronized static void sentDataToWeb(List<ServerData> datas) {
    if (!clients.isEmpty() && !datas.isEmpty()) {
      StringBuffer buffer = new StringBuffer();
      buffer.append(
          "<table border=1 class=\"hovertable\" style=\"padding: 10px; width: 900px \"><tr style='background-color:#999;'><td>服务器名称</td><td>cpu使用率</td><td>内存使用率</td><td>磁盘使用率</td><td>操作</td></tr>");
      for (Iterator<ServerData> iterator = datas.iterator(); iterator.hasNext();) {
        ServerData serverData = (ServerData) iterator.next();
        String cpuAlarm =
            serverData.getCpuPercent() > ServerData.threshold ? "bgcolor=#ff0000;" : "";
        String memAlarm =
            serverData.getMemoryPercent() > ServerData.threshold ? "bgcolor=#ff0000;" : "";
        String diskAlarm =
            serverData.getDiskPercent() > ServerData.threshold ? "bgcolor=#ff0000;" : "";
        buffer.append("<tr onmouseover=\"this.style.backgroundColor='#ffff66';\" onmouseout=\"this.style.backgroundColor='#d4e3e5';\">");
        buffer.append("<td id=\"nodeName\">").append(serverData.getServerNodeName()).append("</td>")
            .append("<td " + cpuAlarm + ">").append(df.format(serverData.getCpuPercent()))
            .append("%</td>").append("<td " + memAlarm + ">")
            .append(df.format(serverData.getMemoryPercent())).append("%</td>")
            .append("<td " + diskAlarm + ">").append(df.format(serverData.getDiskPercent()))
            .append("%</td>");
        buffer.append("<td>").append("\r\n" + 
        		"<button type=\"button\" class=\"delete\" onclick=\"stopServer(this)\"  style=\"width:50px; height:30px;\">关闭</button><button type=\"button\" class=\"getInfo\" onclick=\"getInfo(this)\"  style=\"width:50px; height:30px;\">查看</button>").append("</td>");
        buffer.append("</tr>");
//        inputDB(serverData.getServerNodeName(),serverData.getCpuPercent(),serverData.getMemoryPercent(),serverData.getDiskPercent());
      }
      buffer.append("</table>");
      System.out.println("推送数据到浏览器：" + buffer.toString());
      for (Iterator<Session> iterator = clients.values().iterator(); iterator.hasNext();) {
        Session client = (Session) iterator.next();
        client.getAsyncRemote().sendText(buffer.toString());
      }
    }
  }

  public static void inputDB(String id,double cpu,double mem,double disk) {
	  try {
			// 1.加载驱动程序
			Class.forName("com.mysql.jdbc.Driver");
			//远程调用
//			Connection conn= DriverManager.getConnection("jdbc:mysql://192.168.1.102:3306/sa?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC","root","guo5251314");
			Connection conn= DriverManager.getConnection("jdbc:mysql://localhost.37:3306/sa?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC","root","0000");
			// 2.获得数据连接
//			Connection conn = DriverManager.getConnection(url, username, password);
			// 3.使用数据库的连接创建声明
			String sql = "insert into server values(?,?,?,?)";
			PreparedStatement stmt = (PreparedStatement) conn.prepareStatement(sql);
			stmt.setString(1, id);
			stmt.setDouble(2, cpu);
			stmt.setDouble(3, mem);
			stmt.setDouble(4, disk);
			// 4.使用声明执行SQL语句
			stmt.executeUpdate();
			conn.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

  }
  @OnClose
  public synchronized void onClose(Session session) throws IOException {
    clients.remove(session.getId());
  }

}
