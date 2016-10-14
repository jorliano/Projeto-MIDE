package br.com.jortec.dao;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Transactional
@Repository
public class DadosDao {
	
	
	@PersistenceContext
	private EntityManager manager;

	 public long pesquisaEstatus(int mes, int ano, String tipo )  {  
         return (long) manager
					.createQuery("select count(*) from  OrdemServico where month(data)  =:mes and "
							                                             + "year(data)  =:ano and "
							                                             + "estatus = 'fechada' and "
							                                             + "tipo =:tipo ")
							                                             .setParameter("mes", mes)
							                                             .setParameter("ano", ano)
							                                             .setParameter("tipo", tipo)
							                                             .getSingleResult();  
     }
	
	 public long pesquisaGraficoUsuarioDiario(int dia, int mes, int ano, String encerramento , long idUsuario)  {  
         return (long) manager
					.createQuery("select count(*) from  Servico where  day(dataEncerramento) =:dia and "
							                                        + "month(dataEncerramento)  =:mes and "
							                                        + "year(dataEncerramento)  =:ano and "
							                                        + "encerramento =:encerramento and "
							                                        + "usuario_id =:idUsuario ")
							                                        .setParameter("dia", dia)
							                                        .setParameter("mes", mes)
							                                        .setParameter("ano", ano)
							                                        .setParameter("encerramento", encerramento)
							                                        .setParameter("idUsuario", idUsuario)
							                                        .getSingleResult();  
     }
	 
	 final Logger logger = Logger.getLogger(DadosDao.class);
	
	 public long pesquisaGraficoUsuarioMensal(int mes, int ano, String encerramento , long idUsuario)  {  
		 logger.info("Dados para pesquisa mes "+mes+" ano "+ano+" encerramento "+encerramento+ " id "+idUsuario);
         return (long) manager
					.createQuery("select count(*) from  Servico where month(dataEncerramento)  =:mes and "
							                                        + "year(dataEncerramento)  =:ano and "
							                                        + "encerramento =:encerramento and "
							                                        + "usuario_id =:idUsuario ")							                                        
							                                        .setParameter("mes", mes)
							                                        .setParameter("ano", ano)
							                                        .setParameter("encerramento", encerramento)
							                                        .setParameter("idUsuario", idUsuario)
							                                        .getSingleResult();  
     }
	
	
	 public long qtdUsuario()  {  
         return (long) manager
					.createQuery("select count(*) from  Usuario ").getSingleResult();  
     }
	 
	 public long qtdAdm()  {  
         return (long) manager
					.createQuery("select count(*) from  Administrador ").getSingleResult();  
     }
	 
	 public long qtdOs( )  {  
         return (long) manager
					.createQuery("select count(*) from  OrdemServico ").getSingleResult();  
     }
	
	 public long qtdServico()  {  
         return (long) manager
					.createQuery("select count(*) from  Servico ").getSingleResult();  
     }

	 //Relatorio
	public long servicosConcluidos(long id, int dia, int mes, int ano) {
		String query = "select count(*) from  Servico where usuario_id = "+id+" and "
						                                      + "month(dataEncerramento) = "+mes+" and "
						                                      + "year(dataEncerramento)  = "+ano+" and "
						                                      + "encerramento = 'Encerrado com sucesso'";
		
	   if(dia > 0){
		    query = "select count(*) from  Servico where usuario_id = "+id+" and "
				   + "day(dataEncerramento) = "+dia+" and "
                   + "month(dataEncerramento) = "+mes+" and "
                   + "year(dataEncerramento) = "+ano+" and "
                   + "encerramento = 'Encerrado com sucesso'";  
	   }
		
	return (long) manager
				.createQuery(query).getSingleResult();  
	}
	
	public long servicosPendentes(long id, int dia, int mes, int ano) {
		String query = "select count(*) from  Servico where usuario_id = "+id+" and "
						                                      + "month(dataEncerramento) = "+mes+" and "
						                                      + "year(dataEncerramento)  = "+ano+" and "
						                                      + "encerramento = 'Pendente'";
		
	   if(dia > 0){
		    query = "select count(*) from  Servico where usuario_id = "+id+" and "
				   + "day(dataEncerramento) = "+dia+" and "
                   + "month(dataEncerramento) = "+mes+" and "
                   + "year(dataEncerramento) = "+ano+" and "
                   + "encerramento = 'Pendente'";  
	   }
		
	return (long) manager
				.createQuery(query).getSingleResult();  
	}
	
	public Double valorRecebido(long id, int dia, int mes, int ano) {
		String query = "select SUM(v.pagamento) from  Servico s JOIN s.venda v where usuario_id = "+id+" and "
						                                      + "month(dataEncerramento) = "+mes+" and "
						                                      + "year(dataEncerramento)  = "+ano+" and "
						                                      + "encerramento = 'Encerrado com sucesso'" ;  
		
	   if(dia > 0){
		    query = "select SUM(v.pagamento) from  Servico s  JOIN s.venda v where usuario_id = "+id+" and "
				   + "day(dataEncerramento) = "+dia+" and "
                   + "month(dataEncerramento) = "+mes+" and "
                   + "year(dataEncerramento) = "+ano+" and "
                   + "encerramento = 'Encerrado com sucesso'";  
	   }
		
	   try {
		   return (double) manager.createQuery(query).getSingleResult();  
		} catch (NoResultException | NullPointerException e) {
			e.getStackTrace();
			return 0.0;
	   }
	   
	
	}
	
	public long roteadoVendido(long id, int dia, int mes, int ano) {
		String query = "select SUM(v.quantidade) from  Servico s  JOIN s.venda v where usuario_id = "+id+" and "
																	                 + "month(dataEncerramento) = "+mes+" and "
																	                 + "year(dataEncerramento)  = "+ano+" and "
																	                 + "encerramento = 'Encerrado com sucesso' ";  
		
	   if(dia > 0){
		    query = "select SUM(v.quantidade) from  Servico s  JOIN s.venda v where usuario_id = "+id+" and "
																				 + "day(dataEncerramento) = "+dia+" and "
																				 + "month(dataEncerramento) = "+mes+" and "
																				 + "year(dataEncerramento) = "+ano+" and "
																				 + "encerramento  = 'Encerrado com sucesso' ";  
	   }
		
	
		 try {
			  return (long) manager.createQuery(query).getSingleResult();  
			} 
		 catch (NoResultException | NullPointerException e) {
				e.getStackTrace();
				return 0;
		 }
	}
}
