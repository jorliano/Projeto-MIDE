package br.com.jortec.servico;

import java.util.Properties;

/*import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;*/

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class Email
{  
	/*@Autowired
	SistemaDao dao;
	
	
	  DadosdeEmail email = new DadosdeEmail();
	
      public boolean enviarEmail(String menssage ,String titulo){
    	  
    	  email = dao.busca();
    	  
    	  if(email != null)
    	  {	  
    	    Properties props = new Properties();
          // Parâmetros de conexão com servidor Gmail 
		 
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", "smtp.gmail.com");
			props.put("mail.smtp.port", "587");

            Session session = Session.getDefaultInstance(props,
                      new javax.mail.Authenticator() {
                           protected PasswordAuthentication getPasswordAuthentication()
                           {
                                 return new PasswordAuthentication(email.getRemetente(), email.getSenha());
                           }
                      });
             
            System.out.println("Email : "+email.getRemetente()+" senha :"+email.getSenha());
            // Ativa Debug para sessão 
             session.setDebug(true);

          try {

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(email.getRemetente())); //Remetente

                Address[] toUser = InternetAddress //Destinatário(s)
                           .parse(email.getDestinatario());  

                message.setRecipients(Message.RecipientType.TO, toUser);
                message.setSubject(titulo);//Assunto
                message.setText(menssage);
                //Método para enviar a mensagem criada
                Transport.send(message);
                return true;
               
           } catch (MessagingException e) {
        	   return false;                           
          }
    	 }
    	  else{
    		  return false;
    	  }	  
     }*/
}

