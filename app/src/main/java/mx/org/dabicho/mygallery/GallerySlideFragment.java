package mx.org.dabicho.mygallery;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import mx.org.dabicho.mygallery.model.Image;
import mx.org.dabicho.mygallery.services.BitmapCacheManager;
import mx.org.dabicho.mygallery.util.CurrentImageList;

import static android.util.Log.i;

/**
 * Fragment for the presentation of sliding images to be contained by {@link mx.org.dabicho.mygallery.GallerySlideActivity}
 */
public class GallerySlideFragment extends Fragment {
    private static final String TAG = "GallerySlideFragment";
    private static final String PARAM_SLIDE_TITLE = "slideTitle";
    private static final String PARAM_INITIAP_POSITION = "initiapPosition";

    private List<Image> mImages;
    private String mTitle;
    private int mPosition;
    private ImageView mImageView;
    private ExifInterface exif=null;
    private boolean mExifVisible=false;
    private TextView mBottomTextView;
    private String mCacheKey;

    public static GallerySlideFragment newInstance(int position) {
        GallerySlideFragment fragment = new GallerySlideFragment();
        Bundle args = new Bundle(1);
        args.putInt(PARAM_INITIAP_POSITION, position);
        fragment.setArguments(args);
        fragment.setArguments(args);
        return fragment;
    }


    public GallerySlideFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            mPosition = getArguments().getInt(PARAM_INITIAP_POSITION);
        else
            mPosition = 0;
        mImages = new ArrayList<Image>(CurrentImageList.getInstance().getImages());
        mTitle = CurrentImageList.getInstance().getTitle();
        mCacheKey=mImages.get(mPosition).getImageDataStream();
        Log.i(TAG,"onCreate: "+mCacheKey);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG,"onCreateView: "+mCacheKey);
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_gallery_slide,
                container, false);
        mBottomTextView =(TextView)rootView.findViewById(R.id.image_bottom_textView);
        mImageView = (ImageView) rootView.findViewById(R.id.imageView);

        // Buscamos la imagen en el cache
        String imageFilename = mImages.get(mPosition).getImageDataStream();


        mImageView.setImageResource(R.drawable.templates);


        return rootView;


        /* ----------------------------- */


        /* ------------------------------- */


    }


    /**
     * Sets a bitmap to the fragment and saves it's cacheKey to remove it from the cache when the
     * fragment is detached
     * @param bitmap The bitmap that this fragment displays
     * @param cacheKey its key into the cache
     */
    public void setBitmap(Bitmap bitmap, String cacheKey) {
        Log.i(TAG, "setBitmap: " + mCacheKey);
        if(mImageView==null ) {
            // If the imageview is null, the fragment has probably been detached
            return;
        }
        mImageView.setImageBitmap(bitmap);
        mCacheKey=cacheKey;

    }

    @Override
    public void onAttach(Activity activity) {
        Log.i(TAG,"onAttach: ");
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        Log.i(TAG,"onDetach: "+mCacheKey);
        BitmapCacheManager.getInstance().remove(mCacheKey);
        super.onDetach();
    }
}

