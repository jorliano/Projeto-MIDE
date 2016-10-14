package br.com.jortec.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
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

import br.com.jortec.dao.UsuarioDao;
import br.com.jortec.model.Relatorio;
import br.com.jortec.service.GerarRelatorio;


@RestController 
@Scope("request")
@RequestMapping(value = "/download")
public class Download {

	final Logger logger = Logger.getLogger(Download.class);

	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ResponseEntity<InputStreamResource> app() throws IOException  {
	   
		URL resource = getClass().getResource("/");
		String path = resource.getPath();
		path = path.replace("WEB-INF/classes/", "");
		path = path + "mide.apk";
		
		logger.info("Caminho real da aplicação "+path);				
		File file = new File(path);
		logger.info("Tamanho do file "+file.length());	

	    HttpHeaders respHeaders = new HttpHeaders();	   
	    respHeaders.setContentLength(file.length());
	    respHeaders.setContentDispositionFormData("attachment", "mide.apk");
	    respHeaders.setContentType(MediaType.parseMediaType("application/octet-stream"));

	    InputStreamResource isr = new InputStreamResource(new FileInputStream(file));
	    return new ResponseEntity<InputStreamResource>(isr, respHeaders, HttpStatus.OK);
	}	
}
