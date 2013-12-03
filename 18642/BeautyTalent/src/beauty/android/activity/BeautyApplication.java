package beauty.android.activity;

import beauty.android.util.AsyncImageLoader;
import android.app.Application;

public class BeautyApplication extends Application {
	
	private AsyncImageLoader asyncImageLoader;
	
	// user access token
	private String token;
	private String secret;
	private String userName = "test";
	private int userId = 1;
	private boolean logined = false;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		asyncImageLoader = new AsyncImageLoader();
	}

	public AsyncImageLoader getAsyncImageLoader() {
		return asyncImageLoader;
	}

	public void setAsyncImageLoader(AsyncImageLoader asyncImageLoader) {
		this.asyncImageLoader = asyncImageLoader;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public boolean isLogined() {
		return logined;
	}

	public void setLogined(boolean logined) {
		this.logined = logined;
	}	
}
