package br.com.jortec.mide.dominio;

/**
 * Created by Jorliano on 12/03/2016.
 */
public class ImagemEnviada {


    private long id;
    private byte[] imagem;

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
}
