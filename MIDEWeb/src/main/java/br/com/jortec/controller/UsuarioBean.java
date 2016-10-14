package br.com.jortec.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import br.com.jortec.model.Cliente;
import br.com.jortec.model.Usuario;
import br.com.jortec.servico.HttpConnection;
import br.com.jortec.util.Alerta;
import br.com.jortec.util.Constante;

import com.google.gson.Gson;

@Controller
@Scope("request")
public class UsuarioBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 663627621331088052L;
	// Log4j
	final Logger logger = Logger.getLogger(UsuarioBean.class);

	Usuario usuario = new Usuario();
	List<Usuario> lista = new ArrayList<Usuario>();
	private String confirmeSenha;
	private String senhaVelha;
	private String usuarioPesquisado;
	private int quantidade = 15;	
	Gson gson = new Gson();

	@Autowired
	HttpConnection http;
	
	@Autowired
	UsuarioLogado usuarioLogado;
	
	@Autowired
	Alerta alerta;
	
	@PostConstruct
	public void load(){
		String jsonDados = http.getGetHttp(Constante.URL_ADMINISTRADOR+"listar/"+quantidade);
		listarDados(jsonDados);
		
		logger.info("metodo load chamado ");
	}
	
	
     public void pesquisar() {    	
    	 
    	this.lista.clear();		
    	logger.info("metodo chamado pesquisar "+usuarioPesquisado);
    	
    	
    	if(usuarioPesquisado.equals("")){
    		load();
    	}else{    		
    		String dados = http.getGetHttp(Constante.URL_ADMINISTRADOR+"listarPesquisa/"+usuarioPesquisado);	
    		lista = listarDados(dados);		
    	}
				
	}
     
     public void carregarMais() {
 		quantidade = quantidade + 15; 		
 		
 		String jsonDados = http.getGetHttp(Constante.URL_ADMINISTRADOR+"listar/"+quantidade);
		listarDados(jsonDados);
 		
 	 }
 	

	public String salvar() {
		 boolean hasExiste = false;
		 String label = "salvo"; 	 
		 
		 logger.info("Administrador salvar "+ usuario.getId()+" "+usuario.getLogin());
		
		if (usuario.getId() == 0) {		            
            
            if(!confirmeSenha.equals(usuario.getSenha())){
				hasExiste = true;
				alerta.warn("Senhas não confere", false);				
			}
			
           if(!hasExiste){
			 http.getPostHttp(Constante.URL_ADMINISTRADOR+"cadastrar", "cadastrar", gson.toJson(usuario));
           }           			
			
            
		} else {
			if(validarSenha(usuario.getId())){
			   http.getPostHttp(Constante.URL_ADMINISTRADOR+"editar", "editar", gson.toJson(usuario));
               label = "atualizado";
			}
		}
		
		http.validarRequisição(label);
		
		return "usuario?faces-redirect=true";
	}

	public String deletar() {
		logger.info("Administrador deletado "+ usuario.getId());
		
		if(usuarioLogado.getUsuario().getId() != usuario.getId()){
        	
			http.getGetHttp(Constante.URL_ADMINISTRADOR+"deletar/"+ usuario.getId());
			http.validarRequisição("deletado");
		    lista.clear();
		    return "usuario?faces-redirect=true";
        }
		else{
		  alerta.warn("Não é possivel deletar usuario logado", false);	
		}       
        
       
		return null;
	}	
	
	public String deslogar() {
		logger.info("Administrador deslogado ");
		
		usuarioLogado.desloga();
       
		return "/index?faces-redirect=true";
	}

	public String edita() {
		logger.info("Pagina de edição de adm chamada "+ usuario.getId());
		return "edita";
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public List<Usuario> getLista() {
		if (!lista.isEmpty()) {
			return lista;
		} else {			
			return new ArrayList<Usuario>();
		}		
		
	}

	public void setLista(List<Usuario> lista) {
		this.lista = lista;
	}

	public String getConfirmeSenha() {
		return confirmeSenha;
	}

	public void setConfirmeSenha(String confirmeSenha) {
		this.confirmeSenha = confirmeSenha;
	}

	public List listarDados(String jsonDados) {		
		
		logger.info("JsonRecebido " + jsonDados);

		try {
			JSONArray jsonArray = new JSONArray(jsonDados);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = new JSONObject(jsonArray.get(i)
						.toString());
				Usuario u = new Usuario();
				u.setId(jsonObject.getInt("id"));				
				u.setLogin(jsonObject.getString("login"));
				u.setSenha(jsonObject.getString("senha"));
				u.setEmail(jsonObject.getString("email"));
				u.setTelefone(jsonObject.getString("telefone"));				
				u.setPrimeiroNome(jsonObject.getString("primeiroNome"));		
				u.setSobreNome(jsonObject.getString("sobreNome"));		
				u.setGenero(jsonObject.getString("genero"));		
				u.setEstadoCivil(jsonObject.getString("estadoCivil"));					
				u.setSobre(jsonObject.getString("sobre"));	
				u.setDataAniversario(new Date());
				u.setTwitter(jsonObject.getString("twitter"));		
				u.setFacebook(jsonObject.getString("facebook"));	
				this.lista.add(u);
                
				
			}
		} catch (JSONException e) {
			logger.info("Erro ao carregar lista " + e.getMessage());
			return new ArrayList<Usuario>();
		}
		
		logger.info("lista size " + lista.size());
		
		
		return this.lista;
	}
    
	

	public String getUsuarioPesquisado() {
		return usuarioPesquisado;
	}

	public void setUsuarioPesquisado(String usuarioPesquisado) {
		this.usuarioPesquisado = usuarioPesquisado;
	}

	public void setSenhaVelha(String senhaVelha) {
		this.senhaVelha = senhaVelha;
	}
	public String getSenhaVelha() {
		return senhaVelha;
	}
	
	
	
	public boolean validarSenha(long id){
		
		String dados = http.getGetHttp(Constante.URL_ADMINISTRADOR+"pesquisarPorId/"+id);
		try 
		{
			JSONObject jo = new JSONObject(dados);
			logger.info("Senha atual "+jo.getString("senha")+" Senha antiga "+senhaVelha);
		     
			if(senhaVelha.equals("")){						
				usuario.setSenha(jo.getString("senha"));			
			}
			else{			
				 if(senhaVelha.equals(jo.getString("senha"))){
					 if(confirmeSenha.equals(usuario.getSenha())){						
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
