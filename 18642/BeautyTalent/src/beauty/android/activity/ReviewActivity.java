package beauty.android.activity;

import com.google.gson.Gson;

import java.io.*;
import java.util.*;

import org.apache.http.HttpResponse;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.entity.mime.*;
import org.apache.http.entity.mime.content.*;
import org.apache.http.impl.client.*;

import beauty.android.msg.bean.*;
import beauty.android.util.*;
import beauty.web.action.service.msg.*;
import android.content.*;
import android.graphics.*;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;

/**
 * to send a review: content, timestamp, pid, replyId, userId, type
 * 
 * @author amixyue
 * 
 */
public class ReviewActivity extends CommonActivity {

	public static enum Type {
		origin, reply
	}

	private static final String coUrl = CommonActivity.host
			+ "getcomments.d?type=%s&id=%s";
	private static final String postUrl = CommonActivity.host + "addcomment.d";
	private static final int targetW = 100, targetH = 100;

	private static final int len = 33;
	private static final int TAKE_PHOTO = 0;
	private static final int TAKE_VIDEO = 1;

	protected ImageView imageView;
	protected Button btnPhoto;
	protected Button btnSubmit;
	protected Button btnCancel;
	protected TextView lblContent;
	protected EditText txtContent;

	// send bitmap if there is
	protected Bitmap mImageBitmap;
	protected boolean isEdit = false;

	protected MComment[] comments;
	// when comment is none, or the activity is
	// CommentDetailActivity is post orig
	protected MComment comment;
	protected int pid;
	protected CommonActivity.Type type;

	private String strImgPath = "";
	// ///////////////////////////////////// photo related
	private Button.OnClickListener mTakePicOnClickListener = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent takePictureIntent = new Intent(
					MediaStore.ACTION_IMAGE_CAPTURE);

			// error on Xiaomi
			strImgPath = Environment.getExternalStorageDirectory().toString()
					+ "/BEAUTYPIC/";
			File out = new File(strImgPath);
			if (!out.exists()) {
				out.mkdir();
			}
			try {
				out = File.createTempFile("BT_", ".jpg", out);
			} catch (IOException e) {
				displayError(getTag(), e.getMessage());
				return;
			}
			strImgPath = out.getAbsolutePath();
			Uri uri = Uri.fromFile(out);

			takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
			takePictureIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
			startActivityForResult(takePictureIntent, TAKE_PHOTO);
		}
	};

	protected void videoMethod() {
		Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
		startActivityForResult(intent, TAKE_VIDEO);
	}

	protected Bitmap decode(ImageView imageView, String file) {
		/* Get the size of the image */
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(file, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;

		/* Figure out which way needs to be reduced less */
		int scaleFactor = 1;
		scaleFactor = Math.max(photoW / targetW, photoH / targetH);

		Log.w(this.getTag(), String.format(
				"target(%d, %d) photo(%d, %d), scale(%d)", targetW, targetH,
				photoW, photoH, scaleFactor));

		/* Set bitmap options to scale the image decode target */
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
		Bitmap bitmap = BitmapFactory.decodeFile(file, bmOptions);
		return bitmap;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case TAKE_PHOTO:
				try {
					mImageBitmap = decode(imageView, strImgPath);
					imageView.setImageBitmap(mImageBitmap);
					// visible
					imageView.setVisibility(View.VISIBLE);
					btnCancel.setVisibility(View.VISIBLE);
				} catch (Exception e) {
					Log.e(this.getTag(), e.getMessage());
				}
				break;
			default:
				break;
			}
		}
	}

	// /////////////////////////////////////// photo related

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.comment);
		this.btnPhoto = (Button) this.findViewById(R.id.btn_photo);
		this.btnSubmit = (Button) this.findViewById(R.id.btn_submit);
		this.btnCancel = (Button) this.findViewById(R.id.btn_cancel_photo);
		this.imageView = (ImageView) this.findViewById(R.id.photo);
		this.txtContent = (EditText) this.findViewById(R.id.comment_content);
		this.lblContent = (TextView) this.findViewById(R.id.label_content);

		this.setBtnListenerOrDisable(btnPhoto, mTakePicOnClickListener,
				MediaStore.ACTION_IMAGE_CAPTURE);

		this.btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mImageBitmap = null;
				Log.d(getTag(), "cancel the photo");
				// invisible
				imageView.setVisibility(View.GONE);
				btnCancel.setVisibility(View.INVISIBLE);
			}
		});

		this.btnSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String content = txtContent.getText().toString().trim();
				if (content.length() == 0) {
					displayError("Alert", "Content can't be empty");
					return;
				}
				// run asyncTask to send
				new SendComment().execute(content);
			}
		});

		this.parentControl(CommonActivity.MY);

		this.fillSelf();

		if (!this.isLogined() || this.type == CommonActivity.Type.user) {
			this.findViewById(R.id.post_comment).setVisibility(View.GONE);
		}
	}

	protected void fillSelf() {
		Intent i = this.getIntent();
		this.pid = i.getIntExtra("id", -1);
		this.type = (CommonActivity.Type) i.getSerializableExtra("type");
		new LoadComments().execute(type.toString(), String.valueOf(pid));
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		intent.putExtra("comment", comments[position]);
		intent.setClass(this, ReviewDetailActivity.class);
		startActivity(intent);
	}

	@Override
	public String getTag() {
		return "Review";
	}

	class SendComment extends AsyncTask<String, String, BaseMsg> {
		@Override
		protected BaseMsg doInBackground(String... params) {
			String content = params[0];
			String timestamp = String.valueOf(new Date().getTime());
			String pidStr = String.valueOf(pid);
			String uidStr = String.valueOf(beauty.getUserId());

			Type type = null;
			String replyId = null;

			if (comment != null) {
				replyId = String.valueOf(comment.getId());
				type = Type.reply;
			} else {
				// fake!
				replyId = "-1";
				type = Type.origin;
			}

			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(postUrl);
			MultipartEntity ent = new MultipartEntity(
					HttpMultipartMode.BROWSER_COMPATIBLE);

			if (mImageBitmap != null) {
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				mImageBitmap.compress(CompressFormat.JPEG, 0, stream);
				byte[] byteArray = stream.toByteArray();
				ByteArrayBody file = new ByteArrayBody(byteArray, "file");
				ent.addPart("file", file);
			}

			BaseMsg bmsg = new BaseMsg();
			try {
				ent.addPart("content", new StringBody(content));
				ent.addPart("timestamp", new StringBody(timestamp));
				ent.addPart("pid", new StringBody(pidStr));
				ent.addPart("userId", new StringBody(uidStr));
				ent.addPart("type", new StringBody(type.toString()));
				ent.addPart("replyId", new StringBody(replyId));
				if (isEdit) {
					ent.addPart("edit", new StringBody("true"));
					ent.addPart("id",
							new StringBody(String.valueOf(comment.getId())));
				}
				post.setEntity(ent);
				HttpResponse resp = client.execute(post);
				bmsg = new Gson().fromJson(readJson(resp.getEntity()
						.getContent()), BaseMsg.class);
			} catch (Exception e) {
				e.printStackTrace();
				bmsg.addError(e.getMessage());
			}
			return bmsg;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			displayProgressDialog();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(BaseMsg result) {
			super.onPostExecute(result);
			progressDialog.dismiss();

			if (result == null)
				return;

			if (result.getErrors() != null && result.getErrors().size() > 0) {
				displayError("Post Comment", parseError(result.getErrors()));
			} else {
				Intent i = new Intent(ReviewActivity.this, ReviewActivity.class);
				i.putExtra("id", pid);
				i.putExtra("type", CommonActivity.Type.product);
				startActivity(i);
			}
		}
	}

	// ////////
	class LoadComments extends AsyncTask<String, String, CommentMsg> {
		@Override
		protected CommentMsg doInBackground(String... arg0) {
			Log.d(getTag(), "url: " + String.format(coUrl, arg0[0], arg0[1]));

			CommentMsg csm = new CommentMsg();
			try {
				csm = new Gson().fromJson(
						getJsonFromGet(String.format(coUrl, arg0[0], arg0[1])),
						CommentMsg.class);
			} catch (Exception e) {
				csm.addError(e.getMessage());
			}
			return csm;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			displayProgressDialog();
		}

		@Override
		protected void onPostExecute(CommentMsg result) {
			super.onPostExecute(result);
			progressDialog.dismiss();

			if (result == null)
				return;

			if (result.getErrors() != null && result.getErrors().size() > 0) {
				displayError(getTag(), parseError(result.getErrors()));
				return;
			}
			// update UI
			comments = result.getComments();
			if (comments == null || comments.length == 0) {
				findViewById(R.id.no_record).setVisibility(View.VISIBLE);
			} else {
				DATAS = new String[comments.length];
				for (int i = 0; i < comments.length; i++) {
					DATAS[i] = comments[i].getShort(len);
				}
				setListAdapter(new HtmlAdapter(ReviewActivity.this, DATAS));
				Log.d(getTag(), DATAS[0]);
				// this is needed when within the RelativeView
				Utility.setListViewHeightBasedOnChildren(listViewAll);
			}
		}
	}
}
