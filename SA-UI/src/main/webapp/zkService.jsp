<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>分布式机群管理</title>
<meta name="viewport" content="width=device-width, initial-scale=1">

<link rel="stylesheet" href="css/style.css">
<script type="text/javascript" src="js/jquery.min.js"></script>
<style media="screen">
.table {
	text-align: center;
	padding: 10px;
	width: 500px
}
/* hovertable */
table.hovertable {
	font-family: verdana, arial, sans-serif;
	font-size: 11px;
	color: #333333;
	border-width: 1px;
	border-color: #999999;
	border-collapse: collapse;
}

table.hovertable th {
	background-color: #c3dde0;
	border-width: 1px;
	padding: 8px;
	border-style: solid;
	border-color: #a9c6c9;
}

table.hovertable tr {
	background-color: #d4e3e5;
}

table.hovertable td {
	border-width: 1px;
	padding: 8px;
	border-style: solid;
	border-color: #a9c6c9;
}
/* /hovertable */
html, body {
	width: 100%;
	height: 100%;
	margin: 0;
	padding: 0;
	overflow: hidden;
}

.container {
	width: 100%;
	height: 100%;
	margin: 0;
	padding: 0;
	background-color: #000000;
}

.font>p {
	font-size: 25px;
	text-shadow: 0 0 2px black;
	color: white;
}
/* /hovertable */
.btn {
	width: 500px;
	margin: 20px auto 0 auto;
	height: 25px;
}

.button {
	display: inline-block;
	outline: none;
	cursor: pointer;
	text-align: center;
	text-decoration: none;
	font: 16px/100% 'Microsoft yahei', Arial, Helvetica, sans-serif;
	padding: .5em 2em .55em;
	text-shadow: 0 1px 1px rgba(0, 0, 0, .3);
	-webkit-border-radius: .5em;
	-moz-border-radius: .5em;
	border-radius: .5em;
	-webkit-box-shadow: 0 1px 2px rgba(0, 0, 0, .2);
	-moz-box-shadow: 0 1px 2px rgba(0, 0, 0, .2);
	box-shadow: 0 1px 2px rgba(0, 0, 0, .2);
	text-shadow: 0 1px 1px rgba(0, 0, 0, .3);
}

.button:hover {
	text-decoration: none;
}

.button:active {
	position: relative;
	top: 1px;
}

.green {
	color: #e8f0de;
	border: solid 1px #538312;
	background: #64991e;
	background: -webkit-gradient(linear, left top, left bottom, from(#7db72f),
		to(#4e7d0e));
	background: -moz-linear-gradient(top, #7db72f, #4e7d0e);
	filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#7db72f',
		endColorstr='#4e7d0e');
}

.green:hover {
	background: #538018;
	background: -webkit-gradient(linear, left top, left bottom, from(#6b9d28),
		to(#436b0c));
	background: -moz-linear-gradient(top, #6b9d28, #436b0c);
	filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#6b9d28',
		endColorstr='#436b0c');
}

.green:active {
	color: #a9c08c;
	background: -webkit-gradient(linear, left top, left bottom, from(#4e7d0e),
		to(#7db72f));
	background: -moz-linear-gradient(top, #4e7d0e, #7db72f);
	filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#4e7d0e',
		endColorstr='#7db72f');
}
</style>

</head>
<body>
	<div id="jsi-particle-container" class="container"></div>
	<script>
var RENDERER = {
	BASE_PARTICLE_COUNT :20,
	WATCH_INTERVAL : 50,
	
	init : function(){
		this.setParameters();
		this.reconstructMethods();
		this.setup();
		this.bindEvent();
		this.render();
	},
	setParameters : function(){
		this.$window = $(window);
		this.$container = $('#jsi-particle-container');
		this.$canvas = $('<canvas />');
		this.context = this.$canvas.appendTo(this.$container).get(0).getContext('2d');
		this.particles = [];
		this.watchIds = [];
		this.gravity = {x : 0, y : 0, on : false, radius : 100, gravity : true};
	},
	setup : function(){
		this.particles.length = 0;
		this.watchIds.length = 0;
		this.width = this.$container.width();
		this.height = this.$container.height();
		this.$canvas.attr({width : this.width, height : this.height});
		this.distance = Math.sqrt(Math.pow(this.width / 2, 2) + Math.pow(this.height / 2, 2));
		this.createParticles();
	},
	reconstructMethods : function(){
		this.watchWindowSize = this.watchWindowSize.bind(this);
		this.jdugeToStopResize = this.jdugeToStopResize.bind(this);
		this.render = this.render.bind(this);
	},
	createParticles : function(){
		for(var i = 0, count = (this.BASE_PARTICLE_COUNT * this.width / 500 * this.height / 500) | 0; i < count; i++){
			this.particles.push(new PARTICLE(this));
		}
	},
	watchWindowSize : function(){
		this.clearTimer();
		this.tmpWidth = this.$window.width();
		this.tmpHeight = this.$window.height();
		this.watchIds.push(setTimeout(this.jdugeToStopResize, this.WATCH_INTERVAL));
	},
	clearTimer : function(){
		while(this.watchIds.length > 0){
			clearTimeout(this.watchIds.pop());
		}
	},
	jdugeToStopResize : function(){
		var width = this.$window.width(),
			height = this.$window.height(),
			stopped = (width == this.tmpWidth && height == this.tmpHeight);
			
		this.tmpWidth = width;
		this.tmpHeight = height;
		
		if(stopped){
			this.setup();
		}
	},
	bindEvent : function(){
		this.$window.on('resize', this.watchWindowSize);
		this.$container.on('mousemove', this.controlForce.bind(this, true));
		this.$container.on('mouseleave', this.controlForce.bind(this, false));
	},
	controlForce : function(on, event){
		this.gravity.on = on;
		
		if(!on){
			return;
		}
		var offset = this.$container.offset();
		this.gravity.x = event.clientX - offset.left + this.$window.scrollLeft();
		this.gravity.y = event.clientY - offset.top + this.$window.scrollTop();
	},
	render : function(){
		requestAnimationFrame(this.render);
		
		var context = this.context;
		context.save();
		context.fillStyle = 'hsla(0, 0%, 0%, 0.3)';
		context.fillRect(0, 0, this.width, this.height);
		context.globalCompositeOperation = 'lighter';
		
		for(var i = 0, particles = this.particles, gravity = this.gravity, count = particles.length; i < count; i++){
			var particle = particles[i];
			
			for(var j = i + 1; j < count; j++){
				particle.checkForce(context, particles[j]);
			}
			particle.checkForce(context, gravity);
			particle.render(context);
		}
		context.restore();
	}
};
var PARTICLE = function(renderer){
	this.renderer = renderer;
	this.init();
};
PARTICLE.prototype = {
	THRESHOLD : 100,
	SPRING_AMOUNT : 0.001,
	LIMIT_RATE : 0.2,
	GRAVIY_MAGINIFICATION : 10,
	
	init : function(){
		this.radius = this.getRandomValue(5, 15);
		this.x = this.getRandomValue(-this.renderer.width * this.LIMIT_RATE, this.renderer.width * (1 + this.LIMIT_RATE)) | 0;
		this.y = this.getRandomValue(-this.renderer.width * this.LIMIT_RATE, this.renderer.height * (1 + this.LIMIT_RATE)) | 0;
		this.vx = this.getRandomValue(-3, 3);
		this.vy = this.getRandomValue(-3, 3);
		this.ax = 0;
		this.ay = 0;
		this.gravity = false;
		this.transformShape();
	},
	getRandomValue : function(min, max){
		return min + (max - min) * Math.random();
	},
	transformShape : function(){
		var velocity = Math.sqrt(this.vx * this.vx + this.vy * this.vy);
		this.scale = 1 - velocity / 15;
		this.hue = ((180 + velocity * 10) % 360) | 0;
	},
	checkForce : function(context, particle){
		if(particle.gravity && !particle.on){
			return;
		}
		var dx = particle.x - this.x,
			dy = particle.y - this.y,
			distance = Math.sqrt(dx * dx + dy * dy),
			magnification = (particle.gravity ? this.GRAVIY_MAGINIFICATION : 1);
			
		if(distance > this.THRESHOLD * magnification){
			return;
		}
		var rate = this.SPRING_AMOUNT / magnification / (this.radius + particle.radius);
		this.ax = dx * rate * particle.radius;
		this.ay = dy * rate * particle.radius;
		
		if(!particle.gravity){
			particle.ax = -dx * rate * this.radius;
			particle.ay = -dy * rate * this.radius;
		}
		if(distance > this.THRESHOLD){
			return;
		}
		context.lineWidth = 3;
		context.strokeStyle = 'hsla(' + this.hue + ', 70%, 30%, ' + (Math.abs(this.THRESHOLD - distance) / this.THRESHOLD) + ')';
		context.beginPath();
		context.moveTo(this.x, this.y);
		context.lineTo(particle.x, particle.y);
		context.stroke();
	},
	render : function(context){
		context.save();
		context.fillStyle = 'hsl(' + this.hue + ', 70%, 40%)';
		context.translate(this.x, this.y);
		context.rotate(Math.atan2(this.vy, this.vx) + Math.PI / 2);
		context.scale(this.scale, 1);
		context.beginPath();
		context.arc(0, 0, this.radius, 0, Math.PI * 2, false);
		context.fill();
		context.restore();
		
		this.x += this.vx;
		this.y += this.vy;
		this.vx += this.ax;
		this.vy += this.ay;
		
		if(this.x < -this.radius && this.vx < 0 || (this.x > this.renderer.width + this.radius) && this.vx > 0 || this.y < -this.radius && this.vy < 0 || (this.y > this.renderer.height + this.radius) && this.vy > 0){
			var theta = this.getRandomValue(0, Math.PI * 2),
				sin = Math.sin(theta),
				cos = Math.cos(theta),
				velocity = this.getRandomValue(-3, 3);
				
			this.x = -(this.renderer.distance + this.radius) * cos + this.renderer.width / 2;
			this.y = -(this.renderer.distance + this.radius) * sin + this.renderer.height / 2;
			this.vx = velocity * cos;
			this.vy = velocity * sin;
		}
		this.transformShape();
	}
};
$(function(){
	RENDERER.init();
});
</script>
	<div id="nav-1">
		<ul class="nav">
			<li class="slide1"></li>
			<li class="slide2"></li>
			<li><a class="active" href="zkService.jsp">人员信息</a></li>
			<li><a href="monitor.jsp">监测服务器</a></li>
			<li><a href="client.jsp">监测客户端</a></li>

		</ul>
	</div>
	<div id="nav-2">
		<div align="center">
			<div class="font">
				<p>
					<strong>zk服务调用连接远程数据服务器</strong><br>
				</p>
			</div>
			   
			<div id="msg"
				style="background-color: #eee; padding: 10px; width: 500px"></div>
			<div style="width: 900px; height: 25px;"></div>
			<div id="data"></div>
			<div class="btn">
				<a onclick="print()" class="button green" style="float: left"">打印</a>
			</div>
		</div>
	</div>

	<script src='js/jquery.min.js'></script>
	<script src="js/script.js"></script>
	<script src="js/table.js"></script>
</body>

<script type="text/javascript">
	var websocket = null;
	//判断当前浏览器是否支持WebSocket
	if ('WebSocket' in window) {
		websocket = new WebSocket("ws://localhost:8082/person.do");
	} else {
		alert('Not support websocket');
	}
	//连接发生错误的回调方法
	websocket.onerror = function() {
		showMsg("发生异常...");
	};
	//连接成功建立的回调方法
	websocket.onopen = function(event) {
		showMsg("zk服务调用成功");
	};
	//接收到消息的回调方法
	websocket.onmessage = function() {
		showServerData(event.data);
	};
	//连接关闭的回调方法
	websocket.onclose = function() {
		showMsg("zk服务调用失败");
	};
	//监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接
	window.onbeforeunload = function() {
		websocket.close();
	};
	//显示消息
	function showServerData(data) {
		document.getElementById('data').innerHTML = data;
	}
	//显示连接状态
	function showMsg(html) {
		document.getElementById('msg').innerHTML = "消息：" + html;
	}
	//打印
	function print() {
		window.open("queryData.jsp","打印信息","height=200,width=500,top=200,left=500,toolbar=no,menubar=no,location=no, status=no,scrollbars=yes,resizable=yes")
	}
</script>

</html>