package br.com.jortec.mide;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;

import br.com.jortec.mide.Util.Util;
import br.com.jortec.mide.dominio.Imagem;
import br.com.jortec.mide.dominio.Material;
import br.com.jortec.mide.dominio.OrdemServico;
import br.com.jortec.mide.dominio.Servico;
import br.com.jortec.mide.dominio.Usuario;
import br.com.jortec.mide.dominio.Venda;
import br.com.jortec.mide.fragments.MaterialFragment;
import br.com.jortec.mide.fragments.ImagemFragment;
import br.com.jortec.mide.interfaces.Comunicador;
import br.com.jortec.mide.internet.EnvioOsService;
import br.com.jortec.mide.service.LocalizacaoGps;
import br.com.jortec.mide.service.SerializableBitmap;
import de.greenrobot.event.EventBus;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class FecharOsActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSIONS_CODE = 10;
    private Spinner spTipoEncerramento, spParentesco, spTipoPagamento, spQuantidade;
    private EditText edtResponsavel, edtDescricao, edtValorPagamento;
    private RadioGroup rgRoteador;
    private Toolbar toolbar, bottomToolbar;
    private TextInputLayout tilResponsavel, tilValor;

    private Servico servico;
    private long idOs;
    private long idServico;
    private String json;
    private int quantidaRotiador;

    private ArrayList<Material> materiais;
    private ArrayList<Uri> imagens;
    private ArrayList<Imagem> listaImagem;

    Comunicador comunicador;
    RelativeLayout rlLadnScap;
    Fragment fragment;
    private FragmentManager fm;


    Realm realm;
    RealmResults<Servico> servicos;
    LocalizacaoGps gps;

    @Override
    protected void onResume() {
        super.onResume();
        if (!Util.verifyGps(this)) {
            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
        gps = new LocalizacaoGps(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fechar_os);

        realm = Realm.getDefaultInstance();
        servicos = realm.where(Servico.class).findAll();


        if (getIntent() != null) {
            idOs = getIntent().getLongExtra(OrdemServico.FECHAMENTO_OS, 0);
        }

        iniciarViews();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.rlf_material_imagem, fragment, "TAG");
        ft.commit();

        servico = servicos.where().equalTo("ordem_servico_id", idOs).findFirst();
        preencerCampos();

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }


        rgRoteador.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rbSim) {
                    spQuantidade.setVisibility(View.VISIBLE);
                    quantidaRotiador = Integer.parseInt(spQuantidade.getSelectedItem().toString());
                } else {
                    quantidaRotiador = 0;
                    spQuantidade.setVisibility(View.GONE);
                }
            }
        });
        bottomToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.action_camera:

                        fragment = new ImagemFragment();
                        comunicador = (Comunicador) fragment;

                        if (rlLadnScap.isShown()) {
                            FragmentTransaction ft = fm.beginTransaction();
                            ft.replace(R.id.rlf_material_imagem, fragment, "TAG");
                            ft.commit();

                            if (!imagens.isEmpty()) {
                                for (Uri uri : imagens)
                                    comunicador.responde(uri);
                            }
                        } else {
                            if (!imagens.isEmpty()) {
                                startActivity(new Intent(FecharOsActivity.this, ImagenActivity.class)
                                        .putExtra(Imagem.BUNDLE_DADOS, imagens));
                            } else {
                                startActivity(new Intent(FecharOsActivity.this, ImagenActivity.class));
                            }
                        }
                        break;
                    case R.id.action_material:

                        fragment = new MaterialFragment();
                        comunicador = (Comunicador) fragment;

                        if (rlLadnScap.isShown()) {
                            FragmentTransaction ft = fm.beginTransaction();
                            ft.replace(R.id.rlf_material_imagem, fragment, "TAG");
                            ft.commit();

                            if (!materiais.isEmpty()) {
                                for (Material material : materiais)
                                    comunicador.responde(material);
                            }
                        } else {
                            if (!materiais.isEmpty()) {
                                startActivity(new Intent(FecharOsActivity.this, MaterialActivity.class)
                                        .putExtra(Material.BUNDLE_DADOS, servico.getId()));
                            } else {
                                startActivity(new Intent(FecharOsActivity.this, MaterialActivity.class));
                            }
                        }

                        break;
                }
                return true;
            }
        });
        bottomToolbar.inflateMenu(R.menu.menu_bottom);
        bottomToolbar.findViewById(R.id.im_enviar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isValido()) {

                    salvarEstadoNoBanco("fechada");

                    if (Util.verifyConnection(v.getContext())) {
                        startService(new Intent(FecharOsActivity.this, EnvioOsService.class));
                    }

                    imprimirMensagem("Os feichada");
                    setResult(Activity.RESULT_OK, new Intent());
                    finish();
                }
            }
        });
    }

    public void iniciarViews() {

        toolbar = (Toolbar) findViewById(R.id.os_toolbar);
        spTipoEncerramento = (Spinner) findViewById(R.id.sp_encerramento);
        spParentesco = (Spinner) findViewById(R.id.sp_parentesco);
        spQuantidade = (Spinner) findViewById(R.id.sp_quantidade_router);
        spTipoPagamento = (Spinner) findViewById(R.id.sp_tipo_pagamento);
        edtResponsavel = (EditText) findViewById(R.id.edt_responsavel);
        edtDescricao = (EditText) findViewById(R.id.edt_descricao);
        edtValorPagamento = (EditText) findViewById(R.id.edt_valor);
        bottomToolbar = (Toolbar) findViewById(R.id.os_toolbar_bottom);
        rgRoteador = (RadioGroup) findViewById(R.id.rg_op_router);

        spTipoEncerramento.setAdapter(new ArrayAdapter<String>(this, R.layout.item_sp, getResources().getStringArray(R.array.itens_tipo_os)));
        spTipoEncerramento.setSelection(0);
        spParentesco.setAdapter(new ArrayAdapter<String>(this, R.layout.item_sp, getResources().getStringArray(R.array.itens_parentesco)));
        spParentesco.setSelection(0);
        spTipoPagamento.setAdapter(new ArrayAdapter<String>(this, R.layout.item_sp, getResources().getStringArray(R.array.itens_pagamento)));
        spTipoPagamento.setSelection(0);
        String quantidade[] = {"1", "2", "3", "4", "5"};
        spQuantidade.setAdapter(new ArrayAdapter<String>(this, R.layout.item_sp, quantidade));
        spQuantidade.setSelection(0);

        tilResponsavel = (TextInputLayout) findViewById(R.id.til_responsavel);
        tilValor = (TextInputLayout) findViewById(R.id.til_valor);

        toolbar.setTitle(R.string.tituloBarraOsFechada);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        if (materiais == null) {
            materiais = new ArrayList<>();
            imagens = new ArrayList<>();
        }
        listaImagem = new ArrayList<>();


        rlLadnScap = (RelativeLayout) findViewById(R.id.rlf_material_imagem);
        fragment = new MaterialFragment();
        fm = getSupportFragmentManager();
        comunicador = (Comunicador) fragment;


        final boolean[] isUpdating = {false};
        final NumberFormat nf = NumberFormat.getCurrencyInstance();
        edtValorPagamento.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Evita que o método seja executado varias vezes.
                // Se tirar ele entre em loop
                if (isUpdating[0]) {
                    isUpdating[0] = false;
                    return;
                }
                isUpdating[0] = true;

                String str = s.toString();
                // Verifica se já existe a máscara no texto.
                boolean hasMask = ((str.indexOf("R$") > -1 || str.indexOf("$") > -1) &&
                        (str.indexOf(".") > -1 || str.indexOf(",") > -1));
                // Verificamos se existe máscara
                if (hasMask) {
                    // Retiramos a máscara.
                    str = str.replaceAll("[R$]", "").replaceAll("[,]", "")
                            .replaceAll("[.]", "");
                }
                try {
                    // Transformamos o número que está escrito no EditText em  monetário.

                    str = nf.format(Double.parseDouble(str) / 100);
                    edtValorPagamento.setText(str);
                    edtValorPagamento.setSelection(edtValorPagamento.getText().length());
                } catch (NumberFormatException e) {
                    s = "";
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public boolean isValido() {
        if (edtResponsavel.getText().toString().equals("")) {
            tilResponsavel.setError("Responsavel é necessário");
            edtResponsavel.requestFocus();
            return false;
        } else {
            tilResponsavel.setErrorEnabled(false);
        }

        if (spTipoPagamento.getSelectedItemPosition() > 0 || quantidaRotiador > 0) {
            if (edtValorPagamento.getText().toString().equals("")) {
                tilValor.setError("Valor é necessário");
                tilValor.requestFocus();
                return false;
            } else {
                tilValor.setErrorEnabled(false);
            }
        }

        //preencherDados();

        return true;
    }

    public void salvarEstadoNoBanco(String estatus) {
      /*  Servico servicoSelecionado = null;

        for (Servico s : servicos) {
            if (s.getOrdem_servico_id() == idOs) {
                servicoSelecionado = s;
            }
        }*/

        preencherDados(servico );


        realm.beginTransaction();
        servico.setEstatus(estatus);
        realm.copyToRealmOrUpdate(servico);
        realm.commitTransaction();

        servicos = realm.where(Servico.class).findAll();
        servico = servicos.where().equalTo("id", servico.getId()).findFirst();
        Log.i("LOG", "id dos ervico pesquisado apos salvar no banco" + servico.getId());


        realm.beginTransaction();
        servico.getMaterias().clear();
        servico.getMaterias().addAll(materiais);
        realm.commitTransaction();

        realm.beginTransaction();
        servico.getImagems().clear();
        servico.getImagems().addAll(listaImagem);
        realm.commitTransaction();


    }

    public void preencherDados(Servico s) {

        Log.i("LOG", "cordenadas " + gps.getLatitude() + " , " + gps.getLatitude());
        Venda v = null;

        if(s == null) {
            servico = new Servico();
            servicos.sort("id", Sort.DESCENDING);
            long id = servicos.size() == 0 ? 1 : servicos.get(0).getId() + 1;
            Log.i("LOG", "id do servico a ser cadastrado " + getIntent().getLongExtra("id_servico", 0));
            servico.setId(id);
            v = new Venda();
        }else{

            servico = s;
            v = servico.getVenda();
        }


        realm.beginTransaction();
        servico.setIdWeb(getIntent().getLongExtra("id_servico", 0));
        servico.setOrdem_servico_id(idOs);
        servico.setNomeCliente(edtResponsavel.getText().toString());
        servico.setEncerramento(spTipoEncerramento.getSelectedItem().toString());
        servico.setParentesco(spParentesco.getSelectedItem().toString());
        servico.setDescricao(edtDescricao.getText().toString());
        servico.setLatitude(gps.getLatitude());
        servico.setLongitude(gps.getLatitude());
        servico.setData(new Date());
        servico.setEstatus("andamento");

        v.setEquipamento("Rotiador");
        v.setTipo(spTipoPagamento.getSelectedItem().toString());
        if (!edtValorPagamento.getText().toString().equals("")) {
            // Retirar a máscara.
            String str = edtValorPagamento.getText().toString();
            str = str.replaceAll("[R$]", "").replaceAll("[,]", "");
            v.setPagamento(Double.parseDouble(str));
            if (Double.parseDouble(str) < 0.1)
                v.setTipo("indefinido");
        } else {
            v.setPagamento(0.0);
            v.setTipo("indefinido");
        }
        if (quantidaRotiador > 0) {
            v.setQuantidade(Integer.parseInt(spQuantidade.getSelectedItem().toString()));
        } else {
            v.setQuantidade(0);
        }

        servico.setVenda(v);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        servico.setUsuario_id(preferences.getLong(Usuario.ID, 0));
        realm.commitTransaction();

        if(!imagens.isEmpty()) {

            listaImagem.clear();
            for (int i = 0; i < imagens.size(); i++) {
                 Imagem im = new Imagem();
               //  im.setImagem(converteFileToByteArray(imagens.get(i)));
                 Log.i("LOG", "Uri para string "+ imagens.get(i).toString());

                im.setCaminho(imagens.get(i).toString());
                listaImagem.add(im);

            }
        }
    }


    public void preencerCampos(){

        if (servico != null) {

            String[] arrayEncerramento = getResources().getStringArray(R.array.itens_tipo_os),
                     arrayParentesco = getResources().getStringArray(R.array.itens_parentesco),
                     arrayPagamento = getResources().getStringArray(R.array.itens_pagamento);

            for (int i = 0;i < arrayEncerramento.length; i++){
                if(arrayEncerramento[i].equals(servico.getEncerramento())){
                    spTipoEncerramento.setSelection(i);
                    break;
                }
            }

            for (int i = 0;i < arrayParentesco.length; i++){
                if(arrayParentesco[i].equals(servico.getParentesco())){
                    spTipoPagamento.setSelection(i);
                    break;
                }
            }

            spTipoPagamento.setSelection(0);
            for (int i = 0;i < arrayPagamento.length; i++){
                if(arrayPagamento[i].equals(servico.getVenda().getPagamento())){
                    spTipoPagamento.setSelection(i);
                    break;
                }
            }

            edtResponsavel.setText(servico.getNomeCliente());
            edtDescricao.setText(servico.getDescricao());
            edtValorPagamento.setText(String.valueOf(servico.getVenda().getPagamento()));

            if(servico.getVenda().getQuantidade() > 0){
               // rgRoteador
               spQuantidade.setSelection(servico.getVenda().getQuantidade() - 1);
               spQuantidade.setVisibility(View.VISIBLE);
            }


            for (Material m : servico.getMaterias()) {
                materiais.add(m);
               // if (rlLadnScap.isShown()) {
                    comunicador.responde(m);
             //   }
            }
            for (Imagem i : servico.getImagems())
                imagens.add(Uri.parse(i.getCaminho()));


        }


    }
    //LISTENER
    public void onEvent(ArrayList<Uri> lista) {
        if (!lista.isEmpty()) {
            imagens.clear();
            imagens = lista;
            Log.i("LOG", "Lista de imagens  qtd " + imagens.size());
        }
    }

    public void onEvent(Material material) {
        if (material != null) {
            if (!materiais.contains(material)) {
                materiais.add(material);
                Log.i("LOG", "Lista de mateiais  qtd " + materiais.size());
            } else {
                materiais.remove(material);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fechar_os, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == android.R.id.home) {
            finish();
        }

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        salvarEstadoNoBanco("andamento");
        Log.i("LOG", "Activity fechar destruida ");
        realm.close();
    }

    private void imprimirMensagem(String mensagem) {
        Toast.makeText(getApplicationContext(), mensagem, Toast.LENGTH_LONG).show();
    }

    public byte[] converteFileToByteArray(Uri selectedFileUri) {

        try {
            File file = new File(selectedFileUri.getPath());
            Bitmap b = SerializableBitmap.decodeFile(file);
            file.delete();
            return SerializableBitmap.converterBytes(b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
