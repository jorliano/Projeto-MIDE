package br.com.jortec.mide.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;


import java.util.Date;

import br.com.jortec.mide.LoginActivity;
import br.com.jortec.mide.MainActivity;
import br.com.jortec.mide.R;
import br.com.jortec.mide.Util.Formate;
import br.com.jortec.mide.Util.Util;
import br.com.jortec.mide.dominio.Chat;
import br.com.jortec.mide.dominio.OrdemServico;
import br.com.jortec.mide.dominio.Servico;
import br.com.jortec.mide.dominio.Usuario;
import de.greenrobot.event.EventBus;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by Jorliano on 27/01/2016.
 */
public class MyGcmListenerService extends GcmListenerService {

    private OrdemServico os;
    private int natureza;
    private boolean isRemove = false;

    @Override
    public void onMessageReceived(String from, Bundle data) {
        //super.onMessageReceived(from, data);

        Log.i("Dados recebidos ", data.toString());
        natureza = Integer.parseInt( data.getString("natureza") );

        switch (natureza){
            //caso 1 salvar Ordem de Serviço
            case 1: carregarDados(data);
                break;
            //caso 2 remover OS
            case 2: remover(data.getString("id"));
                break;
            //caso 3 salvar mensage do chat
            case 3: salvarMensage(data);
                break;
        }


    }

    private void setNotificationApp(String titulo, String mensage) {
        final int id = 3030;

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setTicker("teste")
                .setSmallIcon(R.mipmap.logo)
                .setContentTitle(titulo)
                .setContentText(mensage)
                .setAutoCancel(true);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean status = preferences.getBoolean("status", false);

        Intent it = null;
        if(preferences.getLong(Usuario.ID,0) > 0){
           it = new Intent(this, MainActivity.class);
        }else {
           it = new Intent(this, LoginActivity.class);
        }

        PendingIntent pi = PendingIntent.getActivity(this, 0, it, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pi);

        // BIG CONTENT
            NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
            //bigText.setBigContentTitle( data.getString("title") );
            bigText.bigText(mensage);
            builder.setStyle(bigText);

            /*NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
                String[] messages = new String[6];
                builder.setNumber(messages.length);
            inboxStyle.setSummaryText("thiengocalopsita@gmail.com");
            for( int i = 0; i < messages.length; i++ ){
                messages[i] = "Test "+(new Random()).nextInt(9999);
                inboxStyle.addLine( messages[i] );
            }
            builder.setStyle(inboxStyle);*/

        //builder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});



     /*   new Thread(){
            @Override
            public void run() {
                super.run();
                Bitmap img1 = null, img2 = null;

                try {
                    img1 = Picasso.with(getBaseContext()).load( data.getString("large_icon") ).get();
                    img2 = Picasso.with(getBaseContext()).load( data.getString("big_picture") ).get();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                builder.setLargeIcon( img1 );
                builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(img2));


                builder.setPriority(NotificationCompat.PRIORITY_HIGH);
                builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

                Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                builder.setSound(uri);

                NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                nm.notify(id, builder.build());
            }
        }.start();*/

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(uri);

        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(id, builder.build());
    }

    public void carregarDados(Bundle bundle){

            OrdemServico os = new OrdemServico();
            Log.i("LOG", " id do  serviço no registroGcm "+bundle.getString("id")+" data da os "+Formate.stringParaData(bundle.getString("data")));

            os.setTipo(bundle.getString("tipo"));
            os.setCodigo(Long.parseLong(bundle.getString("codigo")));
            os.setIdServico(Long.parseLong(bundle.getString("id")));
            os.setNomeCliente(bundle.getString("nomeCliente"));
            os.setContato(bundle.getString("contato"));
            os.setEndereco(bundle.getString("endereco"));
            os.setBairro(bundle.getString("bairro"));
            os.setCidade(bundle.getString("cidade"));
            os.setTelefone(bundle.getString("telefone"));
            os.setData(Formate.stringParaData(bundle.getString("data")));
            os.setAutenticacao(bundle.getString("autenticacao"));
            os.setDadosAcesso(bundle.getString("dadosAcesso"));
            os.setDescricao(bundle.getString("descricao"));
            os.setHora(bundle.getString("hora"));

            cadastrar(os);

            if (!Util.isMyApplicationTaskOnTop(this)) {
                setNotificationApp("Nova Ordem de Serviço",bundle.getString("nomeCliente"));
            }else {
                EventBus.getDefault().post(new Bundle());
            }
    }


    public void cadastrar(OrdemServico os ){

        Realm realm = Realm.getDefaultInstance();
        RealmResults<OrdemServico> ordemServicos = realm.where(OrdemServico.class).findAll();

        //GERAR ID
        ordemServicos.sort("id", Sort.DESCENDING);
        long id = ordemServicos.size() == 0? 1: ordemServicos.get(0).getId() + 1;
        os.setId(id);

        realm.beginTransaction();
        realm.copyToRealmOrUpdate(os);
        realm.commitTransaction();
    }

    public void remover(String id ){

        Realm realm = Realm.getDefaultInstance();
        RealmResults<OrdemServico> ordemServicos = realm.where(OrdemServico.class).findAll();


        for (int i = 0 ;i < ordemServicos.size(); i++){
            Log.i("LOG", "ids" + ordemServicos.get(i).getIdServico());
            if(ordemServicos.get(i).getIdServico() == Long.parseLong(id)){
                OrdemServico ordemServico = ordemServicos.get(i);
                realm.beginTransaction();
                ordemServico.removeFromRealm();
                realm.commitTransaction();

                Log.i("LOG", "os removida");
            }
        }


    }

    public void salvarMensage (Bundle bundle){

        if (!Util.isMyApplicationTaskOnTop(this)) {
            setNotificationApp("Nova Mensage",bundle.getString("mensage"));
        }else {
            EventBus.getDefault().post(new Chat());
        }

        Chat c = new Chat();
        Log.i("LOG", " menssage recebida "+bundle);

        c.setRemetente(bundle.getString("remetente"));
        c.setMensage(bundle.getString("mensage"));
        c.setEstatus("estatus");
        c.setData(new Date());
        c.setDestinatario("destinatario");
        c.setHora(bundle.getString("hora"));

        Realm realm = Realm.getDefaultInstance();
        RealmResults<Chat> chats = realm.where(Chat.class).findAll();

        //GERAR ID
        chats.sort("id", Sort.DESCENDING);
        long id = chats.size() == 0? 1: chats.get(0).getId() + 1;
        c.setId(id);

        realm.beginTransaction();
        realm.copyToRealmOrUpdate(c);
        realm.commitTransaction();
    }
}
