package beauty.android.activity;

import beauty.android.util.HorizontalPager;
import beauty.android.util.HorizontalPager.OnScreenSwitchListener;
import android.os.Bundle;
import android.view.*;
import android.view.View.*;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;

public class TutorialActivity extends CommonActivity implements
		OnScreenSwitchListener {

	private static final int TOTAL_IMAGES = 7;
	private static HorizontalPager hp;
	private ViewGroup dotParent;
	private Button button;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_tutorial);
		
		this.parentControl(CommonActivity.MORE);

		dotParent = (ViewGroup) this.findViewById(R.id.linear_dots);
		hp = (HorizontalPager) this.findViewById(R.id.horizontal_pager);
		hp.setOnScreenSwitchListener(this);
		onScreenSwitched(0);
		setOnClickListeners();
		hp.getCurrentScreen();
	}

	@Override
	public void onScreenSwitched(int screen) {
		dotParent.removeAllViews();

		for (int i = 0; i < TOTAL_IMAGES; i++) {
			ImageView dot = new ImageView(this);
			int resId = i == screen ? R.drawable.white_circle
					: R.drawable.grey_circle;
			dot.setBackgroundResource(resId);
			LinearLayout ll = new LinearLayout(this);
			ll.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1f));
			ll.setGravity(Gravity.CENTER);
			ll.addView(dot);
			dotParent.addView(ll);
		}
		
		findViewById(R.id.prev_button).setVisibility(View.VISIBLE);
		findViewById(R.id.next_button).setVisibility(View.VISIBLE);
		
		if (screen == TOTAL_IMAGES - 1)
			findViewById(R.id.next_button).setVisibility(View.INVISIBLE);
		if (screen == 0)
			findViewById(R.id.prev_button).setVisibility(View.INVISIBLE);
	}

	private void setOnClickListeners() {
		button = (Button) this.findViewById(R.id.prev_button);
		button.setVisibility(View.INVISIBLE);

		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int cur;
				if ((cur = hp.getCurrentScreen()) > 0) {
					hp.setCurrentScreen(cur - 1, true);
				}
			}

		});

		button = (Button) this.findViewById(R.id.next_button);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int cur;
				if ((cur = hp.getCurrentScreen()) < TOTAL_IMAGES - 1) {
					hp.setCurrentScreen(cur + 1, true);
				}
			}
		});
	}

	@Override
	public String getTag() {
		return "turorial";
	}

}
