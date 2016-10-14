package br.com.jortec.mide;

import android.*;
import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import br.com.jortec.mide.Util.Util;
import br.com.jortec.mide.dominio.Usuario;
import br.com.jortec.mide.internet.HttpConnection;
import br.com.jortec.mide.service.LocalizacaoGps;
import br.com.jortec.mide.service.Update;

public class LoginSegundoActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSIONS_CODE = 1;
    private Button btLogar;
    private EditText edtSenha;
    Usuario us;
    SharedPreferences preferences;
    ProgressDialog progressDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_segundo);

        //VERIFICA ATUALIZAÇÃO
        preferences = PreferenceManager.getDefaultSharedPreferences(LoginSegundoActivity.this);
        boolean atualização = preferences.getBoolean(HttpConnection.ATUALIZACAO, false);

        if(atualização == true) {

            new AlertDialog.Builder(this)
                    .setTitle(R.string.titulo_dialogo)
                    .setMessage("Existe uma nova versão para atualizar, deseja atualizar ?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (ContextCompat.checkSelfPermission(LoginSegundoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                if (ActivityCompat.shouldShowRequestPermissionRationale(LoginSegundoActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                    Log.i("LOG", "Permisão negada uma vez ");
                                    alerta("Necessario permitir para atualizar o sistema sistema");
                                } else {
                                    ActivityCompat.requestPermissions(LoginSegundoActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_CODE);
                                }
                            } else {
                                if (!Util.verifyGps(LoginSegundoActivity.this))
                                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                                Update atualizaApp = new Update(LoginSegundoActivity.this);
                                atualizaApp.execute(HttpConnection.URL + BuildConfig.VERSION_CODE);
                            }
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                           dialog.dismiss();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();


        }else{
            Update atualizaApp = new Update(this);
            atualizaApp.execute(HttpConnection.URL+"verificaAtualizacao/"+BuildConfig.VERSION_CODE);
        }

        iniciarViews();
        us.setLogin(preferences.getString(Usuario.LOGIN,""));


        btLogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isValido()) {
                    us.setSenha(edtSenha.getText().toString());

                    if (Util.verifyConnection(v.getContext())) {
                        new HttpAsyncPOST().execute();
                        progressDialog = new ProgressDialog(LoginSegundoActivity.this,R.style.MyAlertDialog);
                        //R.style.AppTheme_Dark_Dialog);
                        progressDialog.setIndeterminate(true);
                        progressDialog.setMessage("Authenticando...");
                        progressDialog.show();
                    }
                    else{
                        android.support.design.widget.Snackbar.make(v, "Verifique sua conexão com a internet", android.support.design.widget.Snackbar.LENGTH_SHORT)
                                .show();
                    }
                }else {
                    imprimirMensagem("Campoo não pode ser vazio");
                }

            }
        });
    }

    public boolean isValido() {
        if (edtSenha.getText().toString().equals("")) {
            imprimirMensagem("Campo não pode ser nulo");
            edtSenha.requestFocus();
            return false;
        }

        return true;
    }

    public void iniciarViews() {
        btLogar = (Button) findViewById(R.id.bt_logar);
        edtSenha = (EditText) findViewById(R.id.edt_senha);

        //edtSenha.setText("Atualizado com sucesso");
        us = new Usuario();
    }

    private class HttpAsyncPOST extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... urls) {
            return post();
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i("LOG", "resposta : " + result);
            progressDialog.dismiss();

            if (!result.equals("") && result.length() < 10) {
                long id = Long.parseLong(result);
                if (id > 0) {

                    preferences.edit().putLong(Usuario.ID, id).apply();

                    Log.i("LOG", "Usuario logado id: " + preferences.getLong(Usuario.ID, 0));
                    startActivity(new Intent(LoginSegundoActivity.this, MainActivity.class));
                    // imprimirMensagem(result);
                } else {
                    imprimirMensagem("Senha invalida");
                }
            } else  {
                imprimirMensagem("Falha ao se comunicar com o servidor");
            }

        }

    }

    private String post() {
        Log.i("LOG", "Requisição Post Logar chamada ");
        return HttpConnection.getPost(HttpConnection.URL + "logar", "logar", new Gson().toJson(us));
    }
    private void imprimirMensagem(String mensagem) {
        Toast.makeText(getApplicationContext(), mensagem, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LoginSegundoActivity.this);
        preferences.edit().putLong(Usuario.ID, 0).apply();
        Log.i("LOG", "Usuario deslogado id: " + preferences.getLong(Usuario.ID, 0));
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case REQUEST_PERMISSIONS_CODE:
                for(int i = 0 ; i < permissions.length; i++){
                    if(permissions[i].equalsIgnoreCase(Manifest.permission.WRITE_EXTERNAL_STORAGE ) &&
                            grantResults[i] == PackageManager.PERMISSION_GRANTED )
                    {

                        Log.i("LOG", "permissão aceita ");
                        Update atualizaApp = new Update(this);
                        atualizaApp.execute(HttpConnection.URL+ BuildConfig.VERSION_CODE);

                    } else {
                        alerta("Necessario permitir para atualizar o sistema sistema");
                        Log.i("LOG", "permissão negada " );
                    }
                }
        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void alerta(String msg){
        new AlertDialog.Builder(this)
                .setTitle(R.string.titulo_dialogo)
                .setMessage(msg)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(LoginSegundoActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_CODE);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}