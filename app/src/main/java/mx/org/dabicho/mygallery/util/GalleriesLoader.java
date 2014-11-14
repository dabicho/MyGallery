package mx.org.dabicho.mygallery.util;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.ImageColumns;

import java.util.ArrayList;
import java.util.List;

import mx.org.dabicho.mygallery.model.ContentProviderGallery;
import mx.org.dabicho.mygallery.model.Gallery;
import mx.org.dabicho.mygallery.model.SimpleCover;

/**
 * Cargador de galerias. Crea la lista de galerías disponibles
 */
public class GalleriesLoader extends DataLoader<List<Gallery>> {
    private static final String TAG = "GalleriesLoader";


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

    }

    @Override
    public List<Gallery> loadInBackground() {
        Context context=getContext();
        RenderScriptUtils.getInstance(context);
        ArrayList<Gallery> galerias = new ArrayList<Gallery>();
        Cursor lCursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                galleryQueryProjection, "1=1) group by ( " + ImageColumns.BUCKET_ID, null,
                ImageColumns.BUCKET_DISPLAY_NAME + " asc");
                //ImageColumns.BUCKET_DISPLAY_NAME + " asc");
        lCursor.moveToFirst();
        while (!lCursor.isAfterLast()) {
            Gallery lGallery = new ContentProviderGallery();
            lGallery.setName(lCursor.getString(0));
            lGallery.setGalleryId(lCursor.getLong(1));
            lGallery.setCount(lCursor.getLong(2));
            lGallery.setCover(new SimpleCover(context,null, lCursor.getLong(1)));
            galerias.add(lGallery);
            //Log.i(TAG, "loadInBackground: " + lGallery.getName());
            lCursor.moveToNext();
        }
        lCursor.close();
        return galerias;
    }
}
