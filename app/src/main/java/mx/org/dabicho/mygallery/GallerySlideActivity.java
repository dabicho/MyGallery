package mx.org.dabicho.mygallery;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import mx.org.dabicho.mygallery.util.CurrentImageList;


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
        mViewPager=(ViewPager)findViewById(R.id.gallery_slide_pager);
        mPagerAdapter= new GallerySlideFragmentStatePagerAdapter(getFragmentManager());
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
            Log.i(TAG, "getItem: "+i);
            CurrentImageList.getInstance().setCurrentPosition(i);
            return GallerySlideFragment.newInstance(i);

        }

        @Override
        public int getCount() {
            return CurrentImageList.getInstance().getImages().size();
        }
    }

}
