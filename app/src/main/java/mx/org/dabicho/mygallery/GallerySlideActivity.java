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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;

import mx.org.dabicho.mygallery.model.Image;
import mx.org.dabicho.mygallery.services.BitmapCacheManager;
import mx.org.dabicho.mygallery.util.CurrentImageList;
import mx.org.dabicho.mygallery.util.ImageUtils;

import static android.util.Log.i;
import static android.util.Log.v;


/**
 * Activity for the presentation of images from a gallery in fullscreen
 */
//TODO implement fullscreen
public class GallerySlideActivity extends Activity {
    private static final String TAG = "GallerySlideActivity";

    private static final int NUM_PAGES = 2;
    private ViewPager mViewPager;
    private FragmentStatePagerAdapter mPagerAdapter;

    private LinearLayout mImageDataView;

    private boolean mDataVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_gallery_slide_view_pager);
        mViewPager = (ViewPager) findViewById(R.id.gallery_slide_pager);
        mPagerAdapter = new GallerySlideFragmentStatePagerAdapter(getFragmentManager());

        mImageDataView=(LinearLayout)findViewById(R.id.slide_data_linearLayout);




        mViewPager.setOffscreenPageLimit(NUM_PAGES);
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
                if(mDataVisible) {
                    Image image = CurrentImageList.getInstance().getImages().
                            get(i);
                    updateDataView(image);
                }

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

    }

    void updateDataView(Image image){
        mImageDataView.removeAllViews();
        View view=getLayoutInflater().inflate(R.layout.image_detail,null);

        TextView textView=(TextView)view.findViewById(R.id.detail_textView);
        textView.setText(image.getDateTimeOriginal());
        mImageDataView.addView(view);

        view=getLayoutInflater().inflate(R.layout.image_detail,null);
        textView=(TextView)view.findViewById(R.id.detail_textView);
        textView.setText(new File(image.getImageDataStream()).getName());

        mImageDataView.addView(view);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    class GallerySlideFragmentStatePagerAdapter extends FragmentStatePagerAdapter {


        public GallerySlideFragmentStatePagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int i) {

            i(TAG, "getItem: " + i + ": " + mViewPager.getWidth() + "x" + mViewPager.getHeight());
            GallerySlideFragment gallerySlideFragment;
            gallerySlideFragment = GallerySlideFragment.newInstance(i);
            CurrentImageList.getInstance().setCurrentPosition(i);

            Bitmap bitmap = BitmapCacheManager.getInstance().get(CurrentImageList.getInstance()
                    .getImages()
                    .get(i).getImageDataStream());

            if(bitmap == null) {

                ImageLoader imageLoader = new ImageLoader(i, gallerySlideFragment);
                i(TAG, "getItem: Starting imageLoader");
                imageLoader.execute();
                i(TAG, "getItem: imageLoader started");
            } else
                gallerySlideFragment.setBitmap(bitmap, CurrentImageList.getInstance().getImages()
                        .get(i).getImageDataStream());
            return gallerySlideFragment;

        }

        @Override
        public int getCount() {

            return CurrentImageList.getInstance().getImages().size();
        }


    }

    /**
     * This loader is in charge of loading an image into a bitmap in the background and displaying
     * it in it's correspondig fragment.
     */
    private class ImageLoader extends AsyncTask<Integer, Void, Bitmap> {
        Image mImage;
        int mImageIdx;
        GallerySlideFragment mGallerySlideFragment;

        /**
         *
         * @param imageIdx The index for the image data inside the gallery
         * @param gallerySlideFragment the GallerySlideFragment which will display the image
         */
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
            Bitmap bitmap = BitmapCacheManager.getInstance().get(mImage.getImageDataStream());


            if(bitmap == null) {
                ExifInterface exif = mImage.loadExif();
                BitmapFactory.Options lOptions = new BitmapFactory.Options();
                lOptions.inJustDecodeBounds = true;

                BitmapFactory.decodeFile(mImage.getImageDataStream(), lOptions);


                lOptions.inSampleSize = Math.min(ImageUtils.calculateMinInSampleSize(lOptions,
                                preferedSize.x, preferedSize.y, true),
                        ImageUtils.calculateMinInSampleSize(lOptions,
                                preferedSize.x, preferedSize.y, false));


                i(TAG, "onCreateView: SampleSize: " + lOptions.inSampleSize);
                lOptions.inJustDecodeBounds = false;
                if(exif != null)
                    bitmap = ImageUtils.rotateBitmap(exif,
                            BitmapFactory.decodeFile(mImage.getImageDataStream(), lOptions));
                else
                    bitmap = BitmapFactory.decodeFile(mImage.getImageDataStream(), lOptions);
                BitmapCacheManager.getInstance().put(mImage.getImageDataStream(),
                        bitmap);
            }
            System.gc();
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(mGallerySlideFragment != null)
                mGallerySlideFragment.setBitmap(bitmap,
                        mImage.getImageDataStream());
        }
    }

    /**
     * Gesture listener to detect double tap and single tap.
     * A single tap will set the visibility and animation of some elements that display over the
     * image fragment. This elements contain data about the image and some actions that can be
     * taken on the image
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
            mDataVisible = !mDataVisible;
            if(mDataVisible) {
                updateDataView(CurrentImageList.getInstance().getImages().get(mViewPager.getCurrentItem()));
                mImageDataView.setVisibility(View.VISIBLE);

            } else
                mImageDataView.setVisibility(View.INVISIBLE);
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {

            i(TAG, "onDoubleTap: ");
            return true;
        }
    }


}
