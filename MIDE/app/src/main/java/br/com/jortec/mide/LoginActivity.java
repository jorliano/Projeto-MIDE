package br.com.jortec.mide;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.com.jortec.mide.Util.Util;
import br.com.jortec.mide.dominio.Servico;
import br.com.jortec.mide.dominio.Usuario;
import br.com.jortec.mide.internet.HttpConnection;
import br.com.jortec.mide.service.LocalizacaoGps;
import io.realm.Realm;
import io.realm.RealmResults;

public class LoginActivity extends AppCompatActivity {

    private Button btLogar;
    private EditText edtLogin;
    private EditText edtSenha;
    Usuario us;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        preferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
       if(!preferences.getString(Usuario.LOGIN, "").equals("")){
            startActivity(new Intent(this, LoginSegundoActivity.class));
            finish();
       }
        iniciarViews();

        btLogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isValido()) {
                    us.setLogin(edtLogin.getText().toString());
                    us.setSenha(edtSenha.getText().toString());

                    if (Util.verifyConnection(v.getContext())) {
                        new HttpAsyncPOST().execute();
                    }
                    else{
                        android.support.design.widget.Snackbar.make(v, "Verifique sua conexão com a internet", android.support.design.widget.Snackbar.LENGTH_SHORT)
                                                                   .show();
                    }
                }else {
                    imprimirMensagem("Campoos não podem ser vazios");
                }

            }
        });
    }

    public boolean isValido() {
        if (edtLogin.getText().toString().equals("")) {
            imprimirMensagem("Campo não pode ser nulo");
            edtLogin.requestFocus();
            return false;
        }
        if (edtSenha.getText().toString().equals("")) {
            imprimirMensagem("Campo não pode ser nulo");
            edtSenha.requestFocus();
            return false;
        }

        return true;
    }

    public void iniciarViews() {
        btLogar = (Button) findViewById(R.id.bt_logar);
        edtLogin = (EditText) findViewById(R.id.edt_login);
        edtSenha = (EditText) findViewById(R.id.edt_senha);

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


            if (!result.equals("") && result.length() < 10) {
                long id = Long.parseLong(result);
                if (id > 0) {

                    preferences.edit().putLong(Usuario.ID, id).apply();
                    preferences.edit().putString(Usuario.LOGIN, us.getLogin()).apply();

                    Log.i("LOG", "Usuario logado id: " + preferences.getLong(Usuario.ID, 0));
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    // imprimirMensagem(result);
                }else {
                    imprimirMensagem("Login ou senha invalida");
                }
            } else  {
                imprimirMensagem("falha ao se comunicar com o servidor");
            }

        }

    }

    private String post() {
        Log.i("LOG", "Requisição Post Logar chamada ");
        return HttpConnection.getPost(HttpConnection.URL+"logar", "logar", new Gson().toJson(us));
    }
    private void imprimirMensagem(String mensagem) {
        Toast.makeText(getApplicationContext(), mensagem, Toast.LENGTH_LONG).show();
    }

}
