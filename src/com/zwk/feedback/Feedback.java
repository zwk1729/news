package com.zwk.feedback;

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
public class Feedback extends Activity {
	private Button post;
	private EditText content;
	private EditText contact;
	private TextView feedbackmsg;
	private ProgressDialog progressDialog = null;
	String posturl = "http://weixinzwk.sinaapp.com/mail/feedback.php";
	private Handler handler;
	Thread feedbackthread;

	@TargetApi(Build.VERSION_CODES.KITKAT)
	@SuppressLint("InlinedApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feedback);
		// 沉浸模式设置************************************************************
		if (android.os.Build.VERSION.SDK_INT > 18) {
			Window window = getWindow();
			window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.setFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
			// 设置根布局的内边距
			LinearLayout linear = (LinearLayout) findViewById(R.id.linearfeedback);
			linear.setPadding(0, getActionBarHeight() + getStatusBarHeight(),
					0, 0);

		}
		// 沉浸模式设置************************************************************
		post = (Button) findViewById(R.id.post_button);
		content = (EditText) findViewById(R.id.feedback);
		contact = (EditText) findViewById(R.id.contact);
		feedbackmsg = (TextView) findViewById(R.id.feedbackmsg);
		post.setOnClickListener(new PostListener());
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setTitle("反馈");
	}

	class PostListener implements OnClickListener {

		@SuppressLint("HandlerLeak")
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String ce = content.getText().toString();
			String ca = contact.getText().toString();

			if (!ce.equals("")) {
				if (!ca.equals("")) {
					handler = new Handler() {

						public void handleMessage(Message msg) {
							switch (msg.what) {
							case 1:
								finish();
								break;
							default:
								feedbackmsg
								.setText("反馈失败，请重联系zwk1729@foxmail.com.");
								break;
							}
						}
					};
					feedback(ce, ca);
					feedbackthread.start();
					System.err.println("222222222222222222");
				} else {
					Toast.makeText(getApplicationContext(), "不方便就随便写写吧。",
							Toast.LENGTH_SHORT).show();
				}

			} else {
				Toast.makeText(getApplicationContext(), "什么都不说不太好吧！",
						Toast.LENGTH_SHORT).show();
			}

		}

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

	public void feedback(final String ce, final String ca) {
		// TODO Auto-generated method stub
		progressDialog = ProgressDialog.show(this, "请稍等...", "反馈中...", true);
		progressDialog.setCancelable(true);
		feedbackthread = new Thread() {

			public void run() {

				HttpPost httpRequest = new HttpPost(posturl);
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("contact", ca));
				params.add(new BasicNameValuePair("text", ce));
				try {
					httpRequest.setEntity(new UrlEncodedFormEntity(params,
							HTTP.UTF_8));
					HttpClient httpClient = new DefaultHttpClient();
					HttpResponse httpResponse = httpClient.execute(httpRequest);

					String result = EntityUtils.toString(httpResponse
							.getEntity());
					System.err.println(result);
					progressDialog.dismiss();

					handler.sendEmptyMessage(1);

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
	// 沉浸模式设置************************************************************
	
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
	// 沉浸模式设置************************************************************
	

}
