package com.jayshawn.weixin.bean;

import java.util.List;

public class NewsMessage extends BaseMessage{
	
	private int ArticleCount;
	private List<News> Articles;
	
	public int getArticleCount() {
		return ArticleCount;
	}
	public void setArticleCount(int articleCount) {
		ArticleCount = articleCount;
	}
	public List<News> getArticles() {
		return Articles;
	}
	public void setArticles(List<News> articles) {
		Articles = articles;
	}
	public NewsMessage(String toUserName, String fromUserName, long createTime, String msgType, int articleCount, List<News> articles) {
		super(toUserName, fromUserName, createTime, msgType);
		ArticleCount = articleCount;
		Articles = articles;
	}
	public NewsMessage() {

	}
	
	
	
}
