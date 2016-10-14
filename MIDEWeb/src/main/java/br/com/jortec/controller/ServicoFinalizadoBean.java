package br.com.jortec.controller;

import java.io.Serializable;
import java.text.Format;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;
import org.primefaces.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import br.com.jortec.model.Cliente;
import br.com.jortec.model.Imagem;
import br.com.jortec.model.Material;
import br.com.jortec.model.OrdemServico;
import br.com.jortec.model.Servico;
import br.com.jortec.model.Venda;
import br.com.jortec.servico.HttpConnection;
import br.com.jortec.servico.ImagemValidator;
import br.com.jortec.util.Alerta;
import br.com.jortec.util.Constante;
import br.com.jortec.util.Formate;

@Controller
@Scope("request")
public class ServicoFinalizadoBean implements Serializable {

	// Log4j
	final Logger logger = Logger.getLogger(ServicoFinalizadoBean.class);

	private List<Servico> listaServicoFechado= new ArrayList<Servico>();
	private List<Material> listaMaeriais = new ArrayList<Material>();
	private List<String> imagens = new ArrayList<String>();

	OrdemServico os = new OrdemServico();
	Cliente cliente = new Cliente();
	Servico servico = new Servico();
	Venda venda = new Venda();
	
		
	JSONArray jsonArray;
	private int quantidade = 15;	
	private String servicoPesquisado;	
	

	@Autowired
	Alerta alerta;

	@Autowired
	HttpConnection http;

	@PostConstruct
	public void load() {
		String dados = http.getGetHttp(Constante.URL + "listarFechado/"+quantidade);
		listarServico(dados);
	}	
	
    public void pesquisar() {		
    	
    	this.listaServicoFechado.clear();		
    	
    	if(servicoPesquisado.equals("")){
    		load();
    	}else{
    		
    		logger.info("metodo chamado pesquisar "+servicoPesquisado);    		
    		String dados = http.getGetHttp(Constante.URL+"listarPesquisa/"+servicoPesquisado);
    		logger.info("metodo chamado pesquisar "+dados);
    		listaServicoFechado = listarServico(dados);
    	}		
		
	}	
    
    public void carregarMais() {		
		logger.info("Metodo carregar mais chamado");
			
		quantidade = 30;		
		load();
	}

	public String visualizar() {		
		
		  logger.info("Metodo visualizar chamado id da pesquisa "+servico.getId()); 
		  String jsonDados = http.getGetHttp(Constante.URL+"pesquisarPorId/"+servico.getId());
		  
		  logger.info("Dados recebidos no pesquisar serviço geral "+jsonDados);		 
		  
		  try {
		  
			  JSONObject jo = new JSONObject(jsonDados);
			  
			  servico.setData(Formate.longParaDatastring(jo.getLong("data")));			  
			  servico.setDescricao(jo.getString("descricao"));
			  servico.setEncerramento(jo.getString("encerramento"));
			  servico.setHora(jo.getString("hora"));
			  servico.setLatitude(jo.getString("latitude"));
			  servico.setLongitude(jo.getString("longitude"));
			  servico.setNomeCliente(jo.getString("nomeCliente"));
			  servico.setParentesco(jo.getString("parentesco"));
			  servico.setDataEncerramento(Formate.longParaDatastring(jo.getLong("dataEncerramento")));
			  
			  
			  String dadosVenda = http.getGetHttp(Constante.URL+ "pesquisarVendaPorId/"+ jo.getLong("id"));									
			  Venda  venda = new Gson().fromJson(dadosVenda, Venda.class);				
			  servico.setVenda(venda);
			 			 
			  
			  //CARREGAR MATERIAL DO SERVICO 
			  JSONArray jsonMateriais = new  JSONArray(jo.getString("materiais")); 
			  for (int i = 0; i < jsonMateriais.length(); i++) {
			  
				  Material m = new Material();
				  m.setDescricao(jsonMateriais.getJSONObject
				  (i).getString("descricao"));
				  m.setQuantidade(jsonMateriais.getJSONObject(i).getInt("quantidade"));
				  
				  listaMaeriais.add(m);
			  
			  }
			  
			  //CARREGAR IMAGENS DO SERVICO 
			  JSONArray jsonImagens = new  JSONArray(jo.getString("imagens")); 
			  for (int i = 0; i < jsonImagens.length(); i++) {
			  
				  String caminho = new
				  ImagemValidator().caregarImagem(jsonImagens.getJSONObject(i).getString("imagem"), "imagem"+i+".jpg"); 
				  imagens.add(caminho); 
			   }			  
			  
			  logger.info("Dados carregados");
			  
		  } 
		  catch (JSONException e) {
		    logger.info("erro ao carregar dados do servico" + e.getMessage());
          }
		 

		return "estatus";
	}

	public Servico getServico() {
		return servico;
	}

	public void setServico(Servico servico) {
		this.servico = servico;
	}

	
	
	public List<Servico> getlistaServicoFechado() {
		if (!listaServicoFechado.isEmpty()) {
			return listaServicoFechado;
		} else {
			return new ArrayList<Servico>();
		}
		
	}

	public void setlistaServicoFechado(List<Servico> listaServicoFechado) {
		this.listaServicoFechado = listaServicoFechado;
	}


	public List listarServico(String jsonDados ) {

		logger.info("Dados recebidos no listar serviço " + jsonDados);

		if (jsonDados.length() > 10) {
			try {
				JSONArray jsonArray = new JSONArray(jsonDados);
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject = new JSONObject(jsonArray.get(i).toString());

					Servico s = new Servico();
					s.setId(jsonObject.getLong("id"));
					s.setHora(jsonObject.getString("hora"));	

					logger.info("Id da pesquisa usuario"+ jsonObject.getLong("usuario_id"));
					String dadosUsuario = http.getGetHttp(Constante.URL_USUARIO+ "pesquisarPorId/"+ jsonObject.getLong("usuario_id"));									
					Cliente cliente = new Gson().fromJson(dadosUsuario,	Cliente.class);
					
					String dadosOs = http.getGetHttp(Constante.URL_ORDEM_SERVICO+ "pesquisarPorId/"+ jsonObject.getLong("ordem_servico_id"));
					logger.info("Dados no pesquisar os por id "+dadosOs);
					OrdemServico os = new Gson().fromJson(dadosOs,OrdemServico.class);

					s.setCliente(cliente);
					s.setOrdemServico(os);

					listaServicoFechado.add(s);

				}
			} catch (JSONException | JsonSyntaxException e) {
				logger.info("erro ao carregar dados dos clientes "
						+ e.getMessage());
			}
		}
		return listaServicoFechado;
	}

	public OrdemServico getOs() {
		return os;
	}

	public void setOs(OrdemServico os) {
		this.os = os;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public List<String> getImagens() {
		return imagens;
	}

	public void setImagens(List<String> imagens) {
		this.imagens = imagens;
	}

	public List<Material> getListaMaeriais() {
		return listaMaeriais;
	}

	public void setListaMaeriais(List<Material> listaMaeriais) {
		this.listaMaeriais = listaMaeriais;
		
	}

	public String getData() {
		return Formate.dataParaStringExtenso(new Date());
	}

	public String getServicoPesquisado() {
		return servicoPesquisado;
	}

	public void setServicoPesquisado(String servicoPesquisado) {
		this.servicoPesquisado = servicoPesquisado;
	}

	public Venda getVenda() {
		return venda;
	}

	public void setVenda(Venda venda) {
		this.venda = venda;
	}
   
   
    
}
