package com.jayshawn.weixin.bean;

public class AccessToken {
	
	/**
	 * 由于xstream包的toXML()方法存在漏洞，所以这里命名不使用下划线
	 */
	private String accessToken;
	private long expiresIn;
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
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	
		
}
