package com.jayshawn.weixin.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.jayshawn.weixin.bean.UserInfo;
import com.jayshawn.weixin.bean.WebAccessToken;
import com.jayshawn.weixin.util.WeixinUtil;

import net.sf.json.JSONObject;

/**
 * Servlet implementation class AuthServlet
 */
public class AuthServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AuthServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String code = request.getParameter("code");
		if(code!=null){
			String url = "https://api.weixin.qq.com/sns/oauth2/access_token?"
					+ "appid=" + WeixinUtil.AppID + "&secret="
					+ WeixinUtil.AppSecret
					+ "&code=" + code
					+ "&grant_type=authorization_code";
			JSONObject jsonObject = WeixinUtil.doGet(url);
			String openid = jsonObject.getString("openid");
			String access_token = jsonObject.getString("access_token");
			String url2 =  "https://api.weixin.qq.com/sns/userinfo?access_token="
		            + access_token
		            + "&openid="
		            + openid + "&lang=zh_CN";
			jsonObject = WeixinUtil.doGet(url2);
			Gson gson = new Gson();
			UserInfo userInfo = new UserInfo();
			userInfo = gson.fromJson(new String(jsonObject.toString().getBytes(), "UTF-8"), UserInfo.class);
			
			request.setAttribute("userInfo", userInfo);
			request.getRequestDispatcher("userinfo.jsp").forward(request, response);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
