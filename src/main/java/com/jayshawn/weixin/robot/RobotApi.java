package com.jayshawn.weixin.robot;

import com.jayshawn.weixin.util.WeixinUtil;

import net.sf.json.JSONObject;

public class RobotApi {
	private static final String Robot_URL = "http://www.tuling123.com/openapi/api";
	private static final String APIkey = "118d225ce8a6488a8f56d2096a7e7c84";
	
	public static JSONObject getRobot(String content, String toUserName){
		String url = Robot_URL+"?key="+APIkey+"&info="+content+"&userid="+toUserName;
		return WeixinUtil.doGet(url);
	}
}
