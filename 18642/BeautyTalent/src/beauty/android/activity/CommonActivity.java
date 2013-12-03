package beauty.android.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import twitter4j.*;

import beauty.android.service.GPSTracker;
import android.app.*;
import android.content.*;
import android.content.DialogInterface.OnCancelListener;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.*;
import android.os.*;
import android.provider.Settings;
import android.util.Log;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.widget.AdapterView.*;

/**
 * common activity
 * 
 */
public abstract class CommonActivity extends ListActivity implements
		OnItemClickListener, OnClickListener {

	public static enum Type {
		benefit, brand, category, tag, product, user, fav, none
	}

	public static final String host = "http://10.0.0.3:8080/Beauty-web/";
	// the application
	protected BeautyApplication beauty;

	public BeautyApplication getBeauty() {
		return beauty;
	}

	// the GPSTraker
	protected GPSTracker gps;

	/*
	 * Checks to see if the phone has access to internet.
	 */
	protected boolean checkConnection() {
		if (!isOnline()) {
			this.displayServerError();
			return false;
		} else {
			// gps
			gps = new GPSTracker(this);

			if (gps.canGetLocation()) {
				Log.d("My Location", "latitude:" + gps.getLat()
						+ ", longitude: " + gps.getLng());
			} else {
				if (!gps.isGPSEnabled() && !gps.isNetEnabled())
					showLocationDialog(R.string.location_error);
				else if (gps.isGPSEnabled())
					showLocationDialog(R.string.location_network);
				else if (gps.isNetEnabled())
					showLocationDialog(R.string.location_gps);
				return false;
			}
		}
		return true;
	}

	/*
	 * Checks for internet connection.
	 */
	private boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	/*
	 * Displays alert telling users that it can't connect to server.
	 */
	protected void displayServerError() {
		this.displayError("Alert", this.getString(R.string.server_error));
	}

	protected void displayError(String title, String msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		builder.setTitle(title);
		builder.setMessage(msg);
		builder.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.create().show();
	}

	protected void displayErrorWithListener(String title, String msg,
			DialogInterface.OnClickListener l) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		builder.setTitle(title);
		builder.setMessage(msg);
		builder.setPositiveButton(android.R.string.ok, l);
		builder.create().show();
	}

	/** ==============BEGAN (ItemId)================= **/
	public static final int INDEX = 1;
	public static final int SEARCH = 2;
	public static final int MORE = 3;
	public static final int RECORD = 4;
	public static final int ADVICE = 5;
	public static final int EXIT = 6;
	public static final int PRODUCT = 7;
	public static final int FAV = 8;
	public static final int MY = 9;
	public static boolean exit;

	protected String[] DATAS;
	protected byte[][] bits;

	public Intent intent = new Intent();
	/** ==============END(ItemId)================= **/

	/** ==============BEGAN(ProgressDialog)============= **/
	public ProgressDialog progressDialog = null;
	/** ==============END(ProgressDialog)============= **/

	/** ==============BEGAN(ImageView)============ **/
	protected ImageView imageViewIndex = null;
	protected ImageView imageViewProduct = null;
	protected ImageView imageViewSearch = null;
	protected ImageView imageViewMyBeauty = null;
	protected ImageView imageViewMore = null;

	protected LinearLayout layoutIndex = null;
	protected LinearLayout layoutProduct = null;
	protected LinearLayout layoutSearch = null;
	protected LinearLayout layoutMyBeauty = null;
	protected LinearLayout layoutMore = null;
	/** ==============END(ImageView)============ **/

	public ListView listViewAll = null;
	public TextView textViewTitle = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		beauty = (BeautyApplication) this.getApplication();

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.textViewTitle = (TextView) findViewById(R.id.title);
		if (textViewTitle != null)
			this.textViewTitle.setText(R.string.app_name);

		if (!isOnline()) {
			this.displayServerError();
			return;
		}
	}

	/**
	 * exit
	 */
	private void openQiutDialog() {
		new AlertDialog.Builder(this)
				.setTitle(this.getString(R.string.app_name))
				.setMessage(this.getString(R.string.are_you_sure))
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								if (CommonActivity.this instanceof HomeActivity) {
									// home
									System.exit(0);
									finish();
								} else {
									// go to home
									Intent intent = new Intent();
									exit = true;
									intent.setClass(CommonActivity.this,
											HomeActivity.class);
									// if there is one HomeActivity...
									intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
									startActivity(intent);
									finish();
								}
							}
						})
				.setNegativeButton(android.R.string.cancel,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {

							}
						}).show();
	}

	@Override
	public void onClick(View v) {
		if (v == layoutProduct) {
			imageViewProduct.setImageResource(R.drawable.menu_product_pressed);
			intent.setClass(this, ProductActivity.class);
			intent.putExtra("type", Type.none);
			startActivity(intent);
		} else if (v == layoutIndex) {
			imageViewIndex.setImageResource(R.drawable.menu_home_pressed);
			intent.setClass(this, HomeActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		} else if (v == layoutSearch) {
			imageViewSearch.setImageResource(R.drawable.menu_search_pressed);
			startActivity(new Intent(this, SearchInvoke.class));
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		} else if (v == layoutMyBeauty) {
			imageViewMyBeauty
					.setImageResource(R.drawable.menu_my_beauty_pressed);
			intent.setClass(this, MyBeautyActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		} else if (v == layoutMore) {
			imageViewMore.setImageResource(R.drawable.menu_more_pressed);
			intent.setClass(this, MoreActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
	}

	/**
	 * CommonActivity bottom
	 */
	protected void parentControl(int index) {
		layoutIndex = (LinearLayout) findViewById(R.id.menu_home);
		imageViewIndex = (ImageView) findViewById(R.id.menu_home_img);
		layoutIndex.setOnClickListener(this);
		if (index == INDEX)
			imageViewIndex.setImageResource(R.drawable.menu_home_pressed);

		layoutProduct = (LinearLayout) findViewById(R.id.menu_product);
		imageViewProduct = (ImageView) findViewById(R.id.menu_product_img);
		layoutProduct.setOnClickListener(this);
		if (index == PRODUCT)
			imageViewProduct.setImageResource(R.drawable.menu_product_pressed);

		layoutSearch = (LinearLayout) findViewById(R.id.menu_search);
		imageViewSearch = (ImageView) findViewById(R.id.menu_search_img);
		layoutSearch.setOnClickListener(this);
		if (index == SEARCH)
			imageViewSearch.setImageResource(R.drawable.menu_search_pressed);

		layoutMyBeauty = (LinearLayout) findViewById(R.id.menu_my_beauty);
		layoutMyBeauty.setOnClickListener(this);
		imageViewMyBeauty = (ImageView) findViewById(R.id.menu_my_beauty_img);
		if (index == MY)
			imageViewMyBeauty
					.setImageResource(R.drawable.menu_my_beauty_pressed);

		layoutMore = (LinearLayout) findViewById(R.id.menu_more);
		imageViewMore = (ImageView) findViewById(R.id.menu_more_img);
		layoutMore.setOnClickListener(this);
		if (index == MORE)
			imageViewMore.setImageResource(R.drawable.menu_more_pressed);

		this.listViewAll = (ListView) findViewById(android.R.id.list);

		if (this.listViewAll != null) {
			this.listViewAll.setOnItemClickListener(this);
		}
	}

	/**
	 * This build list for listview
	 * 
	 * @return
	 */
	protected List<Map<String, Object>> buildList() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		for (int i = 0; i < DATAS.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("text", DATAS[i]);
			map.put("img", R.drawable.toright_mark);
			map.put("img_pre", R.drawable.paopao);
			list.add(map);
		}

		return list;
	}

	/**
	 * used by brand, benefit, category, tag, comment
	 */
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		intent.setClass(this, ProductActivity.class);
		startActivity(intent);
	}

	/**
	 * in progress ...
	 */
	protected void displayProgressDialog() {
		progressDialog = android.app.ProgressDialog.show(this,
				this.getString(R.string.app_name),
				this.getString(R.string.in_progress), true);

		progressDialog.setOnCancelListener(new OnCancelListener() {
			public void onCancel(DialogInterface dialog) {
				onBackPressed();
			}
		});
		progressDialog.show();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			openQiutDialog();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/*
	 * Shows specific alert for whether both or either location service is
	 * disabled.
	 */
	protected void showLocationDialog(int resId) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		builder.setTitle(R.string.alert);
		builder.setMessage(resId);
		builder.setPositiveButton(R.string.setting_page,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Intent intent = new Intent();
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						startActivity(intent);
					}
				});
		builder.setNegativeButton(android.R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.create().show();
	}

	protected void setBtnListenerOrDisable(Button btn,
			Button.OnClickListener onClickListener, String intentName) {
		if (isIntentAvailable(this, intentName)) {
			btn.setOnClickListener(onClickListener);
		} else {
			btn.setClickable(false);
		}
	}

	/**
	 * Indicates whether the specified action can be used as an intent. This
	 * method queries the package manager for installed packages that can
	 * respond to an intent with the specified action. If no suitable package is
	 * found, this method returns false.
	 * http://android-developers.blogspot.com/2009/01/can-i-use-this-intent.html
	 * 
	 * @param context
	 *            The application's environment.
	 * @param action
	 *            The Intent action to check for availability.
	 * 
	 * @return True if an Intent with the specified action can be sent and
	 *         responded to, false otherwise.
	 */
	protected boolean isIntentAvailable(Context context, String action) {
		final PackageManager packageManager = context.getPackageManager();
		final Intent intent = new Intent(action);
		List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
				PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}

	public abstract String getTag();

	// // user related
	protected boolean isLogined() {
		return this.beauty.isLogined();
	}

	protected void logout() {
		this.beauty.setToken(null);
		this.beauty.setToken(null);
		this.beauty.setUserName(null);
		this.beauty.setUserId(-1);
		this.beauty.setLogined(false);
	}

	@SuppressWarnings("deprecation")
	protected void sendTweet(String msg) throws Exception {
		if (!this.beauty.isLogined())
			return;

		String token = this.beauty.getToken(), secret = this.beauty.getSecret();
		Twitter twitter = new TwitterFactory().getInstance();
		twitter.setOAuthAccessToken(token, secret);
		try {
			twitter.updateStatus(msg);
		} catch (TwitterException e) {
			e.printStackTrace();
			Log.d(this.getTag(), e.getMessage());
			throw e;
		}
	}

	protected String getJsonFromGet(String url) throws Exception {
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);
		HttpResponse resp = null;
		InputStream is = null;
		HttpEntity entity = null;
		try {
			resp = httpClient.execute(get);
			entity = resp.getEntity();
			is = entity.getContent();
			return readJson(is);
		} catch (Exception e) {
			Log.d(getTag(), e.getMessage());
			throw e;
		} finally {
			if(is != null)
				is.close();
			if(httpClient != null)
			httpClient.getConnectionManager().shutdown();
		}
	}

	protected String readJson(InputStream is) throws Exception{
		if(is == null)
			return "";
		StringBuilder sb = new StringBuilder();
		String line = null;
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		try {
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}
			is.close();
		} catch (IOException e) {
			throw new Exception(e.getMessage());
		}
		return sb.toString();
	}

	protected String parseError(List<String> errors) {
		StringBuilder sb = new StringBuilder();
		for (String e : errors) {
			sb.append(e + ", ");
		}
		return sb.toString();
	}
}
