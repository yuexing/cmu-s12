package beauty.android.util;

import java.util.*;

import android.annotation.SuppressLint;
import android.app.*;
import android.graphics.drawable.*;
import android.view.*;
import android.widget.*;
import beauty.android.activity.CommonActivity;
import beauty.android.activity.R;

public class ImageAndTextListAdapter extends ArrayAdapter<ImageAndText> {

	private AsyncImageLoader asyncImageLoader;
	@SuppressLint("UseSparseArrays")
	private HashMap<Integer, View> called = new HashMap<Integer, View>();

	public ImageAndTextListAdapter(Activity activity,
			List<ImageAndText> imageAndTexts) {
		super(activity, 0, imageAndTexts);
		asyncImageLoader = ((CommonActivity) activity).getBeauty()
				.getAsyncImageLoader();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Activity activity = (Activity) getContext();
		
		if(called.containsKey(position)){
			return called.get(position);
		} 
		
		LayoutInflater inflater = activity.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.common_listview_image_text, null);
		called.put(position, rowView);

		ImageAndText imageAndText = getItem(position);

		// Load the image and set it on the ImageView
		String imageUrl = imageAndText.getUrl();

		final ImageView imageView = (ImageView) rowView.findViewById(R.id.image);
		imageView.setTag(imageUrl);
		
		asyncImageLoader.loadDrawable(imageUrl, new ImageCallback() {
			public void imageLoaded(Drawable imageDrawable, String imageUrl) {
				imageView.setImageDrawable(imageDrawable);
			}
		});

		TextView textView = (TextView) rowView.findViewById(R.id.text);
		textView.setText(imageAndText.getText());
		return rowView;
	}
}

class ViewCache {

	private View baseView;
	private TextView textView;
	private ImageView imageView;

	public ViewCache(View baseView) {
		this.baseView = baseView;
	}

	public TextView getTextView() {
		if (textView == null) {
			textView = (TextView) baseView.findViewById(R.id.text);
		}
		return textView;
	}

	public ImageView getImageView() {
		if (imageView == null) {
			imageView = (ImageView) baseView.findViewById(R.id.image);
		}
		return imageView;
	}
}
