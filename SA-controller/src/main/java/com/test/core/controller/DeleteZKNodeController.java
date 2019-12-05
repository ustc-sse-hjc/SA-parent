package com.test.core.controller;

import java.io.IOException;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import com.test.core.util.MySession;

@ServerEndpoint("/deleteNode.do/{root}/{node}")
public class DeleteZKNodeController implements Watcher{
	private ZooKeeper zk;
	public static final int connectTimeOut = 10 * 1000;// 毫秒
	
	@OnOpen
	public synchronized void onOpen(@PathParam(value="root") String root,@PathParam(value="node") String node,Session session) throws IOException {
		MySession.clients.put(session.getId(), session);
		zk = new ZooKeeper("127.0.0.1:2181", connectTimeOut, this);
		zk.delete("/"+root+"/"+node,-1,new IsCallback(),"content");
		System.out.println("参数是："+root+node);
	}
	
	@OnClose
	public synchronized void onClose(Session session) throws IOException {
		MySession.clients.remove(session.getId());
	}
	/*异步删除节点回调方法简单实现*/
	class IsCallback implements AsyncCallback.VoidCallback{
	    public void processResult(int rc, String path, Object ctx){
	        System.out.println("rc:"+rc+"path:"+path+"Object:"+ctx);
	    }
	}
	@Override
	public void process(WatchedEvent event) {
		// TODO Auto-generated method stub
		
	}
}
