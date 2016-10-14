package br.com.jortec.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.jortec.model.Chat;
import br.com.jortec.model.Servico;


@Transactional
@Repository
public class ChatDao {
	
	@PersistenceContext
	private EntityManager manager ;

	public void salvar(Chat chat) {			
		manager.persist(chat);					
	}	
	
	public void editar(Chat chat) {				
		manager.merge(chat);					
	}	
	
	public void deletar(Chat chat) {				
		manager.remove(manager.merge(chat));				
	}		
	
	
	public List listar(String remetente, String destinatario){				
		return  manager.createQuery("select c from Chat c where remetente = :remetente and destinatario = :destinatario or remetente = :destinatario and destinatario = :remetente", Chat.class)
                .setParameter("remetente", remetente)
                .setParameter("destinatario", destinatario)
                .getResultList();
	}
	
	public List<Chat> listarMais(long id, String remetente, String destinatario) {
		return  manager.createQuery("select c from Chat c where  id > :id and (remetente = :remetente and destinatario = :destinatario  )", Chat.class)
									.setParameter("id", id)
									.setParameter("remetente", remetente)
					                .setParameter("destinatario", destinatario)
					                .getResultList();
	}
	
	public List listarTodos(String remetente){				
		return  manager.createQuery("select a from Chat a where remetente = :remetente ", Chat.class)
                .setParameter("remetente", remetente)                
                .getResultList();
	}

	public long mensagesNaoLidas(String remetente) {
		return  (long) manager.createQuery("select count(*) from Chat  where remetente = :remetente and estatus = 'desmarcado'")
                .setParameter("remetente", remetente)                
                .getSingleResult();
	}

	public Chat pesquisarPorId(long id) {
		return  manager.createQuery("select a from Chat a where id = :id ", Chat.class)
                .setParameter("id", id)                
                .getSingleResult();
	}

	
    
	
	
	
}
