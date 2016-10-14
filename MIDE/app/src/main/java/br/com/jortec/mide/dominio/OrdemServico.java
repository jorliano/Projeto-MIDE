package br.com.jortec.mide.dominio;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Jorliano on 03/03/2016.
 */
public class OrdemServico extends RealmObject implements Serializable{

    public static final String OS = "br.com.jortec.mide.dominio.OrdemServico.OS";
    public static final String BUNDLE_GCM = "br.com.jortec.mide.dominio.OrdemServico.BUNDLE_GCM";
    public static final String FECHAMENTO_OS = "br.com.jortec.mide.dominio.OrdemServico.OS";
    public static final int BUNDLE_ONRESULT = 3;

    @PrimaryKey
    private long id;
    private long codigo;
    private long idServico;
    private String nomeCliente;
    private String contato;
    private String endereco;
    private String bairro;
    private String cidade;
    private String telefone;
    private String autenticacao;
    private String dadosAcesso;
    private String descricao;
    private String tipo;
    private String hora;
    private Date data;



    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCodigo() {
        return codigo;
    }

    public void setCodigo(long codigo) {
        this.codigo = codigo;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }


    public String getContato() {
        return contato;
    }

    public void setContato(String contato) {
        this.contato = contato;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getAutenticacao() {
        return autenticacao;
    }

    public void setAutenticacao(String autenticacao) {
        this.autenticacao = autenticacao;
    }

    public String getDadosAcesso() {
        return dadosAcesso;
    }

    public void setDadosAcesso(String dadosAcesso) {
        this.dadosAcesso = dadosAcesso;
    }


    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }


    public long getIdServico() {
        return idServico;
    }

    public void setIdServico(long idServico) {
        this.idServico = idServico;
    }
}
