package mx.org.dabicho.mygallery;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mx.org.dabicho.mygallery.model.Image;
import mx.org.dabicho.mygallery.services.BitmapCacheManager;
import mx.org.dabicho.mygallery.util.CurrentImageList;
import mx.org.dabicho.mygallery.util.ImageUtils;

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

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_gallery_slide,
                container, false);

         mImageView = (ImageView) rootView.findViewById(R.id.imageView);
        // Buscamos la imagen en el cache
        String imageFilename=mImages.get(mPosition).getImageDataStream();


            mImageView.setImageResource(R.drawable.templates);
            return rootView;


        /* ----------------------------- */


        /* ------------------------------- */


    }

    public void setBitmap(Bitmap bitmap){
        mImageView.setImageBitmap(bitmap);

    }

    @Override
    public void onAttach(Activity activity) {

        super.onAttach(activity);

    }
}

