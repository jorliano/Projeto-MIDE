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

import com.google.gson.Gson;

import br.com.jortec.model.OrdemServico;
import br.com.jortec.servico.HttpConnection;
import br.com.jortec.util.Alerta;
import br.com.jortec.util.Constante;

@Controller
//@Scope("view")
@Scope("request")
public class OrdemServicoBean implements Serializable {
	// Log4j
	final Logger logger = Logger.getLogger(OrdemServicoBean.class);

	OrdemServico ordemServico = new OrdemServico();
	private List<OrdemServico> lista = new ArrayList<OrdemServico>();
	private String ConfirmeSenha;
	private String resposta = "";
	private String osPesquisado;
	private int quantidade = 15;	

	@Autowired
	UsuarioLogado usuarioLogado;

	@Autowired
	HttpConnection http;
	
	@Autowired
	Alerta alerta;
	
	@PostConstruct
	public void load(){
		String jsonDados = http.getGetHttp(Constante.URL_ORDEM_SERVICO+"listar/"+quantidade);
		listarDados(jsonDados);
	}

    public void pesquisar() {		
    	
    	this.lista.clear();		
    	
    	if(osPesquisado.equals("")){
    		load();
    	}
    	else{
    		logger.info("metodo chamado pesquisar "+osPesquisado);
    		String dados = http.getGetHttp(Constante.URL_ORDEM_SERVICO+"listarPesquisa/"+osPesquisado);
    		logger.info("metodo chamado pesquisar "+dados);
    		lista = listarDados(dados);
    	}
		
		
		
	}
   
    public void carregarMais() {
		quantidade = 30;		
		lista.clear();
	}
	
	public String salvar() {		
	    
	    String label = "salvo"; 	    				
		
		logger.info("Metodo salvar iniciado " +new Gson().toJson(ordemServico));
	
		if (ordemServico.getId() == 0 ) {										
			resposta = http.getPostHttp(Constante.URL_ORDEM_SERVICO+"cadastrar", "cadastrar", new Gson().toJson(ordemServico));	
                        
		} else if (ordemServico.getId() > 0) {		
			logger.info("estatus da os " +ordemServico.getEstatus());
			
			if(!ordemServico.getEstatus().equals("andamento")){			
					
				resposta = http.getPostHttp(Constante.URL_ORDEM_SERVICO+"editar",	"editar", new Gson().toJson(ordemServico));
				label = "atualizado";
			}else{
				alerta.warn("Os não pode ser alterada esta em andamento", true);
			}
			
		}
		
		http.validarRequisição(label);
		
		return "os?faces-redirect=true";
	}

	public String deletar() {
		logger.info("Metodo deletar os iniciado " +ordemServico.getId());
		
		if(ordemServico.getEstatus().equals("aberta")){
			
			resposta = http.getGetHttp(Constante.URL_ORDEM_SERVICO+"deletar/"+ordemServico.getId());			
			lista.clear();
			http.validarRequisição("deletado");
			
		}else{
			alerta.warn("Ordem de serviço não pode ser deletada", false);
		}
		
		load();
		return "usuario";
	}
	
	public String edita() {
		return "edita";
	}

	public OrdemServico getOrdemServico() {
		return ordemServico;
	}

	public void setOrdemServico(OrdemServico ordemServico) {
		this.ordemServico = ordemServico;
	}

	public List<OrdemServico> getLista() {
		if (!lista.isEmpty()) {
			return lista;
		} else {
			return new ArrayList<OrdemServico>();
		}
	}

	public void setLista(List<OrdemServico> lista) {
		this.lista = lista;
	}

	public List listarDados(String jsonDados) {

		
		logger.info("Dados recebidos " + jsonDados);

		try {
			JSONArray jsonArray = new JSONArray(jsonDados);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = new JSONObject(jsonArray.get(i).toString());
				
				OrdemServico os = new OrdemServico();
				os.setTipo(jsonObject.getString("tipo"));
		        os.setId(jsonObject.getLong("id"));
		        os.setNomeCliente(jsonObject.getString("nomeCliente"));
		        os.setContato(jsonObject.getString("contato"));
		        os.setEndereco(jsonObject.getString("endereco"));
		        os.setBairro(jsonObject.getString("bairro"));
		        os.setCidade(jsonObject.getString("cidade"));
		        os.setTelefone(jsonObject.getString("telefone"));
		        os.setEstatus(jsonObject.getString("estatus"));
		        os.setData(new Date());
		        os.setAutenticacao(jsonObject.getString("autenticacao"));
		        os.setDadosAcesso(jsonObject.getString("dadosAcesso"));
		        os.setDescricao(jsonObject.getString("descricao"));		       		
				
				lista.add(os);
				

			}
		} catch (JSONException e) {
			logger.info("erro ao carregar dados dos OrdemServicos " + e.getMessage());
		}

		return lista;
	}

	public String getConfirmeSenha() {
		return ConfirmeSenha;
	}

	public void setConfirmeSenha(String confirmeSenha) {
		ConfirmeSenha = confirmeSenha;
	}

	public String getOsPesquisado() {
		return osPesquisado;
	}

	public void setOsPesquisado(String osPesquisado) {
		this.osPesquisado = osPesquisado;
	}	
	
	
	
	
}
