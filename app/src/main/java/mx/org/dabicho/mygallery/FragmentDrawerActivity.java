package mx.org.dabicho.mygallery;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import mx.org.dabicho.mygallery.R;
import mx.org.dabicho.mygallery.templateExamples.NavigationDrawerFragment;

/**
 * An abstract fullscreen activity containing one single fragment and a drawer with another fragment
 */
public abstract class FragmentDrawerActivity extends Activity {
    private View mFragmentView;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;

    /**
     * This method must return the fragment with the main activity content.
     * @return
     */
    abstract Fragment getFragment();

    /**
     * This method must return the fragment for the drawer
     * @return
     */
    abstract NavigationDrawerFragment getDrawerFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_fragment);

        mFragmentView =findViewById(R.id.container);

        if (savedInstanceState == null) {

            getFragmentManager().beginTransaction()
                    .add(R.id.container, getFragment())
                    .commit();


            mNavigationDrawerFragment=getDrawerFragment();
            mTitle = getTitle();


        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        mFragmentView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE
        );
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




}
