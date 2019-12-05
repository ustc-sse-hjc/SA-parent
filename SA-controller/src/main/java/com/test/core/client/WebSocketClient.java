package com.test.core.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import org.springframework.stereotype.Controller;

@Controller
@ServerEndpoint("/client")
public class WebSocketClient{

	private static Map<String, Session> clients = new HashMap<String, Session>();
	private static List<String> list = new ArrayList<String>();

	/**
	 * 连接建立成功调用的方法
	 */
	@OnOpen
	public void onOpen(Session session) {
		clients.put(session.getId(), session);
		InetSocketAddress remoteAddress = WebSocketUtil.getRemoteAddress(session);
		System.out.println("有新连接加入！" + remoteAddress.getAddress());
		String ip = remoteAddress.getHostString();
		if(!list.contains(ip))
			list.add(ip);
		sentDataToWeb(list);
	}

	/**
	 * 推送数据到浏览器
	 * 
	 * @param datas
	 */
	public static void sentDataToWeb(List<String> list) {
		if (!clients.isEmpty() && !list.isEmpty()) {
			StringBuffer buffer = new StringBuffer();
			buffer.append("<table border=1 class=\"hovertable\" style=\"padding: 10px; width: 500px \"><tr style='background-color:#999;'><td>客户端IP地址</td></tr>");
			for (Iterator<String> iterator = list.iterator(); iterator.hasNext();) {
				String remoteAddress = (String) iterator.next();
				buffer.append("<tr onmouseover=\"this.style.backgroundColor='#ffff66';\" onmouseout=\"this.style.backgroundColor='#d4e3e5';\">");
				buffer.append("<td " + remoteAddress + ">").append(remoteAddress).append("</td>");
				buffer.append("</tr>");
			}
			buffer.append("</table>");
			System.out.println("推送数据到浏览器：" + buffer.toString());
			for (Iterator<Session> iterator = clients.values().iterator(); iterator.hasNext();) {
				Session client = (Session) iterator.next();
				client.getAsyncRemote().sendText(buffer.toString());
			}
		}
	}

	@OnClose
	public void onClose(Session session) throws IOException {
		
		InetSocketAddress remoteAddress = WebSocketUtil.getRemoteAddress(session);
		System.out.println("有连接退出！" + remoteAddress);
		list.remove(remoteAddress.getHostString());
		clients.remove(session.getId());
	}

}
