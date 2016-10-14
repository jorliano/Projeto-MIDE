package br.com.jortec.mide;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.com.jortec.mide.Util.Util;
import br.com.jortec.mide.adapter.ChatAdapter;
import br.com.jortec.mide.adapter.OsAdapter;
import br.com.jortec.mide.dominio.Chat;
import br.com.jortec.mide.dominio.Usuario;
import br.com.jortec.mide.interfaces.RecyclerViewOnclickListener;
import br.com.jortec.mide.internet.EnvioChatService;
import br.com.jortec.mide.internet.HttpConnection;
import br.com.jortec.mide.internet.NetworkStateReceiver;
import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class ChatActivity extends AppCompatActivity implements RecyclerViewOnclickListener {

    private EditText edtMenssage;
    private ImageButton btEnviar;
    private Toolbar toolbar;
    private RealmRecyclerView recyclerView;
    private ChatAdapter adapter;
    SharedPreferences preferences;
    Chat chat;
    Realm realm;
    RealmResults<Chat> chats;

    List <Integer> msgSelecionadas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);



        realm = Realm.getDefaultInstance();
        chats = realm.where(Chat.class).findAll();

        iniciarView();


        btEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtMenssage.getText().toString().trim().equals("")) {
                    imprimirMensagem("Digite algo");

                } else {

                    Calendar calendar = Calendar.getInstance();
                    int hora = calendar.get(Calendar.HOUR_OF_DAY);
                    int minuto = calendar.get(Calendar.MINUTE);


                    chat = new Chat();
                    chat.setHora((hora < 10 ? "0"+hora  : hora)+":"+minuto);
                    chat.setEstatus("desmarcada");
                    chat.setMensage(edtMenssage.getText().toString());
                    chat.setDestinatario("sistema");
                    chat.setRemetente(preferences.getString(Usuario.NOME, "Usuario não encontrado"));
                    chat.setData(new Date());



                    //GERAR ID
                    chats = realm.where(Chat.class).findAll();
                    chats.sort("id", Sort.DESCENDING);
                    long id = chats.size() == 0? 1: chats.get(0).getId() + 1;
                    chat.setId(id);

                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(chat);
                    realm.commitTransaction();

                    recyclerView.smoothScrollToPosition(chats.size());

                    if(Util.verifyConnection(v.getContext())) {
                        new HttpAsyncPOST().execute();
                    }
                }
            }
        });
    }

    public void iniciarView(){

        edtMenssage = (EditText) findViewById(R.id.edt_chat);
        btEnviar = (ImageButton) findViewById(R.id.bt_chat);
        toolbar = (Toolbar) findViewById(R.id.chat_toolbar);
        recyclerView = (RealmRecyclerView) findViewById(R.id.chate_recycler);

        toolbar.setTitle(R.string.tituloBarraChat);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        adapter = new ChatAdapter(ChatActivity.this,chats,true,true);
        adapter.setRecyclerViewOnclickListener(this);
        recyclerView.setAdapter(adapter);

        msgSelecionadas = new ArrayList();

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    //LISTENER
    public void onEvent(Chat chat) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                recyclerView.smoothScrollToPosition(chats.size());
                chats = realm.where(Chat.class).findAll();
                adapter.notifyDataSetChanged();

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home: finish();
                break;
            case R.id.menu_deletar:
                  for (int i = msgSelecionadas.size() - 1 ; i > -1; i --){

                      chat = chats.get(msgSelecionadas.get(i));

                      realm.beginTransaction();
                      chat.removeFromRealm();
                      realm.commitTransaction();

                  }
                  msgSelecionadas.clear();
                  toolbar.getMenu().clear();
                  recyclerView.smoothScrollToPosition(chats.size());
                  adapter.notifyAll();
                  imprimirMensagem("Removido com sucesso");
                  break;
        }

        return true;
    }

    public String gerarJson(Chat c) {
        JSONObject jo = new JSONObject();

        try {
            jo.put("destinatario", c.getDestinatario());
            jo.put("remetente", c.getRemetente());
            jo.put("mensage", c.getMensage());
            jo.put("hora", c.getHora());
           // jo.put("data", c.getData());
            jo.put("estatus", c.getEstatus());

        } catch (JSONException e) {
            e.getStackTrace();
        }
        return jo.toString();
    }

    @Override
    public void onclickListener(View view, int position) {
        LinearLayout ll = (LinearLayout) view;
        LinearLayout llParent;

        chat = chats.get(position);

        if(chat.getRemetente().equals("sistema")){
            //Layout da esquerda
            llParent = (LinearLayout) ll.getChildAt(1);
        }else{
            //Layout da direita
            llParent = (LinearLayout) ll.getChildAt(3);
        }

        CardView cv = (CardView)  llParent.getChildAt(0);
        RelativeLayout rl = (RelativeLayout) cv.getChildAt(0);


        if (msgSelecionadas.contains(position)) {

            rl.setBackgroundColor(0);

            for (int i = 0; i < msgSelecionadas.size(); i++) {
                if (msgSelecionadas.get(i).equals(position)) {
                    msgSelecionadas.remove(i);
                }
            }

        } else {
            rl.setBackgroundColor(ChatActivity.this.getResources().getColor(android.R.color.holo_orange_light));
            msgSelecionadas.add(position);
        }

        if (msgSelecionadas.size() == 1) {
            toolbar.getMenu().clear();
            getMenuInflater().inflate(R.menu.menu_chat_opcoes, toolbar.getMenu());

        } else if (msgSelecionadas.size() == 0) {
            toolbar.getMenu().clear();
        }
    }

    private class HttpAsyncPOST extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            return post();
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i("LOG", "resposta : " + result);


            if (result.equals(HttpConnection.RESPOSTA_SUCESSO)) {


                Log.i("LOG", "Menssage enviada com sucesso");
                //imprimirMensagem(result);
                realm.beginTransaction();
                chat.setEstatus("marcada");
                realm.copyToRealmOrUpdate(chat);
                realm.commitTransaction();
                adapter.notifyDataSetChanged();
                edtMenssage.setText("");



            } else  {
                imprimirMensagem("falha ao se comunicar com o servidor");

            }

            MediaPlayer player = MediaPlayer.create(ChatActivity.this, R.raw.msg_text);
            player.seekTo(5);
            player.start();

        }

    }

    private String post() {
        Log.i("LOG", "Requisição Post chat chamada "+gerarJson(chat));
        return HttpConnection.getPost(HttpConnection.URL_CHAT+"enviar", "dados", gerarJson(chat));
    }
    private void imprimirMensagem(String mensagem) {
        Toast.makeText(getApplicationContext(), mensagem, Toast.LENGTH_LONG).show();
    }

}
