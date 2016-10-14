package br.com.jortec.rest;

import java.util.List;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.jortec.dao.UsuarioDao;
import br.com.jortec.model.Usuario;
import br.com.jortec.util.Constante;

import com.google.gson.Gson;


@RestController 
@Scope("request")
@RequestMapping(value = "/usuario")
public class ServicoUsuarioWeb {

	final Logger logger = Logger.getLogger(ServicoUsuarioWeb.class);
	Gson g = new Gson();
	
	@Autowired
	UsuarioDao dao ;


	@RequestMapping(value = "/cadastrar", method = RequestMethod.POST, headers="Accept=application/text")
	public String cadastrar(@RequestParam(value = "cadastrar") String dados) {
		
		logger.info("mettodo cadastrar chamado" + dados);		
		Usuario usuario = g.fromJson(dados, Usuario.class);		

		if (usuario != null) {
			logger.info("Usuario tem dados ");

			dao.salvar(usuario);
			return Constante.SUCESSO;
		} 
		return Constante.FALHOU;
	}

	@RequestMapping(value = "/editar", method = RequestMethod.POST, headers="Accept=application/text")
	public String editar(@RequestParam(value = "editar") String dados) {
		
		logger.info("mettodo editar chamado" + dados);		
		Usuario usuario = g.fromJson(dados, Usuario.class);		
			

		if (usuario != null) {
			 //CONFERIR TOKEN  
			if(!usuario.getEstatus().equals("pendente")){
				Usuario us = dao.pesquisarId(usuario.getId());
				usuario.setRegistroGcm(us.getRegistroGcm());
			}
			
			logger.info("Usuario tem dados ");

			dao.editar(usuario);
			return Constante.SUCESSO;
		} 
		return Constante.FALHOU;
	}

	@RequestMapping(value = "/deletar/{id}", method = RequestMethod.GET, headers="Accept=application/text")
	public String deletar(@PathVariable(value = "id") long id) {
		
		logger.info("metodo deletar usuario chamado "+id);
		
		Usuario us = dao.pesquisarId(id);		
		if(us != null){
			
			if(us.getEstatus().equals("ativo"))
				us.setEstatus("inativo");
			else
				us.setEstatus("ativo");
			
			dao.editar(us);
			return Constante.SUCESSO;
		}		
		return Constante.FALHOU;
	}

	@RequestMapping(value = "/pesquisarPorId/{id}", method = RequestMethod.GET, headers="Accept=application/text")
	public String pesquisarPorId(@PathVariable(value = "id") long id) 
	{
		logger.info("Metodo pesquisar usuario por ID ");		
		return g.toJson(dao.pesquisarId(id));
	}

	@RequestMapping(value = "/listar/{quantidade}", method = RequestMethod.GET, headers="Accept=application/json;charset=utf-8")
	public List<Usuario> listar(@PathVariable(value = "quantidade") int quantidade) {
		logger.info("Metodo listar usuarios chamado ");
		return dao.listar(quantidade);
	}
	
	@RequestMapping(value = "listarCliente", method = RequestMethod.GET, headers="Accept=application/json;charset=utf-8")
	public List<Usuario> listarUsuarioAtivos() {
		logger.info("Metodo listar usuarios ativos chamado ");
		return dao.listarUsuarioAtivos();
	}

	@RequestMapping(value = "/listarPesquisa/{nome}", method = RequestMethod.GET, headers="Accept=application/json;charset=utf-8")
	public List<Usuario> listarPesquisa(@PathVariable(value = "nome") String nome) {
		logger.info("Metodo listar usuarios pesquisado ");
		return dao.listarPesquisa(nome);
	}
	
	
	
}
