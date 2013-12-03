package beauty.android.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import beauty.android.activity.R;
import beauty.android.msg.bean.MProduct;
import beauty.android.util.DBHandler;

public class FavoriteActivity extends CommonActivity {

	private LinearLayout backgroundLinearLayout = null;
	private MProduct[] ps;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.beauty_list);

		this.parentControl(CommonActivity.MY);

		backgroundLinearLayout = (LinearLayout) findViewById(R.id.common_listview_parent);

		DBHandler dbHandler = new DBHandler(this);
		ps = dbHandler.readAll(DBHandler.TBL_FAV);
		
		if (ps.length == 0) {
			backgroundLinearLayout
					.setBackgroundResource(R.drawable.my_favorite_nodata);
		} else {
			DATAS = new String[ps.length];
			for (int i = 0; i < ps.length; i++) {
				DATAS[i] = ps[i].getName();
			}
			setListAdapter(new SimpleAdapter(this, buildList(),
					R.layout.common_listview_text, new String[] { "img",
							"text", "img_pre" }, new int[] { R.id.img,
							R.id.text, R.id.img_pre }));
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		Log.d(this.getTag(), String.valueOf(position));
		intent.putExtra("from", CommonActivity.Type.fav);
		intent.putExtra("product", ps[position]);
		intent.setClass(this, ProductDetailActivity.class);
		startActivity(intent);
	}

	@Override
	public String getTag() {
		return "favorite";
	}
}
