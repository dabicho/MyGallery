package mx.org.dabicho.mygallery.model;

/**
 * Created by dabicho on 11/13/14.
 */
public class Image {
    private long imageId;
    private String imageDataStream;
    private String thumbnailDataStream;

    public long getImageId() {
        return imageId;
    }

    public void setImageId(long imageId) {
        this.imageId = imageId;
    }

    public String getImageDataStream() {
        return imageDataStream;
    }

    public void setImageDataStream(String imageDataStream) {
        this.imageDataStream = imageDataStream;
    }

    public String getThumbnailDataStream() {
        return thumbnailDataStream;
    }

    public void setThumbnailDataStream(String thumbnailDataStream) {
        this.thumbnailDataStream = thumbnailDataStream;
    }

    @Override
    public String toString() {
        return "Image{" +
                "imageId=" + imageId +
                ", imageDataStream='" + imageDataStream + '\'' +
                ", thumbnailDataStream='" + thumbnailDataStream + '\'' +
                '}';
    }
}
