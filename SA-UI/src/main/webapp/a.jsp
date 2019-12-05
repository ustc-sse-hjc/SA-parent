<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
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
</body>
<script type="text/javascript">
	$(function() {

		// 初始化图表
		startCharts();

		// 初始化websocket
		startWebsocket();

	});

	Highcharts.setOptions({
		global : {
			useUTC : false
		}
	});
	var chart2 = null;

	function startCharts() {
		chart2 = new Highcharts.Chart({

			chart : {
				type : 'spline',
				renderTo : 'container',
				animation : Highcharts.svg, // don't animate in old IE
				marginRight : 10
			},
			title : {
				text : '设备上报实时数据-' + ms
			},
			xAxis : {
				type : 'datetime',
				tickPixelInterval : 150
			},
			yAxis : {
				title : {
					text : ms
				},
				plotLines : [ {
					value : 0,
					width : 1,
					color : '#808080'
				} ]
			},
			tooltip : {
				formatter : function() {
					return '<b>'
							+ this.series.name
							+ '</b><br/>'
							+ Highcharts
									.dateFormat('%Y-%m-%d %H:%M:%S', this.x)
							+ '<br/>值:' + Highcharts.numberFormat(this.y, 2);
				}
			},
			legend : {
				enabled : false
			},
			exporting : {
				enabled : false
			},
			series : [ {
				name : ms,
				data : (function() {
					// generate an array of random data
					var data = [], time = (new Date()).getTime(), i;
					for (i = -14; i <= 0; i += 1) {
						data.push({
							x : time + i * 1000,

							y : !+14
						});
					}
					return data;
				}())
			} ]
		}, function(c) {
			activeLastPointToolip(c)
		});
	}

	function activeLastPointToolip(chart) {
		var points = chart.series[0].points;
		chart.tooltip.refresh(points[points.length - 1]);
	}

	//websocket取数据;

	var socket = null;
	function startWebsocket() {
		// 实现化WebSocket对象，指定要连接的服务器地址与端口
		socket = new WebSocket("ws://localhost:8080/iotweb/ws/currentdevicews");
		// 打开事件
		socket.onopen = function() {
			console.log("Socket 已打开");
			socket.send('{"device_name":"' + device_name + '","type":"' + type
					+ '"}');
		};
		// 获得消息事件
		socket.onmessage = function(msg) {
			onMessage(msg);
		};
		// 关闭事件
		socket.onclose = function() {
			console.log("Socket已关闭");
		};
		// 发生了错误事件
		socket.onerror = function() {
			console.log("Socket发生了错误");
		}
	}

	window.onbeforeunload = function() {
		closeWebSocket();
	}
	function closeWebSocket() {
		socket.send("killed");
		//socket.close();
	}

	function onMessage(event) {
		var kk = JSON.parse(event.data);
		var y_value = kk[type];
		var x_value = kk.timestamp;
		//var y_value = kk.x;
		var series = chart2.series[0];
		//var x = (new Date()).getTime(), // current time
		//y = parseInt(y_value);
		var x = Number(x_value), // current time
		y = Number(y_value);

		console.log(x);
		console.log(y);

		series.addPoint([ x, y ], true, true);
		activeLastPointToolip(chart2);
	}
</script>
</html>