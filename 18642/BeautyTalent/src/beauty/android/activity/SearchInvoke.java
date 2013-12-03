package beauty.android.activity;

import beauty.android.msg.bean.*;
import beauty.android.util.*;
import beauty.web.action.service.msg.BaseMsg;

import com.google.gson.Gson;

import android.os.*;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;

public class SearchInvoke extends CommonActivity {
	// UI elements
	Button mStartSearch;
	Spinner mSearchMode;
	EditText mQuery;

	// search will need this
	private CommonActivity.Type type = CommonActivity.Type.none;
	private int id = -1;

	// Menu mode spinner choices
	// This list must match the list found in samples
	final static int ALL = 0;
	final static int BY_BRAND = 1;
	final static int BY_CATE = 2;
	final static int BY_BENEFIT = 3;
	final static int BY_TAG = 4;

	private MBrand[] brands;
	private MCategory[] cates;
	private MTag[] tags;
	private MBenefit[] benefits;

	/**
	 * Called with the activity is first created.
	 * 
	 * We aren't doing anything special in this implementation, other than the
	 * usual activity setup code.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Inflate our UI from its XML layout description.
		setContentView(R.layout.search_invoke);
		
		this.parentControl(CommonActivity.SEARCH);

		// Get display items for later interaction
		mStartSearch = (Button) findViewById(R.id.btn_start_search);
		mSearchMode = (Spinner) findViewById(R.id.spinner_menu_mode);
		mQuery = (EditText) findViewById(R.id.txt_query_prefill);

		// Populate items
		String modes[] = { "ALL", "By Brand", "By Category", "By Benefit",
				"By Tag" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, modes);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSearchMode.setAdapter(adapter);

		mSearchMode.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == BY_BRAND) {
					type = CommonActivity.Type.brand;
					new LoadBrands().execute();
				} else if (position == BY_CATE) {
					type = CommonActivity.Type.category;
					new LoadCates().execute();
				} else if (position == BY_TAG) {
					type = CommonActivity.Type.tag;
					new LoadTags().execute();
				} else if (position == BY_BENEFIT) {
					type = CommonActivity.Type.benefit;
					new LoadBenefits().execute();
				} else if (position == ALL) {
					type = CommonActivity.Type.none;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		// Attach actions to buttons
		mStartSearch.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onSearchRequested();
			}
		});
	}

	/**
	 * This hook is called when the user signals the desire to start a search.
	 * 
	 * By overriding this hook we can insert local or context-specific data.
	 * 
	 * @return Returns true if search launched, false if activity blocks it
	 */
	@Override
	public boolean onSearchRequested() {
		// If your application absolutely must
		// disable search, do it here.
		if (mSearchMode.getSelectedItemPosition() == BY_BENEFIT) {
			return false;
		}

		// It's possible to prefill the query string
		// before launching the search
		// UI. For this demo, we simply copy it
		// from the user input field.
		// For most applications, you can simply pass
		// null to startSearch() to
		// open the UI with an empty query string.
		final String queryPrefill = mQuery.getText().toString();

		// Bundle
		Bundle appDataBundle = new Bundle();
		appDataBundle.putSerializable("type", type);
		appDataBundle.putInt("id", id);

		// Now call the Activity member function that
		// invokes the Search Manager UI.
		startSearch(queryPrefill, false, appDataBundle, false);

		// Returning true indicates that we did launch
		// the search, instead of blocking it.
		return true;
	}

	// ////////////////////Loaders
	class LoadBrands extends AsyncTask<String, String, BaseMsg> {
		private SpinnerDialog dialog;

		@Override
		protected BaseMsg doInBackground(String... arg0) {
			BaseMsg bmsg = new BaseMsg();
			if (!(brands != null && brands.length > 0))
				try {
					brands = new Gson().fromJson(
							getJsonFromGet(BrandActivity.bUrl), MBrand[].class);
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
			if (brands == null || brands.length == 0) {
				displayError("Load Brand", "No Record!");
			} else {
				DATAS = new String[brands.length];
				for (int i = 0; i < brands.length; i++) {
					DATAS[i] = brands[i].getName();
				}

				dialog = new SpinnerDialog(SearchInvoke.this, DATAS,
						new SpinnerDialog.DialogListener() {
							public void cancelled() {
								type = CommonActivity.Type.none;
								mSearchMode.setSelection(ALL);
								dialog.dismiss();
							}

							public void ready(int n) {
								id = brands[n].getId();
								dialog.dismiss();
							}
						});
				dialog.show();
			}
		}
	}

	class LoadCates extends AsyncTask<String, String, BaseMsg> {
		private SpinnerDialog dialog;

		@Override
		protected BaseMsg doInBackground(String... arg0) {
			BaseMsg bmsg = new BaseMsg();
			if (!(cates != null && cates.length > 0))
				try {
					cates = new Gson().fromJson(
							getJsonFromGet(CategoryActivity.cateUrl),
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
				displayError("Load Category", "No Record!");
			} else {
				DATAS = new String[cates.length];
				for (int i = 0; i < cates.length; i++) {
					DATAS[i] = cates[i].getName();
				}
				dialog = new SpinnerDialog(SearchInvoke.this, DATAS,
						new SpinnerDialog.DialogListener() {
							public void cancelled() {
								type = CommonActivity.Type.none;
								mSearchMode.setSelection(ALL);
								dialog.dismiss();
							}

							public void ready(int n) {
								id = cates[n].getId();
								dialog.dismiss();
							}
						});
				dialog.show();
			}
		}
	}

	class LoadBenefits extends AsyncTask<String, String, BaseMsg> {
		private SpinnerDialog dialog;

		@Override
		protected BaseMsg doInBackground(String... arg0) {
			BaseMsg bmsg = new BaseMsg();
			if (!(benefits != null && benefits.length > 0))
				try {
					benefits = new Gson()
							.fromJson(getJsonFromGet(BenefitActivity.beUrl),
									MBenefit[].class);
				}  catch (Exception e) {
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
				displayError("Load Category", "No Record!");
			} else {
				DATAS = new String[benefits.length];
				for (int i = 0; i < benefits.length; i++) {
					DATAS[i] = benefits[i].getName();
				}
				dialog = new SpinnerDialog(SearchInvoke.this, DATAS,
						new SpinnerDialog.DialogListener() {
							public void cancelled() {
								type = CommonActivity.Type.none;
								mSearchMode.setSelection(ALL);
								dialog.dismiss();
							}

							public void ready(int n) {
								id = benefits[n].getId();
								dialog.dismiss();
							}
						});
			}
		}
	}

	class LoadTags extends AsyncTask<String, String, BaseMsg> {
		private SpinnerDialog dialog;

		@Override
		protected BaseMsg doInBackground(String... arg0) {
			BaseMsg bmsg = new BaseMsg();
			if (!(tags != null && tags.length > 0))
				try {
					tags = new Gson().fromJson(getJsonFromGet(TagActivity.tagUrl),
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
			
			if(result.getErrors() != null && result.getErrors().size() > 0){
				displayError(getTag(), parseError(result.getErrors()));
				return;
			}
			
			// update UI
			if (tags == null || tags.length == 0) {
				displayError("Load Tag", "No Record!");
			} else {
				DATAS = new String[tags.length];
				for (int i = 0; i < tags.length; i++) {
					DATAS[i] = tags[i].getName();
				}
				dialog = new SpinnerDialog(SearchInvoke.this, DATAS,
						new SpinnerDialog.DialogListener() {
							public void cancelled() {
								type = CommonActivity.Type.none;
								mSearchMode.setSelection(ALL);
								dialog.dismiss();
							}

							public void ready(int n) {
								id = tags[n].getId();
								dialog.dismiss();
							}
						});
				dialog.show();
			}
		}
	}

	@Override
	public String getTag() {
		return "search_invoke";
	}

}
