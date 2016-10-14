package br.com.jortec.servico;

import java.io.BufferedReader;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import br.com.jortec.util.Alerta;
import br.com.jortec.util.Constante;

@Service
@Scope("request")
public class HttpConnection implements Serializable{
	private String resposta;

	// POST
	public String getPostHttp(String url, String method, String data) {

		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		String answer = "";

		try {
			ArrayList<NameValuePair> valores = new ArrayList<NameValuePair>();
			valores.add(new BasicNameValuePair(method, data));

			httpPost.setEntity(new UrlEncodedFormEntity(valores));
			HttpResponse resposta = httpClient.execute(httpPost);
			answer = EntityUtils.toString(resposta.getEntity());
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
		resposta = answer;
		return (answer);
	}

	// GET
	public String getGetHttp(String URL) {

		HttpClient httpClient = new DefaultHttpClient();
		HttpGet get = new HttpGet(URL);
		String answer = "";

		try {

			HttpResponse resposta = httpClient.execute(get);
			answer = EntityUtils.toString(resposta.getEntity());
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		resposta = answer;
		return (answer);

	}

	// DELETE
	public String getDeleteHttp(String URL) {

		HttpClient httpClient = new DefaultHttpClient();
		HttpDelete delete = new HttpDelete(URL);
		String answer = "";

		try {

			HttpResponse resposta = httpClient.execute(delete);
			answer = EntityUtils.toString(resposta.getEntity());
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
		resposta = answer;
		return (answer);

	}

	public  void validarRequisição(String metodo){
		Alerta alerta = new Alerta();
		
		if(resposta.equals(Constante.SERVER_SUCESSO)){	
		    alerta.info("Dados "+metodo+" com sucesso ", true);
		}
		else if(resposta.equals(Constante.SERVER_FALHOU)){
			alerta.error("Falhou ao "+metodo+" dados ", true);
		}
		else {
			alerta.fatal("Falhou em se comunicar com o servidor", true);
		}
	}

	
}
