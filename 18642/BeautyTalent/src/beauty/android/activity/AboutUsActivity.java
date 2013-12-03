package beauty.android.activity;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class AboutUsActivity extends CommonActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_aboutus);
		
		this.parentControl(CommonActivity.MORE);

		// Display text screen with the given information
		TextView tv = (TextView) findViewById(R.id.about_us_text1);
		tv.setText(R.string.about_us_text1);
		tv.setMovementMethod(LinkMovementMethod.getInstance());
		tv = (TextView) findViewById(R.id.about_us_text2);
		tv.setText(R.string.about_us_text2);
		tv.setMovementMethod(LinkMovementMethod.getInstance());
	}

	@Override
	public String getTag() {
		return "aboutus";
	}
}
