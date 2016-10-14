package br.com.jortec.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Date;
import java.util.List;





import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
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
import com.mysql.jdbc.util.Base64Decoder;

import br.com.jortec.dao.OrdemServicoDao;
import br.com.jortec.dao.ServicoDao;
import br.com.jortec.dao.UsuarioDao;
import br.com.jortec.model.Administrador;
import br.com.jortec.model.Imagem;
import br.com.jortec.model.Material;
import br.com.jortec.model.OrdemServico;
import br.com.jortec.model.Servico;
import br.com.jortec.model.Usuario;
import br.com.jortec.model.Venda;
import br.com.jortec.util.Constante;
import br.com.jortec.util.Formate;


@RestController 
@Scope("request")
@RequestMapping(value = "/mobile")
public class ServicoMobile {

	final Logger logger = Logger.getLogger(UsuarioDao.class);
	Usuario usuario = new Usuario();
	
	@Autowired
	UsuarioDao dao; 
	
	@Autowired
	ServicoDao servicoDao;
	
	@Autowired
	OrdemServicoDao osDao;
	
	@RequestMapping(value = "/logar", method = RequestMethod.POST, headers="Accept=application/json")
	public long logar(@RequestParam(value = "logar") String dados) {

		 logger.info("metdo logar chamado " + dados);

		try {
			JSONObject js = new JSONObject(dados);
			
			Usuario us = dao.logar(js.getString("login"), js.getString("senha"));
			
			if(us != null){
				logger.info("metdo loga tem dados");							
				return us.getId();
								
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return 0;
	}
	
		
	@RequestMapping(value = "/cadastrarToken", method = RequestMethod.POST, headers="Accept=application/json")
	public String cadastrarToken(@RequestParam(value = "cadastrarToken") String dados) {

		 logger.info("metdo cadastrarToken chamado " + dados);

		try {
			JSONObject js = new JSONObject(dados);
			
						
			Usuario us = dao.pesquisarId(js.getLong("id"));			
			us.setRegistroGcm(js.getString("registroGcm"));		
			us.setEstatus("ativo");
			
			if(us != null){				
				dao.editar(us);
			}
			else{
				return "falso";
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "true";
	}

	@RequestMapping(value = "/cadastrar", method = RequestMethod.POST, headers="Accept=application/json")
	public String cadastrar(@RequestParam (value = "cadastrar") String dados) {

		
		List<Material> maeriais = new ArrayList<Material>();		

		logger.info("Dados a serem cadastrados " + dados);		
		

		try {

			JSONObject js = new JSONObject(dados);
			
			long id = js.getLong("id");				
			
			Servico  servico = servicoDao.pesquisarPorId(js.getLong("id"));
			logger.info("id pesquisado "+servico.getId());	
			
			if(servico != null){			
											
				servico.setDescricao(js.getString("descricao"));
				servico.setEncerramento(js.getString("encerramento"));		
				servico.setLatitude(js.getString("latitude"));
				servico.setLongitude(js.getString("longitude"));
				servico.setNomeCliente(js.getString("nomeCliente"));			
				servico.setParentesco(js.getString("parentesco"));
				servico.setDataEncerramento(Formate.stringParaData(js.getString("data")));
				servico.setEstatus("fechado");
			
				servico.setUsuario_id(servico.getUsuario_id());	
				servico.setOrdem_servico_id(servico.getOrdem_servico_id());	
				
				JSONObject vendaJson = new JSONObject(js.getString("venda"));
				Venda v = new Venda();
				v.setEquipamento(vendaJson.getString("equipamento"));
				v.setQuantidade(vendaJson.getInt("quantidade"));
				v.setTipo(vendaJson.getString("tipo"));
				v.setPagamento(vendaJson.getDouble("pagamento"));
				
				servico.setVenda(v);
				
				logger.info("materiais para cadastro ");
				JSONArray jsonMateriais = new JSONArray(js.getString("materiais"));
				for (int i = 0; i < jsonMateriais.length(); i++) {              
					
					Material m = new Gson().fromJson(jsonMateriais.getJSONObject(i).toString(), Material.class);
					m.setId(0);
					maeriais.add(m);
				}									
				servico.setMateriais(maeriais);

					
				logger.info("id do servico ao ser alterado "+servico.getId());	
				servicoDao.editar(servico);
				
				logger.info("id da os a ser alterado "+servico.getOrdem_servico_id());
				OrdemServico os = osDao.pesquisarId(servico.getOrdem_servico_id());
				
				if(os != null){
					if(!servico.getEncerramento().equals("Pendente")){
							logger.info("Os alterado estatus para feichada");
							
							 os.setEstatus("fechada");
					}else{	 
							logger.info("Os alterado estatus para aberta");							
							 os.setEstatus("aberta");
					}	 
					osDao.editar(os);
					 
				}	
				
				return Constante.SUCESSO;			
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return Constante.FALHOU;
	}
	
	@RequestMapping(value = "/cadastrarImagem", method = RequestMethod.POST, headers="Accept=application/json")
	public String cadastrarImagem(@RequestParam (value = "cadastrarImagem") String dados) {

		List<Imagem> imagens = new ArrayList<Imagem>();
		logger.info("Dados a serem cadastrarImagem " + dados);				

		try {

			JSONObject js = new JSONObject(dados);
			
			long id = js.getLong("id");					
			Servico  servico = servicoDao.pesquisarPorId(js.getLong("id"));			
			
			if(servico != null){																		
				
				JSONArray jsonImagens = new JSONArray(js.getString("imagens"));
				for (int i = 0; i < jsonImagens.length(); i++) {
					Imagem im = new Gson().fromJson(jsonImagens.getJSONObject(i).toString(), Imagem.class);								
			    								
					imagens.add(im);
				}	
				if(!servico.getImagens().isEmpty()){
					for (Imagem imagem : servico.getImagens()) {
						imagens.add(imagem);
					}
				}
				logger.info("total de imagens para cadastro "+imagens.size());				
				servico.setImagens(imagens);					
				logger.info("id do servico ao ser alterado "+servico.getId());	
				servicoDao.editar(servico);
				
				
				return Constante.SUCESSO;			
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return Constante.FALHOU;
	}

	@RequestMapping(value = "/pesquisarPorId/{id}", method = RequestMethod.GET,headers="Accept=application/json;charset=utf-8")
	public Usuario pesquisarPorId(@PathVariable(value = "id") long id) 
	{
		logger.info("Metodo pesquisar usuario por ID ");		
		return  dao.pesquisarId(id);
	}
	
	
	@RequestMapping(value = "/verificaAtualizacao/{versao}", method = RequestMethod.GET,headers="Accept=application/text")
	public String verificaAtualizacao(@PathVariable(value = "versao") int versao){
		
        logger.info("Versao da app  "+versao);
		
		URL resource = getClass().getResource("/");
		String path = resource.getPath();
		path = path.replace("WEB-INF/classes/", "");
		path = path + "upload/versao"+(versao+1)+"/app.apk"; 
		
		logger.info("Caminho real da aplicação "+path);
		
		
		   File file = new File(path); // Initialize this to the File path you want to serve.
		   
		 if(file.isFile()){
			 logger.info("Existe dados para atualizar ");
			 return Constante.SUCESSO;
		 }else{
			 logger.info("Não a atualizações");
			 return Constante.FALHOU;
		 }	   				
		
	}
	
	@RequestMapping(value = "/{versao}", method = RequestMethod.GET)
	public InputStreamResource getFile(@PathVariable(value = "versao") int versao) throws IOException  {
	   
         logger.info("Versao da app  "+versao);
		
		URL resource = getClass().getResource("/");
		String path = resource.getPath();
		path = path.replace("WEB-INF/classes/", "");
		path = path + "/upload/versao"+(versao+1)+"/app.apk"; 
		
		logger.info("Caminho real da aplicação "+path);
		
		File file = new File(path);			  

	    InputStreamResource isr = new InputStreamResource(new FileInputStream(file));	    
	    return isr;
	}	
}
