package mx.org.dabicho.mygallery;

import android.app.Fragment;
import android.util.Log;

/**
 * Actividad administradora de galerías
 * Presenta la lista de galerías
 * Cada galería es un bucket de el content provider de imágenes y galerías especiales
 */
public class GalleriesManagerActivity extends FragmentActivity implements GalleriesManagerFragment.OnFragmentInteractionListener {
    private static final String TAG =  "GalleriesManagerActivity";

    @Override
    Fragment getFragment() {
        return GalleriesManagerFragment.newInstance("param1","param2");
    }

    @Override
    public void onFragmentInteraction(String id) {
        Log.i(TAG, "onFragmentInteraction: id: "+id);
    }
}
