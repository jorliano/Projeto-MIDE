package br.com.jortec.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.servlet.http.Part;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import br.com.jortec.model.Usuario;

import br.com.jortec.servico.ImagemValidator;

@Controller
@Scope("session")
public class UsuarioLogado implements Serializable{
   
	private Usuario usuario;		
	private String img;
	private int novasNotificacoes;
	
	
	public void logarUsuario(Usuario usuario){
		this.usuario = usuario;
		img = "/imagens/adm/adm.png";
	}
	
	
	public String desloga() {
 		this.usuario = null;			
		return "/index.xhtml?faces-redirect=true";				
		
	}	
		
	public boolean isLogado(){
		if(usuario != null )
			return true;
		return false;
	}
	
	public int getNovasNotificacoes() {
		return novasNotificacoes;
	}
	
	public void setNovasNotificacoes(int novasNotificacoes) {
		this.novasNotificacoes = novasNotificacoes;
	}
	
	public Usuario getUsuario() {
		return usuario;
	}		

	public String getImg() {
		return img;
	}
	
	
}
