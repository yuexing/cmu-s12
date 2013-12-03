package beauty.android.util;

import android.graphics.drawable.Drawable;

public interface ImageCallback {
	public void imageLoaded(Drawable imageDrawable, String imageUrl);
}