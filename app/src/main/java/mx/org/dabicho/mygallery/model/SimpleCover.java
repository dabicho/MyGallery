package mx.org.dabicho.mygallery.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import mx.org.dabicho.mygallery.GalleriesManagerFragment;
import mx.org.dabicho.mygallery.R;
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

    /**
     *
     * @param id el ID de la cubierta.
     */
    public void setId(String id) {
        mId = id;
    }

    /**
     * Pinta la cubiera con el contenido del cache o con la plantilla
     * @param imageView
     * @return
     */
    @Override
    public boolean paintCover(GalleriesManagerFragment.GalleryItemViewHolder imageView) {
        Bitmap lBitmap;
        i(TAG, "paintCover: ");
        // Si tiene un bitmap, se debe de eliminar su referencia
        if(imageView.getBitmap()!=null){
            i(TAG, "paintCover: hay bitmap anterior");
            // Si el bitmap que se va a pintar y el del cache son el mismo, no se hace nada
            if(mId!=null && imageView.getBitmap()==BitmapCacheManager.getInstance().get(mId)){
                return true;
            }
            // Se decrementa su referencia y elimina
            BitmapCacheManager.getInstance().decreaseRefCount(imageView.getBitmap());
            imageView.setBitmap(null);
        }
        if (mId == null || (lBitmap = BitmapCacheManager.getInstance().get(mId)) == null) {
            // Si no hay id o no hay cache, se coloca la plantilla

            imageView.getImageView().setImageResource(R.drawable.templates);
            return false;

        } else

        { // Se coloca el resultado del cache e incrementa su referencia

            imageView.setBitmap(lBitmap);
            BitmapCacheManager.getInstance().increaseRefCount(lBitmap);
            return true;
        }

    }


}
