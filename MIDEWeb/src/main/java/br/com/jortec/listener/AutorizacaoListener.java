package br.com.jortec.listener;




import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import br.com.jortec.controller.UsuarioLogado;

public class AutorizacaoListener implements PhaseListener{

	@Override
	public void afterPhase(PhaseEvent event) {
		FacesContext ctx = event.getFacesContext();
		String paginaAcessada = ctx.getViewRoot().getViewId();
		
		System.out.println("pagina acessada : "+paginaAcessada);
		if("/index.xhtml".equals(paginaAcessada)){
			System.out.println("estou na mesma pagina");
			return;                                                                                                                                  
		}
				
		UsuarioLogado usuarioLogado =ctx.getApplication().
				evaluateExpressionGet(ctx,"#{usuarioLogado}", UsuarioLogado.class);
	
	   if(!usuarioLogado.isLogado()){
	    	NavigationHandler handler = ctx.getApplication().getNavigationHandler();
	    	handler.handleNavigation(ctx, null, "/index?faces-redirect=true");	    	
	    	
	    	ctx.renderResponse();
	    }
	    
	    
	}

	@Override
	public void beforePhase(PhaseEvent event) {
		// TODO Auto-generated method stub
		
	}

	//intercepitar na primeira fase do jsf
	@Override
	public PhaseId getPhaseId() {
		
		return PhaseId.RESTORE_VIEW;
	}

}
