package beauty.android.activity;

import beauty.android.msg.bean.MRetail;
import beauty.android.service.GPSTracker;
import beauty.android.util.*;
import beauty.web.action.service.msg.RetailMsg;

import com.google.android.maps.*;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.*;
import android.content.*;
import android.content.DialogInterface.OnCancelListener;
import android.net.*;
import android.os.*;
import android.provider.Settings;
import android.util.Log;

public class LocalRetailsActivity extends MapActivity {

	private MapView mapView;
	private MyLocationOverlay meOverlay;

	private static final String retailUrl = CommonActivity.host
			+ "getretails.d?pid=%s";

	private MRetail[] retails;

	private ProgressDialog progressDialog;

	// the GPSTraker
	protected GPSTracker gps;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_local_retails);

		if (!this.checkConnection()) {
			return;
		}

		mapView = (MapView) this.findViewById(R.id.mapView);
		mapView.setBuiltInZoomControls(true);

		new LoadRetails().execute(String.valueOf(this.getIntent().getIntExtra(
				"pid", -1)));

	}

	@Override
	public void onResume() {
		super.onResume();
		if (meOverlay != null)
			meOverlay.enableCompass();
	}

	@Override
	public void onPause() {
		super.onPause();
		if (meOverlay != null)
			meOverlay.disableCompass();
	}

	class LoadRetails extends AsyncTask<String, String, RetailMsg> {

		@Override
		protected RetailMsg doInBackground(String... arg0) {
			// send request
			Log.d("LocalRetail", "url: " + String.format(retailUrl, arg0[0]));

			// get msg
			RetailMsg rmsg = new Gson().fromJson(
					getJsonFromGet(String.format(retailUrl, arg0[0])),
					RetailMsg.class);

			return rmsg;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			displayProgressDialog();
		}

		@Override
		protected void onPostExecute(RetailMsg result) {
			super.onPostExecute(result);
			progressDialog.dismiss();

			if (result == null)
				return;

			if (result.getErrors() == null || result.getErrors().size() == 0) {
				retails = result.getRetails();
			} else {
				displayError("Load Retails", parseError(result.getErrors()));
				return;
			}

			List<Overlay> mapOverlays = mapView.getOverlays();
			GeoPoint geoPoint = null, userGeo = null;
			RetailItem item = null;

			NearbyOverlay aOverlay = null;
			// user
			userGeo = geoPoint = new GeoPoint((int) (gps.getLat() * 1E6),
					(int) (gps.getLng() * 1E6));
			aOverlay = new NearbyOverlay(LocalRetailsActivity.this
					.getResources().getDrawable(R.drawable.mark_red),
					LocalRetailsActivity.this);
			item = new RetailItem(geoPoint, "You are here", "Welcome to beauty");
			item.setRetail(null);
			aOverlay.addOverlay(item);
			mapOverlays.add(aOverlay);
			aOverlay.populateNow();

			// this may not work
			meOverlay = new MyLocationOverlay(LocalRetailsActivity.this,
					mapView);
			mapOverlays.add(meOverlay);

			int minLat = userGeo.getLatitudeE6(), maxLat = userGeo
					.getLatitudeE6();
			int minLng = userGeo.getLongitudeE6(), maxLng = userGeo
					.getLongitudeE6();

			// retails
			if (retails != null && retails.length > 0) {
				aOverlay = new NearbyOverlay(LocalRetailsActivity.this
						.getResources().getDrawable(R.drawable.mark_blue),
						LocalRetailsActivity.this);
				for (MRetail r : retails) {
					// in microdegrees (degrees * 1E6)
					geoPoint = new GeoPoint((int) (r.getLat() * 1E6),
							(int) (r.getLng() * 1E6));
					item = new RetailItem(geoPoint, r.getName(),
							r.getFormatted_address());
					item.setRetail(r);
					aOverlay.addOverlay(item);

					minLat = Math.min(minLat, geoPoint.getLatitudeE6());
					maxLat = Math.max(maxLat, geoPoint.getLatitudeE6());
					minLng = Math.min(minLng, geoPoint.getLongitudeE6());
					maxLng = Math.max(maxLng, geoPoint.getLongitudeE6());
				}
				mapOverlays.add(aOverlay);
				aOverlay.populateNow();
			}

			MapController mc = mapView.getController();
			mc.zoomToSpan(Math.abs(minLat - maxLat), Math.abs(minLng - maxLng));
			mc.animateTo(new GeoPoint((maxLat + minLat) / 2,
					(maxLng + minLng) / 2));
			mapView.postInvalidate();
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	// ////////////////////Duplicated code from CommonActivity

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
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
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

	protected String getJsonFromGet(String url) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);
		StringBuilder sb = new StringBuilder();
		HttpResponse resp;
		try {
			resp = httpClient.execute(get);
			HttpEntity entity = resp.getEntity();
			InputStream is = entity.getContent();

			String line = null;
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}
			is.close();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			displayError("getJsonFromGet", e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			displayError("getJsonFromGet", e.getMessage());
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
