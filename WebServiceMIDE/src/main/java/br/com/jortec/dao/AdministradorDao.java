package br.com.jortec.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.jortec.model.Administrador;


@Transactional
@Repository
public class AdministradorDao {
	
	@PersistenceContext
	private EntityManager manager ;

	public void salvar(Administrador adm) {			
		manager.persist(adm);					
	}	
	
	public void editar(Administrador adm) {				
		manager.merge(adm);					
	}	
	
	public void deletar(Administrador adm) {				
		manager.remove(manager.merge(adm));				
	}	
	
	public Administrador logar(String login, String senha) {

		try {
			return manager
					.createQuery("select a from Administrador a where login = :login and senha = :senha",
							Administrador.class).setParameter("login", login).setParameter("senha", senha)
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	
	public Administrador pesquisarId(long id) {

		try {
			return manager
					.createQuery("select a from Administrador a where id = :id",
							Administrador.class).setParameter("id", id)
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	
	public List listar(int quantidade){
		final Logger logger = Logger.getLogger(Administrador.class);
		logger.info("Listando adms entytitiManeger "+manager);
		
		
		return  manager.createQuery("select a from Administrador a order by id desc", Administrador.class)
				.setFirstResult(0)
				.setMaxResults(quantidade)
				.getResultList();
	}
    
	public List listarPesquisa( String nome) {
		return  manager.createQuery("select a from Administrador a where primeiroNome like '"+nome+"%' ", Administrador.class)
				.getResultList();
	}
	
	
}
