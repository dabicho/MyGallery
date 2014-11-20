package mx.org.dabicho.mygallery.util;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import mx.org.dabicho.mygallery.model.Image;

/**
 * This is a singleton to hold the current images list
 */
public class CurrentImageList {

    private static CurrentImageList mCurrentImageList;
    private List<Image> mImages;
    private String mTitle;
    private int mCurrentPosition;

    public static CurrentImageList getInstance(){
        if(mCurrentImageList==null) {
            mCurrentImageList = new CurrentImageList();
        }
        return mCurrentImageList;
    }

    private CurrentImageList(){

    }

    /**
     * Sets the images list
     * @param images
     */
    public void setImages(final List<Image> images){
        mImages= Collections.unmodifiableList(images);
    }

    /**
     *
     * @return an unmodifiable representation of the images list
     */
    public List<Image> getImages(){
        return mImages;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public int getCurrentPosition() {
        return mCurrentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        mCurrentPosition = currentPosition;
    }
}
