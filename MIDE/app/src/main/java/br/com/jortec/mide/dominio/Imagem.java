package br.com.jortec.mide.dominio;

import android.graphics.Bitmap;
import android.net.Uri;

import java.io.Serializable;
import java.util.ArrayList;

import io.realm.RealmObject;

/**
 * Created by Jorliano on 04/03/2016.
 */
public class Imagem extends RealmObject implements Serializable {

    public static final int BUNDLE_KEY = 2;
    public static final String BUNDLE_KEY_ONRESULT = "br.com.jortec.mide.dominio.Imagem";
    public static final String BUNDLE_DADOS = "br.com.jortec.mide.dominio.Imagem.BUNDLE_DADOS";

    private long id;
    private byte[] imagem;
    private String caminho;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public byte[] getImagem() {
        return imagem;
    }

    public void setImagem(byte[] imagem) {
        this.imagem = imagem;
    }


    public String getCaminho() {
        return caminho;
    }

    public void setCaminho(String caminho) {
        this.caminho = caminho;
    }
}
