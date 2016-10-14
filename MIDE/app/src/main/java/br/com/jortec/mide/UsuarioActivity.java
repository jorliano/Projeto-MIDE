package br.com.jortec.mide;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import br.com.jortec.mide.Util.Util;
import br.com.jortec.mide.dominio.Usuario;
import br.com.jortec.mide.internet.HttpConnection;

public class UsuarioActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView tvNome;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(preferences.getString(Usuario.NOME, "").equals("")){

            if (Util.verifyConnection(this)) {
                new HttpAsyncPOST().execute();
            }
        }


        iniciarView();
    }

    public void iniciarView(){
        toolbar = (Toolbar) findViewById(R.id.tb_usuario);
        toolbar.setTitle("Usuario");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tvNome = (TextView) findViewById(R.id.tv_usuario_nome);
        tvNome.setText(preferences.getString(Usuario.NOME, "Usuario não encontrado"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_usuario, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class HttpAsyncPOST extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            return post();
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i("LOG", "resposta : " + result);


            if (!result.equals("")) {

                try {
                    JSONObject jo = new JSONObject(result);
                    preferences.edit().putString(Usuario.NOME, jo.getString("primeiroNome")+" "+jo.getString("sobreNome")).apply();
                    tvNome.setText(jo.getString("primeiroNome")+" "+jo.getString("sobreNome"));
                    Log.i("LOG", "Usuario salvo em preferencias ");

                } catch (JSONException e) {
                    e.printStackTrace();
                }



            } else  {
                imprimirMensagem("falha ao se comunicar com o servidor");
            }

        }

    }

    private String post() {
        Log.i("LOG", "Requisição Post pesquisar chamada "+preferences.getLong(Usuario.ID, 0));
        return HttpConnection.getGet(HttpConnection.URL + "pesquisarPorId/" + preferences.getLong(Usuario.ID, 0));
    }
    private void imprimirMensagem(String mensagem) {
        Toast.makeText(getApplicationContext(), mensagem, Toast.LENGTH_LONG).show();
    }
}
