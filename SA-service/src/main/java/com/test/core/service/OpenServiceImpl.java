package com.test.core.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Perms;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.springframework.stereotype.Service;

@Service("openService")
public class OpenServiceImpl implements OpenService, Watcher {

	@Override
	public void startService() {
		// TODO Auto-generated method stub
		System.out.println("开始创建新服务节点");
		try {
			main(null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private ZooKeeper zk;
	public String serverNodeName = null;// 服务器节点名称，也是zk node节点的名称
	public static final int connectTimeOut = 10 * 1000;// 毫秒

	CountDownLatch downLatch = new CountDownLatch(1);

	public static void main(String[] args) throws Exception {
		new OpenServiceImpl().connectTozk();
	}

	/**
	 * 连接到zookeeper
	 * 
	 * @throws IOException
	 */
	public void connectTozk() throws Exception {

		zk = new ZooKeeper("127.0.0.1:2181", connectTimeOut, this);
		System.out.println("####" + zk.toString());

		/**
		 * 开始模拟服务器监控指标变化
		 */
		startRuntimeDataChange();

		downLatch.await();
	}

	/**
	 * 创建临时顺序节点
	 */
	public void createServerNode() {

		ServerData initData = new ServerData(0, 0, 0);// 初始化服务器监控指标数据
		try {
			List<ACL> acls = new ArrayList<ACL>();
			ACL acl = new ACL(Perms.ALL, new Id("world", "anyone"));
			acls.add(acl);
			String serverName = zk.create(ServerData.serverNodePreFix, ServerData.toBytes(initData), acls,
					CreateMode.EPHEMERAL_SEQUENTIAL);
			this.serverNodeName = serverName;
			System.out.println("成功连接到zk，并创建数据节点：" + serverNodeName);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("创建临时节点失败...");
			closeClient();
		}
	}

	/**
	 * 模拟指标变化
	 */
	public void startRuntimeDataChange() {
		Runnable r = new Runnable() {
			public void run() {
				while (true) {
					if (serverNodeName != null && zk != null) {
						ServerData runtimeData = new ServerData(ThreadLocalRandom.current().nextDouble(1d, 100d),
								ThreadLocalRandom.current().nextDouble(1d, 100d),
								ThreadLocalRandom.current().nextDouble(1d, 100d));
						try {
							zk.setData(serverNodeName, ServerData.toBytes(runtimeData), -1);
							System.out.println("设置" + serverNodeName + "的监控数据成功...");
						} catch (Exception e) {
							e.printStackTrace();
							System.out.println("设置" + serverNodeName + "的监控数据失败...");
						}
					}
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
					}
				}
			}
		};
		r.run();

	}

	public void process(WatchedEvent event) {
		System.out.println("*************************************");
		System.out.println("事件类型：" + event.getType());
		System.out.println("监听到事件：" + event);
		// 初次连接，创建服务器节点
		if (event.getState().equals(KeeperState.SyncConnected)) {
			createServerNode();
		} else {
			// 其他事件不需要关系。
		}
	}

	public void closeClient() {
		try {
			zk.close();
			downLatch.countDown();
			System.out.println("关闭连接...");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void newService() {
		// TODO Auto-generated method stub
		System.out.println("开始创建新服务节点");
		try {
			main(null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
