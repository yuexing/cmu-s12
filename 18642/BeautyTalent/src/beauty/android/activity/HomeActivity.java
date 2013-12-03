package beauty.android.activity;

import java.util.*;

import android.annotation.SuppressLint;
import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;

@SuppressLint("HandlerLeak")
@SuppressWarnings("deprecation")
public class HomeActivity extends CommonActivity {
	
	private static final int BRAND = 0;
	private static final int CATEGORY = 1;
	private static final int BENEFIT = 2;
	private static final int TAG = 3;

	/** =================Gallery=============== */
	
	private Gallery pictureGallery = null;
	private int[] picture = { R.drawable.gallery1, R.drawable.gallery2,
			R.drawable.gallery3, R.drawable.gallery4, R.drawable.gallery5,
			R.drawable.gallery6, };
	// show picture
	private int index = 0;

	/**
	 * timer
	 */
	private TimerTask task = new TimerTask() {
		@Override
		public void run() {
			Message message = new Message();
			message.what = 2;
			index = pictureGallery.getSelectedItemPosition();
			index++;
			handler.sendMessage(message);
		}
	};

	/**
	 * timer Handler
	 */
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 2:
				pictureGallery.setSelection(index);
				break;
			default:
				break;
			}
		}

	};
	
	/** ======================================= */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.second_tiem);

		// gallery
		this.pictureGallery = (Gallery) findViewById(R.id.gallery);
		ImageAdapter adapter = new ImageAdapter(this);
		this.pictureGallery.setAdapter(adapter);
		Timer timer = new Timer();
		timer.schedule(task, 2000, 2000);

		this.parentControl(CommonActivity.INDEX);		
		
		DATAS = new String[] { "By Brand", "By Category", "By Benefit",
				"By Tag" };		
		setListAdapter(new SimpleAdapter(this, buildList(),
				R.layout.common_listview_text, new String[] { "img", "text",
						"img_pre" }, new int[] { R.id.img, R.id.text,
						R.id.img_pre }));
	}

	

	@Override
	public void onItemClick(AdapterView<?> list, View view, int position,
			long id) {
		if (position == BRAND) {
			intent.setClass(HomeActivity.this, BrandActivity.class);
			startActivity(intent);
		} else if (position == CATEGORY) {
			intent.setClass(HomeActivity.this, CategoryActivity.class);
			startActivity(intent);
		} else if (position == BENEFIT) {
			intent.setClass(HomeActivity.this, BenefitActivity.class);
			startActivity(intent);
		} else if (position == TAG) {
			intent.setClass(HomeActivity.this, TagActivity.class);
			startActivity(intent);
		}
	}

	/**
	 * 
	 */
	class ImageAdapter extends BaseAdapter {
		
		private Context context;

		public ImageAdapter(Context context) {
			this.context = context;
		}

		@Override
		public int getCount() {
			return Integer.MAX_VALUE;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView = new ImageView(context);
			imageView.setImageResource(picture[position % picture.length]);
			imageView.setScaleType(ImageView.ScaleType.FIT_XY);
			imageView.setLayoutParams(new Gallery.LayoutParams(
					Gallery.LayoutParams.FILL_PARENT,
					Gallery.LayoutParams.FILL_PARENT));			
			return imageView;
		}

	}

	@Override
	protected void onResume() {
		if (CommonActivity.exit) {
			CommonActivity.exit = false;
		}
		super.onResume();
	}



	@Override
	public String getTag() {
		return "home";
	}

}
