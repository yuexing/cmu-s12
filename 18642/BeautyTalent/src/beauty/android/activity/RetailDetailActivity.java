package beauty.android.activity;

import beauty.android.msg.bean.MRetail;
import android.os.Bundle;
import android.content.Intent;
import android.text.Html;
import android.widget.TextView;

public class RetailDetailActivity extends CommonActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_retail_detail);
		
		this.parentControl(CommonActivity.PRODUCT);
		
		Intent i = this.getIntent();
		MRetail r = (MRetail) i.getSerializableExtra("retail");
		
		// display values
		TextView lbl_name = (TextView) findViewById(R.id.name);
		TextView lbl_address = (TextView) findViewById(R.id.address);
		TextView lbl_phone = (TextView) findViewById(R.id.phone);
		TextView lbl_postcode = (TextView) findViewById(R.id.postcode);
		TextView lbl_location = (TextView) findViewById(R.id.location);

		// Check for null data from google
		// Sometimes place details might missing
		String name = r.getName() == null ? "Not present" : r.getName();
		String address = r.getFormatted_address() == null ? "Not present"
				: r.getFormatted_address();
		String postcode = r.getPostcode() == null ? "Not present" : r.getPostcode();
		String phone = r.getPhoneNumber() == null ? "Not present" : r.getPhoneNumber();
		String latitude = r.getLat() == 0 ? "Not present"
				: new Double(r.getLat()).toString();
		String longitude = r.getLng() == 0 ? "Not present"
				:  new Double(r.getLng()).toString();

		lbl_name.setText(name);
		lbl_address.setText(address);
		lbl_phone.setText(Html
				.fromHtml("<b>Phone:</b> " + phone));
		lbl_postcode.setText(Html
				.fromHtml("<b>Postcode:</b> " + postcode));
		lbl_location.setText(Html
				.fromHtml("<b>Latitude:</b> "
						+ latitude
						+ ", <b>Longitude:</b> "
						+ longitude));
	}

	@Override
	public String getTag() {
		return "retaildetail";
	}
}
