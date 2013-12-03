package beauty.android.activity;

import com.google.gson.Gson;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.widget.RatingBar.OnRatingBarChangeListener;

import beauty.android.msg.bean.*;
import beauty.android.util.*;
import beauty.web.action.service.msg.BaseMsg;
import beauty.web.action.service.msg.ProductMsg;

public class ProductDetailActivity extends CommonActivity implements
		OnClickListener {

	private static final String RateFormat = "%d, %d has rated, %d has reviewed";

	private DBHandler dbHandler = new DBHandler(this);

	private ImageView productImage = null;
	private TextView productName = null;
	private TextView productBrand = null;
	private TextView productCate = null;
	private TextView productBenefits = null;
	private TextView productTags = null;
	private TextView productRate = null;
	private TextView productIntro = null;
	private TextView productPrice = null;

	private Button viewMap = null;
	private Button readReviews = null;
	private Button addFav = null;

	private RatingBar curRate = null;

	private MProduct product; // the current product

	// add to favorite
	private boolean added = false;

	private CommonActivity.Type from;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product_detail);
		this.parentControl(CommonActivity.PRODUCT);

		productName = (TextView) this.findViewById(R.id.product_name);
		productBrand = (TextView) this.findViewById(R.id.product_brand);
		productCate = (TextView) this.findViewById(R.id.product_category);
		productBenefits = (TextView) this.findViewById(R.id.product_benefits);
		productTags = (TextView) this.findViewById(R.id.product_tags);
		productRate = (TextView) this.findViewById(R.id.product_rate);
		productImage = (ImageView) this.findViewById(R.id.product_image);
		productIntro = (TextView) this.findViewById(R.id.product_intro);
		productPrice = (TextView) this.findViewById(R.id.product_price);

		readReviews = (Button) this.findViewById(R.id.btn_read_review);
		readReviews.setOnClickListener(this);
		viewMap = (Button) this.findViewById(R.id.btn_view_map);
		viewMap.setOnClickListener(this);
		addFav = (Button) this.findViewById(R.id.btn_add_fav);
		
		// login user can add to favorite or remove from favorite
		if(this.isLogined()){
			addFav.setOnClickListener(this);
		} else {
			addFav.setVisibility(View.GONE);
		}
		
		curRate = (RatingBar) this.findViewById(R.id.cur_rate);

		product = (MProduct) this.getIntent().getSerializableExtra("product");
		
		if (this.getIntent().getSerializableExtra("from") != null) {
			from = (Type) this.getIntent().getSerializableExtra("from");
		}
		
		this.curRate.setOnRatingBarChangeListener(new OnRatingBarChangeListener(){
			@Override
			public void onRatingChanged(RatingBar arg0, float arg1, boolean arg2) {
				int rate = (int) curRate.getRating();
				Toast.makeText(ProductDetailActivity.this,
						"your rate is: " + String.valueOf(curRate.getRating()),
						Toast.LENGTH_SHORT).show();
				new RateProduct().execute(String.valueOf(product.getId()), String.valueOf(rate));
			}
			
		});

		fillProduct();
	}

	@Override
	public void onClick(View view) {
		if (view == viewMap) {
			intent.setClass(this, LocalRetailsActivity.class);
			intent.putExtra("pid", product.getId());
			startActivity(intent);
			return;
		} else if (view == readReviews) {
			intent.setClass(this, ReviewActivity.class);
			intent.putExtra("type", CommonActivity.Type.product);
			intent.putExtra("id", product.getId());
			startActivity(intent);
			return;
		} else if (view == addFav) {
			if (this.added) {
				dbHandler.delete(product.getId(), DBHandler.TBL_FAV);
				this.added = false;
				this.addFav.setText(this.getString(R.string.add_fav));
			} else {
				dbHandler.add(product, DBHandler.TBL_FAV);
				this.added = true;
				this.addFav.setText(this.getString(R.string.del_fav));
			}
			return;
		} else {
			super.onClick(view);
			return;
		}
	}

	private String parseBes(MBenefit[] bes) {
		StringBuilder sb = new StringBuilder();
		if (bes == null || bes.length == 0)
			return "";
		else {
			sb.append(bes[0].getName() + "(" + bes[0].getProductCount() + ")");
			for (int i = 1; i < bes.length; i++) {
				sb.append(", " + bes[i].getName() + "("
						+ bes[i].getProductCount() + ")");
			}
		}
		return sb.toString();
	}

	private String parseTags(MTag[] tags) {
		StringBuilder sb = new StringBuilder();
		if (tags == null || tags.length == 0)
			return "";
		else {
			sb.append(tags[0].getName() + "(" + tags[0].getProductCount() + ")");
			for (int i = 1; i < tags.length; i++) {
				sb.append(", " + tags[i].getName() + "("
						+ tags[i].getProductCount() + ")");
			}
		}
		return sb.toString();
	}

	@SuppressLint("UseValueOf")
	private void fillProduct() {

		if (this.from == CommonActivity.Type.fav) {
			new LoadProduct().execute(String.valueOf(product.getId()));
			return;
		}

		this.productName.setText(product.getName());
		this.productBrand.setText(product.getBrand().getName());
		this.productCate.setText(product.getCategory().getName());
		this.productIntro.setText(product.getIntroduction());
		this.productBenefits.setText(parseBes(product.getBenefits()));
		this.productTags.setText(parseTags(product.getTags()));
		this.productPrice.setText(new Double(product.getPrice()).toString());
		this.productRate.setText(String.format(RateFormat, product.getRate(),
				product.getRateNum(), product.getCommentNum()));

		if (dbHandler.read(product.getId(), DBHandler.TBL_FAV) != null) {
			this.added = true;
			this.addFav.setText(this.getString(R.string.del_fav));
		}
		
		if (dbHandler.read(product.getId(), DBHandler.TBL_RATE) != null){
			this.curRate.setIsIndicator(true);
		}

		// Load the image and set it on the ImageView
		String imageUrl = CommonActivity.host + product.getImage();

		AsyncImageLoader asyncImageLoader = this.getBeauty()
				.getAsyncImageLoader();

		this.productImage.setTag(imageUrl);
		asyncImageLoader.loadDrawable(imageUrl,
				new ImageCallback() {
					public void imageLoaded(Drawable imageDrawable,
							String imageUrl) {
						productImage.setImageDrawable(imageDrawable);
					}
				});
	}

	@Override
	public String getTag() {
		return "productdetails";
	}

	class RateProduct extends AsyncTask<String, String, BaseMsg> {
		
		private static final String rateUrl = CommonActivity.host
				+ "addrate.d?pid=%s&rate=%s";

		private int rate;
		
		@Override
		protected BaseMsg doInBackground(String... arg0) {			
			Log.d(getTag(), "url: " + String.format(rateUrl, arg0[0], arg0[1]));
			this.rate = Integer.valueOf(arg0[1]);
			BaseMsg bsm = new BaseMsg();
			try {
				bsm = new Gson().fromJson(getJsonFromGet(String.format(rateUrl, arg0[0], arg0[1])),
						BaseMsg.class);
			} catch (Exception e) {
				bsm.addError(e.getMessage());
			}
			return bsm;
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

			if(result == null)
				return;
			
			if (result.getErrors() != null && result.getErrors().size() > 0) {
				displayError("Rate Products", parseError(result.getErrors()));
				return;
			}
			
			product.setRate((product.getRate() * product.getRateNum() + this.rate)/ (product.getRateNum() + 1));
			product.setRateNum(product.getRateNum() + 1);
			
			productRate.setText(String.format(RateFormat, product.getRate(),
					product.getRateNum(), product.getCommentNum()));
			
			curRate.setIsIndicator(true);
			dbHandler.add(product, DBHandler.TBL_RATE);
		}
	}
	
	class LoadProduct extends AsyncTask<String, String, ProductMsg> {

		private static final String proUrl = CommonActivity.host
				+ "getproducts.d?type=one&id=%s";

		@Override
		protected ProductMsg doInBackground(String... arg0) {			
			Log.d(getTag(), "url: " + String.format(proUrl, arg0[0]));
			ProductMsg psm = new ProductMsg();
			try {
				psm = new Gson().fromJson(getJsonFromGet(String.format(proUrl, arg0[0])),
						ProductMsg.class);
			}  catch (Exception e) {
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

			if(result == null)
				return;
			
			if (result.getErrors() != null && result.getErrors().size() > 0) {
				displayError("Load Products", parseError(result.getErrors()));
				return;
			}

			// update UI
			MProduct[] products = result.getProducts();
			if (products == null || products.length == 0) {
				findViewById(R.id.no_record).setVisibility(View.VISIBLE);

				new DBHandler(ProductDetailActivity.this).delete(
						product.getId(), DBHandler.TBL_FAV);
				
				displayErrorWithListener("Alert", "The product has been removed", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						startActivity(new Intent(ProductDetailActivity.this, FavoriteActivity.class));
					}					
				});
			} else {
				product = products[0];
				// call fillProduct again
				from = CommonActivity.Type.none;
				fillProduct();
			}
		}
	}
}
