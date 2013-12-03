package beauty.android.activity;

import com.google.gson.Gson;

import twitter4j.Twitter;
import twitter4j.http.AccessToken;
import twitter4j.http.RequestToken;
import beauty.web.action.service.msg.BaseMsg;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.webkit.*;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewActivity extends CommonActivity {

	private WebView webView;

	private Twitter twitter;
	private RequestToken requestToken;

	private final static String URL = "x-oauthflow-twitter://callback";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);
		webView = (WebView) findViewById(R.id.webView1);
		
		intent = new Intent();
		intent.setClass(WebViewActivity.this, MyBeautyActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {

				Log.w(getTag(), "load: " + url);

				if (url.contains(URL)) {
					Uri uri = Uri.parse(url.substring(url.indexOf(URL)));
					String verifier = uri.getQueryParameter("oauth_verifier");
					AccessToken accessToken;
					try {
						accessToken = twitter.getOAuthAccessToken(requestToken,
								verifier);

						String token = accessToken.getToken();
						String secret = accessToken.getTokenSecret();
						String name = accessToken.getScreenName();
						int id = accessToken.getUserId();

						beauty.setToken(token);
						beauty.setSecret(secret);
						beauty.setUserName(name);
						beauty.setUserId(id);
						beauty.setLogined(true);

						Log.w(getTag(), name + ", " + id);
						new AddUser().execute(String.valueOf(id), name);
						view.stopLoading();
					} catch (Exception e) {
						view.stopLoading();
						startActivity(intent);
					} 
				} else {
					view.loadUrl(url);
				} 
				return true;
				
			}
		});

		String url = this.getIntent().getStringExtra("url");
		twitter = (Twitter) this.getIntent().getSerializableExtra("twitter");
		requestToken = (RequestToken) this.getIntent().getSerializableExtra(
				"requestToken");
		//
		webView.clearCache(true);
		//
		CookieSyncManager.createInstance(this); 
	    CookieManager cookieManager = CookieManager.getInstance();
	    cookieManager.removeAllCookie();
	    //
	    webView.getSettings().setSavePassword(false);
	    //
		webView.loadUrl(url);
	}

	@Override
	public String getTag() {
		return "webview";
	}

	class AddUser extends AsyncTask<String, String, BaseMsg> {

		private static final String addUrl = CommonActivity.host
				+ "adduser.d?id=%s&name=%s";

		@Override
		protected BaseMsg doInBackground(String... arg0) {
			BaseMsg bmsg = new BaseMsg();
			try {
				return new Gson()
						.fromJson(getJsonFromGet(String.format(addUrl, arg0[0],
								arg0[1])), BaseMsg.class);
			} catch (Exception e) {
				bmsg.addError(e.getMessage());
			}
			return null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			displayProgressDialog();
		}

		@Override
		protected void onPostExecute(BaseMsg result) {
			super.onPostExecute(result);
			progressDialog.dismiss();

			if (result.getErrors() != null && result.getErrors().size() > 0) {
				displayError(getTag(), parseError(result.getErrors()));
			}
			startActivity(intent);
		}
	}
}