package br.com.gotwitter.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class ProfileTwitter implements Parcelable {

    private String id;
    private String name;
    private String  screen_name;
    private String  location;
    private String  description;
    private String  url;
    private String  followers_count;
    private String  friends_count;
    private String  statuses_count;
    private String  profile_image_url;
    private String  profile_banner_url;
    private List<Twitter> mList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScreen_name() {
        return screen_name;
    }

    public void setScreen_name(String screen_name) {
        this.screen_name = screen_name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFollowers_count() {
        return followers_count;
    }

    public void setFollowers_count(String followers_count) {
        this.followers_count = followers_count;
    }

    public String getFriends_count() {
        return friends_count;
    }

    public void setFriends_count(String friends_count) {
        this.friends_count = friends_count;
    }

    public String getStatuses_count() {
        return statuses_count;
    }

    public void setStatuses_count(String statuses_count) {
        this.statuses_count = statuses_count;
    }

    public String getProfile_image_url() {
        return profile_image_url;
    }

    public void setProfile_image_url(String profile_image_url) {
        this.profile_image_url = profile_image_url;
    }

    public String getProfile_banner_url() {
        return profile_banner_url;
    }

    public void setProfile_banner_url(String profile_banner_url) {
        this.profile_banner_url = profile_banner_url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Twitter> getmList() {
        return mList;
    }

    public void setmList(List<Twitter> mList) {
        this.mList = mList;
    }

    public static  final String KEY_PROFILE_TWITTER = "profile";


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.screen_name);
        dest.writeString(this.location);
        dest.writeString(this.description);
        dest.writeString(this.url);
        dest.writeString(this.followers_count);
        dest.writeString(this.friends_count);
        dest.writeString(this.statuses_count);
        dest.writeString(this.profile_image_url);
        dest.writeString(this.profile_banner_url);
        dest.writeList(this.mList);
    }

    public ProfileTwitter() {
    }

    protected ProfileTwitter(Parcel in) {
        this.name = in.readString();
        this.screen_name = in.readString();
        this.location = in.readString();
        this.description = in.readString();
        this.url = in.readString();
        this.followers_count = in.readString();
        this.friends_count = in.readString();
        this.statuses_count = in.readString();
        this.profile_image_url = in.readString();
        this.profile_banner_url = in.readString();
        this.mList = new ArrayList<>();
        in.readList(this.mList, Twitter.class.getClassLoader());
    }

    public static final Creator<ProfileTwitter> CREATOR = new Creator<ProfileTwitter>() {
        @Override
        public ProfileTwitter createFromParcel(Parcel source) {
            return new ProfileTwitter(source);
        }

        @Override
        public ProfileTwitter[] newArray(int size) {
            return new ProfileTwitter[size];
        }
    };

    @Override
    public String toString() {
        return "ProfileTwitter{" +
                "name='" + name + '\'' +
                ", screen_name='" + screen_name + '\'' +
                ", location='" + location + '\'' +
                ", description='" + description + '\'' +
                ", url='" + url + '\'' +
                ", followers_count='" + followers_count + '\'' +
                ", friends_count='" + friends_count + '\'' +
                ", statuses_count='" + statuses_count + '\'' +
                ", profile_image_url='" + profile_image_url + '\'' +
                ", profile_banner_url='" + profile_banner_url + '\'' +
                ", mList=" + mList +
                '}';
    }
}
