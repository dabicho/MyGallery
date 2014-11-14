package mx.org.dabicho.mygallery.model;

import android.os.Parcel;

/**
 * Created by dabicho on 11/3/14.
 */
public class ContentProviderGallery extends Gallery {
    @Override
    public GalleryType getGalleryType() {
        return GalleryType.CONTENT_PROVIDER;
    }




}
