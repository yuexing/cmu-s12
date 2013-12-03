package beauty.android.activity;

import java.util.ArrayList;
import java.util.List;

import beauty.android.msg.bean.MProduct;
import beauty.android.util.ImageAndText;
import beauty.android.util.ImageAndTextListAdapter;
import beauty.android.util.SearchSuggestionSampleProvider;
import beauty.web.action.service.msg.ProductMsg;

import com.google.gson.Gson;

import android.app.*;
import android.os.*;
import android.provider.SearchRecentSuggestions;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.content.*;

public class SearchQueryResults extends CommonActivity {

	private CommonActivity.Type type;
	private int id;
	private String q;
	private MProduct[] products;

	/**
	 * Called with the activity is first created.
	 * 
	 * After the typical activity setup code, we check to see if we were
	 * launched with the ACTION_SEARCH intent, and if so, we handle it.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Inflate our UI from its XML layout description.
		setContentView(R.layout.beauty_list);

		this.parentControl(CommonActivity.SEARCH);

		// get and process search query here
		final Intent queryIntent = getIntent();
		final String queryAction = queryIntent.getAction();
		if (Intent.ACTION_SEARCH.equals(queryAction)) {
			doSearchQuery(queryIntent, "onCreate()");
		}
	}

	/**
	 * Called when new intent is delivered.
	 * 
	 * This is where we check the incoming intent for a query string.
	 * 
	 * @param newIntent
	 *            The intent used to restart this activity
	 */
	@Override
	public void onNewIntent(final Intent newIntent) {
		super.onNewIntent(newIntent);

		// get and process search query here
		final Intent queryIntent = getIntent();
		final String queryAction = queryIntent.getAction();
		if (Intent.ACTION_SEARCH.equals(queryAction)) {
			doSearchQuery(queryIntent, "onNewIntent()");
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		intent.putExtra("product", products[position]);
		intent.setClass(this, ProductDetailActivity.class);
		startActivity(intent);
	}

	/**
	 * Generic search handler.
	 * 
	 * In a "real" application, you would use the query string to select results
	 * from your data source, and present a list of those results to the user.
	 */
	private void doSearchQuery(final Intent queryIntent, final String entryPoint) {
		// The search query is provided as an
		// "extra" string in the query intent
		q = queryIntent.getStringExtra(SearchManager.QUERY);

		// Record the query string in the recent
		// queries suggestions provider.
		SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
				SearchSuggestionSampleProvider.AUTHORITY,
				SearchSuggestionSampleProvider.MODE);
		suggestions.saveRecentQuery(q, null);

		// If your application provides context data for its searches,
		// you will receive it as an "extra" bundle in the query intent.
		// The bundle can contain any number of elements,
		// using any number of keys;
		// For this Api Demo we're just using a single string,
		// stored using "demo key".
		final Bundle appData = queryIntent
				.getBundleExtra(SearchManager.APP_DATA);
		id = appData.getInt("id");
		type = (Type) appData.getSerializable("type");

		new LoadProducts().execute();
	}

	@Override
	public String getTag() {
		return "search_result";
	}

	// ///////////Loader
	class LoadProducts extends AsyncTask<String, String, ProductMsg> {
		private static final String proUrl = CommonActivity.host
				+ "search.d?type=%s&id=%s&q=%s";
		private static final String allProUrl = CommonActivity.host
				+ "search.d?q=%s";

		@Override
		protected ProductMsg doInBackground(String... arg0) {
			ProductMsg psm = new ProductMsg();
			try {
				if (type != CommonActivity.Type.none) {
					Log.d(getTag(),
							"url: " + String.format(proUrl, type, id, q));

					psm = new Gson().fromJson(
							getJsonFromGet(String.format(proUrl, type, id, q)),
							ProductMsg.class);

				} else {
					Log.d(getTag(), "url: " + String.format(allProUrl, q));
					psm = new Gson().fromJson(
							getJsonFromGet(String.format(allProUrl, q)),
							ProductMsg.class);
				}
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
				displayError("Search", parseError(result.getErrors()));
				return;
			}
			// update UI
			products = result.getProducts();
			if (products == null || products.length == 0) {
				SearchQueryResults.this.findViewById(R.id.no_record)
						.setVisibility(View.VISIBLE);
			} else {
				List<ImageAndText> alist = new ArrayList<ImageAndText>();
				for (int i = 0; i < products.length; i++) {
					alist.add(new ImageAndText(products[i].getName(),
							CommonActivity.host + products[i].getImage()));
				}

				setListAdapter(new ImageAndTextListAdapter(
						SearchQueryResults.this, alist));
			}
		}
	}
}
