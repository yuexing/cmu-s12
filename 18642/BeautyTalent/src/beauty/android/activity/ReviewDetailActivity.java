package beauty.android.activity;

import java.text.DateFormat;
import java.util.Date;

import beauty.android.msg.bean.MComment;
import beauty.android.util.AsyncImageLoader;
import beauty.android.util.HtmlAdapter;
import beauty.android.util.ImageCallback;
import beauty.android.util.Utility;
import android.content.*;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;

public class ReviewDetailActivity extends ReviewActivity {

	private static final String commentDetailFormat = "<p>%s<br/> by <em>%s</em> at <em>%s</em>";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	protected void fillSelf() {
		Intent in = this.getIntent();
		comment = (MComment) in.getSerializableExtra("comment");
		this.pid = comment.getProductId();
		this.lblContent.setText("Reply to The Comment:");

		// list of comment
		Date time = new Date();
		comments = comment.getComments();
		DATAS = new String[comments.length + 1];

		// the comment itself
		if (this.isLogined() && comment.getUserId() == beauty.getUserId()) {
			Button edit = (Button) this.findViewById(R.id.edit);
			edit.setVisibility(View.VISIBLE);
			edit.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					isEdit = true;
					txtContent.setText(comment.getContent());
					txtContent.requestFocus();
				}
			});
		}

		if (comment.getImage() != null) {
			final ImageView selfPhoto = (ImageView) this
					.findViewById(R.id.self_photo);
			selfPhoto.setVisibility(View.VISIBLE);
			String imageUrl = CommonActivity.host + comment.getImage();
			AsyncImageLoader asyncImageLoader = this.getBeauty()
					.getAsyncImageLoader();
			selfPhoto.setTag(imageUrl);
			asyncImageLoader.loadDrawable(imageUrl, new ImageCallback() {
				public void imageLoaded(Drawable imageDrawable, String imageUrl) {
					selfPhoto.setImageDrawable(imageDrawable);
				}
			});
		}

		time.setTime(comment.getTimestamp());
		DATAS[0] = String.format(commentDetailFormat
				+ " <br/><em>%d</em> Replies are as follows:</p>", comment.getContent(), comment
				.getUserName(), DateFormat.getTimeInstance().format(time),
				comment.getReplyCount());

		for (int i = 0; i < comments.length; i++) {
			time.setTime(comments[i].getTimestamp());
			DATAS[i + 1] = String.format(commentDetailFormat,
					comments[i].getContent(), comments[i].getUserName() == null ? "unknown" : comments[i].getUserName(),
					DateFormat.getTimeInstance().format(time));
		}

		setListAdapter(new HtmlAdapter(ReviewDetailActivity.this, DATAS));
		// this is needed when within the RelativeView
		Utility.setListViewHeightBasedOnChildren(listViewAll);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		return;
	}

	@Override
	public String getTag() {
		return "commentdetail";
	}
}
