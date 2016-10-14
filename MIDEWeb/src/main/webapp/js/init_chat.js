/**
 * Autor Jorliano
 */

$(document).ready(function(){
	  
	           url = "http://localhost/WebServiceMIDE/chat/listarUsuario";   		          	          	        
		       $(function(){
					carregarDados(url);
				});
				
				 function carregarDados(url){	
					 $.getJSON(url, function(data) {
					   console.log(data);   
					                          
					   
					   $.each(data , function(i, v) {
						   
						 var op = "";					        
					         op +=' <div class="media-status">';
					         op +='<span class="badge badge-success">'+v.mensages+'</span>';
					         op +='</div>';					         
					         op +='<div class="media-body">';
					         op +='<h4 class="media-heading">'+v.nome+'</h4>';					        
					         op +='<div class="media-heading-sub">'+v.cargo+' </div>';
					         op +='</div>';
					        
			        					          
						      		         
					         $('.todos').show();   
					         $('#item'+i).show();
					         $('#item'+i).append(op);
					     });				  			   					   
		          });	
				 
			}
				 
				 $('.users li').each(function(){
					 $(this).hide();
				 });
				 
				
	});

 var id = 0, contador = 0;
 var getListaMenssages = function(a,url){	 
	   
	   if(id > 0){
		   url = url+id;
	   }
	   
	 
	   $.get(url, function(data) {
		   console.log(data);   
		   
		   var e=a.find(".page-quick-sidebar-chat-user-messages");
		   var s=function(i,a,e,t,r){
      		var s="";
      		return s+='<div class="post '+i+'">',
      		       s+='<i class="avatar icon-user alt=""/>',
      		       s+='<div class="message">',
      		       s+='<span class="arrow"></span>',
      		       s+='<label class="name">'+t+'</label>&nbsp;',
      		       s+='<span class="datetime">'+a+"</span>",
      		       s+='<span class="body">',s+=r,
      		       s+="</span>",s+="</div>",
      		       s+="</div>"}
      		       
		   if(data.length > 0  ){
			   if(data[data.length - 1].id > id ){
				   id = data[data.length - 1].id;
				   console.log("id ultimo "+id+" destinatario "+data[data.length - 1].destinatario);
		      }
	       }
		   
		   $.each(data , function(i, v) {  			   			  
			   
			   if(v.remetente == "sistema"){
				   c=s("out",v.hora,"avatar3",v.remetente,v.mensage);					                    		     
  		       e.append(c);
			   }
			   else{
				   c=s("in",v.hora,"avatar2",v.remetente,v.mensage);					                    		     
  		       e.append(c);
			   }
			 
		     });	
		   
		  				   
		  
     });	
	
	   setTimeout(temporizador,5000);			
	   
	    console.log("antes da função ");   
	   
	   
	    				        	    
	    function temporizador() {
			  
	    	   console.log("depois da função ");   
	    	 
	    	   console.log(a.hasClass( "page-quick-sidebar-content-item-shown" ));   
	    	   contador ++;
			   
			   if(a.hasClass( "page-quick-sidebar-content-item-shown" ) && contador < 30 ){
				   console.log("repetir a função");
				   url = "http://localhost/WebServiceMIDE/chat/listarMais/";  
				   
				   //alert("Função de tempo chamada");
				    getListaMenssages(a,url);
				    //setTimeout(temporizador,10000);
			   }else{
				   id = 0;
			   }
			   
			  
			  
		}
 }



var QuickSidebar=function(){
	
	
	var destinatario;	
	var hora;
	var contador = 0;
	
	var i=function(){
		$(".dropdown-quick-sidebar-toggler a, .page-quick-sidebar-toggler, .quick-sidebar-toggler").click(function(i){
			$("body").toggleClass("page-quick-sidebar-open")
		})},
		 
	    a=function(){
			  var i=$(".page-quick-sidebar-wrapper"),
			      a=i.find(".page-quick-sidebar-chat"),
			      e=function(){
				     var e,t=i.find(".page-quick-sidebar-chat-users");
				         e=i.height()-i.find(".nav-tabs").outerHeight(!0),
				           App.destroySlimScroll(t),				           
				           t.attr("data-height",e),
				           App.initSlimScroll(t);
				           var r=a.find(".page-quick-sidebar-chat-user-messages"),
				           s=e-a.find(".page-quick-sidebar-chat-user-form").outerHeight(!0);
				           s-=a.find(".page-quick-sidebar-nav").outerHeight(!0),App.destroySlimScroll(r),
				           r.attr("data-height",s),
				           App.initSlimScroll(r)};e(),
				           App.addResizeHandler(e),
				           i.find(".page-quick-sidebar-chat-users .media-list > .media").click(function(){				        	   
				            	 				            	 
				                destinatario = $(this).find('.media-heading').html().trim();
				                console.log("chat chamado "+ $(this).find('.media-heading').html());		
				                
				                $( ".post" ).remove();				           	 
				         	    url = "http://localhost/WebServiceMIDE/chat/listar/"+destinatario+"/sistema";   
				            	 
				                getListaMenssages(a,url);			        	   				        	   
				        	    a.addClass("page-quick-sidebar-content-item-shown")
				        	    				        	   
				        	   
				           });
				           i.find(".page-quick-sidebar-chat-user .page-quick-sidebar-back-to-list").click(function(){
				        	    	a.removeClass("page-quick-sidebar-content-item-shown")});
				                  
				                    var t=function(i)
				                    {				                    	
				                    	i.preventDefault();
				                    	var e=a.find(".page-quick-sidebar-chat-user-messages"),
				                    	t=a.find(".page-quick-sidebar-chat-user-form .form-control"),
				                    	r=t.val();				                    	
				                    	if(0!==r.length){
				                    		
				                    		n=new Date;
				                    		hora = n.getHours()+":"+n.getMinutes();
				                    		
				                    		var json = {
				                    		           remetente: 'sistema',
				                    		           destinatario: destinatario,
				                    		           mensage: r,
				                    		           hora: hora
				                    		     };
				                    		
				                    		var dados = JSON.stringify(json); 
				                    		
				                    		console.log("Json enviado "+dados);
				                    		

				                    		       $.ajax({
				                    		           type: "POST",
				                    		           url: "http://localhost/WebServiceMIDE/chat/enviar_mobile",
				                    		           dataType: "text",
				                    		           success: function (msg) {		                    		        	   				                    		        	   
				                    		              
				                    		        	   if (msg) {
				                    		        		   console.log("Resposta "+msg);                    		                   
				                    		                   
				                    		                        var s=function(i,a,e,t,r){
										                    		var s="";
										                    		return s+='<div class="post '+i+'">',
										                    		       s+='<i class="avatar icon-user" alt=""/>',
										                    		       s+='<div class="message">',
										                    		       s+='<span class="arrow"></span>',
										                    		       s+='<label class="name">'+t+'</label>&nbsp;',
										                    		       s+='<span class="datetime">'+a+"</span>",
										                    		       s+='<span class="body">',s+=r,
										                    		       s+="</span>",s+="</div>",
										                    		       s+="</div>"},n=new Date,										                    		     
										                    		       
										                    		       c=s("out",hora,"avatar3","sistema",r);		
										                    		       e.append(c);      
										                    		       e.slimScroll({scrollTo:"1000000px"});            		                   			                    		                   				                    		                   				                    		                   
				                    		                   
										                    		     										                    		     
				                    		                   
				                    		               } else {
				                    		                   alert("Não foi possivel enviar dados !");
				                    		               }
				                    		           },

				                    		           data: {"dados" : dados},
				                    		           
				                    		       });
				                    		
					                    		     
				                        }
				                    	
				                     };
				                     a.find(".page-quick-sidebar-chat-user-form .btn").click(t),
					                 a.find(".page-quick-sidebar-chat-user-form .form-control").keypress(function(i){
					                  	   return 13==i.which?(t(i),!1):void 0})},
					                    		       e=function(){
					                    		    	   var i=$(".page-quick-sidebar-wrapper"),
					                    		               a=(i.find(".page-quick-sidebar-alerts"),
					                    		               function(){
					                    		            	   var a,e=i.find(".page-quick-sidebar-alerts-list");
					                    		                	   a=i.height()-i.find(".nav-justified > .nav-tabs").outerHeight(),
					                    		                	
					                    		                	   App.destroySlimScroll(e),e.attr("data-height",a),
					                    		                	   App.initSlimScroll(e)});
					                    		    	               a(),App.addResizeHandler(a)},t=function(){var i=$(".page-quick-sidebar-wrapper"),
					                    		                	   a=(i.find(".page-quick-sidebar-settings"),
					                    		                	    function(){
					                    		                		   var a,e=i.find(".page-quick-sidebar-settings-list");
					                    		                			   a=i.height()-i.find(".nav-justified > .nav-tabs").outerHeight(),
					                    		                			   App.destroySlimScroll(e),e.attr("data-height",a),
					                    		                			   App.initSlimScroll(e)});a(),App.addResizeHandler(a)};
					                    		         return{
					                    		        	 init:function(){i(),a(),e(),t()}}}();
					                    		         App.isAngularJsApp()===!1&&jQuery(document).ready(function(){
					                    		        	 QuickSidebar.init()
				 });/*






var QuickSidebar=function(){
	var i=function(){$(".dropdown-quick-sidebar-toggler a, .page-quick-sidebar-toggler, .quick-sidebar-toggler").click(function(i){$("body").toggleClass("page-quick-sidebar-open")})},
	    a=function(){
		    var i=$(".page-quick-sidebar-wrapper"),
		        a=i.find(".page-quick-sidebar-chat"),
		        e=function(){var e,t=i.find(".page-quick-sidebar-chat-users");
		        e=i.height()-i.find(".nav-tabs").outerHeight(!0),App.destroySlimScroll(t),t.attr("data-height",e),
		        App.initSlimScroll(t);
		    
		    var r=a.find(".page-quick-sidebar-chat-user-messages"),
		        s=e-a.find(".page-quick-sidebar-chat-user-form").outerHeight(!0);
		        s-=a.find(".page-quick-sidebar-nav").outerHeight(!0),App.destroySlimScroll(r),
		        r.attr("data-height",s),App.initSlimScroll(r)};
		    
		    e(),
		    App.addResizeHandler(e),
		    i.find(".page-quick-sidebar-chat-users .media-list > .media").click(function(){
		    	a.addClass("page-quick-sidebar-content-item-shown")}),
		    	i.find(".page-quick-sidebar-chat-user .page-quick-sidebar-back-to-list").click(function(){
		    		a.removeClass("page-quick-sidebar-content-item-shown")});
		    
		    var t=function(i){i.preventDefault();
		    var e=a.find(".page-quick-sidebar-chat-user-messages"),
		        t=a.find(".page-quick-sidebar-chat-user-form .form-control"),
		        r=t.val();
		        if(0!==r.length){
		        	var s=function(i,a,e,t,r){
		        		var s="";
		        		return s+='<div class="post '+i+'">',
		        		       s+='<img class="avatar" alt="" src="'+Layout.getLayoutImgPath()+t+'.jpg"/>',
		        		       s+='<div class="message">',s+='<span class="arrow"></span>',
		        		       s+='<a href="#" class="name">Bob Nilson</a>;',
		        		       s+='<span class="datetime">'+a+"</span>",
		        		       s+='<span class="body">',s+=r,s+="</span>",
		        		       s+="</div>",s+="</div>"},
		        		       n=new Date,
		        		       c=s("out",n.getHours()+":"+n.getMinutes(),"Bob Nilson","avatar3",r);
		        		       c=$(c),
		        		       e.append(c),
		        		       e.slimScroll({scrollTo:"1000000px"}),
		        		       t.val(""),
		        		       setTimeout(function(){
		        		    	   var i=new Date,
		        		    	   a=s("in",i.getHours()+":"+i.getMinutes(),"Ella Wong","avatar2","Lorem ipsum doloriam nibh...");
		        		    	   a=$(a),
		        		    	   e.append(a),
		        		    	   e.slimScroll({scrollTo:"1000000px"})},3e3)}};
		        		      a.find(".page-quick-sidebar-chat-user-form .btn").click(t),a.find(".page-quick-sidebar-chat-user-form .form-control").keypress(function(i){
		        		    	  return 13==i.which?(t(i),!1):void 0})},
		        		    	  e=function(){
		        		    		  var i=$(".page-quick-sidebar-wrapper"),a=(i.find(".page-quick-sidebar-alerts"),function(){
		        		    			  var a,e=i.find(".page-quick-sidebar-alerts-list");
		        		    			  a=i.height()-i.find(".nav-justified > .nav-tabs").outerHeight(),
		        		    			  App.destroySlimScroll(e),e.attr("data-height",a),
		        		    			  App.initSlimScroll(e)});a(),App.addResizeHandler(a)},
		        		    			  t=function(){var i=$(".page-quick-sidebar-wrapper"),
		        		    			  a=(i.find(".page-quick-sidebar-settings"),function(){
		        		    				  var a,e=i.find(".page-quick-sidebar-settings-list");
		        		    				  a=i.height()-i.find(".nav-justified > .nav-tabs").outerHeight(),
		        		    				  App.destroySlimScroll(e),
		        		    				  e.attr("data-height",a),
		        		    				  App.initSlimScroll(e)});a(),
		        		    				  App.addResizeHandler(a)};
		        		    				  return{init:function(){i(),a(),e(),t()}}}();App.isAngularJsApp()===!1&&jQuery(document).ready(function(){
		        		    					  QuickSidebar.init()});



*/

