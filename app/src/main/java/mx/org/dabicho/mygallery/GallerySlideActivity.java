package mx.org.dabicho.mygallery;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import java.io.IOException;

import mx.org.dabicho.mygallery.model.Image;
import mx.org.dabicho.mygallery.services.BitmapCacheManager;
import mx.org.dabicho.mygallery.util.CurrentImageList;
import mx.org.dabicho.mygallery.util.ImageUtils;

import static android.util.Log.i;


/**
 * Activity for the presentation of images from a gallery in fullscreen
 */
public class GallerySlideActivity extends Activity {
    private static final String TAG = "GallerySlideActivity";

    private static final int NUM_PAGES = 5;
    private ViewPager mViewPager;
    private FragmentStatePagerAdapter mPagerAdapter;
    private GallerySlideFragment mFragment;

    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_gallery_slide_view_pager);
        mViewPager = (ViewPager) findViewById(R.id.gallery_slide_pager);
        mPagerAdapter = new GallerySlideFragmentStatePagerAdapter(getFragmentManager());
        mViewPager.setOffscreenPageLimit(5);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(CurrentImageList.getInstance().getCurrentPosition());

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    class GallerySlideFragmentStatePagerAdapter extends FragmentStatePagerAdapter {
        Fragment[] paginas = new Fragment[5];


        public GallerySlideFragmentStatePagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int i) {

            i(TAG, "getItem: " + i + ": " + mViewPager.getWidth() + "x" + mViewPager.getHeight());
            GallerySlideFragment gallerySlideFragment;
            gallerySlideFragment=GallerySlideFragment.newInstance(i);
            CurrentImageList.getInstance().setCurrentPosition(i);

            ImageLoader imageLoader = new ImageLoader(i,gallerySlideFragment);
            i(TAG, "getItem: Starting imageLoader");
            imageLoader.execute();
            i(TAG, "getItem: imageLoader started");
            return gallerySlideFragment;

        }

        @Override
        public int getCount() {

            return CurrentImageList.getInstance().getImages().size();
        }
    }



    private class ImageLoader extends AsyncTask<Integer, Void, Bitmap> {
        Image mImage;
        int mImageIdx;
        GallerySlideFragment mGallerySlideFragment;
        ImageLoader(int imageIdx, GallerySlideFragment gallerySlideFragment){
            mImageIdx=imageIdx;
            mImage=CurrentImageList.getInstance().getImages().get(mImageIdx);
            mGallerySlideFragment=gallerySlideFragment;
        }

        @Override
        protected Bitmap doInBackground(Integer... params) {
            Point preferedSize=new Point();
            GallerySlideActivity.this.getWindowManager().getDefaultDisplay().getSize(preferedSize);
            i(TAG, "doInBackground: "+mImageIdx);


            i(TAG, "doInBackground: "+mImage.getImageDataStream());
            Bitmap bitmap = BitmapCacheManager.getInstance().get(mImage.getImageDataStream());

            if (bitmap == null) {
                BitmapFactory.Options lOptions = new BitmapFactory.Options();
                lOptions.inJustDecodeBounds = true;

                BitmapFactory.decodeFile(mImage.getImageDataStream(), lOptions);
                ExifInterface exif = null;
                try {
                    exif = new ExifInterface(mImage.getImageDataStream());
                    i(TAG, "generateSimpleCoverBitmap: " + Thread.currentThread().getId() + " orientación: " + exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL));
                    switch (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                        case ExifInterface.ORIENTATION_ROTATE_90:
                        case ExifInterface.ORIENTATION_ROTATE_270:
                        case ExifInterface.ORIENTATION_TRANSPOSE:
                        case ExifInterface.ORIENTATION_TRANSVERSE:
                            lOptions.inSampleSize = ImageUtils.calculateMinInSampleSize(lOptions,
                                    preferedSize.y, preferedSize.x, true);
                            break;
                        default:
                            lOptions.inSampleSize = ImageUtils.calculateMinInSampleSize(lOptions,
                                    preferedSize.x, preferedSize.y, false);

                    }
                } catch (IOException e) {
                    Log.e(TAG, "onCreateView: ", e);
                }

                i(TAG, "onCreateView: SampleSize: " + lOptions.inSampleSize);
                lOptions.inJustDecodeBounds = false;
                bitmap=ImageUtils.rotateBitmap(exif,
                        BitmapFactory.decodeFile(mImage.getImageDataStream(), lOptions));
                BitmapCacheManager.getInstance().put(mImage.getImageDataStream(),
                        bitmap);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap aBitmap) {
            mGallerySlideFragment.setBitmap(aBitmap);
        }
    }

}
