package com.zwk.login;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.zwk.news.R;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.KITKAT)
@SuppressLint("InlinedApi")
public class Login extends Activity {
	/* MyDatabaseHelper dbHelper; */
	private Button login;
	private EditText username;
	private EditText password;
	private TextView loginmsg;
	private ProgressDialog progressDialog = null;
	private Handler handler;
	Thread loginthread;
	SharedPreferences Preferences;
	SharedPreferences.Editor editor;

	@TargetApi(Build.VERSION_CODES.KITKAT)
	@SuppressLint("InlinedApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		Preferences =getSharedPreferences("news_login", MODE_PRIVATE);
		editor = Preferences.edit();
		
		
		// 沉浸模式设置************************************************************
		if (android.os.Build.VERSION.SDK_INT > 18) {
			Window window = getWindow();
			window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.setFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
			LinearLayout linear = (LinearLayout) findViewById(R.id.denglu);
			linear.setPadding(0, getActionBarHeight() + getStatusBarHeight(),
					0, 0);
			// 设置根布局的内边距
		}
		// 沉浸模式设置************************************************************
		login = (Button) findViewById(R.id.signin_button);
		username = (EditText) findViewById(R.id.username_edit);
		password = (EditText) findViewById(R.id.password_edit);
		loginmsg = (TextView) findViewById(R.id.loginmsg);
		login.setOnClickListener(new LoginListener());
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setTitle("登录");

	}

	class LoginListener implements OnClickListener {

		@SuppressLint("HandlerLeak")
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			final String name = username.getText().toString();
			final String pwd = password.getText().toString();

			if (!name.equals("")) {
				if (!pwd.equals("")) {
					handler = new Handler() {

						public void handleMessage(Message msg) {
							switch (msg.what) {
							case 0:
								loginmsg.setText("登录失败，用户名或密码不正确");
								break;
							case 1:
								System.err
										.println("1111111118888888888888888888889");
								editor.putString("name", name);
								editor.putString("pwd", pwd);
								editor.putBoolean("islogin", true);
								editor.commit();
								finish();
								break;
							default:
								// do something
								break;
							}
						}
					};
					loginpost(name, pwd);
					loginthread.start();
					System.err.println("111111111111111111111111111111111");
				} else {
					Toast.makeText(getApplicationContext(), "请输入密码",
							Toast.LENGTH_SHORT).show();
				}

			} else {
				Toast.makeText(getApplicationContext(), "请输入帐号",
						Toast.LENGTH_SHORT).show();

			}

		}

	}

	public void Register(View view) {

		Intent intent = new Intent();
		intent.setClass(Login.this, Register.class);
		startActivity(intent);

	}

	public void loginpost(final String name, final String pwd) {
		// TODO Auto-generated method stub
		progressDialog = ProgressDialog.show(this, "请稍等...", "登录中...", true);
		progressDialog.setCancelable(true);

		loginthread = new Thread() {
			@Override
			public void run() {

				String url = "http://1.weixinzwk.sinaapp.com/user/login.php";	
				HttpPost httpRequest = new HttpPost(url);
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("username", name));
				params.add(new BasicNameValuePair("password", pwd));
				try {
					httpRequest.setEntity(new UrlEncodedFormEntity(params,
							HTTP.UTF_8));
					HttpClient httpClient = new DefaultHttpClient();
					HttpResponse httpResponse = httpClient.execute(httpRequest);

					String result = EntityUtils.toString(httpResponse
							.getEntity());
					System.err.println(result);
					progressDialog.dismiss();
					if (result.equals("login_success")) {
						handler.sendEmptyMessage(1);
					} else {
						handler.sendEmptyMessage(0);
					}
					if (httpResponse.getStatusLine().getStatusCode() == 200) {

					} else {
						System.out.println("连接失败，原因未知。");
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		};

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