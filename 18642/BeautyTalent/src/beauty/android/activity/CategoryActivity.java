package beauty.android.activity;

import com.google.gson.Gson;

import beauty.android.msg.bean.MCategory;
import beauty.web.action.service.msg.BaseMsg;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;

public class CategoryActivity extends CommonActivity {
	public static final String cateUrl = CommonActivity.host + "getcates.d";
    MCategory[] cates;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.beauty_list);		

		this.parentControl(CommonActivity.PRODUCT);
		
		new LoadCates().execute();
	}

	@Override
	public String getTag() {
		return "category";
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		intent.putExtra("type", CommonActivity.Type.category);
		intent.putExtra("id", cates[position].getId());
		intent.setClass(this, ProductActivity.class);
		startActivity(intent);
	}
	
	class LoadCates extends AsyncTask<String, String, BaseMsg> {

		@Override
		protected BaseMsg doInBackground(String... arg0) {
			BaseMsg bmsg = new BaseMsg();
			try {
				cates = new Gson().fromJson(getJsonFromGet(cateUrl),
						MCategory[].class);
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
			if (cates == null || cates.length == 0) {
				CategoryActivity.this.findViewById(R.id.no_record)
						.setVisibility(View.VISIBLE);
			} else {
				DATAS = new String[cates.length];
				for (int i = 0; i < cates.length; i++) {
					DATAS[i] = cates[i].getName();
				}

				setListAdapter(new SimpleAdapter(CategoryActivity.this,
						buildList(), R.layout.common_listview_text,
						new String[] { "img", "text", "img_pre" }, new int[] {
								R.id.img, R.id.text, R.id.img_pre }));
			}
		}
	}
}

