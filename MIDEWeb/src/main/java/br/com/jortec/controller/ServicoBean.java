package br.com.jortec.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
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
import br.com.jortec.model.OrdemServico;
import br.com.jortec.model.Servico;
import br.com.jortec.servico.HttpConnection;
import br.com.jortec.util.Alerta;
import br.com.jortec.util.Constante;
import br.com.jortec.util.Formate;

import com.google.gson.Gson;

@Controller
//@Scope("request")
@Scope("view")
public class ServicoBean implements Serializable {

	// Log4j
	final Logger logger = Logger.getLogger(ServicoBean.class);
    
	private List<Servico> listaServicoAberto = new ArrayList<Servico>();	
	private List<Cliente> listaCliente = new ArrayList<Cliente>();
	private List<OrdemServico> listaOs = new ArrayList<OrdemServico>();
	private List horas = new ArrayList<String>();

	OrdemServico os = new OrdemServico();
	Cliente cliente = new Cliente();
	Servico servico = new Servico();
	
	//relatorio
    private String dia = "1", mes = "1", ano = "2016";
    private List dias, meses, anos;
    private String periodo;
    private String referencia;
    private String linkRelatorio;
	
	JSONArray jsonArray;	
	private long servicoPesquisado;	
	private int paginaAtual = 10;		
	private int pagina = 1;
	


	@Autowired
	Alerta alerta;

	@Autowired
	HttpConnection http;

	@PostConstruct
	public void load() {
		for (int i = 8; i < 19; i++) {
			if(i < 10)
			horas.add("0"+i+":00");
			else
			horas.add(i+":00");	
		}
	}
	
	public void proximaPagina(){
		if (paginaAtual >= 10 && listaServicoAberto.size() > paginaAtual - 1) {
			paginaAtual = paginaAtual + 10;
			
			pagina ++;
			listaServicoAberto.clear();

		}
		if (paginaAtual < 10) {
			paginaAtual = 10;					
			listaServicoAberto.clear();

		}
	}
	public void paginaAnterior() {
		if (paginaAtual > 10) {			
			paginaAtual = paginaAtual - 10;
			
			pagina --;
			listaServicoAberto.clear();
		}
	}   

	public String salvar() {

		if (os.getId() > 0 && cliente.getId() > 0) {

			servico.setOrdemServico(os);
			servico.setCliente(cliente);			

			logger.info("Metodo salvar iniciado");		

			String resposta = http.getPostHttp(Constante.URL + "cadastrar",
					"cadastrar", new Gson().toJson(servico));

			if (resposta.equals(Constante.SERVER_SUCESSO)) {
				
				alerta.info("Solicitaçâo enviada com sucesso", false);
				listaServicoAberto.clear();
				listaOs.clear();
				
			} else  {
				logger.info("Falhou , tente novamente ");
				alerta.error("Falhou , tente novamente" , false);
			}
			
		} else {
			logger.info("Não é possivel criar serviço");
			alerta.warn("Não é possivel criar o serviço", false);
		}
		return null;
	}

	public String deletar() {

		logger.info("Metodo deletar iniciado " + cliente.getId());
		String resposta = http.getGetHttp(Constante.URL + "deletar/"
				+ servico.getId());

		http.validarRequisição("deletar");
		listaServicoAberto.clear();
		return "servico";
	}

	public List<Cliente> getlistaCliente() {
		if (!listaCliente.isEmpty()) {
			return listaCliente;
		} else {
			String dados = http.getGetHttp(Constante.URL_USUARIO
					+ "listarCliente");
			return listarDados(dados);
		}
	}

	public void setLista(List<Cliente> listaCliente) {
		this.listaCliente = listaCliente;
	}

	public List listarDados(String jsonDados) {

		logger.info("Dados recebidos no listar usuarios" + jsonDados);

		try {
			JSONArray jsonArray = new JSONArray(jsonDados);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = new JSONObject(jsonArray.get(i)
						.toString());

				Cliente c = new Cliente();
				c.setId(jsonObject.getInt("id"));
				c.setPrimeiroNome(jsonObject.getString("primeiroNome"));
				c.setSobreNome(jsonObject.getString("sobreNome"));

				listaCliente.add(c);

			}
		} catch (JSONException e) {
			logger.info("erro ao carregar dados dos clientes " + e.getMessage());
		}

		return listaCliente;
	}

	public List<OrdemServico> getlistaOs() {
		if (!listaOs.isEmpty()) {
			return listaOs;
		} else {
			String dados = http.getGetHttp(Constante.URL_ORDEM_SERVICO
					+ "listarOsAbertas");
			return listarOs(dados);
		}
	}

	public void setListaOs(List<OrdemServico> listaOs) {
		this.listaOs = listaOs;
	}

	public List listarOs(String jsonDados) {

		logger.info("Dados recebidos no listar no listar os " + jsonDados);

		try {
			JSONArray jsonArray = new JSONArray(jsonDados);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = new JSONObject(jsonArray.get(i)
						.toString());
				OrdemServico os = new OrdemServico();
				os.setId(jsonObject.getInt("id"));
				os.setNomeCliente(jsonObject.getString("nomeCliente"));

				listaOs.add(os);

			}
		} catch (JSONException e) {
			logger.info("erro ao carregar dados dos clientes " + e.getMessage());
		}

		return listaOs;
	}

	public Servico getServico() {
		return servico;
	}

	public void setServico(Servico servico) {
		this.servico = servico;
	}

	public List<Servico> getListaServicoAberto() {
		if (!listaServicoAberto.isEmpty()) {
			return listaServicoAberto;
		} else {
			String dados = http.getGetHttp(Constante.URL + "listarAberto/"+paginaAtual);
			return listarServico(dados,listaServicoAberto);
		}

	}
	
	public void setListaServicoAberto(List<Servico> listaServicoAberto) {
		this.listaServicoAberto = listaServicoAberto;
	}

	public List listarServico(String jsonDados , List lista) {

		logger.info("Dados recebidos no listar serviço " + jsonDados);

		if (jsonDados.length() > 10) {
			try {
				JSONArray jsonArray = new JSONArray(jsonDados);
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject = new JSONObject(jsonArray.get(i)
							.toString());

					Servico s = new Servico();
					s.setId(jsonObject.getLong("id"));
					s.setHora(jsonObject.getString("hora"));					

					logger.info("Id da pesquisa usuario "+ jsonObject.getLong("usuario_id"));
					String dadosUsuario = http.getGetHttp(Constante.URL_USUARIO+ "pesquisarPorId/"+ jsonObject.getLong("usuario_id"));												
					logger.info("Id da pesquisa usuario "+ dadosUsuario);
					Cliente cliente = new Gson().fromJson(dadosUsuario,	Cliente.class);
					
					logger.info("Id da pesquisa os "+ jsonObject.getLong("ordem_servico_id"));
					String dadosOs = http.getGetHttp(Constante.URL_ORDEM_SERVICO+ "pesquisarPorId/"+ jsonObject.getLong("ordem_servico_id"));
					
					OrdemServico os = new Gson().fromJson(dadosOs,OrdemServico.class);

					s.setCliente(cliente);
					s.setOrdemServico(os);

					lista.add(s);

				}
			} catch (JSONException e) {
				logger.info("erro ao carregar dados dos clientes "
						+ e.getMessage());
			}
		}
		return lista;
	}
	
	public void gerarRelatorio(){
		
		logger.info("id relatorio"+ cliente.getId()); 
		
		if(cliente.getId() != 0 ){
			for (int i = 0; i < listaCliente.size(); i++) {
				if(listaCliente.get(i).getId() == cliente.getId()){
					referencia = listaCliente.get(i).getPrimeiroNome() +" "+listaCliente.get(i).getSobreNome();
				}
			}
		}else{
			referencia = "todos";
		}
		
		logger.info("dados do relatorio \n periodo "+periodo +"\n referencia "+referencia +" "+ listaCliente.size()
				+ "\n dia "+dia+"\n mes "+mes+"\n ano "+ano);
		
		JSONObject json = new JSONObject();
		try {
			
			json.put("periodo", periodo);
			json.put("referencia", referencia);
			json.put("dia", dia);
			json.put("mes", mes);
			json.put("ano", ano);
			
			String dados = http.getPostHttp(Constante.URL_DADOS+ "relatorio", "relatorio", json.toString());
			if(!dados.equals(Constante.SERVER_SUCESSO)){
				alerta.error("Não foi possivel gerar o relatorio", false);
			}else{
				alerta.info("Relatorio gerado om sucesso , clik no butão imprirmir", false);
				linkRelatorio = Constante.URL_DADOS+"printRelatorio";
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public List getHoras() {			
		return horas;
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

	public String getData() {
		return Formate.dataParaStringExtenso(new Date());
	}
   
    public long getServicoPesquisado() {
		return servicoPesquisado;
	}
    
    public void setServicoPesquisado(long servicoPesquisado) {
		this.servicoPesquisado = servicoPesquisado;
	}
	
    public int getPagina() {
		return pagina;
	}
    public int getPaginaAtual() {
		return paginaAtual;
	}

	public String getLinkRelatorio() {
		return linkRelatorio;
	}

	public String getDia() {
		return dia;
	}

	public void setDia(String dia) {
		this.dia = dia;
	}

	public String getMes() {
		return mes;
	}

	public void setMes(String mes) {
		this.mes = mes;
	}

	public String getAno() {
		return ano;
	}

	public void setAno(String ano) {
		this.ano = ano;
	}

	public String getPeriodo() {
		return periodo;
	}

	public void setPeriodo(String periodo) {
		this.periodo = periodo;
	}

	public String getReferencia() {
		return referencia;
	}

	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}

	public List getDias() {
        dias = new ArrayList<>();
		
		for (int i = 1; i < 32; i++) {
			dias.add(i);
		}
		return dias;
	}

	public void setDias(List dias) {		
		this.dias = dias;
	}

	public List getMeses() {
		String[] arrayMeses = {"janeiro","fevereiro","março","abril","mail","junho","julho","agosto","setembro","outubro","novembro","dezembro"};
		meses = new ArrayList<>();
		
		for (int i = 0; i < arrayMeses.length ; i++) {			
			meses.add(arrayMeses[i]);
		}
		return meses;
	}

	public void setMeses(List meses) {
		this.meses = meses;
	}

	public List getAnos() {
		anos = new ArrayList<>();
		Calendar c = Calendar.getInstance();
		
		for (int i = 2016 ; i <= c.get(Calendar.YEAR); i++) {
			anos.add(i);
		}
		
		
		return anos;
	}

	public void setAnos(List anos) {
		this.anos = anos;
	}
    
    
}
