package mx.org.dabicho.mygallery.services;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by dabicho on 11/3/14.
 */
public class GalleriesDatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME="MyGallery.DB";
    private static final int DB_VERSION=1;

    public GalleriesDatabaseHelper(Context context){
        super(context, DB_NAME,null, DB_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Para galerías de content provider, el nombre es el nombre del bucket
        //db.execSQL("CREATE TABLE gallery (id integer, type integer, count integer, name text," +
        //        " description text, PRIMARY KEY (id, type)");



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
