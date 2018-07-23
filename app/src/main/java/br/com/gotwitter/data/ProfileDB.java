package br.com.gotwitter.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import br.com.gotwitter.model.ProfileTwitter;
import br.com.gotwitter.model.Twitter;

public class ProfileDB extends SQLiteOpenHelper {

    private static final String NAME_DATABASE = "gotwitter";

    private static final String _ID = "_id";

    private static final String PROFILE_COLUMN = "profile";
    private static final String TWITTER_COLUMN = "twitter";


    //columns profile
    private static final String NAME = "name";
    private static final String NICKNAME = "nickname";
    private static final String URL_SITE = "url_site";
    private static final String URL_IMG_PERFIL = "url_img";
    private static final String URL_IMG_BANNER = "url_back";
    private static final String FOLLOWERS_COUNT = "followers_count";
    private static final String STATUSES_COUNT = "statuses_count";
    private static final String FRIENDS_COUNT = "friends_count";
    private static final String LOCATION = "location";
    private static final String DESCRIPTION = "description";

    //columns twitter
    private static final String TEXT = "text";
    private static final String DATA = "data";

    private static final int VERSION = 1;

    public ProfileDB(Context context) {
        super(context, NAME_DATABASE, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //data profile
        db.execSQL("CREATE TABLE IF NOT EXISTS " + PROFILE_COLUMN + "(" +
                _ID + " integer primary key autoincrement," +
                NAME + " text," +
                NICKNAME + " text," +
                URL_SITE + " text," +
                URL_IMG_PERFIL + " text," +
                URL_IMG_BANNER + "  text," +
                FOLLOWERS_COUNT + "  text," +
                STATUSES_COUNT + " text," +
                FRIENDS_COUNT + " text," +
                LOCATION + " text," +
                DESCRIPTION + " text)");


        //list 10 twitter this user up
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TWITTER_COLUMN + "(" +
                _ID + "  integer primary key autoincrement," +
                TEXT + " text," +
                DATA + " text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void saveProfile(ProfileTwitter p) {

        SQLiteDatabase db = getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(NAME, p.getName());
            values.put(NICKNAME, p.getScreen_name());
            values.put(URL_SITE, p.getUrl());
            values.put(URL_IMG_PERFIL, p.getProfile_image_url());
            values.put(URL_IMG_BANNER, p.getProfile_banner_url());
            values.put(FOLLOWERS_COUNT, p.getFollowers_count());
            values.put(STATUSES_COUNT, p.getStatuses_count());
            values.put(FRIENDS_COUNT, p.getFriends_count());
            values.put(LOCATION, p.getLocation());
            values.put(DESCRIPTION, p.getDescription());

            if (hasProfile(p.getScreen_name(), db) > 0) {
                String[] whereArgs = new String[]{p.getScreen_name()};
                db.update(PROFILE_COLUMN, values, NICKNAME + "=?", whereArgs);

            } else {
                db.insert(PROFILE_COLUMN, "", values);
            }

        } finally {
            db.close();
        }
    }

    public ProfileTwitter getProfile() {
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.query(PROFILE_COLUMN, null, null, null,
                null, null, null);
        return getProfileFromBD(c, db);
    }

    private ProfileTwitter getProfileFromBD(Cursor c, SQLiteDatabase db) {
        ProfileTwitter mProfileTwitter = new ProfileTwitter();
        if (c.moveToNext()) {
            do {

                mProfileTwitter.setId(String.valueOf(c.getLong(c.getColumnIndex(_ID))));
                mProfileTwitter.setDescription(c.getString(c.getColumnIndex(DESCRIPTION)));
                mProfileTwitter.setFollowers_count(c.getString(c.getColumnIndex(FOLLOWERS_COUNT)));
                mProfileTwitter.setFriends_count(c.getString(c.getColumnIndex(FRIENDS_COUNT)));
                mProfileTwitter.setLocation(c.getString(c.getColumnIndex(LOCATION)));
                mProfileTwitter.setName(c.getString(c.getColumnIndex(NAME)));
                mProfileTwitter.setProfile_banner_url(c.getString(c.getColumnIndex(URL_IMG_BANNER)));
                mProfileTwitter.setProfile_image_url(c.getString(c.getColumnIndex(URL_IMG_PERFIL)));
                mProfileTwitter.setScreen_name(c.getString(c.getColumnIndex(NICKNAME)));
                mProfileTwitter.setStatuses_count(c.getString(c.getColumnIndex(STATUSES_COUNT)));
                mProfileTwitter.setUrl(c.getString(c.getColumnIndex(URL_SITE)));


            } while (c.moveToNext());

            mProfileTwitter.setmList(getAllList());
        }
        return mProfileTwitter;
    }

    private int hasProfile(String nick, SQLiteDatabase db) {
        Cursor c = db.query(PROFILE_COLUMN, null, NICKNAME + "= '" + nick + "'", null, null, null, null);
        return c.getCount();
    }

    public void saveTwitter(List<Twitter> list) {
        SQLiteDatabase db = getWritableDatabase();
        //delete os existentes
        deleteALlTwitter();

        try {
            //insert all the list
            ContentValues values;
            for( Twitter t : list ) {
                values = new ContentValues();
                values.put(TEXT, t.getTwitter_txt());
                values.put(DATA, t.getData_create());
                db.insert(TWITTER_COLUMN, "", values);
            }
        } finally {
            db.close();
        }
    }

    private boolean deleteALlTwitter() {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TWITTER_COLUMN, _ID + "> 0" , null) > 0;


    }

    //GET ALL TWITTEL IN SQLite
    public List<Twitter> getAllList() {
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.query(TWITTER_COLUMN, null, null, null,
                null, null, null);
        return toList(c, db);
    }

    private List<Twitter> toList(Cursor c, SQLiteDatabase db) {
        List<Twitter> list = new ArrayList<>();
        if (c.moveToNext()) {
            do {
                Twitter t = new Twitter();
                t.setId(String.valueOf(c.getLong(c.getColumnIndex(_ID))));
                t.setTwitter_txt(c.getString(c.getColumnIndex(TEXT)));
                t.setData_create(c.getString(c.getColumnIndex(DATA)));
                list.add(t);

            } while (c.moveToNext());
        }
        return list;
    }


}
