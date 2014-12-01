package mx.org.dabicho.mygallery.model;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.provider.MediaStore;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import mx.org.dabicho.mygallery.GalleriesManagerFragment;
import mx.org.dabicho.mygallery.R;
import mx.org.dabicho.mygallery.services.BitmapCacheManager;
import mx.org.dabicho.mygallery.util.ImageUtils;
import mx.org.dabicho.mygallery.util.RenderScriptUtils;

import static android.util.Log.e;
import static android.util.Log.i;
import static android.util.Log.w;

/**
 * Cubierta que consta de un simple ImageView correspondiente a una imagen en el content provider
 */
public class SimpleCover extends Cover {
    private static final String TAG = "SimpleCover";
    private String mId = null;
    private long mGalleryId = -1;


    public SimpleCover(Context context, String id, long galleryId) {
        super(context);


        mGalleryId = galleryId;
        mId = creaNombreArchivo();

    }

    /**
     * @param id el ID de la cubierta.
     */
    public void setId(String id) {
        mId = creaNombreArchivo();
    }

    /**
     * Pinta la cubiera con el contenido del cache o con la plantilla
     *
     * @param galleryItemViewHolder
     * @return
     */
    @Override
    public boolean paintCover(GalleriesManagerFragment.GalleryItemViewHolder galleryItemViewHolder) {
        Bitmap lBitmap;

        i(TAG, "paintCover: ");
        // Si tiene un bitmap, se debe de eliminar su referencia
        if (galleryItemViewHolder.getBitmap() != null) {
            i(TAG, "SimpleCover: hay bitmap anterior");
            // Si el bitmap que se va a pintar y el del cache son el mismo, no se hace nada
            if (mId != null && galleryItemViewHolder.getBitmap() == BitmapCacheManager.getInstance().get(mId)) {
                return true;
            }
            // Se decrementa su referencia y elimina

            galleryItemViewHolder.setBitmap(null);
        }
        if (mId == null || (lBitmap = BitmapCacheManager.getInstance().get(mId)) == null) {
            // Si no hay id o no hay cache, se coloca la plantilla
            i(TAG, "SimpleCover: mId " + mId + " Tam: " + galleryItemViewHolder.getImageView().getWidth()
                    + " x " + galleryItemViewHolder.getImageView().getHeight());
            galleryItemViewHolder.getImageView().setImageResource(R.drawable.templates);

            return false;

        } else { // Se coloca el resultado del cache e incrementa su referencia
            i(TAG, "SimpleCover: Se pinta con cache");
            galleryItemViewHolder.setBitmap(lBitmap);

            Log.i(TAG, "paintCover: increaseRefCount");
            return true;
        }

    }

    @Override
    public Bitmap generateCover(int preferredWidth, int preferredHeight) {
        // TODO guardar el bitmap como jpg al terminar en memoria interna
        // TODO buscar el bitmap jpg al iniciar
        Bitmap lBitmap;
        try {
            FileInputStream fis = mContext.openFileInput(creaNombreArchivo());
            lBitmap = BitmapFactory.decodeStream(fis);
            BitmapCacheManager.getInstance().put(creaNombreArchivo(), lBitmap);
            i(TAG, "generateCover: Se leyó archivo y se agrego a cache como: " + creaNombreArchivo());
            return lBitmap;
        } catch (FileNotFoundException fnfe) {
            // NADA
        }
        i(TAG, "generateCover: size: " + preferredWidth + " x " + preferredHeight);

        String[] queryProjection = {
                MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.TITLE};
        String[] selectionArgs = new String[]{String.valueOf(mGalleryId)};
        Cursor lCursor = mContext.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                queryProjection, MediaStore.Images.ImageColumns.BUCKET_ID + "= ?",
                selectionArgs, MediaStore.Images.ImageColumns.TITLE);

        lCursor.moveToFirst();
        i(TAG, "generateCover: FILE0: " + Thread.currentThread().getId() + " " + lCursor.getString(0));
        mId = creaNombreArchivo();

        i(TAG, "generateCover: gID " + mGalleryId + " : " + lCursor.getCount());
        lBitmap = generateSimpleCoverBitmap(lCursor.getString(0),
                preferredWidth,
                preferredHeight);
        if (lCursor.getString(0) == null || lBitmap == null) {
            i(TAG, "generateCover: nulos: " + lCursor.getString(0) + "-" + lBitmap);
        } else {
            BitmapCacheManager.getInstance().put(creaNombreArchivo(), lBitmap);
            i(TAG, "generateCover: Nuevo bitmap en cache: " + creaNombreArchivo());
        }
        lCursor.close();
        return lBitmap;
    }

    /**
     * Este método abre la imágen source y realiza un recorte del centro que se asemeje al center
     * crop de ImageView
     *
     * @param source el path a la imagen original
     * @param width  el ancho deseado de recorte
     * @param height el alto deseado de recorte
     * @return una imagen de width x height que representa un segmento central de la imagen source
     */
    public Bitmap generateSimpleCoverBitmap(String source, int width, int height) {
        if (source == null || width <= 0 || height <= 0)
            return null;
        int x;
        int y;
        int dWidth;
        int dHeight;
        Bitmap lBitmap;

        int thumbnailStartX = 1;
        int thumbnailStartY = 1;
        int thumbnailWidth;
        int thumbnailHeight;

        i(TAG, "generateSimpleCoverBitmap: " + Thread.currentThread().getId() + " " + width + " x " + height);
        BitmapFactory.Options lOptions = new BitmapFactory.Options();
        lOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(source, lOptions);
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(source);
            i(TAG, "generateSimpleCoverBitmap: " + Thread.currentThread().getId() + " orientación: " + exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL));
            switch (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                case ExifInterface.ORIENTATION_ROTATE_270:
                case ExifInterface.ORIENTATION_TRANSPOSE:
                case ExifInterface.ORIENTATION_TRANSVERSE:
                    lOptions.inSampleSize = ImageUtils.calculateMaxInSampleSize(lOptions, width, height, true);
                    break;
                default:
                    lOptions.inSampleSize = ImageUtils.calculateMaxInSampleSize(lOptions, width, height, false);
            }
        } catch (IOException e) {
            e(TAG, "generateSimpleCoverBitmap: No fué posible leer los metadatos exif de el archivo",
                    e);
            lOptions.inSampleSize = ImageUtils.calculateMaxInSampleSize(lOptions, width, height);
        }
        i(TAG, "generateSimpleCoverBitmap: " + Thread.currentThread().getId() + " " + lOptions.outWidth + "" +
                " x" + " " + lOptions.outHeight);

        lOptions.inJustDecodeBounds = false;
        lBitmap = ImageUtils.rotateBitmap(exif, BitmapFactory.decodeFile(source, lOptions));
        //TODO Calcular la dimension del thumbnail aquí.
        // The thumbnail bitmap ocuppies at most 5/7 of the cover height and 1/3rd of the cover width
        thumbnailWidth = (int) ((float) lBitmap.getWidth() / (float) lBitmap.getHeight()
                * (float) height * 5F / 7F);


        if (thumbnailWidth > 1F / 3F * (float) width) {
            thumbnailWidth = (int) (1F / 3F * (float) width);
            thumbnailHeight = (int) ((float) lBitmap.getHeight() / (float) lBitmap.getWidth() *
                    1F / 3F * (float) width);
        } else {
            thumbnailHeight = (int) (5F / 7F * (float) height);
        }


        i(TAG, "generateSimpleCoverBitmap: " + Thread.currentThread().getId() + " " + lBitmap.getWidth() +
                " x " +
                lBitmap.getHeight() + " " + lOptions.inSampleSize);
        dHeight = height * lBitmap.getWidth() / width;
        if (dHeight <= lBitmap.getHeight()) {
            dWidth = lBitmap.getWidth();
        } else {
            dHeight = lBitmap.getHeight();
            dWidth = height * lBitmap.getWidth() / width;
        }

        x = (lBitmap.getWidth() - dWidth) / 2;
        if (x < 0)
            x = 0;
        y = (lBitmap.getHeight() - dHeight) / 2;
        if (y < 0)
            y = 0;
        Bitmap croppedBitmap = Bitmap.createBitmap(lBitmap, x, y, dWidth, dHeight);
        Bitmap thumbnailBitmap = Bitmap.createScaledBitmap(lBitmap, thumbnailWidth - 4,
                thumbnailHeight - 4, true);
        lBitmap.recycle();
        lBitmap = Bitmap.createScaledBitmap(croppedBitmap, width, height, false);
        croppedBitmap.recycle();
        croppedBitmap = getMutableBitmap(mContext.getCacheDir().toString(), lBitmap);

        croppedBitmap = renderscriptHelper(croppedBitmap);


        Canvas canvas = new Canvas(croppedBitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(0x0AAFFFFFF);

        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);

        paint.setStyle(Paint.Style.STROKE);

        paint.setColor(Color.WHITE);
        canvas.drawRect(thumbnailStartX, thumbnailStartY, thumbnailStartX + thumbnailWidth,
                thumbnailStartY + thumbnailHeight, paint);
        paint.setColor(Color.BLACK);
        canvas.drawRect(thumbnailStartX + 1, thumbnailStartY + 1, thumbnailStartX +
                thumbnailWidth - 1, thumbnailStartY + thumbnailHeight - 1, paint);
        canvas.drawBitmap(thumbnailBitmap, thumbnailStartX + 2, thumbnailStartY + 2, paint);

        System.gc();
        i(TAG, "generateSimpleCoverBitmap: " + Thread.currentThread().getId() + " " + croppedBitmap.getWidth()
                + " x " +
                "" + croppedBitmap
                .getHeight());
        try {
            FileOutputStream fos = mContext.openFileOutput(creaNombreArchivo(), 0);
            croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            fos.close();
        } catch (FileNotFoundException fnf) {
            e(TAG, "generateSimpleCoverBitmap: No fué posible crear el archivo", fnf);
        } catch (IOException ioe) {
            e(TAG, "generateSimpleCoverBitmap: No fué posible cerrar el archivo", ioe);
        }

        return croppedBitmap;
    }

    private Bitmap renderscriptHelper(Bitmap croppedBitmap) {
        RenderScriptUtils lRenderScriptUtils = RenderScriptUtils.getInstance(mContext);
        return lRenderScriptUtils.blurBitmap(croppedBitmap);

    }

    private String creaNombreArchivo() {
        i(TAG, "creaNombreArchivo: " + CoverType.SimpleCover.name() + "." +
                mGalleryId + ".jpg");
        return CoverType.SimpleCover.name() + "." +
                mGalleryId + ".jpg";
    }


    /**
     * método estático para generar un bitmap mutable a partir de uno inmutable para poder editarlo.
     * Escribe a disco en el directorio indicado en dir y recicla el bitmap inmutable
     *
     * @param dir
     * @param inmutableBitmap
     * @return
     */
    private synchronized static Bitmap getMutableBitmap(String dir, Bitmap inmutableBitmap) {
        Bitmap mutableBitmap = null;
        int width = inmutableBitmap.getWidth();
        int height = inmutableBitmap.getHeight();
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(dir + "bitmap.raw", "rw");
            FileChannel channel = randomAccessFile.getChannel();
            MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, 0,
                    inmutableBitmap.getAllocationByteCount());
            inmutableBitmap.copyPixelsToBuffer(map);
            inmutableBitmap.recycle();
            mutableBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            map.position(0);
            mutableBitmap.copyPixelsFromBuffer(map);
            channel.close();
            randomAccessFile.close();

        } catch (FileNotFoundException e) {
            // crear bitmap vacio y copiar por memoria...
        } catch (IOException e) {

        } finally {
            if (mutableBitmap == null) {
                mutableBitmap = inmutableBitmap.copy(Bitmap.Config.ARGB_8888, true);
                inmutableBitmap.recycle();
            }
        }
        return mutableBitmap;
    }
}
