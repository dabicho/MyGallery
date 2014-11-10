package mx.org.dabicho.mygallery;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;

/**
 * Actividad administradora de galerías
 * Presenta la lista de galerías
 * Cada galería es un bucket de el content provider de imágenes y galerías especiales
 */
public class GalleriesManagerActivity extends FragmentActivity implements GalleriesManagerFragment.OnFragmentInteractionListener {
    private static final String TAG = "GalleriesManagerActivity";
    private Fragment mFragment;

    @Override
    Fragment getFragment() {
        if(mFragment == null)
            mFragment = GalleriesManagerFragment.newInstance("param1", "param2");
        return mFragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        Log.i(TAG, "onFragmentInteraction: id: " + id);
    }


}
