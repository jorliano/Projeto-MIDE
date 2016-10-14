package br.com.jortec.mide.dominio;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Jorliano on 03/03/2016.
 */
public class Servico extends RealmObject implements Serializable {

    @PrimaryKey
    private long id;

    private long idWeb;
    private long usuario_id;
    private long ordem_servico_id;
    private String nomeCliente;
    private String parentesco;
    private String descricao;
    private String encerramento;
    private String longitude;
    private String latitude;
    private String estatus;
    private Date data;
    private RealmList<Material> materias;
    private RealmList<Imagem> imagems;
    private Date dtaEncerramento;
    private Venda venda;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getEncerramento() {
        return encerramento;
    }

    public void setEncerramento(String encerramento) {
        this.encerramento = encerramento;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }


    public RealmList<Material> getMaterias() {
        return materias;
    }

    public void setMaterias(RealmList<Material> materias) {
        this.materias = materias;
    }

    public RealmList<Imagem> getImagems() {
        return imagems;
    }

    public void setImagems(RealmList<Imagem> imagems) {
        this.imagems = imagems;
    }


    public String getParentesco() {
        return parentesco;
    }

    public void setParentesco(String parentesco) {
        this.parentesco = parentesco;
    }

    public long getUsuario_id() {
        return usuario_id;
    }

    public void setUsuario_id(long usuario_id) {
        this.usuario_id = usuario_id;
    }

    public long getOrdem_servico_id() {
        return ordem_servico_id;
    }

    public void setOrdem_servico_id(long ordem_servico_id) {
        this.ordem_servico_id = ordem_servico_id;
    }

    public long getIdWeb() {
        return idWeb;
    }

    public void setIdWeb(long idWeb) {
        this.idWeb = idWeb;
    }


    public Date getDtaEncerramento() {
        return dtaEncerramento;
    }

    public void setDtaEncerramento(Date dtaEncerramento) {
        this.dtaEncerramento = dtaEncerramento;
    }

    public Venda getVenda() {
        return venda;
    }

    public void setVenda(Venda venda) {
        this.venda = venda;
    }


    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }
}
