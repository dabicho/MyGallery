package mx.org.dabicho.mygallery.services;


import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

import static android.util.Log.i;


/**
 * Sencillo administrador de bitmaps
 */
public class BitmapCacheManager {
    private static final String TAG = "BitmapCacheManager";

    static private BitmapCacheManager cacheManager;

    private LruCache<String, Bitmap> lruCache;

    private BitmapCacheManager() {
        lruCache = new LruCache<String, Bitmap>(6){
            @Override
            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                super.entryRemoved(evicted, key, oldValue, newValue);
                i(TAG, "entryRemoved: "+key);
                //oldValue.recycle();
            }
        };
    }

    static public BitmapCacheManager getInstance() {
        if (cacheManager == null) {
            cacheManager = new BitmapCacheManager();
        }
        return cacheManager;
    }

    public void put(String key, Bitmap value) {
        Log.i(TAG,"put: Bitmap is recycled: "+value.isRecycled());
        lruCache.put(key, value);
    }

    public Bitmap get(String key) {

        return lruCache.get(key);
    }




}
