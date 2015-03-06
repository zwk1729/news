package com.zwk.news;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.List;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

@SuppressLint("InlinedApi")
@TargetApi(Build.VERSION_CODES.KITKAT)
public class NewsFragment extends Fragment {

	private ListView listView;
	private Handler handler = new Handler();
	private ProgressDialog progressDialog = null;
	private String url = null;
	private String type = null;
	List<NewsItem> dataList = null;
	private MyAdapter adapter = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle b = getArguments();
		url = b.getString("URL");
		type = b.getString("TITLE");

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.news_list, container, false);

		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		initView(view);
		loaderData();

	}

	public void loaderData() {

		progressDialog = ProgressDialog.show(getActivity(), "请稍等...", "加载中...",
				true);
		progressDialog.setCancelable(true);
		new Thread() {
			@Override
			public void run() {
				try {

					URL lin = new URL(url);
					InputStream in = lin.openStream();

					dataList = PullParseUtils.getNewsItem(in, type);
					progressDialog.dismiss();
					in.close();
					handler.post(buildUpdateViewRunnable());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();

	}
	//获取状态栏高度
	public int getStatusBarHeight() {
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, statusBarHeight = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = getResources().getDimensionPixelSize(x);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return statusBarHeight;
	}

	// 获取ActionBar的高度
	public int getActionBarHeight() {
		TypedValue tv = new TypedValue();
		int actionBarHeight = 0;
		if (getActivity().getTheme().resolveAttribute(
				android.R.attr.actionBarSize, tv, true))// 如果资源是存在的、有效的
		{
			actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,
					getResources().getDisplayMetrics());
		}
		return actionBarHeight;
	}

	@TargetApi(Build.VERSION_CODES.KITKAT)
	@SuppressLint({ "InlinedApi", "CutPasteId" })
	private void initView(View view) {
		listView = (ListView) view.findViewById(R.id.common_list_view);
		if (android.os.Build.VERSION.SDK_INT > 18) {
			Window window = getActivity().getWindow();
			window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.setFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
			// 设置根布局的内边距
			ListView layout1 = (ListView) view
					.findViewById(R.id.common_list_view);
			layout1.setPadding(0, getActionBarHeight() + getStatusBarHeight(),
					0, 0);

		}

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, // item

					long arg3) {
				NewsItem item = dataList.get(arg2);
				Intent intent = new Intent(getActivity(),// 跳转到详细新闻
						NewDetailActivity.class);
				intent.putExtra("guid", item.getGuid());
				startActivity(intent);

			}
		});
	}

	public Runnable buildUpdateViewRunnable() {
		return new Runnable() {
			public void run() {
				adapter = new MyAdapter(getActivity(), dataList);
				listView.setAdapter(adapter);
				adapter.notifyDataSetChanged();
			}
		};
	}

	class MyAdapter extends BaseAdapter {
		List<NewsItem> dataList = null;
		Activity context = null;

		public MyAdapter(Activity context, List<NewsItem> dataList) {
			this.context = context;
			this.dataList = dataList;
		}

		@Override
		public int getCount() {
			return dataList.size();
		}

		@Override
		public Object getItem(int arg0) {
			return dataList.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View v, ViewGroup parent) { // item添加内容
			HolderView holder = null;
			if (v == null) {
				v = View.inflate(context, R.layout.news_item, null);
				holder = new HolderView();
				holder.titleTv = (TextView) v.findViewById(R.id.new_title);
				holder.pushDateTv = (TextView) v
						.findViewById(R.id.new_pushdate);
				holder.descTv = (TextView) v.findViewById(R.id.new_desc);
				v.setTag(holder);
			} else {
				holder = (HolderView) v.getTag();
			}
			NewsItem item = dataList.get(position);

			holder.titleTv.setText(Html.fromHtml(item.getTitle()));
			holder.pushDateTv.setText(item.getPubDate());
			String desc = item.getDescription();
			holder.descTv.setText(desc);

			return v;
		}

	}

	static class HolderView {
		TextView titleTv;
		TextView pushDateTv;
		TextView descTv;
	}

}
