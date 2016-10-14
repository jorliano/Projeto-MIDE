package br.com.jortec.service;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class conteudoDaMensage implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public List<String> registration_ids;
	public Map<String, String> data;

	public void addRegId(String regId) {
		if (registration_ids == null)
			registration_ids = new LinkedList<String>();
		registration_ids.add(regId);
	}

	public void createData(int natureza, String id, String codigo, String nomeCliente, String contato
			, String endereco, String bairro, String cidade, String telefone, String autenticacao
			, String dadosAcesso, String descricao, String tipo, String dataCampo, String hora) {
		
		if (data == null)
			data = new HashMap<String, String>();

				
		data.put("natureza", String.valueOf(natureza));
		data.put("id",id);
		data.put("codigo", codigo);
		data.put("nomeCliente", nomeCliente);
		data.put("contato", contato);
		data.put("endereco", endereco);
		data.put("bairro", bairro);
		data.put("cidade", cidade);
		data.put("telefone", telefone);
		data.put("autenticacao", autenticacao);
		data.put("dadosAcesso", dadosAcesso);
		data.put("descricao", descricao);
		data.put("tipo", tipo);
		data.put("data", dataCampo);		
		data.put("hora", hora);		
	   
	   
	}
	
	public void createDataRemover(int natureza, String id) {		
		
		data = new HashMap<String, String>();		
		data.put("natureza", String.valueOf(natureza));
		data.put("id",id);	   
	}
	
   public void createDataChate(int natureza, String remetente, String destinatario, String mensage, String hora, String estatus) {		
		
		data = new HashMap<String, String>();		
		data.put("natureza", String.valueOf(natureza));
		data.put("remetente",remetente);	   
		data.put("destinatario",destinatario);	   
		data.put("mensage",mensage);	   
		data.put("hora",hora);	   
		data.put("estatus",estatus);	   
		   
	}
}
