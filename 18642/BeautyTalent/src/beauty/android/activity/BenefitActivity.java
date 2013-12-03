package beauty.android.activity;


import com.google.gson.Gson;

import beauty.android.msg.bean.MBenefit;
import beauty.web.action.service.msg.BaseMsg;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

public class BenefitActivity extends CommonActivity {

	public static final String beUrl = CommonActivity.host + "getbenefits.d";
	MBenefit[] benefits;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.beauty_list);

		this.parentControl(CommonActivity.PRODUCT);

		new LoadBenefits().execute();

	}

	@Override
	public String getTag() {
		return "benefit";
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		intent.putExtra("type", CommonActivity.Type.benefit);
		intent.putExtra("id", benefits[position].getId());
		intent.setClass(this, ProductActivity.class);
		startActivity(intent);
	}

	class LoadBenefits extends AsyncTask<String, String, BaseMsg> {

		@Override
		protected BaseMsg doInBackground(String... arg0) {
			BaseMsg bmsg = new BaseMsg();
			try {
				benefits = new Gson().fromJson(getJsonFromGet(beUrl),
						MBenefit[].class);
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

			if(result.getErrors() != null && result.getErrors().size() > 0){
				displayError(getTag(), parseError(result.getErrors()));
				return;
			}
			
			// update UI
			if (benefits == null || benefits.length == 0) {
				BenefitActivity.this.findViewById(R.id.no_record)
						.setVisibility(View.VISIBLE);
			} else {
				DATAS = new String[benefits.length];
				for (int i = 0; i < benefits.length; i++) {
					DATAS[i] = benefits[i].getName();
				}

				setListAdapter(new SimpleAdapter(BenefitActivity.this,
						buildList(), R.layout.common_listview_text,
						new String[] { "img", "text", "img_pre" }, new int[] {
								R.id.img, R.id.text, R.id.img_pre }));
			}
		}
	}
}
