package br.com.jortec.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import br.com.jortec.dao.ChatDao;
import br.com.jortec.dao.UsuarioDao;
import br.com.jortec.model.Administrador;
import br.com.jortec.model.Chat;
import br.com.jortec.model.OrdemServico;
import br.com.jortec.model.Relatorio;
import br.com.jortec.model.Servico;
import br.com.jortec.model.Usuario;
import br.com.jortec.service.EnviarGCM;
import br.com.jortec.service.GerarRelatorio;
import br.com.jortec.service.conteudoDaMensage;
import br.com.jortec.util.Constante;


@RestController 
@Scope("request")
@RequestMapping(value = "/chat")
public class ServicoChat {

	final Logger logger = Logger.getLogger(ServicoChat.class);
	
	@Autowired
	ChatDao dao;
	
	@Autowired
	UsuarioDao usuarioDao;
	
	Chat chat = new Chat();
	conteudoDaMensage content = new conteudoDaMensage();
	Gson g = new Gson();

	List<Usuario> listaUsuarios = new ArrayList<Usuario>();
	List<Chat> lista = new ArrayList<Chat>();
	
	//@RequestMapping(value = "/", method = RequestMethod.GET)
	
	@RequestMapping(value = "/listar/{destinatario}/{remetente}", method = RequestMethod.GET,headers="Accept=application/json;charset=utf-8")
	public List<Chat> listaChate(@PathVariable ("remetente") String remetente, @PathVariable ("destinatario")  String destinatario) {
		
		logger.info("Metodo listar chate chamado "+remetente+" "+destinatario);	
		lista = dao.listar(remetente, destinatario);
		
		for (int i = 0; i < lista.size(); i++) {
		   if(lista.get(i).getDestinatario().equals(remetente));{
			   
			   chat = lista.get(i);
			   if(chat.getEstatus().equals("desmarcado")){
				 chat.setEstatus("marcado");   
				 dao.editar(chat);
			   }
			   
		   }
		}
		
		return  lista;
	}
	
	@RequestMapping(value = "/listarMais/{id}", method = RequestMethod.GET,headers="Accept=application/json;charset=utf-8")
	public List<Chat> listaChate(@PathVariable ("id") long id) {
		
		logger.info("Metodo listarMais chate chamado "+id);	
		
		chat = dao.pesquisarPorId(id);
		if(chat.getDestinatario().equals("sistema")){
			lista = dao.listarMais(id,chat.getRemetente(), chat.getDestinatario());
		}else{
			
			chat.setRemetente(chat.getDestinatario());
			chat.setDestinatario(chat.getRemetente());
			lista = dao.listarMais(id,chat.getRemetente(), chat.getDestinatario());
		}
		
		
		
		
		for (int i = 0; i < lista.size(); i++) {
		   if(lista.get(i).getDestinatario().equals(chat.getRemetente()));{
			   
			   chat = lista.get(i);
			   if(chat.getEstatus().equals("desmarcado")){
				 chat.setEstatus("marcado");   
				 dao.editar(chat);
			   }
			   
		   }
		}
		
		return  lista;
	}
	
	@RequestMapping(value = "/enviar_mobile", method = RequestMethod.POST, headers="Accept=application/json;charset=utf-8")
	public String enviarMobile(@RequestParam(value="dados") String dados) {
		
		logger.info("mettodo enviar para mobile chamado " + dados);
		
		String resultado = null;
		
		Chat chat = g.fromJson(dados, Chat.class);
		chat.setData(new Date());
		chat.setEstatus("desmarcado");

		if (chat != null) {
			logger.info("Chat tem dados ");
			
			//LISTAR REMETENTE
			if(chat.getDestinatario().equalsIgnoreCase("Todos")){
				listaUsuarios = usuarioDao.listarUsuarioAtivos();				
			}
			else{
			    Usuario us = usuarioDao.pesquisarPorNome(chat.getDestinatario());	
			    listaUsuarios.add(us);
			}
			
			for (Usuario usuario : listaUsuarios) {
				content.addRegId(usuario.getRegistroGcm());	
				
				logger.info("Destinatario "+usuario.getPrimeiroNome()+" KEY "+usuario.getRegistroGcm());
			}							
										
			content.createDataChate(Constante.CHAT, chat.getRemetente(), chat.getDestinatario(), chat.getMensage(), chat.getHora(), chat.getEstatus());
				
			logger.info("Conteudo do chat enviado ");				
			resultado = EnviarGCM.post(Usuario.API_KEY, content);				
					

			try {
				JSONObject js = new JSONObject(resultado);
				int rs = js.getInt("success");			

				if (rs >= 1 ) {
					logger.info("enviado com sucesso ");
					
					dao.salvar(chat);
					
					return Constante.SUCESSO;
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return Constante.FALHOU;
			}				
			
		} 
		return Constante.FALHOU;		
	}
	
	@RequestMapping(value = "/enviar", method = RequestMethod.POST, headers="Accept=application/json;charset=utf-8")
	public String enviar(@RequestParam(value="dados") String dados) {
		
		logger.info("mettodo enviar chamado " + dados);
		
		String resultado = null;
		
		Chat chat = g.fromJson(dados, Chat.class);
		chat.setData(new Date());
		chat.setEstatus("desmarcado");
		
		if (chat != null) {
			logger.info("Chat tem dados ");
			
			
			dao.salvar(chat);					
			return Constante.SUCESSO;
				
		} 
		return Constante.FALHOU;		
	}
	
	@RequestMapping(value = "/listarUsuario", method = RequestMethod.GET,headers="Accept=application/json;charset=utf-8")
	public String listarUsuario() {
		logger.info("Metodo listarUsuario chate chamado ");
		
		listaUsuarios = usuarioDao.listarUsuarioAtivos();
		
		JSONArray jsonArray = new JSONArray();
		
		for (int i = 0; i < listaUsuarios.size(); i++) {
			
			JSONObject json = new JSONObject();
			long menssagesNaoLidas = dao.mensagesNaoLidas(listaUsuarios.get(i).getPrimeiroNome()+" "+listaUsuarios.get(i).getSobreNome());
			
			try {
				
				json.put("nome", listaUsuarios.get(i).getPrimeiroNome()+" "+listaUsuarios.get(i).getSobreNome());			
				json.put("cargo", listaUsuarios.get(i).getCargo());
				json.put("mensages", menssagesNaoLidas);
			   
				jsonArray.put(json);
			} catch (JSONException e) {
				logger.info("Erro ao carregar json dos usuaiors do chat "+e.getMessage());
				e.printStackTrace();
			}
		}
		
		
		
		return  jsonArray.toString();
	}
}
