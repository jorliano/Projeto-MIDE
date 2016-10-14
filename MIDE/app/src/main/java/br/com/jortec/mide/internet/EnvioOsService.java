package br.com.jortec.mide.internet;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Base64;
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
import br.com.jortec.mide.dominio.Usuario;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Jorliano on 07/03/2016.
 */
public class EnvioOsService extends Service {
    String json = null;
    Servico servico;
    boolean hasConection = true;

    Chat chat;

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("LOG", "service EnvioOsService chamado");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i("LOG", "service onStartCommand chamado");

        //Chamar serviço para envio de os
        Work work = new Work();
        work.start();

        return super.onStartCommand(intent, flags, startId);
    }


    public String gerarJson(Servico servico) {
        JSONObject jo = new JSONObject();
        JSONArray jaMateriais = new JSONArray();

        try {

            jo.put("id", servico.getIdWeb());
            jo.put("usuario_id", servico.getUsuario_id());
            jo.put("ordem_servico_id", servico.getOrdem_servico_id());
            jo.put("nomeCliente", servico.getNomeCliente());
            jo.put("parentesco", servico.getParentesco());
            jo.put("descricao", servico.getDescricao());
            jo.put("encerramento", servico.getEncerramento());
            jo.put("longitude", servico.getLongitude());
            jo.put("latitude", servico.getLatitude());
            jo.put("data", servico.getData());

            JSONObject venda = new JSONObject();
            venda.put("equipamento", servico.getVenda().getEquipamento());
            venda.put("quantidade", servico.getVenda().getQuantidade());
            venda.put("tipo", servico.getVenda().getTipo());
            venda.put("pagamento", servico.getVenda().getPagamento());

            jo.put("venda",venda);

            for (int i = 0; i < servico.getMaterias().size(); i++){
                JSONObject aux = new JSONObject();

                aux.put("descricao", servico.getMaterias().get(i).getDescricao());
                aux.put("quantidade", servico.getMaterias().get(i).getQuantidade());
                jaMateriais.put(aux);
            }
            jo.put("materiais",jaMateriais);

        } catch (JSONException e) {
            e.getStackTrace();
        }
        return jo.toString();
    }

    public String gerarImagensJson(long idServico, List<Imagem> lista) {

        JSONObject jsonObject = new JSONObject();
        JSONArray jaImagens = new JSONArray();

        try {

            jsonObject.put("id", idServico);
            for (int i = 0; i < lista.size(); i++){

                ImagemEnviada img = new ImagemEnviada();
                img.setImagem(lista.get(i).getImagem());
                Gson g = new Gson();
                String gs = g.toJson(img);

                Log.i("LOG", "imagem montada ");

                JSONObject aux = new JSONObject(gs);
                jaImagens.put(aux);
            }
            jsonObject.put("imagens",jaImagens);


        } catch (JSONException e) {
            e.getStackTrace();
        }


        return jsonObject.toString();
    }

    public void notificacao() {
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.logo)
                .setContentTitle("Envio de OS")
                .setContentText("OSs enviadas para o servidor com sucesso")
                .setAutoCancel(true);


        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(uri);

        NotificationManager nm = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
        nm.notify(2, builder.build());
    }

    class Work extends Thread{
        boolean ativo = true;

        public void run(){
           Realm realm = Realm.getDefaultInstance();
           RealmResults<Servico> servicos = realm.where(Servico.class).equalTo("estatus", "fechada").findAll();

           Log.i("LOG", "Thread startada " );
           int cont = 0;

            if(servicos.size() == 0){
                Log.i("LOG", "Não tem os para ser cadastrada ");
            }else {
                Log.i("LOG", "Tem os para ser cadastrada " + servicos.size());
                while (ativo && cont < servicos.size()) {
                    servico = servicos.get(cont);

                    Log.i("LOG", "Json gerado "+ gerarJson(servico));
                    String resposta = HttpConnection.getPost(HttpConnection.URL+"cadastrar", "cadastrar", gerarJson(servico));

                    if (resposta.equals(HttpConnection.RESPOSTA_SUCESSO)) {
                        List<Imagem> imagens = new ArrayList<>();
                        for (Imagem img : servico.getImagems()){
                            imagens.add(img);
                        }
                        while (!imagens.isEmpty()){
                            if (imagens.size() <= 2){
                                HttpConnection.getPost(HttpConnection.URL+"cadastrarImagem", "cadastrarImagem", gerarImagensJson(servico.getIdWeb(),imagens));
                                imagens.clear();
                            }else{
                              List<Imagem> aux = new ArrayList<>();
                                for (int i = 0; i < imagens.size(); i++){
                                    aux.add(imagens.get(i));
                                    imagens.remove(i);
                                    if (i == 1)
                                        break;
                                }
                                HttpConnection.getPost(HttpConnection.URL+"cadastrarImagem", "cadastrarImagem", gerarImagensJson(servico.getIdWeb(),aux));
                            }
                        }

                        realm.beginTransaction();
                        servico.removeFromRealm();
                        realm.commitTransaction();

                        Log.i("LOG", "Dados removidos no background ");
                    } else {
                        ativo = false;
                        Log.i("LOG", "Sem comunicação com o servidor, serviço parado ");
                    }

                    cont++;

                    if (servicos.size() == 0) {
                        notificacao();
                    }
                }
            }
            stopSelf();
        }
    }

}
