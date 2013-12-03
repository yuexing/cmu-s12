package beauty.android.util;

import java.util.ArrayList;

import beauty.android.activity.R;
import beauty.android.activity.RetailDetailActivity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import com.google.android.maps.*;



public class NearbyOverlay extends ItemizedOverlay<RetailItem>{
	
	private ArrayList<RetailItem> mapOverlays = new ArrayList<RetailItem>();

	private Context context;

	public NearbyOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
	}

	public NearbyOverlay(Drawable defaultMarker, Context context) {
		this(defaultMarker);
		this.context = context;
	}

	@Override
	protected RetailItem createItem(int i) {
		return mapOverlays.get(i);
	}

	@Override
	public int size() {
		return mapOverlays.size();
	}

	// this seems to be the on click
	@Override
	protected boolean onTap(final int index) {
		RetailItem item = mapOverlays.get(index);
		AlertDialog.Builder dialog = new AlertDialog.Builder(this.context);
		dialog.setTitle(item.getTitle());
		dialog.setMessage(item.getSnippet());
		// ok
		dialog.setNeutralButton(android.R.string.ok,  new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		// goto
		dialog.setPositiveButton(R.string.go_to, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Intent i = new Intent(context, RetailDetailActivity.class);
				i.putExtra("retail", NearbyOverlay.this.mapOverlays.get(index).getRetail());
				context.startActivity(i);
			}
		});
		dialog.show();
		return true;
	}

	public void addOverlay(RetailItem item) {
		mapOverlays.add(item);
	}

	public void populateNow() {
		this.populate();
	}
}
