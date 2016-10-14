
$(function() {
	
    $('.validate span').hide();
    /* Required message */
    var requiredMsg = "Campo Requerido!";    
    /*Radio Mensagem*/
    var radioMsg = "Selecione uma opção!";
    /*Checkbox Mensagem*/
    var chkMsg = "Marque uma opção!";
    /* E-mail message */
    var mailMsg = "O e-mail informado é inválido!";
    /* CPF message */
    var cpfMsg = "CPF informado é inválido!";
    /* cnpj message */
    var cnpjMsg = "CNPJ informado é inválido!";
    /* Data message */
    var dataMsg = "Data informada é inválida!";    
    /* Numeric message */
    var numericMsg = "O valor deve ser númerico!";
    /* minlength message */
    var minMsg = "Informe ao menos X caracters!";
    /* maxlength message */
    var maxMsg = "A quantidade máxima é de X caracters!";
    /* Password message */
    var passwordMsg = "Senhas não conferem!";
    /* Telefone message */
    var foneMsg = "O telefone informado é inválido!";    
    
    
   
    $('#totop').click(function(){
   	    $('html, body').animate({scrollTop: 0},'slow');
    });
      
    $(".alert").fadeOut(7000); 
       
    /* Aplicando Placeholder com texto do SPAN */
    $(this).find('input').each(function(){
        $(this).attr('placeholder',$(this).parent().find('span').html())
    });
    
    function bt_lock (){        	
    	
    	$('.validate span').hide();
    };	
    
   
    

     /*Requirido inputs defoco*/    
       $( ".required" ).blur(function() {
		
		
            if ( $(this).hasClass('required') && $.trim( $(this).val() ) == "" ){
                $(this).removeClass('valid').addClass('invalid');                
                $(this).parent().find('span').html(requiredMsg).fadeOut(500).fadeIn(500).fadeOut(500).fadeIn(500);
                 
            }
            else
            {
                $(this).removeClass('invalid').addClass('valid');
                $(this).parent().find('span').fadeOut(500);
                 
            }					
    });	
    
    /* minlength value defoco*/
     $( ".minlength" ).blur(function() {		
		
           if ( $(this).attr('minlength')){
                if($.trim($(this).val()).length < $(this).attr('minlength') ){
                    $(this).removeClass('valid').addClass('invalid');
                    //$(this).select();
                    $(this).parent().find('span').html(minMsg.replace(/X/g,$(this).attr('minlength'))).fadeOut(500).fadeIn(500).fadeOut(500).fadeIn(500);
                     
                }
                else{
                    $(this).parent().find('span').fadeOut(500);
                    $(this).removeClass('invalid').addClass('valid');
                     
                }
            }			
    });	

     /* maxlength value defoco*/
     $( ".maxlength" ).blur(function() {		
		
           if ( $(this).attr('maxlength')  && $(this).hasClass('required') ){
                if($.trim($(this).val()).length > $(this).attr('maxlength') ){
                    $(this).removeClass('valid').addClass('invalid');
                    //$(this).select();
                    $(this).parent().find('span').html(maxMsg.replace(/X/g,$(this).attr('maxlength'))).fadeOut(500).fadeIn(500).fadeOut(500).fadeIn(500);			
                     
                }
                else{
                    $(this).parent().find('span').html('').fadeOut(500).fadeIn(500).fadeOut(500).fadeIn(500);
                    $(this).removeClass('invalid').addClass('valid');
                     
                }
            }		
    });	
     /* password*/
     $( ".confirmsenha" ).blur(function() {    	     	 
    		 if ( $.trim( $('.confirmsenha').val() ) == $.trim( $('.senha').val() )){                                       
    			 $(this).parent().find('span').html('').fadeOut(500).fadeIn(500).fadeOut(500).fadeIn(500);
                 $(this).removeClass('invalid').addClass('valid');
                
            }else{
            	//alert( "pq");
            	 $(this).parent().find('.confirmsenha').removeClass('valid').addClass('invalid');                
                 $(this).parent().find('span').html(passwordMsg).fadeOut(500).fadeIn(500).fadeOut(500).fadeIn(500);
            };
     }); 
     
    /* numerico value defoco
     $( ".numerico" ).blur(function() {		
		
          	 if ( $(this).hasClass('numerico') ){
                var nan = new RegExp(/(^-?\d\d*\.\d*$)|(^-?\d\d*$)|(^-?\.\d\d*$)/);
                if (!nan.test($.trim( $(this).val() ))){
                    $(this).removeClass('valid').addClass('invalid');
                    $(this).parent().find('span').html(numericMsg).fadeOut(500).fadeIn(500).fadeOut(500).fadeIn(500);
               p     $(this).select();
                     
                }
                else{
                    $(this).parent().find('span').fadeOut(500);
                    $(this).removeClass('invalid').addClass('valid');
                     
                }
            }
    });	
   */
   /* valida email*/
     $( ".email" ).blur(function() {		
		if ( $(this).hasClass('email') ){
                var er = new RegExp(/^[A-Za-z0-9_\-\.]+@[A-Za-z0-9_\-\.]{2,}\.[A-Za-z0-9]{2,}(\.[A-Za-z0-9])?/);
                if (!er.test($.trim( $(this).val() ))){
                    $(this).removeClass('valid').addClass('invalid');
                    //$(this).select();
                    $(this).parent().find('span').html(mailMsg).fadeOut(500).fadeIn(500).fadeOut(500).fadeIn(500);
                     
                }
                else{
                    $(this).removeClass('invalid').addClass('valid');
                    $(this).parent().find('span').fadeOut(500);
                     
                }
            } 
          	
    });	

   /* valida cpf*/
     $( ".cpf" ).blur(function() {		
		
           if ( $(this).hasClass('cpf') ){
                var cpf = $(this).val().replace('.','');
                cpf = cpf.replace('.','');
                cpf = cpf.replace('-','');
                while(cpf.length < 11) cpf = "0"+ cpf;
                var expReg = /^0+$|^1+$|^2+$|^3+$|^4+$|^5+$|^6+$|^7+$|^8+$|^9+$/;
                var a = [];
                var b = new Number;
                var c = 11;
                for (i=0; i<11; i++){
                    a[i] = cpf.charAt(i);
                    if (i < 9) b += (a[i] * --c);
                }
                if ((x = b % 11) < 2) {
                    a[9] = 0
                } else {
                    a[9] = 11-x
                }
                b = 0;
                c = 11;
                for (y=0; y<10; y++) b += (a[y] * c--);
                if ((x = b % 11) < 2) {
                    a[10] = 0;
                } else {
                    a[10] = 11-x;
                }
                if ((cpf.charAt(9) != a[9]) || (cpf.charAt(10) != a[10]) || cpf.match(expReg))
                {
                    $(this).removeClass('valid').addClass('invalid');
                    //$(this).select();
                    $(this).parent().find('span').html(cpfMsg).fadeOut(500).fadeIn(500).fadeOut(500).fadeIn(500);
                    
                }
                else{
                    $(this).removeClass('invalid').addClass('valid');
                    $(this).parent().find('span').fadeOut(500);
                }
            } 
          	
    });	
    
   /* valida cep*/
     $( ".cep" ).blur(function() {		
		
            if ( $(elm).hasClass('required') && $(elm).hasClass('cep') ){
				var valcep = $.trim($(this).val().replace('-',''));
				var urlws = 'http://cep.republicavirtual.com.br/web_cep.php?cep='+valcep+'&formato=json';	
				var cepr =  $.ajax({url:urlws,async: false}).responseText;
				console.log(cepr);
				cepr = $.parseJSON(cepr)
				if(cepr.resultado == 0){
					$(this).removeClass('valid').addClass('invalid');
					$(this).select();
					$(this).parent().find('span').html('Cep não encontrado, informe um CEP válido.').fadeOut(500).fadeIn(500).fadeOut(500).fadeIn(500);
												
				}else{
					$(this).parent().find('span').fadeOut(500);
					$(this).removeClass('invalid').addClass('valid');				
				}
            }	
    });	
    
/* valida data*/
     $( ".data" ).blur(function() {		
		
           if ( $(this).hasClass('data') ){
                
                var sdata = $(this).val();
                if(sdata.length!=10)
                {
                    $(this).removeClass('valid').addClass('invalid');
                    $(this).select();
                    $(this).parent().find('span').html(dataMsg).fadeOut(500).fadeIn(500).fadeOut(500).fadeIn(500);
                      
                }
                var data        = sdata;
                var dia         = data.substr(0,2);
                var barra1      = data.substr(2,1);
                var mes         = data.substr(3,2);
                var barra2      = data.substr(5,1);
                var ano         = data.substr(6,4);
                if(data.length!=10||barra1!="/"||barra2!="/"||isNaN(dia)||isNaN(mes)||isNaN(ano)||dia>31||mes>12)
                {
                    $(this).removeClass('valid').addClass('invalid');
                    $(this).select();
                    $(this).parent().find('span').html(dataMsg).fadeOut(500).fadeIn(500).fadeOut(500).fadeIn(500);
                              
                }
                if((mes==4||mes==6||mes==9||mes==11) && dia==31){
                    $(this).removeClass('valid').addClass('invalid');
                    $(this).select();
                    $(this).parent().find('span').html(dataMsg).fadeOut(500).fadeIn(500).fadeOut(500).fadeIn(500);
                    
                }
                if(mes==2 && (dia>29||(dia==29 && ano%4!=0))){
                    $(this).removeClass('valid').addClass('invalid');
                    $(this).select();
                    $(this).parent().find('span').html(dataMsg).fadeOut(500).fadeIn(500).fadeOut(500).fadeIn(500);
                    
                }
                if(ano < 1900)
                {
                    $(this).removeClass('valid').addClass('invalid');
                    //$(this).select();
                    $(this).parent().find('span').html(dataMsg).fadeOut(500).fadeIn(500).fadeOut(500).fadeIn(500);
                    
                }                
                else{
                    $(this).removeClass('invalid').addClass('valid');
                    $(this).parent().find('span').fadeOut(500);
                }
            } 
    });	
    
 });
		
	    




