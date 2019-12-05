<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Highcharts Example</title>

<style type="text/css">
#container {
	min-width: 310px;
	height: 400px;
	margin: 0 auto;
}
</style>
</head>
<body>
	<script src="/js/jquery.min.js"></script>
	<script src="/js/highcharts.js"></script>
	<script src="/js/exporting.js"></script>
	<script src="/js/export-data.js"></script>

	<div id="container"></div>

	<script type="text/javascript">
		var cpu = null;
		var mem = null;
		var disk = null;
		var websocket = null;
		var websocket2 = null;
		var websocket3 = null;
		var nodeName = null;
		window.onload = function GetRequest() {
			var url = window.location.href; //获取url中"?"符后的字串
			var theRequest = new Object();
			theRequest = url.split("=");
			nodeName = theRequest[1];

			//判断当前浏览器是否支持WebSocket
			if ('WebSocket' in window) {
				/* alert(nodeName); */
				/* var nodeName=$(obj).parent().parent().find("td").eq(0).text(); */

				websocket = new WebSocket("ws://localhost:8082/getCpu"
						+ nodeName);
				websocket2 = new WebSocket("ws://localhost:8082/getMem"
						+ nodeName);
				websocket3 = new WebSocket("ws://localhost:8082/getDisk"
						+ nodeName);
			} else {
				alert('Not support websocket');
			}
			//3s刷新
			setInterval(function() {
				if ('WebSocket' in window) {
					websocket = new WebSocket("ws://localhost:8082/getCpu"
							+ nodeName);
					websocket2 = new WebSocket("ws://localhost:8082/getMem"
							+ nodeName);
					websocket3 = new WebSocket("ws://localhost:8082/getDisk"
							+ nodeName);
				} else {
					alert('Not support websocket');
				}
			}, 3000);
			//接收到消息的回调方法
			websocket.onmessage = function() {
				showServerData(event.data);
			};
			//监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接
			window.onbeforeunload = function() {
				websocket.close();
			};
			//显示消息
			function showServerData(data) {
				cpu = data;
				if (event.data == "1") {
					console.log("数据更新啦");
				}
			}
			//接收到消息的回调方法
			websocket2.onmessage = function() {
				showServerData2(event.data);
			};
			//监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接
			window.onbeforeunload = function() {
				websocket2.close();
			};
			//显示消息
			function showServerData2(data) {
				mem = data;
				if (event.data == "1") {
					console.log("数据更新啦");
				}
			}

			//接收到消息的回调方法
			websocket3.onmessage = function() {
				showServerData3(event.data);
			};
			//监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接
			window.onbeforeunload = function() {
				websocket3.close();
			};
			//显示消息
			function showServerData3(data) {
				disk = data;
				if (event.data == "1") {
					console.log("数据更新啦");
				}
			}

			var chart = Highcharts
					.chart(
							'container',
							{
								chart : {
									type : 'spline',
									animation : Highcharts.svg, // don't animate in old IE
									marginRight : 10,
									events : {
										load : function() {

											// set up the updating of the chart each second
											var series = this.series[0];
											var series1 = this.series[1];
											var series2 = this.series[2];
											setInterval(
													function() {
														var x = (new Date())
																.getTime(), // current time
														y = parseFloat(cpu), y2 = parseFloat(mem), y3 = parseFloat(disk);
														series.addPoint(
																[ x, y ], true,
																true);

														series1.addPoint([ x,
																y2 ], true,
																true);
														series2.addPoint([ x,
																y3 ], true,
																true);
													}, 3000);
											/* websocket = new WebSocket(
													"ws://localhost:8082/getValue"
															+ nodeName); */
										}
									}
								},

								time : {
									useUTC : false
								},

								title : {
									text : '服务器监控'
								},
								xAxis : {
									type : 'datetime',
									tickPixelInterval : 150
								},
								yAxis : {
									title : {
										text : '使用率%'
									},
									max : 100, // 定义Y轴 最大值  
									min : 0, // 定义最小值  
									minPadding : 0.2,
									maxPadding : 0.2,
									tickInterval : 20, // 刻度值  
									plotLines : [ {
										value : 0,
										width : 1,
										color : '#808080'
									} ]
								},
								tooltip : {
									headerFormat : '<b>{series.name}</b><br/>',
									pointFormat : '{point.x:%Y-%m-%d %H:%M:%S}<br/>{point.y:.2f}'
								},
								legend : {
									enabled : false
								},
								exporting : {
									enabled : false
								},
								credits : {
									enabled : false
								},//去除水印
								plotOptions : {
									line : {
										connectNulls : true,//该设置会连接空值点
										// gapSize:1,//缺失点小于gapSize则连接
										dataLabels : {
											enabled : true
										},
										enableMouseTracking : false
									}
								},
								series : [
										{
											name : 'cpu使用率',
											data : (function() {
												// generate an array of random data
												var data = [], time = (new Date())
														.getTime(), i;

												for (i = -19; i <= 0; i += 1) {
													data.push({
														x : time + i * 1000,
														y : cpu
													});
												}
												return data;
											}())
										},
										{
											name : '内存使用率',
											data : (function() {
												// generate an array of random data
												var data = [], time = (new Date())
														.getTime(), i;
												for (i = -19; i <= 0; i++) {
													data.push({
														x : time + i * 1000,
														y : mem
													});
												}
												return data;
											}())
										},
										{
											name : '磁盘使用率',
											data : (function() {
												// generate an array of random data
												var data = [], time = (new Date())
														.getTime(), i;
												for (i = -19; i <= 0; i++) {
													data.push({
														x : time + i * 1000,
														y : disk
													});
												}
												return data;
											}())
										} ]
							});
		}
	</script>
</body>
</html>
