package beauty.android.activity;

import com.google.gson.Gson;

import beauty.android.msg.bean.MDeal;
import beauty.android.util.HtmlAdapter;
import beauty.web.action.service.msg.BaseMsg;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

public class DealActivity extends CommonActivity {
	private static final String dealFormat = "%s <br/> posted by <em>%s</em>";
	private MDeal[] deals;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.beauty_list);
		this.parentControl(CommonActivity.MY);
		new LoadDeals().execute();
	}

	@Override
	public String getTag() {
		return "deals";
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		if (deals[position].getRetail() != null) {
			Intent intent = new Intent(this, RetailDetailActivity.class);
			intent.putExtra("retail", deals[position].getRetail());
			startActivity(intent);
		}
	}

	class LoadDeals extends AsyncTask<String, String, BaseMsg> {
		public static final String dUrl = CommonActivity.host + "getdeals.d";

		@Override
		protected BaseMsg doInBackground(String... arg0) {
			BaseMsg bmsg = new BaseMsg();
			try {
				deals = new Gson()
						.fromJson(getJsonFromGet(dUrl), MDeal[].class);
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
				displayError(getTag(), parseError(result.getErrors()));
				return;
			}

			// update UI
			if (deals == null || deals.length == 0) {
				DealActivity.this.findViewById(R.id.no_record).setVisibility(
						View.VISIBLE);
			} else {
				DATAS = new String[deals.length];
				for (int i = 0; i < deals.length; i++) {
					DATAS[i] = String.format(dealFormat, deals[i].getContent(),
							deals[i].getRetail() != null ? deals[i].getRetail()
									.getName() : "unknown");
				}

				setListAdapter(new HtmlAdapter(DealActivity.this, DATAS));
			}
		}
	}
}
