package com.test.core.monitor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.websocket.CloseReason;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.test.core.util.MySession;


@ServerEndpoint("/getDisk/{root}/{node}")
public class GetChartDisk {
	Thread thread = new Thread();
//	private static Map<String, Session> clients = new HashMap<String, Session>();

	// 连接打开时执行
	@OnOpen
	public synchronized void onOpen(@PathParam(value="root") String root,@PathParam(value="node") String node,Session session) throws IOException, EncodeException {
		System.out.println("创建新连接:Connected ... " + session.getId());
		MySession.clients.put(session.getId(), session);
		String nodeName = "/" + root + "/" + node; 
		System.out.println("nodeName:"+nodeName);
		
		while (true) {
			ServerData data = ServerMonitorListener.getByName(nodeName);
//			JSONObject object = new JSONObject();
//			object.append("cpu", data.getCpuPercent());
//			object.append("mem", data.getMemoryPercent());
//			object.append("disk", data.getDiskPercent());
//			System.out.println("data:"+object);
			sendMessageToWeb(session, data);
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
//		sendMessageToWeb(session, ServerMonitorListener.serverDatas.get(0));
		
//		ServerData data = ServerMonitorListener.serverDatas.get(0);
//		double cpu = data.getCpuPercent();
//		session.getAsyncRemote().sendObject(cpu);
	}

	
	// 连接关闭时执行
	@OnClose
	public synchronized void onClose(Session session, CloseReason closeReason) {
		MySession.clients.remove(session.getId());
	}

	// 连接错误时执行
	@OnError
	public synchronized void onError(Throwable t) {
		t.printStackTrace();
	}

	public synchronized static void sendMessageToWeb(Session session,ServerData data) throws IOException, EncodeException {
		session.getBasicRemote().sendObject(data.getDiskPercent());
	}
//		@RequestMapping("/getValue")
//		@ResponseBody
//	    public String getValue(HttpServletRequest request,HttpServletResponse response) throws JsonProcessingException{
//			System.out.println("到这里了-----------------");
//			ObjectMapper mapper = new ObjectMapper();
//			String json = mapper.writeValueAsString(ServerMonitorListener.serverDatas.get(0));
//			String callback = request.getParameter("callback");
//			
//	        return callback+"("+json+")";
//	    }
//		
//		@RequestMapping("/getHisDates")
//	    public void getHisDates(HttpServletResponse resp) {
//
//	        ServerData data = ServerMonitorListener.serverDatas.get(0);
//	        try {
//	            resp.getWriter().print(data.getCpuPercent());
//	        } catch (IOException e) {
//	            // TODO Auto-generated catch block
//	            e.printStackTrace();
//	        }
//	        System.out.println("最新的一条数据=" + data);
//	     }
}