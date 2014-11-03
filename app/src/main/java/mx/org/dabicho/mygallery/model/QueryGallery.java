package mx.org.dabicho.mygallery.model;

/**
 * Created by dabicho on 11/3/14.
 */
public class QueryGallery extends Gallery {
    @Override
    public GalleryType getGalleryType() {
        return GalleryType.QUERY;
    }
}
