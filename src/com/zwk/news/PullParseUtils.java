package com.zwk.news;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

public class PullParseUtils {
	public static List<NewsItem> getNewsItem(InputStream is, String type) throws Exception {
		List<NewsItem> list = null;
		NewsItem item = null;
		// String tagName = "";
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		byte[] b = new byte[1024];
		int len = 0;
		while ((len = is.read(b)) != -1) {
			bout.write(b, 0, len);
		}
		byte[] newbs = bout.toByteArray();
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(new ByteArrayInputStream(newbs), "utf-8");

		int event = parser.getEventType();// ������һ���¼�
		while (event != XmlPullParser.END_DOCUMENT) {
			switch (event) {
			case XmlPullParser.START_DOCUMENT:// �жϵ�ǰ�¼��Ƿ����ĵ���ʼ�¼�
				list = new ArrayList<NewsItem>();
				break;
			case XmlPullParser.START_TAG:// �жϵ�ǰ�¼��Ƿ��Ǳ�ǩԪ�ؿ�ʼ�¼�
				if ("item".equals(parser.getName())) {
					item = new NewsItem();
				}
				if (item != null) {
					if("type".equals(parser.getName())){
						item.setType(parser.nextText());
					}else if ("title".equals(parser.getName())) {
						item.setTitle(parser.nextText());
					} else if ("pubDate".equals(parser.getName())) {
						item.setPubDate(parser.nextText());
					} else if ("guid".equals(parser.getName())) {
						item.setGuid(parser.nextText());
					} else if ("description".equals(parser.getName())) {

						String desc = parser.nextText();
						String text = desc.substring(0, desc.indexOf("......"));
						item.setDescription(text);

					}
				}
				break;
			case XmlPullParser.END_TAG:// �жϵ�ǰ�¼��Ƿ��Ǳ�ǩԪ�ؽ����¼�
				if ("item".equals(parser.getName())) {
					if(item.getType().equals(type)){
						list.add(item);
					}
					
					item = null;
				}
				break;
			}
			event = parser.next();// ������һ��Ԫ�ز�������Ӧ�¼�
		}
		return list;
	}

}
