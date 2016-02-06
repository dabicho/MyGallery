package mx.org.dabicho.mygallery.model;

/**
 * Created by dabicho on 11/28/14.
 */
public class Date implements Comparable<Date> {
    private long mYear;
    private Month mMonth;
    private int mDayOfMonth;
    private int mHour;
    private int mMin;
    private double mSec;


    @Override
    public int compareTo(Date another) {
        int c = mMonth.compareTo(another.getMonth());
        if (mYear > another.getYear())
            return 1;
        if (mYear < another.getYear())
            return -1;
        if (c > 0)
            return 1;
        if (c < 0)
            return -1;
        if (mDayOfMonth > another.getDayOfMonth())
            return 1;
        if (mDayOfMonth < another.getDayOfMonth())
            return -1;
        if (mHour > another.getHour())
            return 1;
        if (mHour < another.getHour())
            return -1;
        if (mMin > another.getMin())
            return 1;
        if (mMin < another.getMin())
            return -1;
        if (mSec > another.getSec())
            return 1;
        if (mSec < another.getSec())
            return -1;

        return 0;
    }

    public boolean isBetween(Date begin, Date end) {
        if (compareTo(begin) >= 0 && compareTo(end) <= 0)
            return true;
        return false;
    }

    public long getYear() {
        return mYear;
    }

    public void setYear(long year) {
        mYear = year;
    }

    public Month getMonth() {
        return mMonth;
    }

    public void setMonth(Month month) {
        mMonth = month;
    }

    public int getDayOfMonth() {
        return mDayOfMonth;
    }

    public void setDayOfMonth(int dayOfMonth) {
        mDayOfMonth = dayOfMonth;
    }

    public int getHour() {
        return mHour;
    }

    public void setHour(int hour) {
        mHour = hour;
    }

    public int getMin() {
        return mMin;
    }

    public void setMin(int min) {
        mMin = min;
    }

    public double getSec() {
        return mSec;
    }

    public void setSec(double sec) {
        mSec = sec;
    }
}

enum Month implements Comparable<Month> {
    JAN, FEB, MAR, ABR, MAY, JUN, JUL, AUG, SEP, OCT, NOV, DEC

}
