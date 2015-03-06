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
public class Register extends Activity {
	private EditText username;
	private EditText usermail;
	private EditText password;
	private TextView regmsg;
	private Button reg;
	private ProgressDialog progressDialog = null;
	private Handler handler;
	Thread regthread;

	@TargetApi(Build.VERSION_CODES.KITKAT)
	@SuppressLint("InlinedApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		// 沉浸模式设置************************************************************
		if (android.os.Build.VERSION.SDK_INT > 18) {
			Window window = getWindow();
			window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.setFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
			LinearLayout linear = (LinearLayout) findViewById(R.id.register);
			linear.setPadding(0, getActionBarHeight() + getStatusBarHeight(),
					0, 0);
			// 设置根布局的内边距
		}
		// 沉浸模式设置************************************************************
		username = (EditText) findViewById(R.id.reg_username);
		usermail = (EditText) findViewById(R.id.reg_mail);
		password = (EditText) findViewById(R.id.reg_password);
		regmsg = (TextView) findViewById(R.id.registermsg);
		reg = (Button) findViewById(R.id.reg_button);
		reg.setOnClickListener(new RegListener());
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setTitle("注册");

	}

	class RegListener implements OnClickListener {

		@SuppressLint("HandlerLeak")
		@Override
		public void onClick(View arg0) {
			String name = username.getText().toString();
			String mail = usermail.getText().toString();
			String pwd = password.getText().toString();
			String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
			String PWD_REGEX = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,16}$";
			Boolean mr = mail.matches(EMAIL_REGEX);
			Boolean pr = pwd.matches(PWD_REGEX);

			if (!name.equals("")) {
				if (mr != false) {
					if (pr != false) {
						handler = new Handler() {

							public void handleMessage(Message msg) {
								switch (msg.what) {
								case 0:
									regmsg.setText("注册失败，原因未知，请联系反馈。");
									break;
								case 1:
									finish();
									break;
								case 2:
									regmsg.setText("注册失败，用户名已存在。");
									break;
								case 3:
									regmsg.setText("注册失败，邮箱已注册。");
									break;
								default:
									// do something
									break;
								}
							}
						};
						registerUser(name, mail, pwd);
						regthread.start();
					} else {
						Toast.makeText(getBaseContext(), "密码格式不正确",
								Toast.LENGTH_SHORT).show();
					}

				} else {
					Toast.makeText(getBaseContext(), "邮箱格式不正确",
							Toast.LENGTH_SHORT).show();
				}

			} else {
				Toast.makeText(getBaseContext(), "用户名不能为空", Toast.LENGTH_SHORT)
						.show();
			}

		}
	}

	public void registerUser(final String name, final String mail,
			final String pwd) {
		progressDialog = ProgressDialog.show(this, "请稍等...", "注册中...", true);
		progressDialog.setCancelable(true);

		regthread = new Thread() {

			@Override
			public void run() {
				String url = "http://1.weixinzwk.sinaapp.com/user/register.php";
				HttpPost httpRequest = new HttpPost(url);
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("username", name));
				params.add(new BasicNameValuePair("mail", mail));
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
					if (result.equals("reg_success")) {
						handler.sendEmptyMessage(1);
					} else if (result.equals("reg_faile")) {
						handler.sendEmptyMessage(0);
					} else if (result.equals("user_name_exist")) {
						handler.sendEmptyMessage(2);
					} else if (result.equals("email_exist")) {
						handler.sendEmptyMessage(3);
					}

					if (httpResponse.getStatusLine().getStatusCode() == 200) {

					} else {
						System.out
								.println("连接失败，原因未知。");
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
