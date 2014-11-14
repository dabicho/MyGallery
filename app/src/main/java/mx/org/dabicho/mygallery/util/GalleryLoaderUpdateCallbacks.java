package mx.org.dabicho.mygallery.util;

import java.util.List;

import mx.org.dabicho.mygallery.model.Image;

/**
 * Set of callbacks that serve to signal that the list has been updated
 */
public interface GalleryLoaderUpdateCallbacks {
    /**
     * Signals that the list has been updated
     *
     * @param imageList
     */
    void updateGallery(List<Image> imageList);
}
