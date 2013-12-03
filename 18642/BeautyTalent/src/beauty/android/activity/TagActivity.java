package beauty.android.activity;

import com.google.gson.Gson;

import beauty.android.activity.R;
import beauty.android.msg.bean.MTag;
import beauty.web.action.service.msg.BaseMsg;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

public class TagActivity extends CommonActivity {
	
	public static final String tagUrl = CommonActivity.host + "gettags.d";
	MTag[] tags;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.beauty_list);

		this.parentControl(CommonActivity.PRODUCT);
		
		new LoadTags().execute();
	}

	@Override
	public String getTag() {
		return "tag";
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		intent.putExtra("type", CommonActivity.Type.tag);
		intent.putExtra("id", tags[position].getId());
		intent.setClass(this, ProductActivity.class);
		startActivity(intent);
	}

	class LoadTags extends AsyncTask<String, String, BaseMsg> {

		@Override
		protected BaseMsg doInBackground(String... arg0) {
			BaseMsg bmsg = new BaseMsg();
			try {
				tags = new Gson().fromJson(getJsonFromGet(tagUrl),
						MTag[].class);
			} catch (Exception e) {
				bmsg.addError(e.getMessage());
			}
			return bmsg;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			displayProgressDialog();
		}

		@Override
		protected void onPostExecute(BaseMsg result) {
			super.onPostExecute(result);
			progressDialog.dismiss();

			if (result.getErrors() != null && result.getErrors().size() > 0) {
				displayError("Search", parseError(result.getErrors()));
				return;
			}
			
			// update UI
			if (tags == null || tags.length == 0) {
				TagActivity.this.findViewById(R.id.no_record)
						.setVisibility(View.VISIBLE);
			} else {
				DATAS = new String[tags.length];
				for (int i = 0; i < tags.length; i++) {
					DATAS[i] = tags[i].getName();
				}

				setListAdapter(new SimpleAdapter(TagActivity.this,
						buildList(), R.layout.common_listview_text,
						new String[] { "img", "text", "img_pre" }, new int[] {
								R.id.img, R.id.text, R.id.img_pre }));
			}
		}
	}
}
