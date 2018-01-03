<%@ page language="java" contentType="text/html;charset=UTF-8" import="java.util.*" import="com.jayshawn.weixin.jssdk.JSConfig" 
import="com.jayshawn.weixin.util.*" 
pageEncoding="UTF-8"%>
<% JSConfig jsConfig = (JSConfig)request.getSession().getAttribute("jsConfig");%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
<meta name="viewpoint" content="initial-scale=1.0;width=device-width"/>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="http://res.wx.qq.com/open/js/jweixin-1.2.0.js"></script>
<script type="text/javascript" src="js/jquery-1.9.1.min.js"></script>
<title>JS-SDK测试</title>
</head>
<body>
	
	<h4>JS-SDK测试</h4>
  
	<script type="text/javascript">
	    function jssdk(){
			$.ajax({
			data: "get",
			url: "http://jayshawn.vicp.io/weixin/SignatureServlet?url="+location.href.split('#')[0],
			cache: false,
			async: false,
			success: function (data) {
				wx.config({	
					debug: <%= jsConfig.isDebug()%>,
				    appId: '<%= jsConfig.getAppId()%>',
				    timestamp: '<%= jsConfig.getTimestamp()%>',
				    nonceStr: '<%= jsConfig.getNonceStr()%>',
				    signature: data.toString(),
				    jsApiList: ["onMenuShareTimeline","onMenuShareAppMessage"]
				});
				
				
				wx.ready(function(){
					wx.onMenuShareTimeline({
					    title: 'ItCenter主页', // 分享标题
					    link: 'http://jayshawn.vicp.io/weixin', // 分享链接，该链接域名或路径必须与当前页面对应的公众号JS安全域名一致
					    imgUrl: 'http://jayshawn.vicp.io/weixin/image/ico.png', // 分享图标
					    success: function () { 
					        alert('分享成功');
					    },
					    cancel: function () { 
					        // 用户取消分享后执行的回调函数
					    }
					});
					
					wx.onMenuShareAppMessage({
					    title: '', // 分享标题
					    desc: '', // 分享描述
					    link: '', // 分享链接，该链接域名或路径必须与当前页面对应的公众号JS安全域名一致
					    imgUrl: '', // 分享图标
					    type: '', // 分享类型,music、video或link，不填默认为link
					    dataUrl: '', // 如果type是music或video，则要提供数据链接，默认为空
					    success: function () { 
					        // 用户确认分享后执行的回调函数
					    },
					    cancel: function () { 
					        // 用户取消分享后执行的回调函数
					    }
					});
			
				wx.error(function(res){
					
				});
				});
			}
			});
		}
		
		window.onload = function(){
			jssdk();
		}

	</script>
	
</body>
</html>