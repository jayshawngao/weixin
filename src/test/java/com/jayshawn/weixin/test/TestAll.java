package com.jayshawn.weixin.test;

import static org.junit.Assert.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.junit.After;
import org.junit.Test;

import com.jayshawn.weixin.bean.AccessToken;
import com.jayshawn.weixin.bean.Image;
import com.jayshawn.weixin.bean.ImageMessage;
import com.jayshawn.weixin.bean.News;
import com.jayshawn.weixin.bean.NewsMessage;
import com.jayshawn.weixin.robot.RobotApi;
import com.jayshawn.weixin.translate.TransApi;
import com.jayshawn.weixin.util.MessageUtil;
import com.jayshawn.weixin.util.WeixinUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class TestAll {

	@Test
	public void testNewsMessageToXml() {
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
		newsMessage.setFromUserName("123");
		newsMessage.setToUserName("456");
		System.out.println(MessageUtil.newsMessageToXml(newsMessage));
	}
	
	@Test
	public void testGetAccessToken() throws NumberFormatException, IOException{
		System.out.println(WeixinUtil.getAccessToken());
	}
	
	@Test
	public void testUpload() throws IOException{
		String path = "D:\\workspace\\weixin\\src\\main\\webapp\\image\\ico.png";
		System.out.println(WeixinUtil.upload(path, "image"));
	}
	
	
	@Test
	public void testUploadMenu() throws NumberFormatException, IOException{
//		System.out.println(MessageUtil.creatMenu());
		System.out.println(WeixinUtil.uploadMenu(MessageUtil.creatMenu()));
	}
	
	@Test
	public void testGetTransResult() throws UnsupportedEncodingException{
//		System.out.println(TransApi.getTransResult("你好", "zh", "en"));
//		System.out.println(TransApi.encode("abcd123"));
		String string = "{\"from\":\"zh\",\"to\":\"en\",\"trans_result\":[{\"src\":\"你好\",\"dst\":\"Hello,\"}]}";
		System.out.println(JSONArray.fromObject(JSONObject.fromObject(string).getString("trans_result")).getJSONObject(0).get("src"));
	}
	
	@Test
	public void testGetRobot(){
		System.out.println(RobotApi.getRobot("小狗的图片", "123"));
	}
	
	@Test
	public void test(){
		System.out.println(System.currentTimeMillis());
	}
	

	


}
