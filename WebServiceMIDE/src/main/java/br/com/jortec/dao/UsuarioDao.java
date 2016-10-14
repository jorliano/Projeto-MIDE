package br.com.jortec.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.jortec.model.Usuario;


@Repository
@Transactional
public class UsuarioDao {
	
	@PersistenceContext
	private EntityManager manager;

	public void salvar(Usuario usuario) {				
		manager.persist(usuario);					
	}	
	
	public void editar(Usuario usuario) {				
		manager.merge(usuario);						
	}	
	
	public void deletar(Usuario usuario) {				
		usuario.setEstatus("inativo");
					
		//manager.remove(usuario);
		manager.merge(usuario);			
			
	}	
	
	public Usuario logar(String login, String senha) {

		try {
			return manager
					.createQuery("select u from Usuario u where login = :login and senha = :senha and estatus <> 'inativo'",
							Usuario.class).setParameter("login", login).setParameter("senha", senha)
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	
	public Usuario pesquisarId(long id) {

		try {
			return manager
					.createQuery("select u from Usuario u where id = :id",
							Usuario.class).setParameter("id", id)
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	
	public List listar(int quantidade){
		return  manager.createQuery("select u from Usuario u order by estatus,id ", Usuario.class)
				.setFirstResult(0)
				.setMaxResults(quantidade)
				.getResultList();
	}

	public List<Usuario> listarUsuarioAtivos() {
		return  manager.createQuery("select u from Usuario u where registroGcm IS NOT NULL and estatus='ativo' order by primeiroNome", Usuario.class)
				.getResultList();
	}
	
	public List listarPesquisa( String nome) {
		return  manager.createQuery("select u from Usuario u where primeiroNome like '"+nome+"%' ", Usuario.class)
				.getResultList();
	}

	public Usuario pesquisarPorNome(String nome) {
		try {
			return manager
					.createQuery("select u from Usuario u where CONCAT(primeiroNome,' ',sobreNome) = :nome",
							Usuario.class).setParameter("nome", nome)
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

}
