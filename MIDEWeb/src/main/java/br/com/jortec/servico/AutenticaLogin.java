package br.com.jortec.servico;

import org.apache.log4j.Logger;
import org.primefaces.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import br.com.jortec.model.Usuario;
import br.com.jortec.util.Alerta;
import br.com.jortec.util.Constante;

import com.google.gson.Gson;

@Service
@Scope("request")
public class AutenticaLogin {
	final Logger logger = Logger.getLogger(AutenticaLogin.class);
	Gson gson = new Gson();
	
	@Autowired
	HttpConnection http;

	public Usuario autenticaLogUsuario(String login, String senha) {
		Usuario usuario = new Usuario();
		usuario.setLogin(login);
		usuario.setSenha(senha);		
		try{		
			
			logger.info("login " + login+ " senha "+senha);
			String resposta = http.getPostHttp(Constante.URL_ADMINISTRADOR+"logar","logar", gson.toJson(usuario));
			logger.info("dados recebidos" + resposta);			
			
			JSONObject jo = new JSONObject(resposta);
			usuario.setId(jo.getLong("id"));
			usuario.setPrimeiroNome(jo.getString("primeiroNome"));
					
			
		}catch(Exception e){
			logger.info("erro ao se autenticar " + e.getMessage());
		}
		
		if (usuario.getId() > 0) {
			return usuario;
		}
		else
			return null;
	}
          
}
