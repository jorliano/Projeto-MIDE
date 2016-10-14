package br.com.jortec.mide.internet;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.com.jortec.mide.R;
import br.com.jortec.mide.dominio.Chat;
import br.com.jortec.mide.dominio.Imagem;
import br.com.jortec.mide.dominio.ImagemEnviada;
import br.com.jortec.mide.dominio.Servico;
import de.greenrobot.event.EventBus;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Jorliano on 07/03/2016.
 */
public class EnvioChatService extends Service {


    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("LOG", "service EnvioChatService chamado");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i("LOG", "service onStartCommand chamado");

        //Chamar serviço para envio de mesnsage do chat
        WorkChat  workChat = new WorkChat();
        workChat.start();

        return super.onStartCommand(intent, flags, startId);
    }

    class WorkChat extends Thread{
        boolean ativo = true;
        Chat chat;

        public void run(){
            Realm realm = Realm.getDefaultInstance();
            RealmResults<Chat> chats = realm.where(Chat.class).findAll();

            Log.i("LOG", "Thread workChat startada " );
            int cont = 0;

            if(chats.size() == 0){
                Log.i("LOG", "Não tem servico para enviar ");
            }else {

                Log.i("LOG", "Percorrendo mensages ");
                while (ativo && cont < chats.size()) {

                    chat = chats.get(cont);

                    if(chat.getEstatus().equals("desmarcada")){

                        String resposta = HttpConnection.getPost(HttpConnection.URL_CHAT+"enviar", "dados", gerarJsonChat(chat));

                        if (resposta.equals(HttpConnection.RESPOSTA_SUCESSO)) {

                            Log.i("LOG", "Menssage enviada com sucesso");

                            realm.beginTransaction();
                            chat.setEstatus("marcada");
                            realm.copyToRealmOrUpdate(chat);
                            realm.commitTransaction();

                            EventBus.getDefault().post(new Chat());

                        }
                        else {
                            ativo = false;
                            Log.i("LOG", "Sem comunicação com o servidor, serviço parado ");
                        }


                    }
                    cont++;
                }
            }
            stopSelf();
        }
    }

    public String gerarJsonChat(Chat c) {
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
}
