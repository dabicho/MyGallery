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
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;
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
    private TextView mImageDataTextView;
    private boolean mDataVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_gallery_slide_view_pager);
        mViewPager = (ViewPager) findViewById(R.id.gallery_slide_pager);
        mPagerAdapter = new GallerySlideFragmentStatePagerAdapter(getFragmentManager());
        mImageDataTextView = (TextView) findViewById(R.id.image_bottom_textView);
        mViewPager.setOffscreenPageLimit(5);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(CurrentImageList.getInstance().getCurrentPosition());
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int i, float v, int i2) {
                i(TAG, "onPageScrolled: ");
            }

            @Override
            public void onPageSelected(int i) {
                i(TAG, "onPageSelected: ");
                mImageDataTextView.setText(new File(CurrentImageList.getInstance().getImages().
                        get(i).getImageDataStream()).getName());
            }

            @Override
            public void onPageScrollStateChanged(int i) {
                i(TAG, "onPageScrollStateChanged: ");
            }
        });

        final GestureDetector gestureDetector = new GestureDetector(this, new GestureListener());
        mViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return gestureDetector.onTouchEvent(event);
            }

        });

        CurrentImageList.getInstance().getImages().get(mViewPager.getCurrentItem()).loadExif();
        mImageDataTextView.setText(CurrentImageList.getInstance().getImages().get(mViewPager.getCurrentItem()).getImageDataStream());
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
            gallerySlideFragment = GallerySlideFragment.newInstance(i);
            CurrentImageList.getInstance().setCurrentPosition(i);

            ImageLoader imageLoader = new ImageLoader(i, gallerySlideFragment);
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

        ImageLoader(int imageIdx, GallerySlideFragment gallerySlideFragment) {
            mImageIdx = imageIdx;
            mImage = CurrentImageList.getInstance().getImages().get(mImageIdx);
            mGallerySlideFragment = gallerySlideFragment;
        }

        @Override
        protected Bitmap doInBackground(Integer... params) {

            Point preferedSize = new Point();
            GallerySlideActivity.this.getWindowManager().getDefaultDisplay().getSize(preferedSize);
            i(TAG, "doInBackground: " + mImageIdx);


            i(TAG, "doInBackground: " + mImage.getImageDataStream());
            Bitmap bitmap = null;//BitmapCacheManager.getInstance().get(mImage.getImageDataStream());

                mImage.loadExif();
            ExifInterface exif = mImage.loadExif();

            if (bitmap == null) {
                BitmapFactory.Options lOptions = new BitmapFactory.Options();
                lOptions.inJustDecodeBounds = true;

                BitmapFactory.decodeFile(mImage.getImageDataStream(), lOptions);


                lOptions.inSampleSize = Math.min(ImageUtils.calculateMinInSampleSize(lOptions,
                                preferedSize.x, preferedSize.y, true),
                        ImageUtils.calculateMinInSampleSize(lOptions,
                                preferedSize.x, preferedSize.y, false));


                i(TAG, "onCreateView: SampleSize: " + lOptions.inSampleSize);
                lOptions.inJustDecodeBounds = false;
                if (exif != null)
                    bitmap = ImageUtils.rotateBitmap(exif,
                            BitmapFactory.decodeFile(mImage.getImageDataStream(), lOptions));
                else
                    bitmap = BitmapFactory.decodeFile(mImage.getImageDataStream(), lOptions);
                //BitmapCacheManager.getInstance().put(mImage.getImageDataStream(),
                //        bitmap);
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            mGallerySlideFragment.setBitmap(bitmap);
        }
    }

    /**
     * Gesture listener to detect double tap and single tap.
     * A single tap will set the visibility and animation of some elements that display over the
     * image fragment.
     * A double tap will
     */
    class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {

            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            i(TAG, "onSingleTapConfirmed: ");
            mDataVisible=!mDataVisible;
            if (mDataVisible) {
                mImageDataTextView.setVisibility(View.VISIBLE);
            } else
                mImageDataTextView.setVisibility(View.INVISIBLE);
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {

            i(TAG, "onDoubleTap: ");
            return true;
        }
    }


}
