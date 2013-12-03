package beauty.android.util;

import android.app.*;
import android.content.*;
import android.view.*;
import android.widget.*;
import android.os.*;
import beauty.android.activity.*;

public class SpinnerDialog extends Dialog {
    private String[] mList;
    private Context mContext;
    private Spinner mSpinner;

   public interface DialogListener {
        public void ready(int n);
        public void cancelled();
    }

    private DialogListener mReadyListener;

    public SpinnerDialog(Context context, String[] list, DialogListener readyListener) {
        super(context);
        mReadyListener = readyListener;
        mContext = context;
        mList = list;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spinner_dialog);
        mSpinner = (Spinner) findViewById (R.id.dialog_spinner);
        
        ArrayAdapter<String> adapter = new ArrayAdapter<String> (mContext, android.R.layout.simple_spinner_dropdown_item, mList);
        mSpinner.setAdapter(adapter);

        Button buttonOK = (Button) findViewById(R.id.dialogOK);
        Button buttonCancel = (Button) findViewById(R.id.dialogCancel);
        
        buttonOK.setOnClickListener(new android.view.View.OnClickListener(){
            public void onClick(View v) {
                int n = mSpinner.getSelectedItemPosition();
                mReadyListener.ready(n);
                SpinnerDialog.this.dismiss();
            }
        });
        
        buttonCancel.setOnClickListener(new android.view.View.OnClickListener(){
            public void onClick(View v) {
                mReadyListener.cancelled();
                SpinnerDialog.this.dismiss();
            }
        });
    }
}