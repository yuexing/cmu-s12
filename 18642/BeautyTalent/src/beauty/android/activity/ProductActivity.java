package beauty.android.activity;

import java.util.*;

import com.google.gson.Gson;

import beauty.android.activity.R;
import beauty.android.msg.bean.MProduct;
import beauty.android.util.*;
import beauty.web.action.service.msg.ProductMsg;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

public class ProductActivity extends CommonActivity {

	private MProduct[] products;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.beauty_list);
		this.parentControl(CommonActivity.PRODUCT);
		Intent i = this.getIntent();
		// when it is none, the id will be default and this does not matter
		new LoadProducts().execute(i.getSerializableExtra("type").toString(),
				String.valueOf(i.getIntExtra("id", -1)));
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		intent.putExtra("product", products[position]);
		intent.setClass(this, ProductDetailActivity.class);
		startActivity(intent);
	}

	@Override
	public String getTag() {
		return "product";
	}

	class LoadProducts extends AsyncTask<String, String, ProductMsg> {
		private static final String proUrl = CommonActivity.host
				+ "getproducts.d?type=%s&id=%s";

		@Override
		protected ProductMsg doInBackground(String... arg0) {
			Log.d(getTag(), "url: " + String.format(proUrl, arg0[0], arg0[1]));

			ProductMsg psm = new ProductMsg();
			try {
				psm = new Gson().fromJson(
						getJsonFromGet(String.format(proUrl, arg0[0], arg0[1])),
						ProductMsg.class);
			} catch (Exception e) {
				psm.addError(e.getMessage());
			}
			return psm;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			displayProgressDialog();
		}

		@Override
		protected void onPostExecute(ProductMsg result) {
			super.onPostExecute(result);
			progressDialog.dismiss();

			if (result == null)
				return;

			if (result.getErrors() != null && result.getErrors().size() > 0) {
				displayError("Load Products", parseError(result.getErrors()));
				return;
			}

			// update UI
			products = result.getProducts();
			if (products == null || products.length == 0) {
				ProductActivity.this.findViewById(R.id.no_record)
						.setVisibility(View.VISIBLE);
			} else {
				List<ImageAndText> alist = new ArrayList<ImageAndText>();
				for (int i = 0; i < products.length; i++) {
					alist.add(new ImageAndText(products[i].getName(),
							CommonActivity.host + products[i].getImage()));
				}

				Log.w(getTag(), "set List Adapter");
				setListAdapter(new ImageAndTextListAdapter(
						ProductActivity.this, alist));
			}
		}
	}
}
