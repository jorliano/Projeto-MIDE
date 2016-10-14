package br.com.jortec.mide.service;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.Task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import br.com.jortec.mide.BuildConfig;
import br.com.jortec.mide.LoginSegundoActivity;
import br.com.jortec.mide.dominio.Usuario;
import br.com.jortec.mide.internet.HttpConnection;

/**
 * Created by Jorliano on 19/03/2016.
 */
public class Update  extends AsyncTask<String,Void,Void> {
    private Context context;
    public Update (Context context){
        this.context = context;
    }

    @Override
    protected Void doInBackground(String... arg0) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        if(!preferences.getBoolean(HttpConnection.ATUALIZACAO, false) == true ){

            //VERIFICA ATUALIZAÇÃO
            String resposta = HttpConnection.getGet(arg0[0]);
            if(resposta.equals(HttpConnection.RESPOSTA_SUCESSO)){
                Log.i("LOG"," Existe atualização");
                preferences.edit().putBoolean(HttpConnection.ATUALIZACAO,true).apply();
            }else{
                Log.i("LOG"," Não existe atualização");
            }

        }else {


            try {

                InputStream is = HttpConnection.getGetFile(arg0[0]);

                if (is != null) {

                    String PATH = "/mnt/sdcard/Download/";
                    File file = new File(PATH);
                    file.mkdirs();
                    File outputFile = new File(file, "update.apk");
                    if (outputFile.exists()) {
                        outputFile.delete();
                    }

                    FileOutputStream fos = new FileOutputStream(outputFile);

                    Log.e("LOG", "Update ");
                    //InputStream is = c.getInputStream();


                    byte[] buffer = new byte[1024];
                    int len1 = 0;
                    while ((len1 = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, len1);
                    }
                    fos.close();
                    is.close();

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(new File("/mnt/sdcard/Download/update.apk")), "application/vnd.android.package-archive");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // without this flag android returned a intent error!
                    context.startActivity(intent);
                    preferences.edit().putBoolean(HttpConnection.ATUALIZACAO,false).apply();
                }

            } catch (Exception e) {
                Log.e("UpdateAPP", "Update error! " + e.getStackTrace());
            }
        }
        return null;
    }}
