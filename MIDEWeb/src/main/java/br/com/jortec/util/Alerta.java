package br.com.jortec.util;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author Jorliano
 */
@Component
@Scope("request")
public class Alerta implements Serializable {
	public static void info(String mensagem, boolean time) {
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, mensagem, null));

		time(time);
	}	

	public void warn(String mensagem, boolean time) {
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_WARN, mensagem, null));
		time(time);
	}

	public void error(String mensagem, boolean time) {
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_ERROR, mensagem, null));
		time(time);
	}
	
	public void fatal(String mensagem, boolean time) {
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_FATAL, mensagem, null));
		time(time);
	}

	private static void time(boolean time) {
		// aumentar tempo de requisição da menssage
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext externalContext = ctx.getExternalContext();
		Flash flash = externalContext.getFlash();
		flash.setKeepMessages(time);
	}

	
}
