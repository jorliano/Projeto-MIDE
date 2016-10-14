package br.com.jortec.mide.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.com.jortec.mide.FecharOsActivity;
import br.com.jortec.mide.R;
import br.com.jortec.mide.Util.Formate;
import br.com.jortec.mide.Util.Util;
import br.com.jortec.mide.adapter.ImagemAdapter;
import br.com.jortec.mide.dominio.Imagem;
import br.com.jortec.mide.dominio.OrdemServico;
import br.com.jortec.mide.interfaces.Comunicador;
import br.com.jortec.mide.interfaces.RecyclerViewOnclickListener;
import br.com.jortec.mide.service.SerializableBitmap;
import de.greenrobot.event.EventBus;

/**
 * Created by Jorliano on 17/04/2016.
 */
public class ImagemFragment extends Fragment implements RecyclerViewOnclickListener, Comunicador {
    private ArrayList<SerializableBitmap> listaBitmap;
    List<SerializableBitmap> serializableBitmaps;
    private ArrayList<Uri> listaUris = new ArrayList<>();
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private android.support.design.widget.FloatingActionButton fab;
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

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_imagem, null);

        iniciarViews();

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_deletar:
                        for (int i = imagensSelecionadas.size(); i > 0; i--) {
                            Log.i("LOG", "imagemSelecionada " + imagensSelecionadas.get(i - 1));

                            adapter.removeItem((Integer) imagensSelecionadas.get(i - 1));
                        }
                        adapter.notifyDataSetChanged();
                        toolbar.getMenu().clear();
                        getActivity().getMenuInflater().inflate(R.menu.menu_imagen, toolbar.getMenu());
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
                            Toast.makeText(getContext(),"Limite máximo de imagens excedido",Toast.LENGTH_SHORT).show();
                        }
                        break;

                }

                return true;
            }
        });

        return view;
    }

    public void iniciarViews() {

        toolbar = (Toolbar) view.findViewById(R.id.imagem_toolbar);
        recyclerView = (RecyclerView) view.findViewById(R.id.imagem_recycler);
        relativeLayout = (RelativeLayout) view.findViewById(R.id.rl_imagem);
        fab = (android.support.design.widget.FloatingActionButton) view.findViewById(R.id.fab_camera);
        listaBitmap = new ArrayList<>();
        imagensSelecionadas = new ArrayList();
        listaBitmap = new ArrayList<>();
        serializableBitmaps = new ArrayList<>();

        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(view.getContext(), grids, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ImagemAdapter(view.getContext(), listaUris);
        adapter.setRecyclerViewOnclickListener(this);
        recyclerView.setAdapter(adapter);

        toolbar.inflateMenu(R.menu.menu_imagen);
        toolbar.setTitle("Imagens");
        toolbar.getMenu().getItem(1).setVisible(false);

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri uri;


        if (requestCode == FOTO_TIRADA) {
            if (resultCode == Activity.RESULT_OK) {

                String nomeImagem = String.valueOf(System.currentTimeMillis());

                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                if (Util.isExternalStorageWritable()){
                    uri =  salvarParaCartao(nomeImagem, bitmap);
                }else {
                    uri =  salvarParaMemoriaInterna(nomeImagem, bitmap);
                }

                if (uri != null){
                    listaUris.add(uri);
                    verificarLista();
                    adapter.notifyDataSetChanged();

                    EventBus.getDefault().post(uri);
                }
            }
        } else if (requestCode == IMAGEM_SDCARD) {
            if (resultCode == Activity.RESULT_OK) {

                try {
                    ClipData clipdata = data.getClipData();
                    Log.i("LOG", "imagens selecionadas " + data);
                    if(clipdata != null ) {
                        for (int i = 0; i < clipdata.getItemCount(); i++) {
                            String nomeImagem = String.valueOf(System.currentTimeMillis());

                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(view.getContext().getContentResolver(), clipdata.getItemAt(i).getUri());
                            if (Util.isExternalStorageWritable()){
                                uri =  salvarParaCartao(nomeImagem, bitmap);
                            }else {
                                uri =  salvarParaMemoriaInterna(nomeImagem, bitmap);
                            }
                            if (uri != null && i < LIMITE_IMAGENS){
                                listaUris.add(uri);
                                EventBus.getDefault().post(uri);
                            }
                        }
                    }else{
                        String nomeImagem = String.valueOf(System.currentTimeMillis());
                        Uri uriImagem = data.getData();
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(view.getContext().getContentResolver(), uriImagem);
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
    public void responde(Object obj) {
          Uri uri = (Uri) obj;
          if(listaUris != null)
            listaUris.add(uri);
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
            toolbar.inflateMenu(R.menu.menu_imagem_opcoes);
            menuInflado = true;

        } else if (imagensSelecionadas.size() == 0) {
            toolbar.getMenu().clear();
            toolbar.inflateMenu(R.menu.menu_imagen);
            toolbar.getMenu().getItem(1).setVisible(false);
            menuInflado = false;
        }
    }

    public void verificarLista() {
        if (listaUris.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            TextView tv = new TextView(view.getContext());
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
        File path = view.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

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
            FileOutputStream outStream = view.getContext().openFileOutput(fileName, Context.MODE_PRIVATE);
            pBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.close();
            Uri uri = Uri.parse(view.getContext().getFileStreamPath(fileName).toURI().toString());
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


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().post(listaUris);
    }
}
