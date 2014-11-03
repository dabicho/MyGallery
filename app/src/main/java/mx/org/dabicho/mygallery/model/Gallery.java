package mx.org.dabicho.mygallery.model;

import android.graphics.Bitmap;

/**
 * Describe una galería de imágenes
 */
public class Gallery {
    private String mName;
    private long mCount;
    private long mId;
    private Bitmap mBitmap;

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

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }
}
