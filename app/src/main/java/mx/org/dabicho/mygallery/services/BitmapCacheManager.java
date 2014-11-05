package mx.org.dabicho.mygallery.services;


import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static android.util.Log.i;
import static android.util.Log.v;


/**
 * Sencillo administrador de bitmaps
 */
public class BitmapCacheManager {
    private static final String TAG = "BitmapCacheManager";

    static private BitmapCacheManager cacheManager;

    private LruCache<String, Bitmap> lruCache;

    private Map<Bitmap, Integer> mBitmapRefCountMap=new ConcurrentHashMap<Bitmap, Integer>();
    private List<Bitmap> mNotCachedBitmaps =new ArrayList<Bitmap>();

    private BitmapCacheManager() {
        lruCache = new LruCache<String, Bitmap>(5){
            /**
             * Remover un bitmap del cache.
             * Si al removerlo sus referencias son 0, se recicla.
             * De lo contrario, se agrega a la lista de bitmaps referenciados
             * @param evicted
             * @param key
             * @param oldValue
             * @param newValue
             */
            @Override
            protected synchronized void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                super.entryRemoved(evicted, key, oldValue, newValue);
                i(TAG, "entryRemoved: "+lruCache.size()+" + "+mNotCachedBitmaps.size());
                i(TAG, "entryRemoved: "+oldValue+" - "+mBitmapRefCountMap.get(oldValue));
                if(mBitmapRefCountMap.get(oldValue)==0){
                    mBitmapRefCountMap.remove(oldValue);
                    oldValue.recycle();
                    System.gc();
                    i(TAG, "entryRemoved: reciclado "+oldValue);
                } else {
                    mNotCachedBitmaps.add(oldValue);
                }


                //oldValue.recycle();
            }
        };
    }

    /**
     *
     * @return Una instancia de este administrador de cache.
     */
    static public BitmapCacheManager getInstance() {
        if (cacheManager == null) {
            cacheManager = new BitmapCacheManager();
        }
        return cacheManager;
    }

    /**
     * Agrega un bitmap al cache con la llave dada y pone su refcount en 1
     * @param key
     * @param value
     */
    public void put(String key, Bitmap value) {

        lruCache.put(key, value);
        mBitmapRefCountMap.put(value,0);
    }

    /**
     * Incrementa el refcount del bitmap si existe.
     * @param bitmap
     */
    public synchronized void  increaseRefCount(Bitmap bitmap){
        i(TAG, "increaseRefCount: "+lruCache.size()+" + "+mNotCachedBitmaps.size());
        if(mBitmapRefCountMap.containsKey(bitmap)){
            mBitmapRefCountMap.put(bitmap,(mBitmapRefCountMap.get(bitmap))+1);
        }
        i(TAG, "increaseRefCount: "+bitmap+" - "+mBitmapRefCountMap.get(bitmap));
    }

    /**
     * Se decrementa la cuenta de referencias del bitmap.
     * Si alcanza un valor 0 y ya no existe en el cache, se recicla.
     * @param bitmap
     */
    public synchronized void decreaseRefCount(Bitmap bitmap){
        i(TAG, "decreaseRefCount: "+lruCache.size()+" + "+mNotCachedBitmaps.size());
        if(mBitmapRefCountMap.containsKey(bitmap)){

            mBitmapRefCountMap.put(bitmap,(mBitmapRefCountMap.get(bitmap))-1);
            if(mBitmapRefCountMap.get(bitmap)==0 && mNotCachedBitmaps.contains(bitmap)){
                mNotCachedBitmaps.remove(bitmap);
                mBitmapRefCountMap.remove(bitmap);
                bitmap.recycle();

                System.gc();
                i(TAG, "decreaseRefCount: reciclado");
            }
            i(TAG, "decreaseRefCount: "+mBitmapRefCountMap.get(bitmap));
        }

    }

    public Bitmap get(String key) {

        return lruCache.get(key);
    }




}
