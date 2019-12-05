package com.test.core.controller;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.test.core.service.PersonService;
import com.test.core.util.MySession;

@ServerEndpoint("/person.do")
public class GetPersonController {
//	private static Map<String, Session> clients = new HashMap<String, Session>();

	@OnOpen
	public void onOpen(Session session) throws IOException {
		MySession.clients.put(session.getId(), (Session) session);
		@SuppressWarnings("resource")
		ApplicationContext context = new FileSystemXmlApplicationContext(
				new String[] { "dubbo-consumer.xml" });
		final PersonService personService = (PersonService) context.getBean("personService");
		System.out.println(personService.getPersons());
		sentDataToWeb(personService.getPersons());
	}
	static DecimalFormat df = new DecimalFormat("#.##");

	public static void sentDataToWeb(List<com.test.core.bean.Person> list) throws IOException {
		System.out.println("lala: "+list);
		if (!MySession.clients.isEmpty() && !list.isEmpty()) {
			StringBuffer buffer = new StringBuffer();
			buffer.append(
					"<table border=1 class=\"hovertable\" style=\"padding: 10px; width: 500px \"><tr style='background-color:#999;'><td>学号</td><td>姓名</td><td>年龄</td><td>性别</td></tr>");
			for (Iterator<com.test.core.bean.Person> iterator = list.iterator(); iterator.hasNext();) {
				com.test.core.bean.Person person = (com.test.core.bean.Person) iterator.next();
				String id = person.getId();
				String name = person.getName();
				Integer age = person.getAge();
				String sex = (person.getSex() == 0) ? "女" : "男";
				buffer.append("<tr onmouseover=\"this.style.backgroundColor='#ffff66';\" onmouseout=\"this.style.backgroundColor='#d4e3e5';\">");
				buffer.append("<td " + id + ">").append(id).append("<td " + name + ">").append(name)
						.append("<td " + age + ">").append(age).append("<td " + sex + ">").append(sex).append("</td>");
				buffer.append("</tr>");
			}
			buffer.append("</table>");
			System.out.println("推送数据到浏览器：" + buffer.toString());
			for (Iterator<Session> iterator = MySession.clients.values().iterator(); iterator.hasNext();) {
				Session client = (Session) iterator.next();
				client.getBasicRemote().sendText(buffer.toString());
			}
		}
	}
	@OnClose
	public void onClose(Session session) throws IOException {
		MySession.clients.remove(session.getId());
	}

}
