package mx.org.dabicho.mygallery.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import mx.org.dabicho.mygallery.services.BitmapCacheManager;

import static android.util.Log.i;

/**
 * Cubierta que consta de un simple ImageView correspondiente a una imagen en el content provider
 */
public class SimpleCover extends Cover {
    private static final String TAG = "SimpleCover";
    private String mId;

    public SimpleCover(Context context, String id) {
        super(context);
        mId = id;
    }

    public void setId(String id) {
        mId = id;
    }

    @Override
    public boolean paintCover(ImageView imageView) {
        Bitmap lBitmap;
        if (mId == null || (lBitmap = BitmapCacheManager.getInstance().get(mId)) == null) {
            i(TAG, "paintCover: Sin Cache");
            imageView.getHeight();
            imageView.getWidth();
            imageView.setImageResource(android.R.drawable.alert_dark_frame);
            return false;

        } else

        {
            i(TAG, "paintCover: En Cache");
            imageView.setImageBitmap(lBitmap);
            return true;
        }

    }


}
