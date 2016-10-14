package br.com.jortec.model;

public class Relatorio {

	private String nome;
	private long concluidos;
	private long pendentes;	
	private Double pagamento;	
	private long quantidade;
	
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}		
	public long getConcluidos() {
		return concluidos;
	}
	public void setConcluidos(long concluidos) {
		this.concluidos = concluidos;
	}
	public long getPendentes() {
		return pendentes;
	}
	public void setPendentes(long pendentes) {
		this.pendentes = pendentes;
	}
	public Double getPagamento() {
		return pagamento;
	}
	public void setPagamento(Double pagamento) {
		this.pagamento = pagamento;
	}
	public long getQuantidade() {
		return quantidade;
	}
	public void setQuantidade(long quantidade) {
		this.quantidade = quantidade;
	}
	
	
	
}
