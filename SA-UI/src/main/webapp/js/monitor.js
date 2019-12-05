function getInfo(obj) {
	document.getElementById('container').toggle();
	// 判断当前浏览器是否支持WebSocket
	if ('WebSocket' in window) {
		var nodeName = $(obj).parent().parent().find("td").eq(0).text();
		websocket = new WebSocket("ws://localhost:8082/getValue" + nodeName);
	} else {
		alert('Not support websocket');
	}
	// 接收到消息的回调方法
	websocket.onmessage = function() {
		showServerData(event.data);
	};
	// 监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接
	window.onbeforeunload = function() {
		websocket.close();
	};
	// 显示消息
	function showServerData(data) {
		mydata = data;
		if (event.data == "1") {
			console.log("数据更新啦");
		}
	}
}

function openServer() {
	var websocket2 = null;
	websocket2 = new WebSocket("ws://localhost:8082/openService.do");
}

function stopServer(obj) {
	var nodeName = $(obj).parent().parent().find("td").eq(0).text();
	$(obj).parent().parent().find("td").eq(1).HTML = "0%";
	$(obj).parent().parent().find("td").eq(2).HTML = "0%";
	$(obj).parent().parent().find("td").eq(3).HTML = "0%";
	var url = "ws://localhost:8082/deleteNode.do" + nodeName;
	var websocket3 = null;
	websocket3 = new WebSocket("ws://localhost:8082/deleteNode.do" + nodeName);
}

var websocket = null;
// 判断当前浏览器是否支持WebSocket
if ('WebSocket' in window) {
	websocket = new WebSocket("ws://localhost:8082/monitor");
} else {
	alert('Not support websocket');
}
// 3s刷新
setInterval(function() {
	if ('WebSocket' in window) {
		websocket = new WebSocket("ws://localhost:8082/monitor");
	} else {
		alert('Not support websocket');
	}
}, 3000);
// 连接发生错误的回调方法
websocket.onerror = function() {
	showMsg("发生异常...");
};
// 连接成功建立的回调方法
websocket.onopen = function(event) {
	showMsg("已连接到监控服务端");
};
// 接收到消息的回调方法
websocket.onmessage = function() {
	showServerData(event.data);
};
// 连接关闭的回调方法
websocket.onclose = function() {
	showMsg("服务端关闭");
};
// 监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接
window.onbeforeunload = function() {
	websocket.close();
};
// 显示消息
function showServerData(data) {
	document.getElementById('data').innerHTML = data;
}
// 显示连接状态
function showMsg(html) {
	document.getElementById('msg').innerHTML = "消息：" + html;
}

var mydata = null;
var websocket = null;
// 判断当前浏览器是否支持WebSocket
//if ('WebSocket' in window) {
//	var nodeName = $(obj).parent().parent().find("td").eq(0).text();
//	websocket = new WebSocket("ws://localhost:8082/getValue" + nodeName);
//} else {
//	alert('Not support websocket');
//}
// 3s刷新
//setInterval(function() {
//	if ('WebSocket' in window) {
//		websocket = new WebSocket("ws://localhost:8082/getValue");
//	} else {
//		alert('Not support websocket');
//	}
//}, 3000);
// 接收到消息的回调方法
//websocket.onmessage = function() {
//	showServerData(event.data);
//};
// 监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接
//window.onbeforeunload = function() {
//	websocket.close();
//};
// 显示消息
function showServerData(data) {
	mydata = data;
	if (event.data == "1") {
		console.log("数据更新啦");
	}
}
/*
 * $(function() {
 *  // 初始化图表 startCharts();
 * 
 * });
 */

//var chart = Highcharts.chart('container', {
//	chart : {
//		type : 'spline',
//		animation : Highcharts.svg, // don't animate in old IE
//		marginRight : 10,
//		events : {
//			load : function() {
//
//				// set up the updating of the chart each second
//				var series = this.series[0];
//
//				setInterval(function() {
//					var x = (new Date()).getTime(), // current time
//					y = parseFloat(mydata);
//					series.addPoint([ x, y ], true, true);
//					websocket = new WebSocket("ws://localhost:8082/getValue");
//				}, 2000);
//
//			}
//		}
//	},
//
//	time : {
//		useUTC : false
//	},
//
//	title : {
//		text : '服务器监控'
//	},
//	xAxis : {
//		type : 'datetime',
//		tickPixelInterval : 150
//	},
//	yAxis : {
//		title : {
//			text : '使用率%'
//		},
//		max : 100, // 定义Y轴 最大值
//		min : 0, // 定义最小值
//		minPadding : 0.2,
//		maxPadding : 0.2,
//		tickInterval : 20, // 刻度值
//		plotLines : [ {
//			value : 0,
//			width : 1,
//			color : '#808080'
//		} ]
//	},
//	tooltip : {
//		headerFormat : '<b>{series.name}</b><br/>',
//		pointFormat : '{point.x:%Y-%m-%d %H:%M:%S}<br/>{point.y:.2f}'
//	},
//	legend : {
//		enabled : false
//	},
//	exporting : {
//		enabled : false
//	},
//	credits : {
//		enabled : false
//	},// 去除水印
//	plotOptions : {
//		line : {
//			connectNulls : true,// 该设置会连接空值点
//			// gapSize:1,//缺失点小于gapSize则连接
//			dataLabels : {
//				enabled : true
//			},
//			enableMouseTracking : false
//		}
//	},
//	series : [ {
//		name : 'cpu',
//		data : (function() {
//			// generate an array of random data
//			var data = [], time = (new Date()).getTime(), i;
//
//			for (i = -19; i <= 0; i += 1) {
//				data.push({
//					x : time + i * 1000,
//					y : mydata
//				});
//			}
//			return data;
//		}())
//	} ]
//});