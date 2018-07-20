package br.com.gotwitter.model;


import android.os.Parcel;
import android.os.Parcelable;

public class Twitter  implements Parcelable {

   private String twitter_txt;
   private String data_create;
   private String id;


   public  Twitter(){}

   public Twitter(String twitter_txt, String data_create) {
      this.twitter_txt = twitter_txt;
      this.data_create = data_create;
   }

   public String getTwitter_txt() {
      return twitter_txt;
   }

   public void setTwitter_txt(String twitter_txt) {
      this.twitter_txt = twitter_txt;
   }

   public String getData_create() {
      return data_create;
   }

   public void setData_create(String data_create) {
      this.data_create = data_create;
   }

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(this.twitter_txt);
      dest.writeString(this.data_create);
   }

   protected Twitter(Parcel in) {
      this.twitter_txt = in.readString();
      this.data_create = in.readString();
   }

   public static final Creator<Twitter> CREATOR = new Creator<Twitter>() {
      @Override
      public Twitter createFromParcel(Parcel source) {
         return new Twitter(source);
      }

      @Override
      public Twitter[] newArray(int size) {
         return new Twitter[size];
      }
   };

   @Override
   public String toString() {
      return "Twitter{" +
              "twitter_txt='" + twitter_txt + '\'' +
              ", data_create='" + data_create + '\'' +
              '}';
   }
}
