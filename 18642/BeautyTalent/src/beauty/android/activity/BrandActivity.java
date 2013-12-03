package beauty.android.activity;

import com.google.gson.Gson;

import beauty.android.msg.bean.MBrand;
import beauty.web.action.service.msg.BaseMsg;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;

public class BrandActivity extends CommonActivity {
	
	public static final String bUrl = CommonActivity.host + "getbrands.d";
	MBrand[] brands;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.beauty_list);
		this.parentControl(CommonActivity.PRODUCT);

		new LoadBrands().execute();
	}

	@Override
	public String getTag() {
		return "brand";
	}
	
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		intent.putExtra("type", CommonActivity.Type.brand);
		intent.putExtra("id", brands[position].getId());
		intent.setClass(this, ProductActivity.class);
		startActivity(intent);
	}
	
	class LoadBrands extends AsyncTask<String, String, BaseMsg> {

		@Override
		protected BaseMsg doInBackground(String... arg0) {
			BaseMsg bmsg = new BaseMsg();
			try {
				brands = new Gson().fromJson(getJsonFromGet(bUrl),
						MBrand[].class);
			}catch (Exception e) {
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
			if (brands == null || brands.length == 0) {
				BrandActivity.this.findViewById(R.id.no_record)
						.setVisibility(View.VISIBLE);
			} else {
				DATAS = new String[brands.length];
				for (int i = 0; i < brands.length; i++) {
					DATAS[i] = brands[i].getName();
				}

				setListAdapter(new SimpleAdapter(BrandActivity.this,
						buildList(), R.layout.common_listview_text,
						new String[] { "img", "text", "img_pre" }, new int[] {
								R.id.img, R.id.text, R.id.img_pre }));
			}
		}
	}
}

