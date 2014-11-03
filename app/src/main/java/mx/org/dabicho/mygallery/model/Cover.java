package mx.org.dabicho.mygallery.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

/**
 * Cubiertas para las galerías
 */
public abstract class Cover {
    protected Context mContext;
    public Cover(Context context) {
        mContext=context;
    }
    public abstract  boolean paintCover(ImageView imageView);
}
