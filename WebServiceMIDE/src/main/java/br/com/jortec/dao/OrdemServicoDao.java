package br.com.jortec.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.jortec.model.OrdemServico;
import br.com.jortec.model.Servico;


@Repository
@Transactional
public class OrdemServicoDao {
	
	
	@PersistenceContext
	private EntityManager manager;

	public void salvar(OrdemServico os) {		
		manager.persist(os);				
	}	
	
	public void editar(OrdemServico os) {				
		manager.merge(os);						
	}	
	
	public void deletar(OrdemServico os) {					
		manager.remove(manager.merge(os));					
	}		
	
	public OrdemServico pesquisarId(long id) {

		try {
			return manager
					.createQuery("select o from OrdemServico o where id = :id",
							OrdemServico.class).setParameter("id", id)
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	
	public List listar(int quantidade){
		return  manager.createQuery("select o from OrdemServico o order by id desc", OrdemServico.class)
				.setFirstResult(0)
				.setMaxResults(quantidade)
				.getResultList();
	}

	public List listarOsAbertas() {
		return  manager.createQuery("select o from OrdemServico o where estatus = 'aberta' order by id desc", OrdemServico.class)
				.getResultList();
	}
	
	public List listarPesquisa( String nome) {
		return  manager.createQuery("select a from OrdemServico a where nomeCliente like '"+nome+"%' ", OrdemServico.class)
				.getResultList();
	}

	public boolean confirmeOs(long id) {

		try {
			List lista = manager.createQuery("select o from Servico o where ordem_servico_id = :id",Servico.class)
					                                                                      .setParameter("id", id)
					                                                                      .getResultList();
			if(!lista.isEmpty())
				return true;
			
			return false;
			
		} catch (NoResultException e) {
			return false;
		}
	}

}
