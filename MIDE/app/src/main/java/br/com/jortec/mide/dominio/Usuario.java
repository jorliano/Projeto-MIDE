package br.com.jortec.mide.dominio;


import java.io.Serializable;

/**
 * Created by Jorliano on 03/03/2016.
 */
public class Usuario implements Serializable{

    public static final String ID = "br.com.jortec.mide.dominio.Usuario.ID";
    public static final String LOGIN = "br.com.jortec.mide.dominio.Usuario.LOGIN";
    public static final String NOME = "br.com.jortec.mide.dominio.Usuario.NOME";
    public static final String ID_KEY ="871503385510";

    private long id;
    private String nome;
    private String estatus;
    private String login;
    private String senha;
    private String registroGcm;
    private String dataCadastro;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }


    public String getRegistroGcm() {
        return registroGcm;
    }

    public void setRegistroGcm(String registroGcm) {
        this.registroGcm = registroGcm;
    }


    public String getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(String dataCadastro) {
        this.dataCadastro = dataCadastro;
    }
}
