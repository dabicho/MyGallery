package mx.org.dabicho.mygallery;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import mx.org.dabicho.mygallery.dummy.DummyContent;
import mx.org.dabicho.mygallery.model.Gallery;
import mx.org.dabicho.mygallery.model.IdConstants;
import mx.org.dabicho.mygallery.model.SimpleCover;
import mx.org.dabicho.mygallery.services.BitmapCacheManager;
import mx.org.dabicho.mygallery.util.GalleriesLoader;
import mx.org.dabicho.mygallery.util.ImageUtils;

import static android.util.Log.i;

/**
 * A fragment representing a list of Galleries.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class GalleriesManagerFragment extends Fragment implements AbsListView.OnItemClickListener {
    private static final String TAG = "GalleriesManagerFragment";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private List<Gallery> mGalleries;

    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ArrayAdapter<Gallery> mAdapter;

    // TODO: Rename and change types of parameters
    public static GalleriesManagerFragment newInstance(String param1, String param2) {
        GalleriesManagerFragment fragment = new GalleriesManagerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public GalleriesManagerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        i(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);


        if(getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        if(mGalleries == null)
            mGalleries = new ArrayList<Gallery>();

        // TODO: Change Adapter to display your content
        mAdapter = new ArrayAdapter<Gallery>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1,
                mGalleries) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                i(TAG, "getView: Asking for view " + position);
                GalleryItemViewHolder lViewHolder;
                if(convertView == null) {
                    convertView = getActivity().getLayoutInflater().inflate(R.layout
                            .gallery_item, null);
                    lViewHolder = new GalleryItemViewHolder();
                    convertView.setTag(lViewHolder);

                } else {
                    lViewHolder = (GalleryItemViewHolder) convertView.getTag();
                }
                lViewHolder.setId(position);
                lViewHolder.setTextView((TextView) convertView.findViewById(R.id.gallery_infoTextView));
                lViewHolder.setImageView((ImageView) convertView.findViewById(R.id.gallery_imageView));

                lViewHolder.getTextView().setText(getItem(position).getName() + ": (" + getItem
                        (position).getCount() + ")");
                if(!getItem(position).paintCover(lViewHolder)) {
                    i(TAG, "getView: task");
                    //lViewHolder.getImageView().setImageResource(R.drawable.brian_up_close);
                    new GalleryItemTask(position, lViewHolder)
                            .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
                }
                i(TAG, "getView: return");
                return convertView;
            }
        };
        // Prepara los loaders. Carga los datos que va a usar la lista
        prepareGalleryLoaders();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);

        // Set the adapter

        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch(ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);
        }
    }


    /**
     * prepara e inicia los loaders
     */
    private void prepareGalleryLoaders() {
        LoaderManager lm = getLoaderManager();
        i(TAG, "prepareGalleryLoaders: Iniciando loader");
        lm.initLoader(IdConstants.GALLERY_LOADER, null, new GalleryLoaderCallbacks());
    }

    /**
     * Callbacks para cargar los datos de las galerías
     * Al terminar de cargarlas, se crea el nuevo arreglo
     */
    private class GalleryLoaderCallbacks implements LoaderManager.LoaderCallbacks<List<Gallery>> {
        @Override
        public Loader<List<Gallery>> onCreateLoader(int id, Bundle args) {

            return new GalleriesLoader(getActivity());
        }

        @Override
        public void onLoadFinished(Loader<List<Gallery>> loader, List<Gallery> data) {
            if(mGalleries != null) {
                mGalleries.clear();

            } else
                mGalleries = new ArrayList<Gallery>();
            mGalleries.addAll(data);
            for(Gallery lGallery : data) {
                i(TAG, "onLoadFinished: " + lGallery.getName());
            }

            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onLoaderReset(Loader<List<Gallery>> loader) {

        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }

    /**
     * Un patrón "holder". Esta clase contiene los elementos que se van a modificar y se relacionan
     * con el id
     */
    public class GalleryItemViewHolder {
        private ImageView mImageView;
        private TextView mTextView;
        private long mId;
        private Bitmap mBitmap;

        public ImageView getImageView() {
            return mImageView;
        }

        public void setImageView(ImageView imageView) {
            mImageView = imageView;
        }

        public TextView getTextView() {
            return mTextView;
        }

        public void setTextView(TextView textView) {
            mTextView = textView;
        }

        public long getId() {
            return mId;
        }

        public void setId(long id) {
            mId = id;
        }

        public void setBitmap(Bitmap bitmap){
            if(bitmap==null){
                i(TAG, "setBitmap: colocando bitmap null");
            }
            if(mBitmap!=null)
                i(TAG, "setBitmap: bitmap anterior no nulo");
            if(mImageView!=null){
                mImageView.setImageBitmap(bitmap);
                mBitmap=bitmap;
            }

        }

        public Bitmap getBitmap() {
            return mBitmap;
        }
    }

    private class GalleryItemTask extends AsyncTask<Void, Void, Gallery> {
        private static final String TAG = "GalleryItemTask";
        private int mId;
        private String mCoverId;
        private GalleryItemViewHolder mViewHolder;
        private Bitmap mBitmap;

        GalleryItemTask(int id, GalleryItemViewHolder galleryItemViewHolder) {
            mViewHolder = galleryItemViewHolder;
            mId = id;
        }


        @Override
        protected void onPostExecute(Gallery galleries) {

            if(mId != mViewHolder.getId()) {

                //mBitmap.recycle();

                return;
            }
            // Validar y actualizar bitmap

            if(mViewHolder.getBitmap()!=null) {
                i(TAG, "onPostExecute: Hay bitmap anterior");
                BitmapCacheManager.getInstance().decreaseRefCount(mViewHolder.getBitmap());
            }
            mViewHolder.setBitmap(mBitmap);
            BitmapCacheManager.getInstance().increaseRefCount(mBitmap);
            //mGalleries.get(mId).setBitmap(mBitmap);

            super.onPostExecute(galleries);
        }

        @Override
        protected Gallery doInBackground(Void... params) {
            // generar bitmap (y posiblemente agregarlo a algún cache)


            String[] queryProjection = {
                    MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.TITLE};
            String[] selectionArgs = new String[]{String.valueOf(mGalleries.get(mId).getId())};
            Cursor lCursor = getView().getContext().getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    queryProjection, MediaStore.Images.ImageColumns.BUCKET_ID + "= ?",
                    selectionArgs, MediaStore.Images.ImageColumns.TITLE);
            lCursor.moveToFirst();
            while(!lCursor.isAfterLast()) {

                lCursor.moveToNext();
            }
            lCursor.moveToFirst();
            i(TAG, "doInBackground: " + mId + " - " + mViewHolder.getId());

            BitmapFactory.Options lOptions = new BitmapFactory.Options();

            lOptions.inJustDecodeBounds = true;
            mBitmap = BitmapFactory.decodeFile(lCursor.getString(0), lOptions);
            lOptions.inSampleSize = ImageUtils.calculateInSampleSize(lOptions,
                    mViewHolder.getImageView().getWidth(),
                    mViewHolder.getImageView().getHeight());
            lOptions.inJustDecodeBounds = false;
            mBitmap = BitmapFactory.decodeFile(lCursor.getString(0), lOptions);
            i(TAG, "doInBackground: bitmap size: " + mBitmap.getWidth() + " x " + mBitmap.getHeight());

            BitmapCacheManager.getInstance().put(lCursor.getString(0), mBitmap);


            if(!mGalleries.get(mId).hasCover()) {
                SimpleCover lSimpleCover = new SimpleCover(getActivity(), lCursor.getString(0));
                mGalleries.get(mId).setCover(lSimpleCover);
            }
            lCursor.close();
            return null;
        }
    }

}
