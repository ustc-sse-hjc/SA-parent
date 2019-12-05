package com.test.core.util;

import java.util.HashMap;
import java.util.Map;

import javax.websocket.Session;

public class MySession {
	public static Map<String, Session> clients = new HashMap<String, Session>();
}
