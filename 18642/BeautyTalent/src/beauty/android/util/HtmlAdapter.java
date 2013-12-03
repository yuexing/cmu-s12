package beauty.android.util;

import beauty.android.activity.R;
import android.app.*;
import android.content.Context;
import android.text.Html;
import android.view.*;
import android.widget.*;

public class HtmlAdapter extends ArrayAdapter<String> {
	
	final Context activity;
	final String[] values;
	
	public HtmlAdapter(Activity activity, String[] objects) {
		super(activity, R.layout.common_listview_text, objects);
		this.activity = activity;
		this.values = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) activity
		        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.common_listview_text_1, parent, false);
		
		TextView textView = (TextView)rowView.findViewById(R.id.text);
		textView.setText(Html.fromHtml(values[position]));		
		return rowView;
	}
}
