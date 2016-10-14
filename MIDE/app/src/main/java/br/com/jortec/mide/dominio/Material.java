package br.com.jortec.mide.dominio;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

import io.realm.RealmObject;

/**
 * Created by Jorliano on 04/03/2016.
 */
public class Material extends RealmObject  {

    public static final String BUNDLE_KEY = "br.com.jortec.mide.dominio.Material";
    public static final int BUNDLE_ONRESULT = 3;
    public static final String BUNDLE_DADOS = "br.com.jortec.mide.dominio.Material.BUNDLE_DADOS";

    private long id;
    private String descricao;
    private int quantidade;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }



}
