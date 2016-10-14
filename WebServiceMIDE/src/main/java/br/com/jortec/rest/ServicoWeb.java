package br.com.jortec.rest;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.jortec.dao.AdministradorDao;
import br.com.jortec.dao.OrdemServicoDao;
import br.com.jortec.dao.ServicoDao;
import br.com.jortec.dao.UsuarioDao;
import br.com.jortec.model.Administrador;
import br.com.jortec.model.OrdemServico;
import br.com.jortec.model.Servico;
import br.com.jortec.model.Usuario;
import br.com.jortec.model.Venda;
import br.com.jortec.service.EnviarGCM;
import br.com.jortec.service.conteudoDaMensage;
import br.com.jortec.util.Constante;
import br.com.jortec.util.Formate;

import com.google.gson.Gson;

@RestController 
@Scope("request")
@RequestMapping(value = "/servico")
public class ServicoWeb {

	final Logger logger = Logger.getLogger(UsuarioDao.class);	
	
	@Autowired
	ServicoDao dao;
	
	@Autowired
	OrdemServicoDao ordemServicoDao;
	
	@Autowired
	UsuarioDao usuarioDao;

	@Autowired
	AdministradorDao administradorDao;
	
	
	Gson g = new Gson();
	Servico servico = new Servico();
	conteudoDaMensage content = new conteudoDaMensage();
	

	@RequestMapping(value = "/cadastrar", method = RequestMethod.POST, headers="Accept=application/text")
	public String cadastrar(@RequestParam(value = "cadastrar") String dados) {
		logger.info("metdo cadastrar serviço chamado " + dados);		

		try {

			JSONObject js = new JSONObject(dados);
            
			servico.setHora(js.getString("hora"));				
            servico.setData(new Date());
            
			JSONObject jsonUsuario = new JSONObject(js.getString("cliente"));		
			servico.setUsuario_id(jsonUsuario.getLong("id"));	
			
			JSONObject jsonOs = new JSONObject(js.getString("ordemServico"));		
			servico.setOrdem_servico_id(jsonOs.getLong("id"));	
			
			if(servico != null){
				
				//Pegar o ultimo id					
				List<Servico> servicos = dao.listarAll();							
				long id = servicos.size() == 0 ? 1 : servicos.get(0).getId() + 1;				
				servico.setId(id);			
				
				logger.info("servico pesquisado " + servico.getId());
				
				String respostaMsg = enviarMensage(servico);
				
				if(respostaMsg.equals(Constante.SUCESSO)){					
					servico.setEstatus("aberto");
					logger.info("id da os no cadastro do servico "+servico.getOrdem_servico_id());
					dao.salvar(servico);
					
					OrdemServico os = ordemServicoDao.pesquisarId(servico.getOrdem_servico_id());
					os.setEstatus("andamento");
					ordemServicoDao.editar(os);
					
					return Constante.SUCESSO;
				}			
				
			}
			

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return Constante.FALHOU;
	}
	
	public String enviarMensage(Servico serviso){
		
		String resultado = "";
		
		logger.info("Ids dos Forinkey id "+ serviso.getId()+"  us " +servico.getUsuario_id()+" os "+servico.getOrdem_servico_id());	
		
        
        OrdemServico os = ordemServicoDao.pesquisarId(servico.getOrdem_servico_id());
        Usuario us = usuarioDao.pesquisarId(servico.getUsuario_id());
		
        logger.info("Dados pesquisados ");	
        logger.info("Data da os  "+os.getData()+" data formatada "+Formate.dataParaString(os.getData()));	
		
        
		if(us != null){			
			content.addRegId(us.getRegistroGcm());
			
			if(os != null){
				
				
				content.createData(   Constante.ORDEM_SERVICO,
						              String.valueOf(servico.getId()),  
						              String.valueOf(os.getId()), 
						              os.getNomeCliente(),
						              os.getContato(),
						              os.getEndereco(),
						              os.getBairro(),
						              os.getCidade(),
						              os.getTelefone(),
						              os.getAutenticacao(), 
						              os.getDadosAcesso(),
						              os.getDescricao(),
						              os.getTipo(), 
						              Formate.dataParaString(os.getData()),
						              servico.getHora());
						

				logger.info("Conteudo enviado ");
				
				 resultado = EnviarGCM.post(Usuario.API_KEY, content);
				
			}
		}		

		try {
			JSONObject js = new JSONObject(resultado);
			int rs = js.getInt("success");

			if (rs > 0) {
				return Constante.SUCESSO;
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Constante.FALHOU;
		}

		return Constante.FALHOU;			
	}

	@RequestMapping(value = "/deletar/{id}", method = RequestMethod.GET, headers="Accept=application/text")
	public String deletar(@PathVariable(value = "id") long id) {
		
		logger.info("metodo deletar servico chamado "+id);
		
		String resultado = null;
		
		Servico servico = dao.pesquisarPorId(id);
		Usuario us = usuarioDao.pesquisarId(servico.getUsuario_id());
		
		if(us != null){			
			logger.info("metodo deletar servico chamado id do serviço "+servico.getId());
						
			content.addRegId(us.getRegistroGcm());							
			content.createDataRemover( Constante.REMOVER_ORDEM_SERVICO, String.valueOf(servico.getId()));
			
			logger.info("Conteudo enviado ");				
			resultado = EnviarGCM.post(Usuario.API_KEY, content);				
		}		

		try {
			JSONObject js = new JSONObject(resultado);
			int rs = js.getInt("success");			

			if (rs >= 0 ) {
				logger.info("enviado com sucesso "+rs+" id do serviço ao deletar "+servico.getId());
				dao.deletar(servico);
				logger.info("eserviço deletado");
				OrdemServico os = ordemServicoDao.pesquisarId(servico.getOrdem_servico_id());
				os.setEstatus("aberta");
				ordemServicoDao.editar(os);
				return Constante.SUCESSO;
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Constante.FALHOU;
		}		
				
		return Constante.FALHOU;
	}	

	@RequestMapping(value = "/pesquisarPorId/{id}", method = RequestMethod.GET, headers="Accept=application/json;charset=utf-8")
	public Servico pesquisarPorId(@PathVariable(value = "id") long id) {
		logger.info("Metodo pesquisarPorId serviço chamado "+ id);		
		return dao.pesquisarPorId(id);
	}
	
	@RequestMapping(value = "/pesquisarVendaPorId/{id}", method = RequestMethod.GET, headers="Accept=application/text;charset=utf-8")
	public String pesquisarVendaPorId(@PathVariable(value = "id") long id) {
		logger.info("Metodo pesquisar venda ");	
		servico = dao.pesquisarPorId(id);		
		
		
		Venda v = new Venda();
		v.setId(servico.getVenda().getId());
		v.setEquipamento(servico.getVenda().getEquipamento());
		v.setPagamento(servico.getVenda().getPagamento());
		v.setTipo(servico.getVenda().getTipo());
		v.setQuantidade(servico.getVenda().getQuantidade());
		
		return new Gson().toJson(v);
	}
	
	@RequestMapping(value = "/listarAberto/{quantidade}", method = RequestMethod.GET, headers="Accept=application/json;charset=utf-8")
	public List<Servico> listarServicoAberto(@PathVariable(value = "quantidade") int quantidade) {
		logger.info("Metodo listar serviços chamado ");
		return dao.listarServicoAberto(quantidade);
	}

	@RequestMapping(value = "/listarFechado/{quantidade}", method = RequestMethod.GET, headers="Accept=application/json;charset=utf-8")
	public List<Servico> listarServicoFechado(@PathVariable(value = "quantidade") int quantidade) {
		logger.info("Metodo listar serviços chamado ");
		return dao.listarServicoFechado(quantidade);
	}	

	@RequestMapping(value = "/monitorar/{id}", method = RequestMethod.GET, headers="Accept=application/json;charset=utf-8")
	public List<Servico> monitorar(@PathVariable(value = "id") long id)  {
		
	 	logger.info("Metodo listar monitoramento chamado "+id);
	 	
	 	 Calendar c = Calendar.getInstance();
         int ano = c.get(Calendar.YEAR);
         int mes = c.get(Calendar.MONTH) + 1;
         int dia = c.get(Calendar.DAY_OF_MONTH);
	 	
	 	logger.info("Data de hoje "+dia+"/"+mes+"/"+ano);
	 	
		List<Servico> lista = dao.listarDia(dia, mes, ano);
		
		//PESQUIADOR APENAS UM USUARIO
		if(id > 0){
			List<Servico> aux = new ArrayList<Servico>();
			
			for (int i = 0; i < lista.size(); i++) {
				if(lista.get(i).getUsuario_id() == id){
					aux.add(lista.get(i));
				}
			}
			lista = aux;
		}
		
		
		logger.info("lista tem dados "+lista.size());
		List<Servico> aux = new ArrayList<Servico>();
		
		for (int i = 0; i < lista.size(); i++) {
			
			OrdemServico os = ordemServicoDao.pesquisarId(lista.get(i).getOrdem_servico_id());
	        Usuario us = usuarioDao.pesquisarId(lista.get(i).getUsuario_id());
			
	        logger.info(" dados OS nome "+os.getNomeCliente()+" us nome "+us.getPrimeiroNome());
	        if(os != null && us != null){
		        
	        	servico = new Servico();
	        	String descrição;
	        	if(lista.get(i).getDescricao().length() > 80){	        	    	
	        		   descrição = lista.get(i).getDescricao().substring(1,77) + "...";
	        	}else{
	        		descrição = lista.get(i).getDescricao();
	        	}	        	
	        	
	        	
	        	servico.setDescricao(us.getPrimeiroNome()+" encerrou OS do(a) "+os.getNomeCliente()+""
		        		            + " , "+descrição);
	        	
		        servico.setHora(String.valueOf(lista.get(i).getDataEncerramento().getHours())+":"+String.valueOf(lista.get(i).getDataEncerramento().getMinutes()));
		        servico.setEncerramento(os.getTipo());
		        
		        logger.info(" descricao os "+servico.getDescricao());
		        aux.add(servico);
	        }
		}
			
		
		return aux;
	}

	@RequestMapping(value = "/listarPesquisa/{codigo}", method = RequestMethod.GET, headers="Accept=application/json;charset=utf-8")
	public List<Servico> listarPesquisa(@PathVariable(value = "codigo") String codigo) {
		logger.info("Metodo listar adms pesquisado ");
		return dao.listarPesquisaServicoFechado(Long.parseLong(codigo));
	}

	@RequestMapping(value = "/adm", method = RequestMethod.GET, headers="Accept=application/json;charset=utf-8")
	public String  adm()  {
		logger.info("Cadastrando usuario padrão");
		
		List us = administradorDao.listar(1);
				
		if(us.isEmpty()){
			Administrador adm = new Administrador();
			adm.setDataCadastro(new Date());
			adm.setLogin("admin");
			adm.setSenha("mide123");
			adm.setEmail("admin@mide.com.br");
			adm.setFacebook("admin@mide.com.br");
			adm.setTwitter("admin@mide.com.br");
			adm.setDataAniversario(new Date());
			adm.setPrimeiroNome("Administrador");
			adm.setSobreNome("MIDE");
			adm.setSobre("Adminstrador padrão do sistema MIDE");
			adm.setTelefone("(00)0000-0000");
			
			administradorDao.salvar(adm);
			
			return "Cadastrado com sucesso\nLOGIN : admin\nSENHA : mide123";
		}
		
		return "Usuario padão ja foi cadastrado";
	}
	
	
}
