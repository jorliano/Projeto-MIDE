package br.com.jortec.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import br.com.jortec.model.Relatorio;
import br.com.jortec.util.Constante;

public class GerarRelatorio {
	
		
	
	public String gerarArquivo(List<Relatorio> lista, String titulo){
		
	
		try {
			
			//Gerar o relatorio
			
			URL resource = getClass().getResource("/");
			String path = resource.getPath();
			path = path.replace("WEB-INF/classes/", "");		
			
			
			Map<String, Object> parametro = new HashMap<String, Object>();
			parametro.put("titulo", titulo);
			
		 	File jasper = new File(path + "relatorio.jasper");			
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasper.getPath(), parametro, new JRBeanCollectionDataSource(lista));
			
			 byte[] b = JasperExportManager.exportReportToPdf(jasperPrint);	  
			 
			 
			    FileOutputStream outPut = new FileOutputStream(path + "relatorioGerado.pdf");			    
				outPut.write(b);
				outPut.flush();
				outPut.close();
				
			return Constante.SUCESSO;
		} catch (JRException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	  return Constante.FALHOU;
	}
}
