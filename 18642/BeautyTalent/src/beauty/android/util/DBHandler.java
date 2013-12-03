package beauty.android.util;

import beauty.android.msg.bean.MProduct;
import android.content.*;
import android.database.*;
import android.database.sqlite.*;

public class DBHandler extends SQLiteOpenHelper {

	private static final int DB_VERSION = 1;
	private static final String DB_NAME = "beauty_android";
	public static final String TBL_FAV = "fav";
	public static final String TBL_RATE = "rate";

	private static final String KEY_PID = "pid";
	private static final String KEY_NAME = "name";
	private static final String KEY_URL = "image";

	private static final String sql_create = "CREATE TABLE %s (" + KEY_PID + " INTEGER PRIMARY KEY, " + KEY_NAME
			+ " TEXT, " + KEY_URL + " TEXT)";

	private static final String sql_drop = "DROP TABLE IF EXISTS %s";
	
	private static final String sql_select = "SELECT * FROM %s"; 
	
	public DBHandler(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	// create tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(String.format(sql_create, TBL_FAV));
		db.execSQL(String.format(sql_create, TBL_RATE));
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		db.execSQL(String.format(sql_drop, TBL_FAV));
		db.execSQL(String.format(sql_drop, TBL_RATE));

		// recreate
		onCreate(db);
	}

	// ////////////////////////////CURD

	// the application should make sure no
	// duplicated pids
	public void add(MProduct p, String tbl) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues v = new ContentValues();
		v.put(KEY_PID, p.getId());
		v.put(KEY_NAME, p.getName());
		v.put(KEY_URL, p.getImage());

		db.insert(tbl, null, v);
		db.close();
	}

	// read according to pid
	public MProduct read(int pid, String tbl) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(tbl,
				new String[] { KEY_PID, KEY_NAME, KEY_URL }, KEY_PID + "=?",
				new String[] { String.valueOf(pid) }, null, null, null);
		
		if (!cursor.moveToFirst()){
			db.close();
			return null;
		}
		MProduct p = new MProduct(cursor.getInt(0), cursor.getString(1), cursor.getString(2));
		db.close();
		return p;
	}
	
	// get all
	public MProduct[] readAll(String tbl){
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(String.format(sql_select, tbl), null);
		
		if(cursor.moveToFirst()){
			MProduct[] ps = new MProduct[cursor.getCount()];
			int i = 0;
			do{
				MProduct p = new MProduct(cursor.getInt(0), cursor.getString(1), cursor.getString(2));
				ps[i++] = p;
			} while(cursor.moveToNext());
			db.close();
			return ps;
		}
		db.close();
		return new MProduct[0];
	}
	
	// update 
	// delete
	public void delete(int id, String tbl) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(tbl, KEY_PID + "=?", new String[]{String.valueOf(id)});
		db.close();
	}
}
