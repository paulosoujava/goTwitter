package br.com.gotwitter.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import br.com.gotwitter.R;

public class Connection {

    private  static NetworkInfo networkInfo;


    public static boolean checkConnection(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            Toast.makeText(context.getApplicationContext(), R.string.check_internet, Toast.LENGTH_SHORT).show();
            return false;

        }
    }
}
