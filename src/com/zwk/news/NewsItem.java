package com.zwk.news;

public class NewsItem {

	private String title;// 新闻标题
	private String type;// 新闻标题
	private String link;// rss链接
	private String pubDate;// 发布日期
	private String guid;// 普通链接
	private String description;// 描述

	public NewsItem() {
		super();
	}


	public NewsItem(String title, String type, String link, String pubDate,
			String guid, String description) {
		super();
		this.title = title;
		this.type = type;
		this.link = link;
		this.pubDate = pubDate;
		this.guid = guid;
		this.description = description;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getPubDate() {
		return pubDate;
	}

	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}