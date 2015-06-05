package mx.org.dabicho.mygallery;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;

import mx.org.dabicho.mygallery.model.GalleryType;

/**
 * Activity to display the images from a gallery
 */
public class GalleryActivity extends FragmentActivity {
    private static final String TAG = "GalleryActivity";

    public static final String EXTRA_GALLERY="mx.org.dabicho.myGallery.GALLERY";


    @Override
    Fragment getFragment() {
        Bundle params=getIntent().getBundleExtra(EXTRA_GALLERY);
        GalleryType galleryType= (GalleryType) params.getSerializable(GalleryFragment.PARAM_GALLERY_TYPE);
        long galleryId=params.getLong(GalleryFragment.PARAM_GALLERY_ID);
        String galleryTitle=params.getString(GalleryFragment.PARAM_GALLERY_TITLE);

        return GalleryFragment.newInstance(galleryId,galleryType,galleryTitle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getActionBar().setBackgroundDrawable(new ColorDrawable(0xDD000000));
        Log.v(TAG, "onCreate: onCreate()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, "onStart: onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "onResume: onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "onPause: onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "onStop: onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy: onDestroy()");
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        Log.v(TAG, "onRestart: onRestart()");
    }
}
