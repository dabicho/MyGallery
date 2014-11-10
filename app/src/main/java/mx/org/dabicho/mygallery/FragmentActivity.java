package mx.org.dabicho.mygallery;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

import mx.org.dabicho.mygallery.R;
import mx.org.dabicho.mygallery.services.BitmapCacheManager;
import mx.org.dabicho.mygallery.util.SystemUiHider;

/**
 * A fullscreen activity containing one single fragment and a drawer with another fragment
 */
public abstract class FragmentActivity extends Activity {
    private static final String TAG =  "FragmentActivity";
    private View fragmentView;

    abstract Fragment getFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        fragmentView=findViewById(R.id.container);

        if (savedInstanceState == null) {

            getFragmentManager().beginTransaction()
                    .add(R.id.container, getFragment())
                    .commit();


        }


    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        /*
        if(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE) {
            BitmapCacheManager.getInstance().decreaseRefCountAll();
            getFragmentManager().beginTransaction().detach(getFragment()).commit();
            setContentView(R.layout.activity_fragment);
            getFragmentManager().beginTransaction().attach(getFragment())
                    .commit();

        } else {
            BitmapCacheManager.getInstance().decreaseRefCountAll();
            getFragmentManager().beginTransaction().detach(getFragment()).commit();
            setContentView(R.layout.activity_fragment);
            getFragmentManager().beginTransaction().attach(getFragment())
                    .commit();

        }
        */
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        fragmentView.setSystemUiVisibility(
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
