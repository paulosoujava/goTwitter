package br.com.gotwitter.data;

import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {

    public static final String PREF_ID = "gotwitter";
    private  static SharedPreferences pref;
    private static SharedPreferences.Editor editor;

    public static void setInteger(Context context, String key, int value ){
     pref = context.getSharedPreferences(PREF_ID, 0);
     editor = pref.edit();
     editor.putInt(key, value);
     editor.apply();
    }
    public static int getInteger(Context context, String key ){
        pref = context.getSharedPreferences(PREF_ID, 0);
        return pref.getInt(key, -1);
    }


    public static void setString(Context context, String key, String value ){
        pref = context.getSharedPreferences(PREF_ID, 0);
        editor = pref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getString(Context context, String key ){
        pref = context.getSharedPreferences(PREF_ID, 0);
        return pref.getString(key, "");
    }

    /*
    Intent i = new Intent(this, ActivityB.class);

// Seta num campo estático da ActivityB
i.putParcelableArrayListExtra("dados", dados);

    ArrayList<Tipo> dados = getIntent().getParcelableArrayListExtra("dados");

     */
//Salvando

}
