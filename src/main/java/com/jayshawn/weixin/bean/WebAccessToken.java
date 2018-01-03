package com.jayshawn.weixin.bean;

/**
 * 网页授权access_token
 * @author Jayshawn
 * @date 2017年9月25日 下午10:35:09
 */
@Deprecated
public class WebAccessToken {

		private String accessToken;
		private long expiresIn;
		private String refreshToken;
		private String openid;
		private String scope;
		private long timestamp;
		public String getAccessToken() {
			return accessToken;
		}
		public void setAccessToken(String accessToken) {
			this.accessToken = accessToken;
		}
		public long getExpiresIn() {
			return expiresIn;
		}
		public void setExpiresIn(long expiresIn) {
			this.expiresIn = expiresIn;
		}
		public String getRefreshToken() {
			return refreshToken;
		}
		public void setRefreshToken(String refreshToken) {
			this.refreshToken = refreshToken;
		}
		public String getOpenid() {
			return openid;
		}
		public void setOpenid(String openid) {
			this.openid = openid;
		}
		public String getScope() {
			return scope;
		}
		public void setScope(String scope) {
			this.scope = scope;
		}
		public long getTimestamp() {
			return timestamp;
		}
		public void setTimestamp(long timestamp) {
			this.timestamp = timestamp;
		}

		
		
		

		
		
		
}
