package mx.org.dabicho.mygallery;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;

import mx.org.dabicho.mygallery.templateExamples.NavigationDrawerFragment;

import static android.util.Log.i;

/**
 * Galleries manager activity
 * Pesents a list of galleries and contains a left-hand drawer with options to select what galleries are displayed: All, local (media store), virtual (references to collections of images from local
 * galleries) or saved search queries.
 *
 */
public class GalleriesManagerActivity extends FragmentDrawerActivity implements GalleriesManagerFragment.OnFragmentInteractionListener, NavigationDrawerFragment.NavigationDrawerCallbacks {
    private static final String TAG = "GalleriesManagerActivity";
    private Fragment mFragment;
    private NavigationDrawerFragment mDrawerFragment;

    @Override
    Fragment getFragment() {
        if (mFragment == null)
            mFragment = GalleriesManagerFragment.newInstance("param1", "param2");
        return mFragment;
    }

    @Override
    NavigationDrawerFragment getDrawerFragment() {
        if (mDrawerFragment == null) {
            mDrawerFragment = (NavigationDrawerFragment)
                    getFragmentManager().findFragmentById(R.id.navigation_drawer);
            mDrawerFragment.setUp(
                    R.id.navigation_drawer,
                    (DrawerLayout) findViewById(R.id.drawer_layout));
            mDrawerFragment.setOptions(new String[]{
                    getString(R.string.title_all_galleries),
                    getString(R.string.title_local_galleries),
                    getString(R.string.title_virtual_galleries),
                    getString(R.string.title_query_galleries),});
        }
        return mDrawerFragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getActionBar().setBackgroundDrawable(new ColorDrawable(0xDD000000));
        Log.d(TAG, "onCreate: onCreate()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: onDestroy()");
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart: onRestart()");
    }


    @Override
    public void onFragmentInteraction(String id) {
        i(TAG, "onFragmentInteraction: id: " + id);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        if (mDrawerFragment != null) {
            setTitle(mDrawerFragment.getOption(position));
            getActionBar().setTitle(mDrawerFragment.getOption(position));
            i(TAG, "onNavigationDrawerItemSelected: "+mDrawerFragment.getOption(position));
        }

    }
}
