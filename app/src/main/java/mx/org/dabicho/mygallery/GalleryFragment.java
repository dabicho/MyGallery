package mx.org.dabicho.mygallery;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import mx.org.dabicho.mygallery.model.Gallery;
import mx.org.dabicho.mygallery.model.GalleryType;
import mx.org.dabicho.mygallery.model.IdConstants;
import mx.org.dabicho.mygallery.model.Image;
import mx.org.dabicho.mygallery.util.GalleryLoader;

import static android.util.Log.i;

/**
 * Fragmento que lista en un grid las imágenes disponibles en la galería
 */
public class GalleryFragment extends Fragment {

    private static final String TAG = "GalleryFragment";

    public static final String PARAM_GALLERY_ID="gallery";
    public static final String PARAM_GALLERY_TYPE="galleryType";
    public static final String PARAM_GALLERY_TITLE="galleryTitle";

    /**
     * El id de la galería
     */
    private long mGalleryId;
    /**
     * El tipo de galería
     */
    private GalleryType mGalleryType;
    /**
     * La lista de imágenes
     */
    private String mGalleryTitle;
    private List<Image> mImages;
    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ArrayAdapter<Image> mAdapter;
    private GridView mImagesGridView;

    public GalleryFragment(){

    }

    public static GalleryFragment newInstance(long galleryId, GalleryType galleryType,String galleryTitle){
        GalleryFragment fragment=new GalleryFragment();
        Bundle args=new Bundle();
        args.putLong(PARAM_GALLERY_ID,galleryId);
        args.putSerializable(PARAM_GALLERY_TYPE,galleryType);
        args.putString(PARAM_GALLERY_TITLE, galleryTitle);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mGalleryId=getArguments().getLong(PARAM_GALLERY_ID);
        mGalleryType=(GalleryType)getArguments().getSerializable(PARAM_GALLERY_TYPE);
        mGalleryTitle=getArguments().getString(PARAM_GALLERY_TITLE);
        i(TAG, "onCreate: for gallery " + mGalleryId + " : " + mGalleryType);

        mAdapter = new ArrayAdapter<Image>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1,
                mImages){

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if(convertView==null){
                    convertView=new TextView(getActivity());
                }
                ((TextView)convertView).setText(getItem(position).getThumbnailDataStream());
                i(TAG, "getView: "+getItem(position).getImageDataStream());
                return convertView;
            }
        };

        // TODO INicializar gridview y mostrarlo

        prepareGalleryLoaders();
    }

    /**
     * Prepares and starts loaders
     */
    private void prepareGalleryLoaders() {
        LoaderManager lm = getLoaderManager();
        i(TAG, "prepareGalleryLoaders: Iniciando loader");
        lm.initLoader(IdConstants.GALLERY_LOADER, null, new GalleryLoaderCallbacks());
    }

    private class GalleryLoaderCallbacks implements LoaderManager.LoaderCallbacks<List<Image>> {
        @Override
        public Loader<List<Image>> onCreateLoader(int id, Bundle args) {
            return new GalleryLoader(getActivity(),mGalleryId,mGalleryType);
        }

        @Override
        public void onLoadFinished(Loader<List<Image>> loader, List<Image> data) {
            if(mImages!=null) {
                mImages.clear();
            }else {
                mImages=new ArrayList<Image>();
            }
            mImages.addAll(data);
            for(Image image: data){
                i(TAG, "onLoadFinished: "+image.toString());
            }
            mAdapter.notifyDataSetChanged();

        }

        @Override
        public void onLoaderReset(Loader<List<Image>> loader) {

        }
    }


}
