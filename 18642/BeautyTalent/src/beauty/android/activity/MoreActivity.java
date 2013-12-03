package beauty.android.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;

public class MoreActivity extends CommonActivity {

	private static final int TUTORIAL = 0;

	private static final int ABOUT = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.beauty_list);

		this.parentControl(CommonActivity.MORE);

		DATAS = new String[] { "Turorial", "About" };

		setListAdapter(new SimpleAdapter(this, buildList(),
				R.layout.common_listview_text, new String[] { "img", "text",
						"img_pre" }, new int[] { R.id.img, R.id.text,
						R.id.img_pre }));
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		if (position == TUTORIAL) {
			intent.setClass(MoreActivity.this, TutorialActivity.class);
			startActivity(intent);
		} else if (position == ABOUT) {
			intent.setClass(MoreActivity.this, AboutUsActivity.class);
			startActivity(intent);
		}
	}

	@Override
	public String getTag() {
		return "more";
	}
}
