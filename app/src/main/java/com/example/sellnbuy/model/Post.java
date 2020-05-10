package com.example.sellnbuy.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;

import java.util.Date;


public class Post implements Parcelable {

    public final static String COLLECTION_NAME = "Posts";
    public final static String POST_ID = "postId";
    public static final String OWNER_ID = "ownerId";
    public static final String OWNER_NAME = "ownerName";
    public static final String POST_IMAGES = "postImages";
    public static final String POST_IMAGE_FORMAT = ".jpeg";
    public static final String IS_AVAILABLE = "isAvailable";
    public static final String TITLE = "title";
    public static final String TIME = "time";
    public static final String CITY = "city";
    public static final String CATEGORY = "category";

    private String ownerId;
    private String ownerName;
    private String title;
    private String description;
    private String price;
    private String condition;
    private String category;
    private String city;
    private String imageUri;
    private boolean isAvailable;
    private Timestamp time;

    public Post() {
    }

    public Post(String ownerId, String ownerName, String title, String description, String price, String condition, String category, String city, String imageUri) {
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.title = title;
        this.description = description;
        if (price.isEmpty())    this.price = "0";
        else                    this.price = price;
        this.condition = condition;
        this.category = category;
        this.city = city;
        this.imageUri = imageUri;
        this.isAvailable = true;
        this.time = new Timestamp(new Date());
    }

    protected Post(Parcel in) {
        ownerId = in.readString();
        ownerName = in.readString();
        title = in.readString();
        description = in.readString();
        price = in.readString();
        condition = in.readString();
        category = in.readString();
        city = in.readString();
        imageUri = in.readString();
        isAvailable = in.readByte() != 0;
        time = in.readParcelable(Timestamp.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ownerId);
        dest.writeString(ownerName);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(price);
        dest.writeString(condition);
        dest.writeString(category);
        dest.writeString(city);
        dest.writeString(imageUri);
        dest.writeByte((byte) (isAvailable ? 1 : 0));
        dest.writeParcelable(time, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    public String getOwnerId() {
        return ownerId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }

    public String getCondition() {
        return condition;
    }

    public String getCategory() {
        return category;
    }

    public String getCity() {
        return city;
    }

    public String getImageUri() {
        return imageUri;
    }

    public boolean getIsAvailable() {
        return isAvailable;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setIsAvailable(boolean avaliable) {
        isAvailable = avaliable;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Post{" +
                "ownerId='" + ownerId + '\'' +
                "ownerName='" + ownerName + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", price='" + price + '\'' +
                ", condition='" + condition + '\'' +
                ", category='" + category + '\'' +
                ", city='" + city + '\'' +
                ", isAvailable=" + isAvailable +
                ", time=" + time +
                '}';
    }
}
