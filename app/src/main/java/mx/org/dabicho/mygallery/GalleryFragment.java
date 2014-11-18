package mx.org.dabicho.mygallery;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import mx.org.dabicho.mygallery.model.Gallery;
import mx.org.dabicho.mygallery.model.GalleryType;
import mx.org.dabicho.mygallery.model.IdConstants;
import mx.org.dabicho.mygallery.model.Image;
import mx.org.dabicho.mygallery.util.GalleryLoader;
import mx.org.dabicho.mygallery.util.GalleryLoaderUpdateCallbacks;

import static android.util.Log.i;

/**
 * Fragmento que lista en un grid las imágenes disponibles en la galería
 */
public class GalleryFragment extends Fragment implements AdapterView.OnItemClickListener {

    private static final String TAG = "GalleryFragment";

    public static final String PARAM_GALLERY_ID = "gallery";
    public static final String PARAM_GALLERY_TYPE = "galleryType";
    public static final String PARAM_GALLERY_TITLE = "galleryTitle";

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

    private View mView;

    public static GalleryFragment newInstance(long galleryId, GalleryType galleryType, String galleryTitle) {
        GalleryFragment fragment = new GalleryFragment();
        Bundle args = new Bundle();
        args.putLong(PARAM_GALLERY_ID, galleryId);
        args.putSerializable(PARAM_GALLERY_TYPE, galleryType);
        args.putString(PARAM_GALLERY_TITLE, galleryTitle);
        fragment.setArguments(args);
        return fragment;
    }

    public GalleryFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        i(TAG, "onCreate: onCreate()");
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            mGalleryId = getArguments().getLong(PARAM_GALLERY_ID);
            mGalleryType = (GalleryType) getArguments().getSerializable(PARAM_GALLERY_TYPE);
            mGalleryTitle = getArguments().getString(PARAM_GALLERY_TITLE);

        }
        getActivity().setTitle(mGalleryTitle);
        if(mImages == null)
            mImages = new ArrayList<Image>();
        i(TAG, "onCreate: for gallery " + mGalleryId + " : " + mGalleryType);

        mAdapter = new ArrayAdapter<Image>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1,
                mImages) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                GalleryThumbnailHolder lThumbnailHolder;
                //TODO Change to return a thumbnail
                if(convertView == null) {
                    // TODO Ver forma de generar mejor la vista
                    convertView = new ImageView(getActivity());
                    AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams
                            ((int) getResources().getDimension(R.dimen.gallery_grid_column_width)
                                    , (int) getResources().getDimension(R.dimen.gallery_grid_column_width));
                    convertView.setLayoutParams(layoutParams);
                    lThumbnailHolder = new GalleryThumbnailHolder();
                    convertView.setTag(lThumbnailHolder);

                } else {
                    lThumbnailHolder = (GalleryThumbnailHolder) convertView.getTag();
                }

                lThumbnailHolder.setId(position);
                lThumbnailHolder.setImageView((ImageView) convertView);

                new ImageThumbnailTask(position, lThumbnailHolder).executeOnExecutor(AsyncTask
                        .THREAD_POOL_EXECUTOR, null);
                //((ImageView) convertView).setImageBitmap(getItem(position).getThumbnail
                //        (getActivity().getContentResolver()));

                return convertView;

            }

        };

        // TODO INicializar gridview y mostrarlo

        prepareGalleryLoaders();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        i(TAG, "onCreateView: onCreateView()");
        mView = inflater.inflate(R.layout.fragment_gallery, container, false);

        mImagesGridView = (GridView) mView.findViewById(android.R.id.list);
        mImagesGridView.setAdapter(mAdapter);
        mImagesGridView.setOnItemClickListener(this);
        return mView;

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //TODO Mostrar imagen al hacer click
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
            return new GalleryLoader(getActivity(), mGalleryId, mGalleryType, 10,
                    new GalleryLoaderUpdateCallbacks() {
                        @Override
                        public boolean updateGallery(final List<Image> imageList) {

                            if(getActivity() != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        i(TAG, "run: updating DATA");
                                        if(mImages != null) {
                                            mImages.clear();
                                        } else {
                                            mImages = new ArrayList<Image>();
                                        }
                                        mImages.addAll(imageList);
                                        mAdapter.notifyDataSetChanged();
                                    }
                                });
                                return false;
                            }
                            return true;
                        }
                    });
        }

        @Override
        public void onLoadFinished(Loader<List<Image>> loader, List<Image> data) {
            if(mImages != null) {
                mImages.clear();
            } else {
                mImages = new ArrayList<Image>();
            }
            mImages.addAll(data);

            if(!mImages.isEmpty()) {

                i(TAG, "onLoadFinished: Lista cargada " + mImages.size());
                mView.findViewById(android.R.id.empty).setVisibility(View.INVISIBLE);
                mView.findViewById(R.id.loading).setVisibility(View.INVISIBLE);
                i(TAG, "onLoadFinished: Vista: " + mView.findViewById(android.R.id.empty));
            }
            mAdapter.notifyDataSetChanged();

        }

        @Override
        public void onLoaderReset(Loader<List<Image>> loader) {
            Log.i(TAG, "onLoaderReset: onLoaderReset()");
        }


    }

    /**
     * Holder. This class holds a reference to the elements that are going to be modified related
     * to an id.
     */

    public class GalleryThumbnailHolder {
        private ImageView mImageView;
        private Bitmap mBitmap;
        private long mId;

        public ImageView getImageView() {
            return mImageView;
        }

        public void setImageView(ImageView imageView) {
            mImageView = imageView;
        }

        public Bitmap getBitmap() {
            return mBitmap;
        }

        public void setBitmap(Bitmap bitmap) {
            if(mImageView != null) {
                mImageView.setImageBitmap(bitmap);
                mBitmap = bitmap;
            }
        }

        public long getId() {
            return mId;
        }

        public void setId(long id) {
            mId = id;
        }
    }

    /**
     * Este AsyncTask carga el thumbnail de cada imágen y lo coloca en su vista. Utiliza
     * GalleryThumbnailHolder para llevar un registro de la correspondencia del bitmap con la vista
     */
    private class ImageThumbnailTask extends AsyncTask<Void, Void, Void> {
        /**
         * The id holds the position inside the gridView list. If this id and the thumbnailHolder
         * id differs, it means they do not correspond to each other and the view is not updated.
         * The ImageView inside the thumbnailHolder is updated only when their ids match.
         */
        private int mId;
        /**
         * The image associated with the id
         */
        Image mImage;
        /**
         * The data to set to the bitmap
         */
        private GalleryThumbnailHolder mThumbnailHolder;

        /**
         * The bitmap that will be loaded
         */
        private Bitmap mBitmap;

        /**
         * @param id              The original id for the task used to check if the thumbnailHolder should be
         *                        updated.
         * @param thumbnailHolder the thumbnail holder to be updated after execution
         */
        ImageThumbnailTask(int id, GalleryThumbnailHolder thumbnailHolder) {
            mId = id;
            mImage = mImages.get(id);
            mThumbnailHolder = thumbnailHolder;
        }

        @Override
        protected Void doInBackground(Void... params) {
            mBitmap = mImages.get(mId).getThumbnail(getActivity().getContentResolver());
            return null;
        }

        @Override
        protected void onPostExecute(Void o) {

            if(mId == mThumbnailHolder.getId()) {
                mThumbnailHolder.setBitmap(mBitmap);
            }
            super.onPostExecute(o);
        }


    }
}
