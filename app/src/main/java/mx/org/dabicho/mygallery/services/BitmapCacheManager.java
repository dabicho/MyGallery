package mx.org.dabicho.mygallery.services;


import android.graphics.Bitmap;
import android.util.LruCache;


/**
 * Sencillo administrador de bitmaps
 */
public class BitmapCacheManager {
    private static final String TAG = "BitmapCacheManager";

    static private BitmapCacheManager cacheManager;

    private LruCache<String, Bitmap> lruCache;

    private BitmapCacheManager() {
        lruCache = new LruCache<String, Bitmap>(250){
            @Override
            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                super.entryRemoved(evicted, key, oldValue, newValue);
                oldValue.recycle();
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
        lruCache.put(key, value);
    }

    public Bitmap get(String key) {
        return lruCache.get(key);
    }




}
