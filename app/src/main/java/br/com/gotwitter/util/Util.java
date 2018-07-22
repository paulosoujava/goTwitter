package br.com.gotwitter.util;

import android.content.Context;
import android.widget.Toast;

public class Util {

    public  static void alert(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

}
