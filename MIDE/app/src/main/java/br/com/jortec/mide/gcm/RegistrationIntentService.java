package br.com.jortec.mide.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.gson.Gson;

import java.io.IOException;

import br.com.jortec.mide.Util.Util;
import br.com.jortec.mide.dominio.Usuario;
import br.com.jortec.mide.internet.HttpConnection;


/**
 * Created by Jorliano on 27/01/2016.
 */
public class RegistrationIntentService extends IntentService {
    public static final String LOG = "LOG";
    Usuario usuario;
    SharedPreferences preferences;
    String token;


    public RegistrationIntentService() {
        super(LOG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean status = preferences.getBoolean("status", false);

       /* int id = Integer.parseInt( Pref.retrievePrefKeyValue(getApplicationContext(),
                Pref.PREF_KEY_ID,
                "0") );

        String nickname = Pref.retrievePrefKeyValue(getApplicationContext(),
                Pref.PREF_KEY_NICKNAME);
       */

        synchronized (LOG) {
            InstanceID instanceID = InstanceID.getInstance(this);
            try {

                // if( id == 0 ){
                if (!status && Util.verifyConnection(this)) {
                    token = instanceID.getToken(usuario.ID_KEY,
                            GoogleCloudMessaging.INSTANCE_ID_SCOPE,
                            null);

                    Log.i("LOG ", "TOKEN"+ token);

                    sendRegistrationId(token);
                }

            } catch (IOException e) {
                e.getMessage();
            }
        }
    }

    private void sendRegistrationId(String token) {
        usuario = new Usuario();
        usuario.setRegistroGcm(token);

        long id = preferences.getLong(Usuario.ID,0);
        usuario.setId(id);


        new HttpAsyncPOST().execute();

    }

    private class HttpAsyncPOST extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            return post();
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i("Script", "ANSWER: " + result);

            if(result.equals("true")){
                preferences.edit().putBoolean("status", token != null && token.trim().length() > 0).apply();
                imprimirMensagem("Ativado");
            }else{
                imprimirMensagem("falhou");
            }

        }

    }

    private String post() {
        Log.i("LOG", "Chamado Post para cadastro do token GCM");
        return HttpConnection.getPost(HttpConnection.URL+"cadastrarToken", "cadastrarToken", new Gson().toJson(usuario));
    }

    ;

    private void imprimirMensagem(String mensagem) {
        Log.i("RESPOSTA", mensagem);
        Toast.makeText(getApplicationContext(), mensagem, Toast.LENGTH_LONG).show();
    }

}


