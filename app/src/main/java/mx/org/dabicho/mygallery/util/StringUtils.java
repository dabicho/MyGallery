package mx.org.dabicho.mygallery.util;

import android.content.Context;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static android.util.Log.i;

/**
 * Utility methods to manipulate strings
 */
public class StringUtils {
    private static final String TAG = "StringUtils";

    /**
     * Gets a formated String representing the given exifDateTime according to the context's
     * MediumDateFormat and TimeFormat
     * @param context
     * @param exifDateTime
     * @return
     */
    public static String formatExifDateTime(Context context, String exifDateTime) {
        if(exifDateTime==null)
            return null;
        try {
            DateFormat df = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
            Date date = df.parse(exifDateTime);
            Calendar calendar = GregorianCalendar.getInstance();
            calendar.setTime(date);

            return android.text.format.DateFormat.getMediumDateFormat(context).format(date) + " at " +
                    android.text.format.DateFormat.getTimeFormat(context).format(date);

            //return date.toString();
        } catch (ParseException pe) {
            Log.e(TAG, "formatExifDateTime: ", pe);
            return null;
        }

    }

    /**
     * Gets a formatted String representing the Country and City name or the latitude and longitude if conversion was not possible
     * @param context
     * @param latLong
     * @return
     */
    public static String getCountryCity(Context context, float[] latLong){
        return "Lat: "+latLong[0]+", Long: "+latLong[1];
    }
}
