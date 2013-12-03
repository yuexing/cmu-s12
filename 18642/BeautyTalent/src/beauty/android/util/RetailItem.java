package beauty.android.util;

import beauty.android.msg.bean.MRetail;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class RetailItem extends OverlayItem{

	private MRetail retail;
	
	public RetailItem(GeoPoint arg0, String arg1, String arg2) {
		super(arg0, arg1, arg2);
	}

	public MRetail getRetail() {
		return retail;
	}

	public void setRetail(MRetail retail) {
		this.retail = retail;
	}
	
	

}
