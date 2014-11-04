package mx.org.dabicho.mygallery.model;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

/**
 * Describe una galería de imágenes
 */
public abstract class Gallery {
    private static final String TAG =  "Gallery";
    private String mName;
    private long mCount;
    private long mId;

    private Cover mCover;

    public abstract GalleryType getGalleryType();

    /**
     * @return el nombre de la galería
     */
    public String getName() {
        return mName;
    }

    /**
     * @param name El nombre de la galería
     */
    public void setName(String name) {
        mName = name;
    }

    /**
     * @return la cantidad de imágenes que contiene la galería
     */
    public long getCount() {
        return mCount;
    }

    /**
     * @param count la cantidad de imágenes que contiene la galería
     */
    public void setCount(long count) {
        mCount = count;
    }

    /**
     * @return id de la galería
     */
    public long getId() {
        return mId;
    }

    /**
     * @param id de la galería
     */
    public void setId(long id) {
        mId = id;
    }

    /**
     *
     * @param cover constructor de cubierta
     */
    public void setCover(Cover cover) {
        mCover = cover;
    }

    public boolean hasCover(){
        return mCover!=null;
    }

    /**
     * Ordena a la cubierta que dibuje el ImageView. Si no tiene cubierta, regresa false
     * @param imageView
     * @return false si no tiene cubierta
     */
    public boolean paintCover(ImageView imageView){
        if(mCover!=null) {
            Log.i(TAG,"paintCover: Cover");
            return mCover.paintCover(imageView);
        }
        else {
            Log.i(TAG, "paintCover: no cover");
            return false;
        }
    }




}

enum GalleryType {
    CONTENT_PROVIDER,
    ALBUM,
    QUERY
}
