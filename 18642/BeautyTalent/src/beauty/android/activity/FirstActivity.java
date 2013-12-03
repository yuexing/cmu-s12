package beauty.android.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

@SuppressLint("HandlerLeak")
public class FirstActivity extends CommonActivity {
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 2:
				Intent intent = new Intent(FirstActivity.this,
						HomeActivity.class);
				FirstActivity.this.startActivityForResult(intent, 0);
				overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
				FirstActivity.this.finish();
			default:
				break;
			}
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.first_tiem);
		
		handler.sendEmptyMessageDelayed(2, 900L);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public String getTag() {
		return "first";
	}
}