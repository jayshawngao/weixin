package com.jayshawn.weixin.util;

import static org.hamcrest.CoreMatchers.nullValue;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.dom4j.DocumentException;
import org.eclipse.jdt.internal.compiler.ast.ThisReference;
import org.omg.PortableServer.ServantActivator;

import com.google.gson.Gson;
import com.jayshawn.weixin.bean.AccessToken;
import com.jayshawn.weixin.bean.Ticket;
import com.jayshawn.weixin.bean.WebAccessToken;
import com.jayshawn.weixin.jssdk.JSConfig;

import net.sf.json.JSONObject;

public class WeixinUtil {
	
	public static final String AppID = 
//			"wx67acc30d4aec85f0"; 
			"wx2d7f1ecd14f6a404"; //接口测试号
	public static final String AppSecret = 
//			"dea1985971c336cf26ffc9ca05542a97"; 
			"16f60c3be58e25c376be5b010a3395ad";  //接口测试号
	public static final String ACESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
	
	public static final String UPLOAD_URL = "https://api.weixin.qq.com/cgi-bin/media/upload?access_token=ACCESS_TOKEN&type=TYPE";
	
	public static final String CREATE_MENU_URL = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";
	
	/**
	 * 发送get请求
	 * @param url
	 * @return
	 */
	public static JSONObject doGet(String url){
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		HttpGet httpGet = new HttpGet(url);
		JSONObject jsonObject = null;
		HttpResponse response;
		try {
			response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			if(entity!=null){
				String result = EntityUtils.toString(entity,"UTF-8");
				jsonObject = JSONObject.fromObject(result);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jsonObject;
	}
	
	/**
	 * 发送post请求
	 * @param url
	 * @param outStr
	 * @return
	 */
	public static JSONObject doPost(String url, String outStr){
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(new StringEntity(outStr, "UTF-8"));
		JSONObject jsonObject = null;
		HttpResponse response;
		try {
			response = httpClient.execute(httpPost);
			String result = EntityUtils.toString(response.getEntity(),"UTF-8");
			jsonObject = JSONObject.fromObject(result);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jsonObject;
	}
	
	/**
	 * 从微信后台获取access_token
	 * @return
	 */
	public static String doGetAccessToken(){
		String path = WeixinUtil.class.getClassLoader().getResource("access_token.xml").getPath();
		String res = "";
		String url = ACESS_TOKEN_URL.replace("APPID", AppID).replaceAll("APPSECRET", AppSecret);
		JSONObject jsonObject = doGet(url);
		AccessToken accessToken = new AccessToken();
		accessToken.setAccessToken(jsonObject.getString("access_token"));
		accessToken.setExpiresIn(jsonObject.getLong("expires_in"));
		accessToken.setTimestamp(new Date().getTime());
		res = accessToken.getAccessToken();
		String xml = MessageUtil.objectToXml(accessToken);
		FileWriter out = null;
		try {
			out = new FileWriter(path);
			out.write(xml);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return res;
	}
	
	/**
	 * 不需要保存web_access_token！
	 * 从微信后台获取web_access_token
	 * @param code
	 * @return
	 * @throws IOException
	 */
	@Deprecated
	public static String doGetWebAccessToken(String code, boolean refresh) throws IOException{
		String path = WeixinUtil.class.getClassLoader().getResource("web_access_token.xml").getPath();
        String result = null;
		String url = null;
		String REFRESH_TOKEN = null;
        if (!refresh) {
			url = "https://api.weixin.qq.com/sns/oauth2/access_token?"
					+ "appid=" + WeixinUtil.AppID + "&secret="
					+ WeixinUtil.AppSecret
					+ "&code=" + code
					+ "&grant_type=authorization_code";
		}else{
			InputStream in = new FileInputStream(path);
			Map<String, String> map = MessageUtil.XmlToMap(in);
			REFRESH_TOKEN = map.get("refresh_token");
			url = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid="
			+AppID
			+"&grant_type=refresh_token"
			+"&refresh_token="+
			REFRESH_TOKEN;
		}
        JSONObject json = WeixinUtil.doGet(url);
        WebAccessToken webAccessToken = new WebAccessToken();
        webAccessToken.setAccessToken(json.getString("access_token"));
        webAccessToken.setExpiresIn(json.getLong("expires_in"));
        webAccessToken.setRefreshToken(json.getString("refresh_token"));
        webAccessToken.setOpenid(json.getString("openid"));
        webAccessToken.setScope(json.getString("scope"));
        webAccessToken.setTimestamp(System.currentTimeMillis());
        result = webAccessToken.getAccessToken();
        String xml = MessageUtil.objectToXml(webAccessToken);
        FileWriter out = null;
        out = new FileWriter(path);
        out.write(xml);
        out.close();
        return result;
        
	}
	
	/**
	 * 根据access_token是否过期选择获取方式
	 * @return access_token
	 * @throws IOException 
	 * @throws NumberFormatException 
	 */
	public static String  getAccessToken() throws NumberFormatException, IOException{
		String path = WeixinUtil.class.getClassLoader().getResource("access_token.xml").getPath();
		String res = "";
		InputStream in = null;
		try {
			in = new FileInputStream(path);
			if(in.available()!=0){
				Map<String, String> map = MessageUtil.XmlToMap(in);
				long temp = new Date().getTime()-Long.valueOf(map.get("timestamp"));
				if(temp<Long.valueOf(map.get("expiresIn"))*1000){
					System.out.println("从本地文件获取access_token");
					res = map.get("accessToken");
				}else{
					System.out.println("由于access_token过期而发生请求");
					res = doGetAccessToken();
				}
			}else{
				System.out.println("由于本地不存在access_token而发生请求");
				res = doGetAccessToken();
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return res;
		
	}
	
	/**
	 * 不需要保存web_access_token！
	 * 根据web_access_token是否过期选择获取方式
	 * @param code
	 * @return
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	@Deprecated
	public static String getWebAccessToken(String code) throws NumberFormatException, IOException{
		String path = WeixinUtil.class.getClassLoader().getResource("web_access_token.xml").getPath();
		String res = "";
		InputStream in = null;
		in = new FileInputStream(path);
		if(in.available()!=0){
			Map<String, String> map = MessageUtil.XmlToMap(in);
			long temp = new Date().getTime()-Long.valueOf(map.get("timestamp"));
			if(temp<Long.valueOf(map.get("expiresIn"))*1000){
				System.out.println("从本地文件获取web_access_token");
				res = map.get("accessToken");
			}else{
				System.out.println("由于web_access_token过期而发生请求");
				res = doGetWebAccessToken(code,true);
			}
		}else{
			System.out.println("由于本地不存在web_access_token而发生请求");
			res = doGetWebAccessToken(code,false);
		}
		in.close();
		return res;
		
	}
	
	/**
	 * 向微信后台传输素材
	 * @param filePath
	 * @param type
	 * @return
	 * @throws IOException
	 */
	public static String upload(String filePath, String type) throws IOException{
		File file = new File(filePath);
		if (!file.exists()||!file.isFile()){
			throw new IOException("文件不存在");
		}
		String url = UPLOAD_URL.replace("ACCESS_TOKEN", getAccessToken()).replace("TYPE", type);
		URL urlObj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();
		con.setRequestMethod("POST");
		con.setDoInput(true);
		con.setDoOutput(true);
		con.setUseCaches(false);
		con.setRequestProperty("Connection", "Keep-Alive");
		con.setRequestProperty("Charset", "UTF-8");
		String BOUNDARY = "----------"+System.currentTimeMillis();
		con.setRequestProperty("Content-Type", "multipart/form-data;boundary="+BOUNDARY);
		StringBuilder sb = new StringBuilder();
		sb.append("--");
		sb.append(BOUNDARY);
		sb.append("\r\n");
		sb.append("Content-Disposition:form/data;name=\"file\";filename=\""+file.getName()+"\"\r\n");
		sb.append("Content-Type:application/octet-stream\r\n\r\n");
		byte[] head = sb.toString().getBytes("utf-8");
		OutputStream out = new DataOutputStream(con.getOutputStream());
		out.write(head);
		DataInputStream in = new DataInputStream(new FileInputStream(file));
		int bytes = 0;
		byte[] bufferOut = new byte[1024];
		while((bytes=in.read(bufferOut))!=-1){
			out.write(bufferOut,0,bytes);
		}
		in.close();
		byte[] foot = ("\r\n--"+BOUNDARY+"--\r\n").getBytes("utf-8");
		out.write(foot);
		out.flush();
		out.close();
		StringBuffer buffer = new StringBuffer();
		BufferedReader reader = null;
		String result = null;
		try {
			reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String line = null;
			while((line=reader.readLine())!=null){
				buffer.append(line);
			}
			if(result==null){
				result = buffer.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(reader!=null){
				reader.close();
			}
		}
		
		JSONObject jsonObject = JSONObject.fromObject(result);
		String mediaId = jsonObject.getString("media_id");
		return mediaId;
		
		
	}
	
	/**
	 * 向微信后台上传菜单
	 * @param menu
	 * @return
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public static int uploadMenu(String menu) throws NumberFormatException, IOException{
		int result = -1;
		String url = CREATE_MENU_URL.replace("ACCESS_TOKEN", getAccessToken());
		JSONObject jsonObject = doPost(url, menu);
		if(jsonObject!=null){
			result = jsonObject.getInt("errcode");
		}
		return result;
	}
	
	public static String getSDKSignature(String ticket,JSConfig jsConfig,String url) throws NumberFormatException, IOException{

		String temp = "jsapi_ticket="+ticket+"&noncestr="+jsConfig.getNonceStr()+"&timestamp="+jsConfig.getTimestamp()+"&url="+url;
		String signature = DigestUtils.sha1Hex(temp);
		return signature;
	}
	
	public static String doGetTicket() throws NumberFormatException, IOException{
		String path = WeixinUtil.class.getClassLoader().getResource("ticket.xml").getPath();
		String res = "";
		JSONObject jsonObject = doGet("https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token="+WeixinUtil.getAccessToken()+"&type=jsapi");
		Ticket ticket = new Ticket();
		ticket.setTicket(jsonObject.getString("ticket"));
		ticket.setExpiresIn(jsonObject.getLong("expires_in"));
		ticket.setTimestamp(System.currentTimeMillis());
		res = ticket.getTicket();
		String xml = MessageUtil.objectToXml(ticket);
		FileWriter out = null;
		try {
			out = new FileWriter(path);
			out.write(xml);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return res;
		
	}
	
	public static String getTicket() throws NumberFormatException, IOException{
		String path = WeixinUtil.class.getClassLoader().getResource("ticket.xml").getPath();
		String res = "";
		InputStream in = null;
		try {
			in = new FileInputStream(path);
			if(in.available()!=0){
				Map<String, String> map = MessageUtil.XmlToMap(in);
				long temp = new Date().getTime()-Long.valueOf(map.get("timestamp"));
				if(temp<Long.valueOf(map.get("expiresIn"))*1000){
					System.out.println("从本地文件获取ticket");
					res = map.get("ticket");
				}else{
					System.out.println("由于ticket过期而发生请求");
					res = doGetTicket();
				}
			}else{
				System.out.println("由于本地不存在ticket而发生请求");
				res = doGetTicket();
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return res;
	}
}
