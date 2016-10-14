package br.com.jortec.servico;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.Normalizer.Form;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.swing.ImageIcon;

import org.apache.log4j.Logger;

public class ImagemValidator  {
	// Log4j
	static Logger logger = Logger.getLogger(ImagemValidator.class);

	public  String caregarImagem(String imgem, String caminho) {
		logger.info("caregarImagem  chamado ");
		try {
			if (imgem.length() > 10) {				

				// Converte a string em bytes
				
				byte[] imagem = new sun.misc.BASE64Decoder().decodeBuffer(imgem);					
				
				ServletContext servletContext = (ServletContext) FacesContext
						.getCurrentInstance().getExternalContext()
						.getContext();

				FileOutputStream outPut = new FileOutputStream(
						servletContext.getRealPath("") + caminho);
				outPut.write(imagem);
				outPut.flush();
				outPut.close();
				logger.info("imgem da imagem exibidos "+servletContext.getRealPath("") + caminho);				
				
				
				return caminho;
			}else{
				logger.info("Fundo branco chamado "+caminho);
				return "imagens/fundo_branco.jpg";
			}
		} catch (FileNotFoundException e) {
			logger.info("erro FileNotFoundException "+e.getMessage());
		} catch (IOException e) {
			logger.info("erro IOException "+e.getMessage());
		}
		return "imagens/fundo_branco.jpg";
	}

	
}
