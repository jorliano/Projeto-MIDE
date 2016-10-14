package br.com.jortec.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="servico")
@XmlRootElement
public class Servico {

	public Servico(){}
	
	public  Servico(long id, long usuario_id, long ordem_servico_id, String hora, String encerramento) {
        this.id = id;
        this.usuario_id = usuario_id;
        this.ordem_servico_id = ordem_servico_id;
        this.hora = hora;
        this.encerramento = encerramento;
    }
	
	
	@Id	
	private long id;	
    
    private String nomeCliente;
    private String parentesco;
    private String descricao;
    private String encerramento;
    private String longitude;
    private String latitude;
    private Date data;
    private Date dataEncerramento;
    private String hora;
    private String estatus;
    
  
    private long usuario_id;
    private long ordem_servico_id;        
    
    @ManyToOne(fetch=FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "venda_id")   
    @JsonIgnore
    private Venda venda;  
	
    @OneToMany(fetch=FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Material> materiais;
    
    @OneToMany(fetch=FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Imagem> imagens;
   
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

	public String getParentesco() {
		return parentesco;
	}

	public void setParentesco(String parentesco) {
		this.parentesco = parentesco;
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

	public List<Material> getMateriais() {
		return materiais;
	}

	public void setMateriais(List<Material> materiais) {
		this.materiais = materiais;
	}

	public List<Imagem> getImagens() {
		return imagens;
	}

	public void setImagens(List<Imagem> imagens) {
		this.imagens = imagens;
	}

	public String getHora() {
		return hora;
	}

	public void setHora(String hora) {
		this.hora = hora;
	}

	public Date getDataEncerramento() {
		return dataEncerramento;
	}

	public void setDataEncerramento(Date dataEncerramento) {
		this.dataEncerramento = dataEncerramento;
	}

	public String getEstatus() {
		return estatus;
	}

	public void setEstatus(String estatus) {
		this.estatus = estatus;
	}

	public Venda getVenda() {
		return venda;
	}

	public void setVenda(Venda venda) {
		this.venda = venda;
	}	
	
	
}
