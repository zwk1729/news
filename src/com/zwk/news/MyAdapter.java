package com.zwk.news;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter {
	private Context context;
	private List<Map<String, String>> dataList;

	public MyAdapter(Context context, List<Map<String, String>> dataList) {
		this.context = context;
		this.dataList = dataList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return dataList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return dataList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View v, ViewGroup arg2) {
		if (v == null) {
			v = View.inflate(context, R.layout.group_item, null);
		}
		TextView tv = (TextView) v.findViewById(R.id.textView1);
		View line_view = v.findViewById(R.id.line_view);
		Map<String, String> map = dataList.get(position);
		tv.setText(map.get("TITLE"));
		if (position == dataList.size() - 1) {
			line_view.setVisibility(View.GONE);
		}
		if (position == 0) {
			line_view.setVisibility(View.VISIBLE);
		}
		return v;
	}

}
