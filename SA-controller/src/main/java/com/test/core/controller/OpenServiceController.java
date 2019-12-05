package com.test.core.controller;

import java.io.IOException;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import com.test.core.service.OpenService;
import com.test.core.util.MySession;


@ServerEndpoint("/openService.do")
public class OpenServiceController {
	
	@OnOpen
	public synchronized void onOpen(Session session) throws IOException {
		MySession.clients.put(session.getId(), session);
		@SuppressWarnings("resource")
		ApplicationContext context = new FileSystemXmlApplicationContext(
				new String[] { "dubbo-consumer.xml" });
		final OpenService openService = (OpenService) context.getBean("openService");
		System.out.println("--------------创建服务--------------");
		openService.newService();
		
	}
	
	@OnClose
	public synchronized void onClose(Session session) throws IOException {
		MySession.clients.remove(session.getId());
	}

}
