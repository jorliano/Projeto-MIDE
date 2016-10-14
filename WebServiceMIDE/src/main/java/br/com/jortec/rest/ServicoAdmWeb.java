package br.com.jortec.rest;


import java.util.Date;
import java.util.List;



import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import br.com.jortec.dao.AdministradorDao;
import br.com.jortec.dao.UsuarioDao;
import br.com.jortec.model.Administrador;
import br.com.jortec.model.Usuario;
import br.com.jortec.util.Constante;


@RestController 
@Scope("request")
@RequestMapping(value = "/administrador")
public class ServicoAdmWeb {

	final Logger logger = Logger.getLogger(UsuarioDao.class);
	Gson g = new Gson();
	
	@Autowired
	AdministradorDao dao;
	
	@RequestMapping(value = "/logar", method = RequestMethod.POST, headers="Accept=application/json;charset=utf-8")
	public Administrador logar(@RequestParam(value="logar") String dados) {
		logger.info("metdo logar administrador chamado " + dados);

		try {
			JSONObject js = new JSONObject(dados);

			// Pega adm valores e compara
			Administrador adm = dao.logar(js.getString("login"), js.getString("senha"));
			
			if (adm != null) {
				logger.info("Retornou o usuario");
				return adm;
			} else {
				return new Administrador();
			}
			
		} catch (Exception e) {
			logger.info("erro ao logar " + e.getStackTrace());
		}
		return new Administrador();
	}


	@RequestMapping(value = "/cadastrar", method = RequestMethod.POST, headers="Accept=application/json;charset=utf-8")
	public String cadastrar(@RequestParam(value="cadastrar") String dados) {
		
		logger.info("mettodo cadastrar chamado" + dados);
		
		Administrador adm = g.fromJson(dados, Administrador.class);
		adm.setDataCadastro(new Date());

		if (adm != null) {
			logger.info("Administrador tem dados ");

			dao.salvar(adm);
			return Constante.SUCESSO;
		} 
		return Constante.FALHOU;
	}

	@RequestMapping(value = "/editar", method = RequestMethod.POST, headers="Accept=application/json;charset=utf-8")
	public String editar(@RequestParam(value="editar") String dados) {
		
		logger.info("mettodo editar chamado" + dados);
		
		Administrador adm = g.fromJson(dados, Administrador.class);

		if (adm != null) {
			logger.info("Administrador tem dados ");
			
			Administrador admPesquisado = dao.pesquisarId(adm.getId());
			adm.setDataCadastro(admPesquisado.getDataCadastro());

			dao.editar(adm);
			
			return Constante.SUCESSO;
		} 
		
		return Constante.FALHOU;
	}

	
	@RequestMapping(value = "/deletar/{id}", method = RequestMethod.GET,headers="Accept=application/text")
	public String deletar(@PathVariable(value="id") long id) {
		
		logger.info("metodo deletar Administrador chamado "+id);
		
		Administrador adm = dao.pesquisarId(id);
		if(adm != null){
			dao.deletar(adm);
			
			return Constante.SUCESSO;
		}		
		return Constante.FALHOU;
	}
	
	
	@RequestMapping(value = "/pesquisarPorId/{id}", method = RequestMethod.GET,headers="Accept=application/json;charset=utf-8")
	public Administrador pesquisarId(@PathVariable(value = "id") long id) {
		
		logger.info("metodo pesquisar por id Administrador chamado "+id);					
		return  dao.pesquisarId(id);
	}
	

	@RequestMapping(value = "/listar/{quantidae}", method = RequestMethod.GET,headers="Accept=application/json;charset=utf-8")
	public List<Administrador> listarAdministrador(@PathVariable(value = "quantidae") int quantidade) {
		logger.info("Metodo listar administradores chamado ");
		return  dao.listar(quantidade);
	}


	@RequestMapping(value = "/listarPesquisa/{nome}", method = RequestMethod.GET,headers="Accept=application/json;charset=utf-8")
	public List<Administrador> listarPesquisa(@PathVariable(value="nome") String nome) {
		logger.info("Metodo listar adms pesquisado ");
		return dao.listarPesquisa(nome);
	}
	
}
