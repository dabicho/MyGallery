package mx.org.dabicho.mygallery;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
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
import mx.org.dabicho.mygallery.services.BitmapCacheManager;
import mx.org.dabicho.mygallery.services.ThumbnailLoader;
import mx.org.dabicho.mygallery.util.CurrentImageList;
import mx.org.dabicho.mygallery.util.GalleryLoader;
import mx.org.dabicho.mygallery.util.GalleryLoaderUpdateCallbacks;

import static android.util.Log.i;

/**
 * Fragment to display a grid of images from a gallery
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

    private ThumbnailLoader<ImageView> mThumbnailLoader;

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

        mThumbnailLoader = new ThumbnailLoader<ImageView>(new Handler(), getActivity());
        mThumbnailLoader.setListener(new ThumbnailLoader.Listener<ImageView>() {
            @Override
            public void onThumbnailLoaded(ImageView imageView, Bitmap thumbnail) {
                if(isVisible()) {

                    imageView.setImageBitmap(thumbnail);
                    AnimationSet set = new AnimationSet(true);
                    AlphaAnimation lAlphaAnimation = new AlphaAnimation(0.0F,1F);
                    //ScaleAnimation lScaleAnimation =new ScaleAnimation(0.2F,1F,0.2F,1F);
                    lAlphaAnimation.setDuration(600);
                    //lScaleAnimation.setDuration(600);
                    set.addAnimation(lAlphaAnimation);
                    //set.addAnimation(lScaleAnimation);
                    imageView.startAnimation(set);
                }
            }
        });
        mThumbnailLoader.start();
        // Asegura que se inicialize el looper
        mThumbnailLoader.getLooper();

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

                //TODO Change to return a thumbnail
                if(convertView == null) {
                    // TODO Ver forma de generar mejor la vista
                    convertView = getActivity().getLayoutInflater().
                            inflate(R.layout.gallery_item,parent,false);
                }

                ImageView imageView = (ImageView)convertView.
                        findViewById(R.id.gallery_item_imageView);

                Image image = mImages.get(position);
                if(image.getThumbnailDataStream()==null || BitmapCacheManager.getInstance().
                        get(image.getThumbnailDataStream())
                        ==null) {
                    //TODO poner imagen por default
                    imageView.setImageResource(R.drawable.templates);
                    mThumbnailLoader.queueThumbnail(imageView,image);


                } else {
                    mThumbnailLoader.dequeueThumbnail(imageView);
                    imageView.setImageBitmap(
                            BitmapCacheManager.getInstance().get(image.getThumbnailDataStream()));
                    AnimationSet set = new AnimationSet(true);
                    AlphaAnimation lAlphaAnimation = new AlphaAnimation(0.0F,1F);
                    //ScaleAnimation lScaleAnimation =new ScaleAnimation(0.2F,1F,0.2F,1F);
                    lAlphaAnimation.setDuration(600);
                    //lScaleAnimation.setDuration(600);
                    set.addAnimation(lAlphaAnimation);
                    //set.addAnimation(lScaleAnimation);
                    convertView.startAnimation(set);
                }



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
    public void onDestroyView() {
        mThumbnailLoader.clearQueue();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mThumbnailLoader.quit();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //TODO Mostrar imagen al hacer click

        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        //mListener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);

        // Lanzar actividad de galería
        Intent intent = new Intent(getActivity(), GallerySlideActivity.class);


        CurrentImageList.getInstance().setImages(mImages);
        CurrentImageList.getInstance().setCurrentPosition(position);
        CurrentImageList.getInstance().setTitle(getActivity().getTitle().toString());

        startActivity(intent);
    }

    /**
     * Prepares and starts loaders
     */
    private void prepareGalleryLoaders() {
        LoaderManager lm = getLoaderManager();
        i(TAG, "prepareGalleryLoaders: Iniciando loader");
        lm.initLoader(IdConstants.GALLERY_LOADER, null, new GalleryLoaderCallbacks());
    }


    /**
     * The loader that loads the images list uses this callback to signal an update to the list
     * TODO Not the best nor a clean solution but works for the time being
     */
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




}
