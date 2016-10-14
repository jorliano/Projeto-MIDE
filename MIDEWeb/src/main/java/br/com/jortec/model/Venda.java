package br.com.jortec.model;

import java.io.Serializable;

public class Venda implements Serializable{		

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long id;	
	
	private Double pagamento;
	private String tipo;
	private String equipamento;	
	private int quantidade;
	
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
	
	
	
	
}
