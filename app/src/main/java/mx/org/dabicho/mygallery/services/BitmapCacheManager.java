package mx.org.dabicho.mygallery.services;


import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static android.util.Log.i;
import static android.util.Log.d;
import static android.util.Log.v;


/**
 * simple bitmap cache manager that also holds a count for external references to the bitmap to
 * recycle them when no longer needed.
 */
public class BitmapCacheManager {
    private static final String TAG = "BitmapCacheManager";

    static private BitmapCacheManager cacheManager;

    private LruCache<String, Bitmap> lruCache;
    /**
     * A list to keep track of how many times is a bitmap referenced
     */
    private Map<Bitmap, Integer> mBitmapRefCountMap=new ConcurrentHashMap<Bitmap, Integer>();
    /**
     * A list of bitmaps no longer on the cache
     */
    private List<Bitmap> mNotCachedBitmaps =new ArrayList<Bitmap>();

    private BitmapCacheManager() {
        lruCache = new LruCache<String, Bitmap>(16){
            /**
             * Remove a bitmap from the cache
             * If its referenced 0 times, it should be recycled.
             * Else, it is added to the referenced bitmaps list
             * @param evicted
             * @param key
             * @param oldValue
             * @param newValue
             */
            @Override
            protected synchronized void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                super.entryRemoved(evicted, key, oldValue, newValue);
                d(TAG, "entryRemoved: "+lruCache.size()+" + "+mNotCachedBitmaps.size());
                d(TAG, "entryRemoved: "+oldValue+" - "+mBitmapRefCountMap.get(oldValue));
                if(mBitmapRefCountMap.get(oldValue)==0){
                    mBitmapRefCountMap.remove(oldValue);
                    oldValue.recycle();
                    System.gc();
                    d(TAG, "entryRemoved: recycled "+oldValue);
                } else {
                    mNotCachedBitmaps.add(oldValue);
                }
            }
        };
    }

    /**
     *
     * @return An instance of this cache manager
     */
    static public BitmapCacheManager getInstance() {
        if (cacheManager == null) {
            cacheManager = new BitmapCacheManager();
        }
        return cacheManager;
    }

    /**
     * Adds a bitmap to the cache and sets its ref. count to 0
     * @param key
     * @param value
     */
    public void put(String key, Bitmap value) {
        d(TAG, "put: "+key+" - "+value);
        lruCache.put(key, value);
        mBitmapRefCountMap.put(value,0);

    }

    /**
     * Increments the refcount for the given bitmap if it exists on the list
     * @param bitmap
     */
    public synchronized void  increaseRefCount(Bitmap bitmap){
        d(TAG, "increaseRefCount: "+lruCache.size()+" + "+mNotCachedBitmaps.size());
        if(bitmap==null)
            return;
        if(mBitmapRefCountMap.containsKey(bitmap)){
            mBitmapRefCountMap.put(bitmap,(mBitmapRefCountMap.get(bitmap))+1);
        }
        d(TAG, "increaseRefCount: "+bitmap+" - "+mBitmapRefCountMap.get(bitmap));
    }

    /**
     * Decreases the refcount for the given bitmap if it exists.
     * If the references reach 0 and it is no longer on the cache, it is recycled.
     * @param bitmap
     */
    public synchronized void decreaseRefCount(Bitmap bitmap){
        d(TAG, "decreaseRefCount: "+lruCache.size()+" + "+mNotCachedBitmaps.size());
        if(mBitmapRefCountMap.containsKey(bitmap)){

            mBitmapRefCountMap.put(bitmap,(mBitmapRefCountMap.get(bitmap))-1);
            if(mBitmapRefCountMap.get(bitmap)==0 && mNotCachedBitmaps.contains(bitmap)){
                mNotCachedBitmaps.remove(bitmap);
                mBitmapRefCountMap.remove(bitmap);
                bitmap.recycle();

                System.gc();
                i(TAG, "decreaseRefCount: recycled");
            }
            i(TAG, "decreaseRefCount: "+mBitmapRefCountMap.get(bitmap));
        }

    }

    public Bitmap get(String key) {
        d(TAG, "get: "+key);
        return lruCache.get(key);
    }
}
