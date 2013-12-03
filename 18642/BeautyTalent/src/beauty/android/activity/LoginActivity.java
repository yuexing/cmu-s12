package beauty.android.activity;

import com.google.gson.Gson;

import beauty.web.action.service.msg.BaseMsg;
import twitter4j.*;
import twitter4j.http.*;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View.OnClickListener;

public class LoginActivity extends CommonActivity implements OnClickListener {

	private Twitter twitter;
	private RequestToken requestToken;

	private final static String consumerKey = "Mg1Hz1dSsFlVPFfRph1wTg";
	private final static String consumeSecret = "OYi9iO315aruTDMYXr8nR4OlEdRJ5iRm1Qv3ohaLbI";

	private final static String URL_SCHEME = "x-oauthflow-twitter";
	private final static String URL = "x-oauthflow-twitter://callback";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mybeauty_login);

		this.parentControl(CommonActivity.MY);

		oAuthLogin();
	}

	@Override
	public String getTag() {
		return "login";
	}

	// ///////// helper functions

	/*
	 * - Creates object of Twitter and sets consumerKey and consumerSecret -
	 * Prepares the URL accordingly and opens the WebView for the user to
	 * provide sign-in details - When user finishes signing-in, WebView opens
	 * your activity back
	 */
	private void oAuthLogin() {
		try {
			twitter = new TwitterFactory().getInstance();
			twitter.setOAuthConsumer(consumerKey, consumeSecret);
			requestToken = twitter.getOAuthRequestToken(URL);
			String authUrl = requestToken.getAuthenticationURL();
			Log.w(this.getTag(), "authUrl: " + Uri.parse(authUrl));
			
			intent = new Intent();
			intent.putExtra("url", authUrl);
			intent.putExtra("twitter", twitter);
			intent.putExtra("requestToken", requestToken);
			intent.setClass(this, WebViewActivity.class);
			this.startActivity(intent);

//			this.startActivity(new Intent(Intent.ACTION_VIEW, Uri
//					.parse(authUrl)).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
//					| Intent.FLAG_ACTIVITY_NO_HISTORY
//					| Intent.FLAG_FROM_BACKGROUND));

		} catch (TwitterException e) {
			e.printStackTrace();
			this.displayError("Twitter Login", e.getMessage());
			Log.e(this.getTag(), e.getMessage());
		}
	}

	@Override
	public void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Uri uri = intent.getData();
		Log.d(this.getTag(), "onNewIntent: " + uri);

		if (uri != null && uri.getScheme().equals(URL_SCHEME)) {
			String verifier = uri.getQueryParameter("oauth_verifier");
			Log.d(this.getTag(), "verifier: " + verifier);
			AccessToken accessToken;
			try {
				accessToken = twitter.getOAuthAccessToken(requestToken,
						verifier);

				String token = accessToken.getToken();
				String secret = accessToken.getTokenSecret();
				String name = accessToken.getScreenName();
				int id = accessToken.getUserId();

				this.beauty.setToken(token);
				this.beauty.setSecret(secret);
				this.beauty.setUserName(name);
				this.beauty.setUserId(id);
				this.beauty.setLogined(true);

				Log.d(this.getTag(), name + ", " + id);

				new AddUser().execute(String.valueOf(id), name);
			} catch (TwitterException e) {
				Log.e(this.getTag(), "on NewIntent: ");
				e.printStackTrace();
			}
		}

		this.startActivity(new Intent(this, MyBeautyActivity.class)
				.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET));
	}

	public class AddUser extends AsyncTask<String, String, BaseMsg> {

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
		}
	}
}
