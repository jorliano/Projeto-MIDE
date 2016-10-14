package br.com.jortec.rest;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.jortec.dao.OrdemServicoDao;
import br.com.jortec.model.OrdemServico;
import br.com.jortec.util.Constante;

import com.google.gson.Gson;



@RestController 
@Scope("request")
@RequestMapping(value = "/ordemservico")
public class ServicoOsWeb {

	final Logger logger = Logger.getLogger(ServicoOsWeb.class);
	Gson g = new Gson();
	
	@Autowired
	OrdemServicoDao dao ;


	@RequestMapping(value = "/cadastrar", method = RequestMethod.POST, headers="Accept=application/text")
	public String cadastrar(@RequestParam(value = "cadastrar") String dados) {
		
		logger.info("mettodo cadastrar chamado" + dados);
		
		OrdemServico os = g.fromJson(dados, OrdemServico.class);
		os.setData(new Date());
		os.setEstatus("aberta");	

		if (os != null) {
			logger.info("OrdemServico tem dados "+os.getDescricao());
			dao.salvar(os);
			return Constante.SUCESSO;
		} 
		return Constante.FALHOU;
	}

	@RequestMapping(value = "/editar", method = RequestMethod.POST, headers="Accept=application/text")
	public String editar(@RequestParam(value = "editar") String dados) {
		
		logger.info("mettodo editar chamado" + dados);
		
		OrdemServico os = g.fromJson(dados, OrdemServico.class);				

		if (os != null) {
			logger.info("OrdemServico tem dados ");
			OrdemServico osPesquisada = dao.pesquisarId(os.getId());
			os.setData(osPesquisada.getData());

			dao.editar(os);
			return Constante.SUCESSO;
		} 
		
		return Constante.FALHOU;
	}

	@RequestMapping(value = "/deletar/{id}", method = RequestMethod.GET, headers="Accept=application/text")
	public String deletar(@PathVariable(value = "id") long id) {
		
		logger.info("metodo deletar OrdemServico chamado "+id);
		
		OrdemServico os = dao.pesquisarId(id);			
		if(os != null){
			
			boolean isUsed = dao.confirmeOs(id);
			
			if(!isUsed){				
				dao.deletar(os);
				return Constante.SUCESSO;
			}
			
			
		}		
		return Constante.FALHOU;
	}	

	@RequestMapping(value = "/pesquisarPorId/{id}", method = RequestMethod.GET, headers="Accept=application/text")
	public String pesquisarPorId(@PathVariable(value = "id") long id) 
	{
		logger.info("Metodo pesquisar servico por ID ");		
		return g.toJson(dao.pesquisarId(id));
	}
	
	@RequestMapping(value = "/listar/{quantidade}", method = RequestMethod.GET, headers="Accept=application/json;charset=utf-8")
	public List<OrdemServico> listarOs(@PathVariable(value = "quantidade") int quantidade) {
		logger.info("Metodo listar os chamado ");
		return dao.listar(quantidade);
	}

	@RequestMapping(value = "/listarOsAbertas", method = RequestMethod.GET, headers="Accept=application/json;charset=utf-8")
	public List<OrdemServico> listarOsAbertas() {
			logger.info("Metodo listar os abertas chamado ");
			return dao.listarOsAbertas();
	}
	
	@RequestMapping(value = "/listarPesquisa/{nome}", method = RequestMethod.GET, headers="Accept=application/json;charset=utf-8")
	public List<OrdemServico> listarPesquisa(@PathVariable String nome) {
		logger.info("Metodo listar os pesquisado ");
		return dao.listarPesquisa(nome);
	}
	

}
