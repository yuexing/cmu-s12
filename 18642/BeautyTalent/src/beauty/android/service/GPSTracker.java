package beauty.android.service;

import android.app.Service;
import android.content.*;
import android.location.*;
import android.os.Bundle;
import android.os.IBinder;

public class GPSTracker extends Service implements LocationListener {

	private Context context;

	private boolean isGPSEnabled = false;
	private boolean isNetEnabled = false;
	private boolean canGetLocation = false;

	private LocationManager locationManager = null;
	private Location location = null;

	private double lat;
	private double lng;

	// The minimum distance to change Updates in meters
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

	// The minimum time between updates in milliseconds
	private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

	public GPSTracker(Context context) {
		this.context = context;
		this.getLocation();
	}

	public Location getLocation() {
		locationManager = (LocationManager) context
				.getSystemService(LOCATION_SERVICE);

		if (locationManager == null) {
			return null;
		}

		isGPSEnabled = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		isNetEnabled = locationManager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

		if (!isGPSEnabled && !isNetEnabled) {
			
		} else {
			this.canGetLocation = true;
			
			if (isGPSEnabled) {
				locationManager.requestLocationUpdates(
						LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES,
						MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
				location = locationManager
						.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				if (location != null) {
					this.lat = location.getLatitude();
					this.lat = location.getLongitude();
				}
			}
			if (location == null) {				
				locationManager.requestLocationUpdates(
						LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES,
						MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
				location = locationManager
						.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				if (location != null) {
					this.lat = location.getLatitude();
					this.lat = location.getLongitude();
				}
			}
		}

		return location;
	}

	/**
	 * Stop using GPS listener Calling this function will stop using GPS in your
	 * app
	 * */
	public void stopUsingGPS() {
		if (locationManager != null) {
			locationManager.removeUpdates(GPSTracker.this);
		}
	}

	public double getLat() {
		return lat;
	}

	public double getLng() {
		return lng;
	}

	/**
	 * Function to check GPS/wifi enabled
	 * 
	 * @return boolean
	 * */
	public boolean canGetLocation() {
		return this.canGetLocation;
	}

	public boolean isGPSEnabled() {
		return isGPSEnabled;
	}

	public boolean isNetEnabled() {
		return isNetEnabled;
	}
	
	@Override
	public void onLocationChanged(Location location) {
		// notify map?
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}
