package com.zwk.login;

import java.lang.reflect.Field;

import com.zwk.news.MainActivity;
import com.zwk.news.R;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

@TargetApi(Build.VERSION_CODES.KITKAT)
@SuppressLint("InlinedApi")
public class Loginoff extends Activity {
	private TextView loginstate;
	private Button loginoff;
	SharedPreferences Preferences;
	SharedPreferences.Editor editor;

	@TargetApi(Build.VERSION_CODES.KITKAT)
	@SuppressLint("InlinedApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loginoff);
		Preferences = getSharedPreferences("news_login", MODE_PRIVATE);
		editor = Preferences.edit();
		// 沉浸模式设置************************************************************
		if (android.os.Build.VERSION.SDK_INT > 18) {
			Window window = getWindow();
			window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.setFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
			LinearLayout linear = (LinearLayout) findViewById(R.id.loginoff);
			linear.setPadding(0, getActionBarHeight() + getStatusBarHeight(),
					0, 0);
			// 设置根布局的内边距
		}
		// 沉浸模式设置************************************************************

		loginstate = (TextView) findViewById(R.id.loginstate);
		loginoff = (Button) findViewById(R.id.loginoff_btu);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setTitle("注销");
		loginstate.setText("您已经登录");
		loginoff.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				editor.clear();
				editor.commit();
				Intent intent = new Intent(getApplicationContext(), MainActivity.class);
				startActivity(intent);

			}
		});

	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
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

}
