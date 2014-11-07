package mx.org.dabicho.mygallery.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import mx.org.dabicho.mygallery.GalleriesManagerFragment;

/**
 * Cubiertas para las galerías
 */
public abstract class Cover {
    protected Context mContext;

    public Cover(Context context) {
        mContext = context;
    }

    /**
     * Dibuja la cubierta en el imageView referenciado por galleryItemViewHolder. Si no hay una
     * cubierta preparada para dibujar, se dibuja una plantilla genérica
     * Este método es llamado desde el hilo principal pues actualiza la interfaz gráfica
     * @param galleryItemViewHolder el bean que contiene el ImageView donde se va a dibujar la cubierta
     * @return true si se ha dibujado la cubierta, false si se ha dibujado una plantilla temporal
     */
    public abstract boolean paintCover(GalleriesManagerFragment.GalleryItemViewHolder galleryItemViewHolder);

    /**
     * Genera la cubierta. Este método debe ser llamado desde otro hilo pues en general hace acceso
     * a recursos de lectura/escritura como archivos/bases de datos o la red, y procesa imágenes.
     * Puede tardar en responder
     * @param preferredWidth El ancho preferido por la vista
     * @param preferredWidth el alto preferido por la vista
     */
    public abstract Bitmap generateCover(int preferredWidth, int preferredHeight);

}

enum CoverType {
    SimpleCover;
}

