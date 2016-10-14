package br.com.jortec.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import br.com.jortec.model.Cliente;
import br.com.jortec.servico.HttpConnection;
import br.com.jortec.util.Alerta;
import br.com.jortec.util.Constante;

@Controller
@Scope("request")
public class SistemaBean implements Serializable {

	private long qtdAdm;
	private long qtdUsuario;
	private long qtdOs;
	private long qtdServico;
	

	
	@Autowired
	HttpConnection http;

	@Autowired
	Alerta alerta;

	// Log4j
	final Logger logger = Logger.getLogger(SistemaBean.class);
			
	@PostConstruct
	public void load() {

         String dados = http.getGetHttp(Constante.URL_DADOS+ "estatus");
         logger.info("Dados recebido  "+dados );
         
        try {
			JSONObject jo = new JSONObject(dados);
			
			qtdUsuario = jo.getLong("usuarios");
			qtdAdm     = jo.getLong("administradores");
			qtdOs      = jo.getLong("ordensServico");
			qtdServico = jo.getLong("servicos");						
					
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.getStackTrace();
		}
			
        logger.info("dados carregados us = "+qtdUsuario+" adm = "+qtdAdm+" os = "+qtdOs+" sv = "+qtdServico );
	}

	public long getQtdAdm() {
		return qtdAdm;
	}

	public long getQtdUsuario() {
		return qtdUsuario;
	}

	public long getQtdOs() {
		return qtdOs;
	}

	public long getQtdServico() {
		return qtdServico;
	}

  
}
