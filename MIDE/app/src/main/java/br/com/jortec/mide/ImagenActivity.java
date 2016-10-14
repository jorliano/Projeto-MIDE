package br.com.jortec.mide;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.ProcessingInstruction;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.com.jortec.mide.Util.Util;
import br.com.jortec.mide.adapter.ImagemAdapter;
import br.com.jortec.mide.dominio.Imagem;
import br.com.jortec.mide.dominio.Material;
import br.com.jortec.mide.dominio.listarImagens;
import br.com.jortec.mide.interfaces.RecyclerViewOnclickListener;
import br.com.jortec.mide.service.SerializableBitmap;
import de.greenrobot.event.EventBus;

public class ImagenActivity extends AppCompatActivity implements RecyclerViewOnclickListener {

    private ArrayList<SerializableBitmap> listaBitmap;
    List<SerializableBitmap> serializableBitmaps;
    private ArrayList<Uri> listaUris;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private ImagemAdapter adapter;
    private RelativeLayout relativeLayout;
    private int grids = 2;
    private int tamanhoArray = 0;
    private List imagensSelecionadas;
    private boolean menuInflado = false;
    private int tamanhoImagens;

    public static final int FOTO_TIRADA = 2;
    public static final int IMAGEM_SDCARD = 3;
    public static final int LIMITE_IMAGENS = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagen);

        iniciarViews();

        if (savedInstanceState != null) {

            listaUris = (ArrayList<Uri>) savedInstanceState.getSerializable("imagens");

            if (imagensSelecionadas.size() > 0) {
                getMenuInflater().inflate(R.menu.menu_imagem_opcoes, toolbar.getMenu());
            }
        }
        //PEGAE IIMAGENS JÁ TIRADAS
        if (getIntent() != null && getIntent().getSerializableExtra(Imagem.BUNDLE_DADOS) != null) {
            listaUris = (ArrayList<Uri>) getIntent().getSerializableExtra(Imagem.BUNDLE_DADOS);
        }

        //PEGAR A ORIENTAÇÃO DA TELA
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            grids = 3;
        } else {
            grids = 2;
        }


        toolbar.setTitle(R.string.tituloBarraOsIMagem);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(this, grids, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ImagemAdapter(this, listaUris);
        adapter.setRecyclerViewOnclickListener(this);
        recyclerView.setAdapter(adapter);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listaUris.size() < LIMITE_IMAGENS) {
                    //Chamar a função da camera
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    startActivityForResult(intent, FOTO_TIRADA);
                }else {
                    Toast.makeText(v.getContext(),"Limite máximo de imagens excedido",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //CASO A LISTA ESTEJA VAZIA
        verificarLista();

    }

    public void iniciarViews() {

        toolbar = (Toolbar) findViewById(R.id.imagem_toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.imagem_recycler);
        relativeLayout = (RelativeLayout) findViewById(R.id.rl_imagem);
        fab = (FloatingActionButton) findViewById(R.id.fab_camera);
        listaBitmap = new ArrayList<>();
        imagensSelecionadas = new ArrayList();
        listaUris = new ArrayList<>();
        listaBitmap = new ArrayList<>();
        serializableBitmaps = new ArrayList<>();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri uri;


        if (requestCode == FOTO_TIRADA) {
            if (resultCode == RESULT_OK) {

                String nomeImagem = String.valueOf(System.currentTimeMillis());

                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                if (Util.isExternalStorageWritable()){
                    uri =  salvarParaCartao(nomeImagem, bitmap);
                }else {
                    uri =  salvarParaMemoriaInterna(nomeImagem, bitmap);
                }
                //tamanhoImagens = SerializableBitmap.byteSizeOf(bitmap) + tamanhoImagens;

                if (uri != null){
                    listaUris.add(uri);
                    verificarLista();
                    adapter.notifyDataSetChanged();
                }
            }
        } else if (requestCode == IMAGEM_SDCARD) {
            if (resultCode == RESULT_OK) {

                try {
                    ClipData clipdata = data.getClipData();
                    Log.i("LOG", "imagens selecionadas " + data);
                    if(clipdata != null ) {
                        for (int i = 0; i < clipdata.getItemCount(); i++) {
                            String nomeImagem = String.valueOf(System.currentTimeMillis());

                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), clipdata.getItemAt(i).getUri());
                            if (Util.isExternalStorageWritable()){
                              uri =  salvarParaCartao(nomeImagem, bitmap);
                            }else {
                              uri =  salvarParaMemoriaInterna(nomeImagem, bitmap);
                            }
                            if (uri != null && i < LIMITE_IMAGENS){
                                listaUris.add(uri);
                            }
                        }
                    }else{
                        String nomeImagem = String.valueOf(System.currentTimeMillis());
                        Uri uriImagem = data.getData();
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uriImagem);
                        if (Util.isExternalStorageWritable()){
                            uri =  salvarParaCartao(nomeImagem, bitmap);
                        }else {
                            uri =  salvarParaMemoriaInterna(nomeImagem, bitmap);
                        }
                        if (uri != null){
                            listaUris.add(uri);
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                verificarLista();
                adapter.notifyDataSetChanged();

            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_imagen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                if (!listaUris.isEmpty()) {
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.titulo_dialogo)
                            .setMessage(R.string.contexto_dialogo)
                            .setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    salvarEstado(listaUris);
                                }
                            })
                            .setNegativeButton(R.string.nao, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    salvarEstado(removerImagem(listaUris));
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                } else {
                    finish();
                }
                break;
            case R.id.menu_deletar:
                for (int i = imagensSelecionadas.size(); i > 0; i--) {
                    Log.i("LOG", "imagemSelecionada " + imagensSelecionadas.get(i - 1));

                    adapter.removeItem((Integer) imagensSelecionadas.get(i - 1));
                }
                adapter.notifyDataSetChanged();
                toolbar.getMenu().clear();
                getMenuInflater().inflate(R.menu.menu_imagen, toolbar.getMenu());
                imagensSelecionadas.clear();
                verificarLista();
                break;
            case R.id.menu_importar:
                if (listaUris.size() < LIMITE_IMAGENS) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setType("image/*");
                    startActivityForResult(intent, IMAGEM_SDCARD);
                }else {
                    Toast.makeText(this,"Limite máximo de imagens excedido",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.action_savalr_imagem:
                salvarEstado(listaUris);
                break;
        }

        return true;
    }

    public void salvarEstado(List uris) {
        EventBus.getDefault().post(uris);
        finish();
    }

    @Override
    public void onclickListener(View view, int position) {

        ImageView rv = (ImageView) view;

        if (imagensSelecionadas.contains(position)) {
            //rv.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            rv.setImageResource(0);

            for (int i = 0; i < imagensSelecionadas.size(); i++) {
                if (imagensSelecionadas.get(i).equals(position)) {
                    imagensSelecionadas.remove(i);
                }
            }

        } else {
            rv.setImageResource(R.drawable.check);
            imagensSelecionadas.add(position);
        }

        if (imagensSelecionadas.size() == 1 && !menuInflado) {
            toolbar.getMenu().clear();
            getMenuInflater().inflate(R.menu.menu_imagem_opcoes, toolbar.getMenu());
            menuInflado = true;

        } else if (imagensSelecionadas.size() == 0) {
            toolbar.getMenu().clear();
            getMenuInflater().inflate(R.menu.menu_imagen, toolbar.getMenu());
            menuInflado = false;
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("imagens", listaUris);

    }

    public void verificarLista() {
        if (listaUris.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            TextView tv = new TextView(this);
            tv.setText("Nenhuma foto foi tirada ainda");
            tv.setId(1);
            tv.setTextColor(getResources().getColor(R.color.primary_text));
            tv.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            tv.setGravity(Gravity.CENTER);
            relativeLayout.addView(tv);
        } else if (relativeLayout.findViewById(1) != null) {
            recyclerView.setVisibility(View.VISIBLE);
            relativeLayout.removeView(relativeLayout.findViewById(1));
        }
    }

    public Uri salvarParaCartao(String fileName, Bitmap pBitmap) {
        File path = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        try {
            File file = new File(path, fileName);
            FileOutputStream outStream = new FileOutputStream(file);
            pBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.close();
            Uri uri = Uri.fromFile(file);
            return uri;

        } catch (IOException e) {
            Log.w("ExternalStorage", "Error writing", e);
        }
        return null;
    }

    public Uri salvarParaMemoriaInterna(String fileName, Bitmap pBitmap) {
        try {
            FileOutputStream outStream = openFileOutput(fileName, Context.MODE_PRIVATE);
            pBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.close();
            Uri uri = Uri.parse(this.getFileStreamPath(fileName).toURI().toString());
            return uri;

        } catch (IOException e) {
            Log.w("Storage", "Error writing", e);
        }
        return null;
    }

    public List<Uri> removerImagem(List<Uri> uris){
        for (int i = 0; i < uris.size(); i++) {
            File file = new File(uris.get(i).getPath());
            if (file.isFile())
                file.delete();
        }
        uris.clear();
        return uris;
    }
}
