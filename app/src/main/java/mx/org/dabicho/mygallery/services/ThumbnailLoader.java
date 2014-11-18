package mx.org.dabicho.mygallery.services;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dabicho on 17/11/14.
 */
public class ThumbnailLoader<Token> extends HandlerThread {
    private static final String TAG = "ThumbnailLoader";


    private static final int MESSAGE_LOAD = 0;
    private static final int MESSAGE_PRELOAD_CACHE = 1;

    Handler mHandler;
    Handler mResponseHandler;

    Map<Token, String> requestMap = Collections.synchronizedMap(new HashMap<Token, String>());

    Listener<Token> mListener;

    /**
     * An interface to be used with this thread to communicate back the result of each request to
     * the calling thread
     *
     * @param <Token>
     */
    public interface Listener<Token> {
        /**
         * This method is to be called when the thumbnail bitmap has been loaded
         *
         * @param token
         * @param thumbnail
         */
        void onThumbnailLoaded(Token token, Bitmap thumbnail);
    }


    /**
     * This constructor receives a handler to communicate the results back to the creator of the
     * thread
     *
     * @param responseHandler
     */
    public ThumbnailLoader(Handler responseHandler) {
        super(TAG);
    }

    public void setListener(Listener<Token> listener) {
        mListener = listener;
    }

    @Override
    protected void onLooperPrepared() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == MESSAGE_LOAD) {
                    Token token = (Token) msg.obj;
                    handleRequest(token);
                } else if(msg.what == MESSAGE_PRELOAD_CACHE) {
                    String uri = (String) msg.obj;
                    if(uri != null && BitmapCacheManager.getInstance().get(uri) == null) {
                        handleCacheRequest(uri);
                    }
                }

            }
        };
        super.onLooperPrepared();
    }

    /**
     * This method handles a request to add an fileName to the bitmap cache. This has less priority
     * than handleRequest
     *
     * @param fileName The full path to the filename
     */
    private void handleCacheRequest(String fileName) {


        final Bitmap bitmap;

        bitmap = BitmapFactory.decodeFile(fileName);
        BitmapCacheManager.getInstance().put(fileName, bitmap);

    }

    /**
     * Handles a request to load a thumbnail. Token is used to update gui data and to reference
     * the filename in a map
     */
    private void handleRequest(final Token token) {

        final String filename = requestMap.get(token);
        if(filename == null)
            return;
        final Bitmap bitmap;
        bitmap = BitmapFactory.decodeFile(filename);
        BitmapCacheManager.getInstance().put(filename, bitmap);
        mResponseHandler.post(new Runnable() {
            @Override
            public void run() {
                if(requestMap.get(token) != filename) {
                    return;
                }
                requestMap.remove(token);
                mListener.onThumbnailLoaded(token, bitmap);
            }
        });

    }

    /**
     * Enqueue a request to add a filename to the cache
     */
    public void queuePreloadCache(String filename){
        if(!mHandler.hasMessages(MESSAGE_PRELOAD_CACHE, filename)){
            mHandler.obtainMessage(MESSAGE_PRELOAD_CACHE,filename).sendToTarget();
        }
    }

    /**
     * Enqueue a request to load an image and a token
     */
    public void queueThumbnail(Token token, String filename){
        requestMap.put(token, filename);
        {
            Message message=mHandler.obtainMessage(MESSAGE_LOAD,token);
            mHandler.sendMessageAtFrontOfQueue(message);
        }
    }

    /**
     * Removes a token from the queue
     * @param token
     */
    public void dequeueThumbnail(Token token){
        requestMap.remove(token);
    }

    /**
     * Clears the message queue and the token map
     */
    public void clearQueue(){
        mHandler.removeMessages(MESSAGE_LOAD);
        requestMap.clear();
    }
}
