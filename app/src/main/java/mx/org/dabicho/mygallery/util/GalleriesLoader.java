package mx.org.dabicho.mygallery.util;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.ImageColumns;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import mx.org.dabicho.mygallery.model.Gallery;

/**
 * Cargador de galerias. Crea la lista de galerías disponibles
 */
public class GalleriesLoader extends DataLoader<List<Gallery>> {
    private static final String TAG = "GalleriesLoader";

    private Context mContext;
    /**
     * Lista de columnas de consulta para las galerías
     */
    private String[] galleryQueryProjection =
            {ImageColumns.BUCKET_DISPLAY_NAME
                    , ImageColumns.BUCKET_ID
                    , " count(*) as COUNT "
            };
    private String[] galleryImagesQueryProjection={};

    public GalleriesLoader(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public List<Gallery> loadInBackground() {
        ArrayList<Gallery> galerias = new ArrayList<Gallery>();
        Cursor lCursor = mContext.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                galleryQueryProjection, "1=1) group by ( " + ImageColumns.BUCKET_ID, null,
                null);
                //ImageColumns.BUCKET_DISPLAY_NAME + " asc");
        lCursor.moveToFirst();
        while (!lCursor.isAfterLast()) {
            Gallery lGallery = new Gallery();
            lGallery.setName(lCursor.getString(0));
            lGallery.setId(lCursor.getLong(1));
            lGallery.setCount(lCursor.getLong(2));
            galerias.add(lGallery);
            Log.i(TAG, "loadInBackground: " + lGallery.getName());
            lCursor.moveToNext();
        }
        lCursor.close();
        return galerias;
    }
}
