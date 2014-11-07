package mx.org.dabicho.mygallery.model;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DrawFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.provider.MediaStore;
import android.renderscript.RenderScript;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
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

import static android.util.Log.e;
import static android.util.Log.i;

/**
 * Represents a gallery with a piece of one of its images and its full version superimposed over it.
 * This cover is the default cover and the first time they are generated,
 * it uses the first image for it's gallery
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
     * @param id id for the cover.It is basically the name for the image for this cover
     */
    public void setId(String id) {
        mId = creaNombreArchivo();
    }

    /**
     * This paints the cover for the gallery. This must be called only from the main thread.
     * {@link mx.org.dabicho.mygallery.GalleriesManagerFragment.GalleryItemViewHolder}
     *
     * @param galleryItemViewHolder
     * @return true if it painted the cover from the bitmap cache, false if it painted the cover
     * with a template.
     */
    @Override
    public boolean paintCover(GalleriesManagerFragment.GalleryItemViewHolder galleryItemViewHolder) {
        Bitmap lBitmap;

        i(TAG, "paintCover: ");
        // Si tiene un bitmap, se debe de eliminar su referencia
        if(galleryItemViewHolder.getBitmap() != null) {
            i(TAG, "SimpleCover: hay bitmap anterior");
            // Si el bitmap que se va a pintar y el del cache son el mismo, no se hace nada
            if(mId != null && galleryItemViewHolder.getBitmap() == BitmapCacheManager.getInstance().get(mId)) {
                return true;
            }
            // Se decrementa su referencia y elimina
            BitmapCacheManager.getInstance().decreaseRefCount(galleryItemViewHolder.getBitmap());
            galleryItemViewHolder.setBitmap(null);
        }
        if(mId == null || (lBitmap = BitmapCacheManager.getInstance().get(mId)) == null) {
            // Si no hay id o no hay cache, se coloca la plantilla
            i(TAG, "SimpleCover: mId " + mId + " Tam: " + galleryItemViewHolder.getImageView().getWidth()
                    + " x " + galleryItemViewHolder.getImageView().getHeight());
            galleryItemViewHolder.getImageView().setImageResource(R.drawable.templates);

            return false;

        } else { // Se coloca el resultado del cache e incrementa su referencia
            i(TAG, "SimpleCover: Se pinta con cache");
            galleryItemViewHolder.setBitmap(lBitmap);
            BitmapCacheManager.getInstance().increaseRefCount(lBitmap);
            return true;
        }

    }

    @Override
    public Bitmap generateCover(int preferredWidth, int preferredHeight) {
        // TODO guardar el bitmap como jpg al terminar en memoria interna
        // TODO buscar el bitmap jpg al iniciar
        Bitmap lBitmap;
        try { // first check if the cover is saved to a file.
            // If it is, I don't bother creating it from database data and load this instead.
            // And of course, add it to the bitmap cache
            FileInputStream fis = mContext.openFileInput(creaNombreArchivo());
            lBitmap = BitmapFactory.decodeStream(fis);
            BitmapCacheManager.getInstance().put(creaNombreArchivo(), lBitmap);
            i(TAG, "generateCover: Se leyó archivo y se agrego a cache como: " + creaNombreArchivo());
            return lBitmap;
        } catch(FileNotFoundException fnfe) {
            // whatever. It just means the file is not there.
        }
        if(preferredHeight <= 0 || preferredWidth <= 0) // If width or height are imposible, don't bother
            return null;
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
        mId = lCursor.getString(0);

        i(TAG, "generateCover: gID " + mGalleryId + " : " + lCursor.getCount());
        lBitmap = generateSimpleCoverBitmap(mContext.getResources(), lCursor.getString(0),
                preferredWidth,
                preferredHeight);
        if(lCursor.getString(0) == null || lBitmap == null) {
            i(TAG, "generateCover: nulos: " + lCursor.getString(0) + "-" + lBitmap);
        } else
            BitmapCacheManager.getInstance().put(creaNombreArchivo(), lBitmap);

        lCursor.close();
        return lBitmap;
    }

    /**
     * generates a bitmap from the source to be of width and height indicated with the full
     * thumbnail version overlayed.
     * It also saves the image as a jpg for later use.
     *
     * @param source path to the original image
     * @param width  width for the cover image
     * @param height height for the cover image
     * @return the bitmap for the cover
     */
    public Bitmap generateSimpleCoverBitmap(Resources resources, String source, int width, int height) {
        if(source == null || width <= 0 || height <= 0)
            return null;
        int x = 0;
        int y = 0;
        int dWidth;
        int dHeight;
        int origWidth;
        int origHeight;
        Bitmap lBitmap;
        i(TAG, "generateSimpleCoverBitmap: " + Thread.currentThread().getId() + " " + width + " x " + height);
        BitmapFactory.Options lOptions = new BitmapFactory.Options();
        lOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(source, lOptions);

        ExifInterface exif = null;
        try {
            exif = new ExifInterface(source);
            i(TAG, "generateSimpleCoverBitmap: " + Thread.currentThread().getId() +
                    " orientación: " + exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL));
            switch(exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                case ExifInterface.ORIENTATION_ROTATE_270:
                case ExifInterface.ORIENTATION_TRANSPOSE:
                case ExifInterface.ORIENTATION_TRANSVERSE:

                    lOptions.inSampleSize = ImageUtils.calculateInSampleSize(lOptions, width, height, true);
                    break;
                default:
                    lOptions.inSampleSize = ImageUtils.calculateInSampleSize(lOptions, width, height, false);

            }
        } catch(IOException e) {
            e(TAG, "generateSimpleCoverBitmap: No fué posible leer los metadatos exif de el archivo",
                    e);
            lOptions.inSampleSize = ImageUtils.calculateInSampleSize(lOptions, width, height);
        }
        i(TAG, "generateSimpleCoverBitmap: " + Thread.currentThread().getId() + " " + lOptions.outWidth + "" +
                " x" + " " + lOptions.outHeight);

        lOptions.inJustDecodeBounds = false;
        lBitmap = ImageUtils.rotateBitmap(exif, BitmapFactory.decodeFile(source, lOptions));
        origWidth=lBitmap.getWidth();
        origHeight=lBitmap.getHeight();

        i(TAG, "generateSimpleCoverBitmap: " + Thread.currentThread().getId() + " " + lBitmap.getWidth() +
                " x " +
                lBitmap.getHeight() + " " + lOptions.inSampleSize);
        dHeight = height * lBitmap.getWidth() / width;
        if(dHeight <= lBitmap.getHeight()) {
            dWidth = lBitmap.getWidth();
        } else {
            dHeight = lBitmap.getHeight();
            dWidth = height * lBitmap.getWidth() / width;
        }

        x = (lBitmap.getWidth() - dWidth) / 2;
        if(x < 0)
            x = 0;
        y = (lBitmap.getHeight() - dHeight) / 2;
        if(y < 0)
            y = 0;
        Bitmap croppedBitmap = Bitmap.createBitmap(lBitmap, x, y, dWidth, dHeight);

        Bitmap croppedScaledBitmap = Bitmap.createScaledBitmap(croppedBitmap, width, height, false);
        croppedBitmap.recycle();
        croppedBitmap = getMutableBitmap(mContext.getCacheDir().toString(), croppedScaledBitmap);

        Canvas canvas = new Canvas(croppedBitmap);
        //TODO usar renderscript para poner blur


        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        int maxThumbnailHeight=height*5/7+6;
        int maxThumbnailWidth=width*1/3+6;
        // if scaling width of the original image with the height scale factor makes the width of
        // the thumbnail less
        // than 1 third of the cropped bitmap width, we use it, else we use the width scale factor
        int thumbnailWidth=origWidth*maxThumbnailHeight/origHeight;
        int thumbnailHeight;
        if(thumbnailWidth<=origWidth/3)
            thumbnailHeight=maxThumbnailHeight;
        else {
            thumbnailWidth=maxThumbnailWidth;
            thumbnailHeight=origHeight*maxThumbnailWidth/origWidth;
        }


        canvas.drawRect(1, 1, thumbnailWidth, thumbnailHeight, paint);
        paint.setColor(Color.BLACK);
        canvas.drawRect(2, 2, thumbnailWidth-2, thumbnailHeight-2, paint);
        Rect origRect=new Rect(0,0,origWidth-1,origHeight-1);
        Rect destRect = new Rect(3,3,thumbnailWidth-4,thumbnailHeight-4);
        Log.i(TAG,"generateSimpleCoverBitmap: "+destRect);
        canvas.drawBitmap(lBitmap,origRect, destRect,paint);
        lBitmap.recycle();


        System.gc();
        i(TAG, "generateSimpleCoverBitmap: " + Thread.currentThread().getId() + " " + croppedBitmap.getWidth()
                + " x " +
                "" + croppedBitmap
                .getHeight());
        try {
            FileOutputStream fos = mContext.openFileOutput(creaNombreArchivo(), 0);
            croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            fos.close();
        } catch(FileNotFoundException fnf) {
            e(TAG, "generateSimpleCoverBitmap: No fué posible crear el archivo", fnf);
        } catch(IOException ioe) {
            e(TAG, "generateSimpleCoverBitmap: No fué posible cerrar el archivo", ioe);
        }

        return croppedBitmap;
    }

    /**
     *
     * @return the string representing the filename for this cover's image
     */
    private String creaNombreArchivo() {
        return CoverType.SimpleCover.name() + "." +
                mGalleryId + ".png";
    }


    /**
     * Gets a mutable version of a given bitmap. It uses a file to save ram.
     *
     * @param dir a directory to use as a cache to avoid using ram memory
     * @param inmutableBitmap the original bitmap
     * @return a mutable bitmap.
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

        } catch(FileNotFoundException e) {
            // whatever
        } catch(IOException e) {
            // whatever
        } finally {
            if(mutableBitmap == null) { // if we couldn't do it with a file, we resort to memory
                mutableBitmap = inmutableBitmap.copy(Bitmap.Config.ARGB_8888, true);
                inmutableBitmap.recycle();
            }
        }
        return mutableBitmap;
    }
}
