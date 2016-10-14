package br.com.jortec.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.inject.Named;
import javax.swing.text.StyledEditorKit.BoldAction;

import org.apache.log4j.Logger;
import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import br.com.jortec.model.Cliente;
import br.com.jortec.model.Usuario;
import br.com.jortec.servico.HttpConnection;
import br.com.jortec.util.Alerta;
import br.com.jortec.util.Constante;
import br.com.jortec.util.Formate;

@Controller
@Scope("request")
public class ClienteBean implements Serializable {
	// Log4j
	final Logger logger = Logger.getLogger(ClienteBean.class);

	Cliente cliente = new Cliente();
	private List<Cliente> lista = new ArrayList<Cliente>();
	private String confirmeSenha;
	private String senhaVelha;
	private String resposta = "";
	private String clientePesquisado;
	private int quantidade = 15;
	

	@Autowired
	HttpConnection http;
	
	@Autowired
	Alerta alerta;
	
	@PostConstruct
	public void load(){
		String dados = http.getGetHttp(Constante.URL_USUARIO+"listar/"+quantidade);
		listarDados(dados);
	}

	public void pesquisar() {	
		this.lista.clear();		
		
		if(clientePesquisado.equals("")){
			load();
		}else{
			logger.info("metodo chamado pesquisar "+clientePesquisado);
			String dados = http.getGetHttp(Constante.URL_USUARIO+"listarPesquisa/"+clientePesquisado);
			lista = listarDados(dados);
		}		
	}
	
	public String salvar() {
		
	    boolean hasExiste = false;
	    String label = "salvo"; 	
		
		logger.info("Metodo salvar iniciado " + cliente.getLogin() +" "+confirmeSenha+" = "+cliente.getSenha());
	
		if (cliente.getId() == 0) {				
			
			if(!confirmeSenha.equals(cliente.getSenha())){
				hasExiste = true;
				alerta.warn("Senhas não confere", false);
				
			}
			
			if(!hasExiste){
				cliente.setEstatus("pendente");
				cliente.setDataCadastro(new Date());
				resposta = http.getPostHttp(Constante.URL_USUARIO+"cadastrar",	"cadastrar", new Gson().toJson(cliente));
			}				
                        
		} else if (cliente.getId() > 0) {
			if(validarSenha(cliente.getId())){
			   resposta = http.getPostHttp(Constante.URL_USUARIO+"editar",	"editar", new Gson().toJson(cliente));
			   label = "atualizado";
			}
		}
		
		http.validarRequisição(label);		
		return "cliente?faces-redirect=true";
	}

	public String deletar() {
		
	   if (cliente.getEstatus().equals("pendente")){			
			alerta.warn("Usuario não pode ser desativado", false);			
			
		}else{
		
			logger.info("Metodo deletar iniciado " + cliente.getId());
			resposta = http.getGetHttp(Constante.URL_USUARIO+"deletar/"+cliente.getId());
			
			http.validarRequisição("realizados");
			lista.clear();
		}
	    load();
		return "usuario";
	}
	
	public void carregarMais() {		
		quantidade = 30;		
		load();
	}
	
	public String edita() {
		return "edita";
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public List<Cliente> getLista() {
		if (!lista.isEmpty()) {
			return lista;
		} else{			
			return new ArrayList<Cliente>();
		}			
	}

	public void setLista(List<Cliente> lista) {
		this.lista = lista;
	}

	public List listarDados( String jsonDados) {
		
		logger.info("Dados recebidos " + jsonDados);

		try {
			JSONArray jsonArray = new JSONArray(jsonDados);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = new JSONObject(jsonArray.get(i)
						.toString());
				Cliente c = new Cliente();
				c.setId(jsonObject.getInt("id"));				
				c.setLogin(jsonObject.getString("login"));
				c.setEstatus(jsonObject.getString("estatus"));
				c.setEmail(jsonObject.getString("email"));
				c.setTelefone(jsonObject.getString("telefone"));				
				c.setPrimeiroNome(jsonObject.getString("primeiroNome"));		
				c.setSobreNome(jsonObject.getString("sobreNome"));		
				c.setGenero(jsonObject.getString("genero"));		
				c.setEstadoCivil(jsonObject.getString("estadoCivil"));	
				c.setCargo(jsonObject.getString("cargo"));	
				c.setSobre(jsonObject.getString("sobre"));	
				c.setDataAniversario(new Date());
				c.setTwitter(jsonObject.getString("twitter"));		
				c.setFacebook(jsonObject.getString("facebook"));				
				
				lista.add(c);
				
			}
		} catch (JSONException  e) {
			logger.info("erro ao carregar dados dos clientes " + e.getMessage());			
		}

		return lista;
	}
	
	

	public String getClientePesquisado() {
		return clientePesquisado;
	}

	public void setClientePesquisado(String clientePesquisado) {
		this.clientePesquisado = clientePesquisado;
	}
	

	public String getConfirmeSenha() {
		return confirmeSenha;
	}

	public void setConfirmeSenha(String confirmeSenha) {
		this.confirmeSenha = confirmeSenha;
	}

	public void setSenhaVelha(String senhaVelha) {
		this.senhaVelha = senhaVelha;
	}
	
	public String getSenhaVelha() {
		return senhaVelha;
	}

	public boolean validarSenha(long id){
		
		String dados = http.getGetHttp(Constante.URL_USUARIO+"pesquisarPorId/"+id);
		logger.info("Data da pesquisa "+dados);
		
		try 
		{
			JSONObject jo = new JSONObject(dados);
			cliente.setDataCadastro(Formate.stringParaData(jo.getString("dataCadastro")));
			cliente.setEstatus(jo.getString("estatus"));
		     
			if(senhaVelha.equals("")){						
				cliente.setSenha(jo.getString("senha"));			
			}
			else{			
				 if(senhaVelha.equals(jo.getString("senha"))){
					 if(confirmeSenha.equals(cliente.getSenha())){						
							return true;					
					}else{
						alerta.warn("Senhas não confere", false);	
						return false;
					}
					 
				 }
				 else{
					alerta.warn("senha incorreta", false);
					return false;				
				}
			}
			
		} 
		catch (JSONException e) {
			alerta.error("Falha ao se comunicar com o servidor ", true);			
			e.printStackTrace();
			return false;
		}					   		
		
		
		return true;
	}
}
