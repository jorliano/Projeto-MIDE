package br.com.jortec.mide.dominio;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.com.jortec.mide.service.SerializableBitmap;

/**
 * Created by Jorliano on 05/03/2016.
 */
public class listarImagens implements Serializable {

    public ArrayList<SerializableBitmap> img;
    public List imagenSelecionadas;

    public listarImagens(ArrayList<SerializableBitmap> img, List imagensSelecionadas) {
        this.img = img;
        this.imagenSelecionadas = imagensSelecionadas;
    }

}
