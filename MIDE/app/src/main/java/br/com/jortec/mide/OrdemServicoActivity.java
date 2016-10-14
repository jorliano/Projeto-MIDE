package br.com.jortec.mide;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import br.com.jortec.mide.dominio.OrdemServico;

import br.com.jortec.mide.fragments.OrdemServicoFragment;
import br.com.jortec.mide.interfaces.Comunicador;
import de.greenrobot.event.EventBus;
import io.realm.Realm;
import io.realm.RealmResults;


public class OrdemServicoActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private OrdemServico os;
    private Realm realm;
    private RealmResults<OrdemServico> ordemServicos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordem_servico);


        if (getIntent() != null && getIntent().getLongExtra(OrdemServico.OS, 0) > 0) {

            realm = Realm.getDefaultInstance();
            ordemServicos = realm.where(OrdemServico.class).findAll();
            os = ordemServicos.where().equalTo("id", getIntent().getLongExtra(OrdemServico.OS, 0)).findFirst();

        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.os_toolbar);
        toolbar.setTitle(R.string.tituloBarraOs);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        FragmentManager fm = getSupportFragmentManager();
        OrdemServicoFragment f = (OrdemServicoFragment) fm.findFragmentById(R.id.fragment_os);
        Comunicador comunicador = (Comunicador) f;
        comunicador.responde(os);

       // EventBus.getDefault().post(os);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            finish();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ordem_servico, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
        }

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null)
            realm.close();
    }
}
