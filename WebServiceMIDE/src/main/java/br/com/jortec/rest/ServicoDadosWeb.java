package br.com.jortec.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.jortec.dao.DadosDao;
import br.com.jortec.dao.UsuarioDao;
import br.com.jortec.model.Relatorio;
import br.com.jortec.model.Usuario;
import br.com.jortec.service.GerarRelatorio;


@RestController 
@Scope("request")
@RequestMapping(value = "/dados")
public class ServicoDadosWeb {

	final Logger logger = Logger.getLogger(ServicoDadosWeb.class);		
	
	@Autowired
	DadosDao dao;
	
	@Autowired
	UsuarioDao usuarioDao; 
	
	String[] meses = {"janeiro","fevereiro","março","abril","mail","junho","julho","agosto","setembro","outubro","novembro","dezembro"}; 

	@RequestMapping(value = "/estatus", method = RequestMethod.GET, headers="Accept=application/json;charset=utf-8")
	public String estatus() {
		logger.info("Metodo estatus chamado dao "+dao);
		
		JSONObject jo = new JSONObject();		
		
		try {
			
			jo.put("usuarios", dao.qtdUsuario());
			jo.put("administradores", dao.qtdAdm());
			jo.put("ordensServico", dao.qtdOs());
			jo.put("servicos", dao.qtdServico());
			
			
		} catch (JSONException e) {
			logger.info("erro ao pesquisar estatus ");
		}
		
		return jo.toString();
	}
	
	@RequestMapping(value = "/graficoGeral", method = RequestMethod.GET,headers="Accept=application/json;charset=utf-8")
	public String graficoGeral() {
		logger.info("Metodo graficoGeral chamado ");
		 
		int semestre = 7 , count = 1;
		
		JSONArray ja = new JSONArray();
		
		Calendar c = Calendar.getInstance();
		if(c.get(Calendar.MONTH) > 6){
			semestre = 13;
			count = 6;
		}			
			
		for (int i = count; i < semestre; i++) {
			JSONObject jo = new JSONObject();
				
			try {
					
					jo.put("mes", meses[i - 1]);				
					jo.put("instalacao", dao.pesquisaEstatus(i, c.get(Calendar.YEAR), "instalacao"));
					jo.put("manutencao", dao.pesquisaEstatus(i, c.get(Calendar.YEAR), "manutencao"));
					jo.put("recolhimento", dao.pesquisaEstatus(i, c.get(Calendar.YEAR), "recolhimento"));
					
					ja.put(jo);
				
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
		return ja.toString();
	}
	
	@RequestMapping(value = "/graficoUsuario/{periodo}", method = RequestMethod.GET,headers="Accept=application/json;charset=utf-8")
	public String graficoUsuario(@PathVariable(value = "periodo") String periodo) {
		logger.info("Metodo graficoUsuario chamado ");
		
		List<Usuario> usuariosAtivos = usuarioDao.listarUsuarioAtivos();
		
		JSONArray ja = new JSONArray();
		
		Calendar c = Calendar.getInstance();
		
		
		for (int i = 0; i < usuariosAtivos.size(); i++) {
			JSONObject jo = new JSONObject();
			
			try {
				
				jo.put("nome", usuariosAtivos.get(i).getPrimeiroNome());
				
				if(periodo.equals("dia")){
					
					jo.put("concluido", dao.pesquisaGraficoUsuarioDiario(c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH )  + 1, c.get(Calendar.YEAR), "Encerrado com sucesso", usuariosAtivos.get(i).getId()));
					jo.put("pendente", dao.pesquisaGraficoUsuarioDiario( c.get(Calendar.DAY_OF_MONTH) , c.get(Calendar.MONTH ) + 1, c.get(Calendar.YEAR), "Pendente", usuariosAtivos.get(i).getId()));
					jo.put("cancelado", dao.pesquisaGraficoUsuarioDiario( c.get(Calendar.DAY_OF_MONTH) , c.get(Calendar.MONTH ) + 1, c.get(Calendar.YEAR), "Cancelada", usuariosAtivos.get(i).getId()));
				}
				else{
					jo.put("concluido", dao.pesquisaGraficoUsuarioMensal(c.get(Calendar.MONTH ) + 1, c.get(Calendar.YEAR), "Encerrado com sucesso", usuariosAtivos.get(i).getId()));
					jo.put("pendente", dao.pesquisaGraficoUsuarioMensal( c.get(Calendar.MONTH ) + 1, c.get(Calendar.YEAR), "Pendente", usuariosAtivos.get(i).getId()));
					jo.put("cancelado", dao.pesquisaGraficoUsuarioMensal(  c.get(Calendar.MONTH ) + 1, c.get(Calendar.YEAR), "Cancelada", usuariosAtivos.get(i).getId()));
				}										
				 
				ja.put(jo);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return ja.toString();
	}
	
	@RequestMapping(value = "/relatorio", method = RequestMethod.POST, headers="Accept=application/json;charset=utf-8")
	public  String relatorio(@RequestParam(value="relatorio") String relatorio) {
			   
		List<Relatorio> lista = new ArrayList<Relatorio>();
		 
		String titulo = "relatorio";
		String periodo = "mes";
		String referente = "totods";
		int dia, mes = 0, ano;
		
		logger.info("dados recebidos no relatorio "+relatorio);
		
		//Pegar os dados	
			try {
				
				JSONObject jo = new JSONObject(relatorio);
				periodo = jo.getString("periodo");
				referente = jo.getString("referencia");
				dia = Integer.parseInt(jo.getString("dia"));
				ano = Integer.parseInt(jo.getString("ano"));
				
				for (int i = 0; i < meses.length; i++) {
					if(meses[i].equals(jo.getString("mes"))){
						mes  = i +1;
					}
				}					
				 
				List<Usuario> listaUsuario = usuarioDao.listarUsuarioAtivos();
				if(periodo.equals("mes")){
					dia = 0;
					titulo = "Relatorio do mês de "+meses[mes - 1 ]+" de "+ano;
				}else{
					titulo = "Relatorio do dia "+dia+" de "+meses[mes - 1]+" de "+ano;
				}
				
				
				
				if(referente.equals("todos")){
						for (int i = 0; i < listaUsuario.size(); i++) {
							Relatorio r = new Relatorio();	
							r.setNome(listaUsuario.get(i).getPrimeiroNome()+" "+listaUsuario.get(i).getSobreNome());
							r.setConcluidos(dao.servicosConcluidos(listaUsuario.get(i).getId(), dia, mes, ano));
							r.setPendentes(dao.servicosPendentes(listaUsuario.get(i).getId(), dia, mes, ano));
							r.setPagamento(dao.valorRecebido(listaUsuario.get(i).getId(), dia, mes, ano));
							r.setQuantidade(dao.roteadoVendido(listaUsuario.get(i).getId(), dia, mes, ano));
							
							lista.add(r);
						}	
						
					}else{
						for (int i = 0; i < listaUsuario.size(); i++) {
							
							String nome = listaUsuario.get(i).getPrimeiroNome()+" "+listaUsuario.get(i).getSobreNome();
							if(nome.equals(referente)){
								Relatorio r = new Relatorio();	
								r.setNome(listaUsuario.get(i).getPrimeiroNome()+" "+listaUsuario.get(i).getSobreNome());
								r.setConcluidos(dao.servicosConcluidos(listaUsuario.get(i).getId(), dia, mes, ano));
								r.setPendentes(dao.servicosPendentes(listaUsuario.get(i).getId(), dia, mes, ano));
								r.setPagamento(dao.valorRecebido(listaUsuario.get(i).getId(), dia, mes, ano));
								r.setQuantidade(dao.roteadoVendido(listaUsuario.get(i).getId(), dia, mes, ano));
								
								lista.add(r);
							}
							
						}		
					}
				
	           
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
		
		return new GerarRelatorio().gerarArquivo(lista, titulo);
	}
	
	@RequestMapping(value = "/printRelatorio", method = RequestMethod.GET,produces="application/pdf")
	public ResponseEntity<InputStreamResource> printRelatorio() throws IOException  {
	   
		URL resource = getClass().getResource("/");
		String path = resource.getPath();
		path = path.replace("WEB-INF/classes/", "");		
		path = path + "relatorioGerado.pdf";
		
		File pdfFile = new File(path);

	    HttpHeaders respHeaders = new HttpHeaders();	   
	    respHeaders.setContentLength(pdfFile.length());
	    respHeaders.setContentDispositionFormData("attachment", "relatorio.pdf");

	    InputStreamResource isr = new InputStreamResource(new FileInputStream(pdfFile));	    
	    return new ResponseEntity<InputStreamResource>(isr, respHeaders, HttpStatus.OK);
	}
	
	
}
