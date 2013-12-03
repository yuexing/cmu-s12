package beauty.android.activity;


import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import beauty.android.util.SpinnerDialog;
import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.*;
import android.util.Log;
import android.view.View;
import android.widget.*;

public class AudioActivity extends CommonActivity {

	private Button btnStart;
	private Button btnStop;
	private Button btnList;
	private TextView txtRecord;
	private TextView txtPlay;
	
	private String strVedioPath = "";	
	private MediaRecorder mMediaRecorder = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_audio);
		this.parentControl(CommonActivity.MY);
		
		this.btnStart = (Button) this.findViewById(R.id.start);
		this.btnStop = (Button) this.findViewById(R.id.stop);
		this.btnList = (Button) this.findViewById(R.id.list);
		
		this.txtPlay = (TextView) this.findViewById(R.id.playing);
		this.txtRecord = (TextView) this.findViewById(R.id.recording);

		this.btnStart.setOnClickListener(this);
		this.btnStop.setOnClickListener(this);
		this.btnList.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v){
		if(v == btnStart){
			startAudio();
		} else if (v == btnStop) {
			stopAudio();
		} else if (v == btnList) {
			listAudio();
		} else {
			super.onClick(v);
		}
		
	}

	@Override
	public String getTag() {
		return "benefit";
	}
	

	protected void startAudio() {
		strVedioPath = Environment.getExternalStorageDirectory().toString()
				+ "/BEAUTYAUD/";
		File out = new File(strVedioPath);
		if (!out.exists()) {
			out.mkdir();
		}
		try {
			out = File.createTempFile("BT_", ".amr", out);
		} catch (IOException e) {
			displayError(getTag(), e.getMessage());
			return;
		}
		strVedioPath = out.getAbsolutePath();
		try {
			mMediaRecorder = new MediaRecorder();
			mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
			mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
			mMediaRecorder.setOutputFile(strVedioPath);
			mMediaRecorder.prepare();
			mMediaRecorder.start();
			
			this.txtRecord.setVisibility(View.VISIBLE);
		} catch (Exception e) {
			displayError(getTag(), e.getMessage());
		}
	}

	protected void stopAudio(){
		mMediaRecorder.stop();  
        mMediaRecorder.release();  
        mMediaRecorder = null;  
        
        this.txtRecord.setVisibility(View.GONE);
	}
	
	protected SpinnerDialog dia;
	protected void listAudio(){
		File dir = new File(Environment.getExternalStorageDirectory(), "BEAUTYAUD");
		final File[] fs = dir.listFiles(new AudFilter());
		String[] fnames = new String[fs.length];
		for(int i = 0; i < fs.length; i++){
			fnames[i] = fs[i].getName();
		}		
		dia = new SpinnerDialog(this, fnames, new SpinnerDialog.DialogListener() {	
			@Override
			public void ready(int n) {
				playAudio(fs[n]);
				dia.dismiss();
			}			
			@Override
			public void cancelled() {
				dia.dismiss();
			}
		});
		dia.show();
	}
	
	protected void playAudio(File file){
		Intent i = new Intent();
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file), "audio");
		this.startActivity(intent); 
		
		this.txtPlay.setVisibility(View.VISIBLE);
	}
	
	private static class AudFilter implements FilenameFilter{
		@Override
		public boolean accept(File arg0, String name) {
			return (name.endsWith(".amr") && name.startsWith("BT_"));
		}
	}

	@Override
	protected void onResume(){
		super.onResume();
		Log.w(getTag(), "Resume... more work can be done");
		this.txtPlay.setVisibility(View.GONE);
	}
	
}
