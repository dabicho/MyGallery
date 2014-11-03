package mx.org.dabicho.mygallery.model;

/**
 * Un album esta formado de referencias a imágenes de una galería Content provider
 */
public class Album extends Gallery {

    @Override
    public GalleryType getGalleryType() {
        return GalleryType.ALBUM;
    }
}
