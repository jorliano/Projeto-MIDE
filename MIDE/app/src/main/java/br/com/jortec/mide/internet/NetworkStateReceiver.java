package br.com.jortec.mide.internet;

/**
 * Created by Jorliano on 07/03/2016.
 */
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.com.jortec.mide.R;
import br.com.jortec.mide.Util.Util;
import br.com.jortec.mide.dominio.Servico;
import br.com.jortec.mide.service.LocalizacaoGps;
import io.realm.Realm;
import io.realm.RealmResults;

public class NetworkStateReceiver extends BroadcastReceiver {



    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        Log.i("LOG", "Broadcast recever chamado");

        if (Util.verifyConnection(context)) {

            Log.i("LOG", "Broadcast Tem conexão");

            Intent it = new Intent(context, EnvioOsService.class);
            context.startService(it);

            Intent itent = new Intent(context, EnvioChatService.class);
            context.startService(itent);
        }

    }


}