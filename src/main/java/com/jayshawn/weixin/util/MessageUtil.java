package com.jayshawn.weixin.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.jayshawn.weixin.bean.AccessToken;
import com.jayshawn.weixin.bean.Image;
import com.jayshawn.weixin.bean.ImageMessage;
import com.jayshawn.weixin.bean.News;
import com.jayshawn.weixin.bean.NewsMessage;
import com.jayshawn.weixin.bean.TextMessage;
import com.jayshawn.weixin.menu.Button;
import com.jayshawn.weixin.menu.ClickButton;
import com.jayshawn.weixin.menu.Menu;
import com.jayshawn.weixin.menu.ViewButton;
import com.jayshawn.weixin.robot.RobotApi;
import com.jayshawn.weixin.translate.TransApi;
import com.thoughtworks.xstream.XStream;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 进行消息格式的转换,或是拼装返回信息
 * @author Jayshawn
 * @date 2017年9月20日 下午11:07:38
 */
public class MessageUtil {
	
	public static final String MESSAGE_TEXT = "text";
	public static final String MESSAGE_IMAGE = "image";
	public static final String MESSAGE_VOICE = "voice";
	public static final String MESSAGE_VIDEO = "video";
	public static final String MESSAGE_SHORTVIDEO = "shortvideo";
	public static final String MESSAGE_LOCATION = "location";
	public static final String MESSAGE_LINK = "link";
	public static final String MESSAGE_EVENT = "event";
	public static final String MESSAGE_SUBSCRIBE = "subscribe";
	public static final String MESSAGE_UNSUBSCRIBE = "unsubscribe";
	public static final String MESSAGE_CLICK = "CLICK";
	public static final String MESSAGE_VIEW = "VIEW";
	public static final String MESSAGE_NEWS = "news";
	public static final String MESSAGE_SCANCODE_PUSH = "scancode_push";
	
	
	
	/**
	 * 将xml转换成Map
	 * @param request
	 * @return	Map对象
	 */
	public static Map<String, String> XmlToMap(InputStream in){
		Map<String, String> map = new HashMap<>();
		SAXReader reader = new SAXReader();
		Document doc;
		try {
			doc = reader.read(in);
			Element root = doc.getRootElement();
			List<Element> list = root.elements();
			for(Element e: list){
				map.put(e.getName(), e.getText());
			}
		} catch (DocumentException e1) {
			e1.printStackTrace();
		}finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return map;
	}
	
	/**
	 * 对象转换成xml字符串
	 * @param object
	 * @return xml字符串
	 */
	public static String objectToXml(Object object){
		 XStream xStream = new XStream();
		 xStream.alias("xml", object.getClass());
		 return xStream.toXML(object);
	}
	
	/**
	 * 帮助菜单
	 * @return
	 */
	private static String menuText(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("来聊天吧 ~随便说点什么试试看\n\n");
		buffer.append("温馨提示：回复内容以\"翻译\"开头，可以对内容进行翻译\n\n");
//		buffer.append("回复1    公众号介绍\n");
//		buffer.append("回复2    微信介绍\n");
//		buffer.append("回复3    获取图文消息\n");
//		buffer.append("回复4    获取图片消息\n");
//		buffer.append("回复?再次显示本提示");
		return buffer.toString();
	}
	
	private static String reply_1(){
		return "推送新鲜的it资讯。";
	}
	
	private static String reply_2(){
		return "微信 (WeChat)是腾讯公司于2011年1月21日推出的一个为智能终端提供即时通讯服务的免费应用程序。";
	}
	
	/**
	 * 将文本信息封装成xml字符串
	 * @param toUserName
	 * @param fromUserName
	 * @param content
	 * @return xml字符串
	 */
	public static String textToXml(String toUserName, String fromUserName, String content){
		TextMessage textMessage = new TextMessage();
		textMessage.setFromUserName(fromUserName);
		textMessage.setToUserName(toUserName);
		textMessage.setMsgType(MessageUtil.MESSAGE_TEXT);
		textMessage.setCreateTime(new Date().getTime());
		textMessage.setContent(content);
		return objectToXml(textMessage);
	}
	
	/**
	 * 根据request选择如何进行回复
	 * @param request
	 * @return
	 * @throws Exception
	 * @throws DocumentException
	 */
	public static String reply(HttpServletRequest request) throws Exception, DocumentException{
		Map<String, String> map = XmlToMap(request.getInputStream());
		String fromUserName = map.get("FromUserName");
		String toUserName = map.get("ToUserName");
		String msgType = map.get("MsgType");
		String content = map.get("Content");
		
		String message = null;
		if(MessageUtil.MESSAGE_TEXT.equals(msgType)){
			if("?".equals(content)||"？".equals(content)){
				message = textToXml(fromUserName, toUserName, menuText());
			}else if(content.startsWith("翻译")){
				content = content.substring(2);
				JSONObject jsonObject = JSONArray.fromObject(JSONObject.fromObject(TransApi.getTransResult(content, "zh", "en"))
						.getString("trans_result")).getJSONObject(0);
				String src = jsonObject.getString("src");
				String dst = jsonObject.getString("dst");
				message = "原文："+src+"\r\n"+"译文："+dst;
				message = textToXml(fromUserName, toUserName, message);
			}
			else{
				message = textToXml(fromUserName, toUserName, RobotApi.getRobot(content, fromUserName).getString("text"));
			}
		}else if(MESSAGE_EVENT.equals(msgType)){
			String event = map.get("Event");
			if(MESSAGE_SUBSCRIBE.equals(event)){
				message = textToXml(fromUserName, toUserName, menuText());
			}else if(MESSAGE_CLICK.equals(event)){
				if(map.get("EventKey").equals("33")){
					message = textToXml(fromUserName, toUserName, menuText());
				}else if(map.get("EventKey").equals("11")){
					message = textToXml(fromUserName, toUserName,reply_1());
				}else if(map.get("EventKey").equals("12")){
					message = textToXml(fromUserName, toUserName, reply_2());
				}else if(map.get("EventKey").equals("13")){
					message = createImageMessage(fromUserName, toUserName);
				}else if(map.get("EventKey").equals("14")){
					message = createNewsMessage(fromUserName, toUserName);
				}
			}else if(MESSAGE_VIEW.equals(event)){
				String url = map.get("EventKey");
				message = textToXml(fromUserName, toUserName, url);
			}else if(MESSAGE_SCANCODE_PUSH.equals(event)){
				String key = map.get("EventKey");
				message = textToXml(fromUserName, toUserName, "你好");
			}

			/**
			 * 开发文档中写的是MsgType	event
			 *			  Event	    location_select
			 *	而实际上是         MsgType   location
			 */
		}else if(MESSAGE_LOCATION.equals(msgType)){
			message = textToXml(fromUserName, toUserName, "您的当前位置是"+map.get("Label"));
		}
		return message;
	}
	
	/**
	 * 图文消息转换成xml字符串
	 * @param newsMessage
	 * @return
	 */
	public static String newsMessageToXml(NewsMessage newsMessage){
		 return objectToXml(newsMessage).replaceAll(News.class.getName(), "item");
	}	
	
	/**
	 * 生成一条图文消息的xml字符串
	 * @param toUserName
	 * @param fromUserName
	 * @return
	 */
	public static String createNewsMessage(String toUserName, String fromUserName){
		News news = new News();
		news.setDescription("ItCenter的目标是为及时推送新鲜的it资讯。");
		news.setPicUrl("http://jayshawn.vicp.io/weixin/image/ico.png");
		news.setTitle("ItCenter介绍");
		news.setUrl("http://jayshawn.vicp.io/weixin/");
		NewsMessage newsMessage = new NewsMessage();
		List<News> list = new ArrayList<>();
		list.add(news);
		newsMessage.setArticleCount(list.size());
		newsMessage.setArticles(list);
		newsMessage.setCreateTime(new Date().getTime());
		newsMessage.setMsgType(MessageUtil.MESSAGE_NEWS);
		newsMessage.setFromUserName(fromUserName);
		newsMessage.setToUserName(toUserName);
		return newsMessageToXml(newsMessage);
	}
	
	/**
	 * 创建ImageMessage
	 * @param toUserName
	 * @param fromUserName
	 * @return
	 */
	public static String createImageMessage(String toUserName, String fromUserName){
		String message = null;
		Image image = new Image();
		image.setMediaId("3fPjgX7bz5qHepd_oReNHpahNi8lQyAggZtAbvNYgp0iws0e_Lc_au6ttTU65Rh_");
		ImageMessage imageMessage = new ImageMessage();
		imageMessage.setCreateTime(System.currentTimeMillis());
		imageMessage.setFromUserName(fromUserName);
		imageMessage.setToUserName(toUserName);
		imageMessage.setMsgType(MESSAGE_IMAGE);
		imageMessage.setImage(image);
		message = objectToXml(imageMessage);
		return message;
		
	}
	
	/**
	 * 组装menu
	 * @return
	 */
	public static String creatMenu(){
		Menu menu = new Menu();

		ClickButton button11 = new ClickButton();
		button11.setName("公众号介绍");
		button11.setKey("11");
		button11.setType("click");
		
		ClickButton button12 = new ClickButton();
		button12.setName("微信介绍");
		button12.setKey("12");
		button12.setType("click");
		
		ClickButton button13 = new ClickButton();
		button13.setName("获取图片消息");
		button13.setKey("13");
		button13.setType("click");
		
		ClickButton button14 = new ClickButton();
		button14.setName("获取图文消息");
		button14.setKey("14");
		button14.setType("click");
		
		Button button1 = new Button();
		button1.setName("获取消息");
		button1.setSub_button(new Button[]{button11,button12,button13,button14});
		
		
		ViewButton button21 = new ViewButton();
		button21.setName("公众号主页");
		button21.setType("view");
		button21.setUrl("http://jayshawn.vicp.io/weixin");
		
		ViewButton button22 = new ViewButton();
		button22.setName("SDK测试");
		button22.setType("view");
		button22.setUrl("http://jayshawn.vicp.io/weixin/SDK");
		
		Button button2 = new Button();
		button2.setName("公众号");
		button2.setSub_button(new Button[]{button21,button22});
		
		ClickButton button31 = new ClickButton();
		button31.setName("扫一扫");
		button31.setKey("31");
		button31.setType("scancode_push");
		
		ClickButton button32 = new ClickButton();
		button32.setName("定位");
		button32.setKey("32");
		button32.setType("location_select");
		
		ClickButton button33 = new ClickButton();
		button33.setName("使用说明");
		button33.setType("click");
		button33.setKey("33");
		
		ViewButton button34 = new ViewButton();
		button34.setName("我的信息");
		button34.setType("view");
		button34.setUrl("http://jayshawn.vicp.io/weixin/GuideServlet");
		
		
		Button button3 = new Button();
		button3.setName("更多");
		button3.setSub_button(new Button[]{button31,button32,button33,button34});
		menu.setButton(new Button[]{button1,button2,button3});
		
		return JSONObject.fromObject(menu).toString();
	}
	
	
	
	

}
