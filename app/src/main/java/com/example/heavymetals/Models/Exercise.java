package com.example.heavymetals.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class Exercise implements Parcelable {
    private int id;  // Add id field to represent exercise_list_id
    private String name;
    private String category;  // category field
    private String imageUrl;

    // Default constructor
    public Exercise() {}

    // Constructor with all parameters (id, name, category, imageUrl)
    public Exercise(int id, String name, String category, String imageUrl) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.imageUrl = imageUrl;
    }

    // Getter methods
    public int getId() {  // Add getter for id
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    // Setter methods
    public void setId(int id) {  // Add setter for id
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // Parcelable methods
    protected Exercise(Parcel in) {
        id = in.readInt();  // Add id to Parcel
        name = in.readString();
        category = in.readString();
        imageUrl = in.readString();
    }

    public static final Creator<Exercise> CREATOR = new Creator<Exercise>() {
        @Override
        public Exercise createFromParcel(Parcel in) {
            return new Exercise(in);
        }

        @Override
        public Exercise[] newArray(int size) {
            return new Exercise[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);  // Add id to Parcel
        dest.writeString(name);
        dest.writeString(category);
        dest.writeString(imageUrl);
    }

    @Override
    public String toString() {
        return "Exercise{" +
                "id=" + id +  // Include id in toString()
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
