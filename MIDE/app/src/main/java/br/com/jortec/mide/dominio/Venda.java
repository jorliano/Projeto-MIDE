package br.com.jortec.mide.dominio;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by Jorliano on 30/03/2016.
 */
public class Venda extends RealmObject {

    private long id;

    private Double pagamento;
    private String tipo;
    private String equipamento;
    private int quantidade;
    private Date data;

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public Double getPagamento() {
        return pagamento;
    }
    public void setPagamento(Double pagamento) {
        this.pagamento = pagamento;
    }
    public String getTipo() {
        return tipo;
    }
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    public String getEquipamento() {
        return equipamento;
    }
    public void setEquipamento(String equipamento) {
        this.equipamento = equipamento;
    }
    public int getQuantidade() {
        return quantidade;
    }
    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }
}
