package com.zwk.news;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;

@TargetApi(Build.VERSION_CODES.KITKAT)
@SuppressLint("InlinedApi")
public class NewDetailActivity extends Activity {
	private String urlStr = "";
	private WebView wv;
	private Handler handler;
	SharedPreferences Preferences;
	Thread webthread;
	String result;

	@TargetApi(Build.VERSION_CODES.KITKAT)
	@SuppressLint({ "SetJavaScriptEnabled", "InlinedApi", "HandlerLeak" })
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news);
		Preferences = getSharedPreferences("news_login", MODE_PRIVATE);

		if (android.os.Build.VERSION.SDK_INT > 18) {
			Window window = getWindow();
			window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.setFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}

		wv = (WebView) findViewById(R.id.news);
		WebSettings webSettings = wv.getSettings();
		webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN); // WebView自适应屏幕大小
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setLoadsImagesAutomatically(true);
		webSettings.setSupportMultipleWindows(true);
		webSettings.setUseWideViewPort(true);

		// webSettings.setBlockNetworkImage(true); //是否屏蔽图片

		urlStr = getIntent().getStringExtra("guid");

		System.err.println("网址：" + urlStr);

		handler = new Handler() {

			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:

					loadHtml(result);
					break;
				default:
					// do something
					break;
				}
			}
		};
		loaderData();
		boolean islogin = Preferences.getBoolean("islogin", false);
		if(islogin == true){
			webthread.start();
		}

		wv.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String urlStr) {
				// TODO Auto-generated method stub
				super.onPageFinished(view, urlStr);

				// 页面下载完毕,却不代表页面渲染完毕显示出来
				if (wv.getContentHeight() != 0) {

					// 这个时候网页才显示
				}
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				// 自身加载新链接,不做外部跳转
				view.loadUrl(url);
				return true;
			}

		});

	}

	// public int getStatusBarHeight() {
	// Class<?> c = null;
	// Object obj = null;
	// Field field = null;
	// int x = 0, statusBarHeight = 0;
	// try {
	// c = Class.forName("com.android.internal.R$dimen");
	// obj = c.newInstance();
	// field = c.getField("status_bar_height");
	// x = Integer.parseInt(field.get(obj).toString());
	// statusBarHeight = getResources().getDimensionPixelSize(x);
	// } catch (Exception e1) {
	// e1.printStackTrace();
	// }
	// return statusBarHeight;
	// }
	//
	// // 获取ActionBar的高度
	// public int getActionBarHeight() {
	// TypedValue tv = new TypedValue();
	// int actionBarHeight = 0;
	// if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv,
	// true))// 如果资源是存在的、有效的
	// {
	// actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,
	// getResources().getDisplayMetrics());
	// }
	// return actionBarHeight;
	// }

	private void loaderData() {

		final boolean islogin = Preferences.getBoolean("islogin", false);
		if (islogin == true) {

			final String username = Preferences.getString("name", null);
			final String password = Preferences.getString("pwd", null);
			System.err
					.println("88888888888888888888888888888888888888888888888888888888888888");
			webthread = new Thread() {
				public void run() {

					HttpPost httpRequest = new HttpPost(urlStr);
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("username", username));
					params.add(new BasicNameValuePair("password", password));
					try {
						httpRequest.setEntity(new UrlEncodedFormEntity(params,
								HTTP.UTF_8));
						HttpClient httpClient = new DefaultHttpClient();
						HttpResponse httpResponse = httpClient
								.execute(httpRequest);

						result = EntityUtils.toString(httpResponse.getEntity());

						String responseString = new String(result);
						result = new String(
								responseString.getBytes("ISO-8859-1"), "utf-8");
						System.err.println(result);
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

		} else {
			Map<String, String> map = new HashMap<String, String>();
			map.put("User-Agent", "Android");
			wv.loadUrl(urlStr, map);

		}

	}

	protected void loadHtml(String result) {
		// TODO Auto-generated method stub
		wv.loadDataWithBaseURL(null, result, "text/html", "utf-8", null);

	}

	@SuppressLint("SdCardPath")
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == 4) {
			wv.destroy();
			File cache = new File("/data/data/"
					+ getApplicationContext().getPackageName() + "/cache");
			File appcache = new File("/data/data/"
					+ getApplicationContext().getPackageName() + "/app_webview");
			clearCache(cache);
			clearCache(appcache);
			finish();
		}

		return true;

	}

	private void clearCache(File file) {

		if (file.exists()) {
			if (file.isFile()) {
				file.delete();
			} else if (file.isDirectory()) {
				File files[] = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					clearCache(files[i]);
				}
			}
			file.delete();
		} else {
			System.err.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		}
	}
}
