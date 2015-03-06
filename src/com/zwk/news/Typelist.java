package com.zwk.news;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

public class Typelist {

	public static List<Map<String, String>> getTypeItem(InputStream is)
			throws Exception {
		List<TypeItem> list = null;
		TypeItem item = null;
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		byte[] b = new byte[1024];
		int len = 0;
		while ((len = is.read(b)) != -1) {
			bout.write(b, 0, len);
		}
		byte[] newbs = bout.toByteArray();
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(new ByteArrayInputStream(newbs), "utf-8");

		int event = parser.getEventType();// 产生第一个事件
		list = new ArrayList<TypeItem>();
		while (event != XmlPullParser.END_DOCUMENT) {
			switch (event) {
			case XmlPullParser.START_DOCUMENT:// 判断当前事件是否是文档开始事件

				break;
			case XmlPullParser.START_TAG:// 判断当前事件是否是标签元素开始事件
				if ("item".equals(parser.getName())) {
					item = new TypeItem();
				}
				if (item != null) {
					if ("id".equals(parser.getName())) {
						String id = parser.nextText();
						item.setId(id);
						String link = "http://weixinzwk.sinaapp.com/mobile/list.php?id="
								+ id;
						System.err.println(link);
					} else if ("type".equals(parser.getName())) {
						String type = parser.nextText();
						item.setType(type);
						System.err.println(type);
					}

				}
				break;
			case XmlPullParser.END_TAG:// 判断当前事件是否是标签元素结束事件
				if ("item".equals(parser.getName())) {
					list.add(item);
					item = null;
				}
				break;
			}
			event = parser.next();// 进入下一个元素并触发相应事件
		}

		int size = list.size();
		List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();

		for (int i = 0; i < size; i++) {

			System.err.println(i + "____---------1111111111111");
			TypeItem type = list.get(i);
			// String map = "map"+i;
			Map<String, String> map = new HashMap<String, String>();
			map.put("TITLE", type.getType());
			System.err.println(type.getType());
			map.put("LINK","http://weixinzwk.sinaapp.com/mobile/list.php");
			System.err.println("http://weixinzwk.sinaapp.com/mobile/list.php?="
					+ type.getId());
			dataList.add(map);
			map.entrySet();
			System.err.println("Typelist___finish");

		}

		System.err.println();
		return dataList;
	}

}
