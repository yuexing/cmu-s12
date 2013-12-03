package beauty.android.activity;

import android.content.*;
import android.os.Bundle;
import android.text.Html;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;

public class MyBeautyActivity extends CommonActivity {

	private static final int FAV = 0;
	private static final int COMMENT = 1;
	private static final int DEAL = 2;
	private static final int AUDIO = 3;

	private static final String nameFormat = "Hello, <b>%s</b>!";

	private Button exitImageView = null;
	private TextView nameText = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mybeauty);
		this.parentControl(CommonActivity.MY);
		DATAS = new String[] { "My Favorite", "My Comment", "My Deals", "My Audio" };

		setListAdapter(new SimpleAdapter(this, buildList(),
				R.layout.common_listview_text, new String[] { "img", "text",
						"img_pre" }, new int[] { R.id.img, R.id.text,
						R.id.img_pre }));

		exitImageView = (Button) findViewById(R.id.exitlogin);
		nameText = (TextView) this.findViewById(R.id.name);

		if (!this.isLogined()) {
			exitImageView.setText(this.getString(R.string.login));
			exitImageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startActivity(new Intent(MyBeautyActivity.this,
							LoginActivity.class));
				}
			});
		} else {
			nameText.setText(Html.fromHtml(String.format(nameFormat,
					this.beauty.getUserName())));

			exitImageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					logout();
					// reload
					startActivity(new Intent(MyBeautyActivity.this,
							MyBeautyActivity.class)
							.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET));
				}
			});
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		this.onCreate(null);
	}

	@Override
	public void onItemClick(AdapterView<?> list, View view, int position,
			long id) {
		if (this.beauty.isLogined()) {
			if (position == FAV) {
				startActivity(new Intent(getApplicationContext(),
						FavoriteActivity.class));
			} else if (position == COMMENT) {
				intent.setClass(this, ReviewActivity.class);
				intent.putExtra("type", CommonActivity.Type.user);
				intent.putExtra("id", this.beauty.getUserId());
				startActivity(intent);
			} else if (position == AUDIO){
				intent.setClass(this, AudioActivity.class);
				startActivity(intent);
			} else if (position == DEAL){
				intent.setClass(this, DealActivity.class);
				startActivity(intent);
			}
		} else {
			this.displayError("Alert", "Please Login First!");
		}
	}

	@Override
	public String getTag() {
		return "mybeauty";
	}
}