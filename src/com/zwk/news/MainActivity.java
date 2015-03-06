package com.zwk.news;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.List;
import java.util.Map;

import com.baidu.frontia.Frontia;
import com.zwk.feedback.About;
import com.zwk.feedback.Feedback;
import com.zwk.login.Login;
import com.zwk.login.Loginoff;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.KITKAT)
@SuppressLint({ "HandlerLeak", "InlinedApi", "CutPasteId" })
public class MainActivity extends Activity {
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	SharedPreferences Preferences;   

	private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence mDrawerTitle;
	private CharSequence mTitle;

	private List<Map<String, String>> dataList;
	private SimpleAdapter adapter = null;
	private Handler handler;

	private String url = "http://weixinzwk.sinaapp.com/mobile/list.php";
	private String title = "头条热点";

	@TargetApi(Build.VERSION_CODES.KITKAT)
	@SuppressLint({ "InlinedApi", "CutPasteId" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		Preferences = getSharedPreferences("news_login", MODE_PRIVATE);

		// 沉浸模式设置开始************************************************************
		if (android.os.Build.VERSION.SDK_INT > 18) {
			Window window = getWindow();
			window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.setFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
			// 设置根布局的内边距

			DrawerLayout layout = (DrawerLayout) findViewById(R.id.drawer_layout);
			layout.setPadding(0, getActionBarHeight() + getStatusBarHeight(),
					0, 0);
			ListView layout1 = (ListView) findViewById(R.id.left_drawer);
			layout1.setPadding(0, getActionBarHeight() + getStatusBarHeight(),
					0, navigationBarHeight());
		} else {
			getOverflowMenu();
		}
		// 沉浸模式设置结束************************************************************

		mTitle = mDrawerTitle = getTitle();
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);

		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					adapter.notifyDataSetChanged(); // 发送消息通知ListView更新
					mDrawerList.setAdapter(adapter); // 重新设置ListView的数据适配器
					break;
				default:

					break;
				}
			}
		};
		mThreadLoadApps.start();
		selectItem(url, title, 1);     //初始化界面
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(false);
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);   //设置actionbar一些状态

	}

	Thread mThreadLoadApps = new Thread() {
		@Override
		public void run() {
			DownloadData();
			handler.sendEmptyMessage(0);
		}

	};
	/**
	 * 下载新闻分类数据
	 */
	protected void DownloadData() {

		try {
			URL lin = new URL(
					"http://weixinzwk.sinaapp.com/mobile/typelist.php");
			InputStream in = lin.openStream();
			dataList = Typelist.getTypeItem(in);
			in.close();
			adapter = new SimpleAdapter(this, dataList, R.layout.group_item,
					new String[] { "TITLE" }, new int[] { R.id.textView1 });

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	/**
	 * 分类列表点击事件
	 * @author ZhangWenKang
	 *
	 */
	
	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			Map<String, String> map = (Map<String, String>) dataList.get(arg2);
			url = map.get("LINK");
			title = map.get("TITLE");
			System.err.println(title + ":" + url);
			selectItem(url, title, arg2);
		}
	}
	//更新中间布局，跳转。
	private void selectItem(String url, String title, int position) { // 替换界面
		Fragment fragment = new NewsFragment();
		Bundle args = new Bundle();
		args.putString("URL", url);
		args.putString("TITLE", title);
		fragment.setArguments(args);
		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.content_frame, fragment).commit();
		fragmentManager.beginTransaction().commitAllowingStateLoss();
		mDrawerList.setItemChecked(position, true);
		setTitle(title);
		mDrawerLayout.closeDrawer(mDrawerList);

	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		switch (item.getItemId()) {
		case R.id.item1:

			if (getLoginstate() == true) {
				Intent intent = new Intent(this, Loginoff.class); //跳转登录信息。
				startActivity(intent);
				System.err
						.println("已登录——————————————————————————————————————————————————————————————————");
				break;
			} else {
				Intent intent = new Intent(this, Login.class);
				startActivity(intent);
				System.err
						.println("未登录——————————————————————————————————————————————————————————————————");
				break;
			}

		case R.id.item2:
			Intent intent1 = new Intent(this,// 跳转到反馈
					Feedback.class);
			startActivity(intent1);
			break;
		case R.id.item3:
			Intent intent2 = new Intent(this,// 跳转到关于
					About.class);
			startActivity(intent2);
			break;

		default:
			break;
		}

		return super.onOptionsItemSelected(item);

	}

	// 状态栏高度修改**********************************************************
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
		if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))// 如果资源是存在的、有效的
		{
			actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,
					getResources().getDisplayMetrics());
		}
		return actionBarHeight;
	}

	// 获取虚拟按键高度修改
	private int navigationBarHeight() {
		return Resources.getSystem().getDimensionPixelSize(
				Resources.getSystem().getIdentifier("navigation_bar_height",
						"dimen", "android"));
	}

	// 高度修改**********************************************************

	
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);

	}

	// 解决安卓4.4以下机型的actionbar按钮问题
	private void getOverflowMenu() {
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");
			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 验证是否已经登录
	private boolean getLoginstate() {

		return Preferences.getBoolean("islogin", false);

	}

	// 退出事件
	long previous = 0;

	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == 4)
			if (System.currentTimeMillis() - previous > 2000) {
				Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
				previous = System.currentTimeMillis();
			} else {

				System.exit(0);
			}
		return true;

	}

}