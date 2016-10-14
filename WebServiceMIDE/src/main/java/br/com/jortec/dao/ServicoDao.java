package br.com.jortec.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.jortec.model.Servico;
import br.com.jortec.model.Venda;


@Repository
@Transactional
public class ServicoDao {	
	
	@PersistenceContext
	private EntityManager manager ;

	public void salvar(Servico servico) {					
		manager.persist(servico);					
	}
	
	public void editar(Servico servico) {					
		manager.merge(servico);			
	}
	
	public void deletar(Servico servico) {				
		manager.remove(manager.merge( servico));					
	}	
	
	public Servico pesquisarPorId(long id) {
		try {			
			return manager
					.createQuery("select s from Servico s where id = :id",
							Servico.class).setParameter("id", id).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	
	
	public List listarServicoAberto(int quantidade){
		return  manager.createQuery("SELECT NEW br.com.jortec.model.Servico( s.id, s.usuario_id, s.ordem_servico_id, s.hora, s.encerramento) from Servico s where s.estatus = 'aberto' ", Servico.class)				
				.setFirstResult(quantidade - 10)
				.setMaxResults(quantidade)
				.getResultList();
	}

	public List listarServicoFechado(int quantidade){
		return  manager.createQuery("SELECT NEW br.com.jortec.model.Servico( s.id, s.usuario_id, s.ordem_servico_id, s.hora, s.encerramento) from Servico s where s.estatus = 'fechado' order by id desc", Servico.class)
				.setFirstResult(0)
				.setMaxResults(quantidade)
				.getResultList();
	}
	
	public List listarAll(){
		return  manager.createQuery("select s from Servico s order by id desc ", Servico.class)
				.getResultList();
	}
	
	public List listarPesquisaServicoFechado( long codigo) {
		return  manager.createQuery("select s from Servico s where id = "+codigo+" and estatus = 'fechado' order by id desc ", Servico.class)
				.getResultList();
	}
	
	public List listarDia(int dia, int mes, int ano){
		return  manager.createQuery("select s from Servico s where day(s.dataEncerramento)   =:dia and " 
																+ "month(s.dataEncerramento) =:mes and "
																+ "year(s.dataEncerramento)  =:ano  order by dataEncerramento desc ", Servico.class)
				                                                  .setParameter("dia", dia)
				                                                  .setParameter("mes", mes)
				                                                  .setParameter("ano", ano)
				                                                  .setFirstResult(0)
				                                                  .setMaxResults(20)
				.getResultList();
	}
	
	 public int quantidade()  {  
         return (int) manager
					.createQuery("select count(*) from  Servico id" ).getSingleResult();  
     }

	
}
