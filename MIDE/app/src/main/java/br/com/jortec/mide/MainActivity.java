package br.com.jortec.mide;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import br.com.jortec.mide.Util.Util;
import br.com.jortec.mide.adapter.OsAdapter;
import br.com.jortec.mide.dominio.OrdemServico;
import br.com.jortec.mide.fragments.OrdemServicoFragment;
import br.com.jortec.mide.gcm.RegistrationIntentService;
import br.com.jortec.mide.interfaces.Comunicador;
import br.com.jortec.mide.interfaces.RecyclerViewOnclickListener;
import br.com.jortec.mide.service.LocalizacaoGps;
import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView;
import de.greenrobot.event.EventBus;
import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity implements RecyclerViewOnclickListener{

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 900 ;
    private static final int REQUEST_PERMISSIONS_CODE = 1;

    private Toolbar toolbar;
    private Drawer.Result navegadorDrawer;
    private RealmRecyclerView recyclerView;
    private RelativeLayout rlOs;
    private RelativeLayout  rlf;
    private FragmentManager fm;
    private OrdemServicoFragment odemServicoFragment;
    private OrdemServico ordemServico;
    private OsAdapter osAdapter;

    private Realm realm;
    private RealmResults<OrdemServico> ordemServicos;

    Comunicador comunicador;

    @Override
    protected void onResume() {
        super.onResume();

        if(!ordemServicos.isEmpty()) {
            comunicador.responde(ordemServicos.get(0));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iniciarViews();

        if(checkPlayServices()){
            Intent it = new Intent(this, RegistrationIntentService.class);
            startService(it);
        }

        // NAVEGATION DRAWER
        navegadorDrawer = new Drawer()
                .withActivity(this)
                .withToolbar(toolbar)
                .withDisplayBelowToolbar(false)
                .withActionBarDrawerToggleAnimated(true)
                .withDrawerGravity(Gravity.START)
                .withSavedInstance(savedInstanceState)
                .withActionBarDrawerToggle(true)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.usuario).withIcon(R.drawable.account),
                        new PrimaryDrawerItem().withName(R.string.chat).withIcon(R.drawable.wechat),
                        new PrimaryDrawerItem().withName(R.string.sobre).withIcon(R.drawable.tipo_usuario)

                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l, IDrawerItem iDrawerItem) {

                        switch (i){
                            case 0 : startActivity(new Intent(view.getContext(), UsuarioActivity.class));
                                break;
                            case 1: startActivity(new Intent(view.getContext(), ChatActivity.class));
                                break;
                            case 2: startActivity(new Intent(view.getContext(), SobreActivity.class));
                                break;
                        }

                    }
                }).build();

        verificarLista();


        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }

        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.rlf_ordem_servico, odemServicoFragment, "TAG");
        ft.commit();


        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            // if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)){
            //     Log.i("LOG", "Permisão negada uma vez ");
            //  }else{
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_CODE);
            //  }
        }else {
            if(!Util.verifyGps(this))
                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));

            new LocalizacaoGps(this);

        }
    }

    public void iniciarViews(){
        rlOs = (RelativeLayout) findViewById(R.id.rl_os);
        rlf = (RelativeLayout) findViewById(R.id.rlf_ordem_servico);
        toolbar = (Toolbar) findViewById(R.id.dados_toolbar);
        recyclerView = (RealmRecyclerView) findViewById(R.id.realm_recycler_os);
        realm = Realm.getDefaultInstance();
        ordemServicos = realm.where(OrdemServico.class).findAll();
        fm = getSupportFragmentManager();

        odemServicoFragment = new OrdemServicoFragment();
        comunicador = (Comunicador) odemServicoFragment;

        toolbar.setTitle(R.string.tituloPrincipal);
        setSupportActionBar(toolbar);

        osAdapter = new OsAdapter(this,ordemServicos,true,true);
        osAdapter.setRecyclerViewOnclickListener(this);
        recyclerView.setAdapter(osAdapter);
    }

    //LISTENER
    public void onEvent(final Bundle bundle) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                ordemServicos = realm.where(OrdemServico.class).findAll();
                verificarLista();
                osAdapter.notifyDataSetChanged();
            }
        });

    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i("LOG", "Este dispositivo não tem suporte.");
                finish();
            }
            return false;
        }
        return true;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == OrdemServico.BUNDLE_ONRESULT) {
            if (resultCode == Activity.RESULT_OK) {

                ordemServicos = realm.where(OrdemServico.class).findAll();
                verificarLista();
                osAdapter.notifyDataSetChanged();

            }
        }
    }

    @Override
    public void onclickListener(View view, int position) {
        ordemServico = ordemServicos.get(position);

        if(rlf.isShown()) {
            comunicador.responde(ordemServico);
        }else{
            startActivity(new Intent(view.getContext(), OrdemServicoActivity.class).putExtra(OrdemServico.OS, ordemServico.getId()));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case REQUEST_PERMISSIONS_CODE:
                for(int i = 0 ; i < permissions.length; i++){
                    if(permissions[i].equalsIgnoreCase(android.Manifest.permission.ACCESS_FINE_LOCATION ) &&
                            grantResults[i] == PackageManager.PERMISSION_GRANTED )
                    {

                        Log.i("LOG", "permissão aceita ");
                        if(!Util.verifyGps(this))
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        new LocalizacaoGps(this);

                    }
                    else if(!permissions[i].equalsIgnoreCase(android.Manifest.permission.WRITE_EXTERNAL_STORAGE ) &&
                            grantResults[i] == PackageManager.PERMISSION_GRANTED )
                    {
                        new AlertDialog.Builder(this)
                                .setTitle(R.string.titulo_dialogo)
                                .setMessage("Necessario permitir serviço por que é indispensalve pro sistema")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS_CODE);
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                        Log.i("LOG", "permissão negada " );
                    }
                }
        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void verificarLista(){


        //CASO A LISTA ESTEJA VAZIA
        if(ordemServicos.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            TextView tv = new TextView(this);
            tv.setText("Não existe itens na lista");
            tv.setId(1);
            tv.setTextColor(getResources().getColor(R.color.primary_text));
            tv.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            tv.setGravity(Gravity.CENTER);
            rlOs.addView(tv);
        }else if(rlOs.findViewById(1) != null){
            recyclerView.setVisibility( View.VISIBLE);
            rlOs.removeView(rlOs.findViewById(1));
        }
    }

}
