/**
 * 12/03/2016
 
*/
	
$(function () {
	
	      
	    
			$.getJSON("http://jortec.ddns.net/WebServiceMIDE/dados/graficoGeral", function(data) {
				   console.log(data);		
				   
				   var instalacao = new Array();  
				   var manutencao = new Array();  
				   var recolhimento = new Array();  
				   var mes = new Array();  
				   var analise = new Array();  
				   var qtdIn = 0;
				   var qtdMt = 0;
				   var qtdRc = 0;
				   
				   $.each(data , function(i, v) {
					   
					   mes.push([v.mes]);	
					   instalacao.push([ v.instalacao]);		
					   manutencao.push([ v.manutencao]);		
					   recolhimento.push([ v.recolhimento]);						   		
					   	
					   
					   analise.push([(v.instalacao + v.manutencao + v.recolhimento )/3 ]);
					     
				    
				       qtdIn = qtdIn + v.instalacao;
				       qtdMt = qtdMt + v.manutencao;
				       qtdRc = qtdRc + v.recolhimento;				
				       
				   });
				   console.log(qtdIn);
				   console.log("iniciar grafico");	
				  
				   
				   $('#grafico').highcharts( 		
				    		
					    	{
					    	
					    	 
					        title: {
					            text: 'HISTORICO DOS SERVIÇOS'
					        },
					        xAxis: {
					            categories: mes       
					        },
					        labels: {
					            items: [{
					                html: 'Total dos serviços',
					                style: {
					                    left: '50px',
					                    top: '18px',
					                    color: (Highcharts.theme && Highcharts.theme.textColor) || 'black'
					                }
					            }]
					        },
					        series: [{
					            type: 'column',
					            name: 'Instalação',
					            data: instalacao
					        }, {
					            type: 'column',
					            name: 'Recolhimento',
					            data: recolhimento
					        }, {
					            type: 'column',
					            name: 'Manutenção',
					            data: manutencao
					        }, {
					            type: 'spline',
					            name: 'media',
					            data: analise,
					            marker: {
					                lineWidth: 2,
					                lineColor: Highcharts.getOptions().colors[3],
					                fillColor: 'white'
					            }
					        }, {
					            type: 'pie',
					            name: 'Total serviços',
					            data: [{
					                name: 'Instalação',
					                y: qtdIn,
					                color: Highcharts.getOptions().colors[0] // Instalação color
					            }, {
					                name: 'Recolhimento',
					                y: qtdRc,
					                color: Highcharts.getOptions().colors[1] // Recolhimento color
					            }, {
					                name: 'Manutenção',
					                y: qtdMt,
					                color: Highcharts.getOptions().colors[2] // Manutenção color
					            }],
					            center: [100, 80],
					            size: 100,
					            showInLegend: false,
					            dataLabels: {
					                enabled: false
					            }
					        }]
					    });
				   
	 });
	      
		
	var url = "http://jortec.ddns.net/WebServiceMIDE/dados/graficoUsuario/dia";
	
	$('select').on('change', function (e) {
	    var optionSelected = $("option:selected", this);   	    
	   
	    if(this.value == 'Dia'){
	    	url = "http://jortec.ddns.net/WebServiceMIDE/dados/graficoUsuario/dia";
	    	graficoUsuario("Dia atual ");
	    }else{
	    	url = "http://jortec.ddns.net/WebServiceMIDE/dados/graficoUsuario/mes";
	    	graficoUsuario("Mês atual  ");
	    	
	    }
	    	
	});
	
	graficoUsuario("Dia atual");
	
	function graficoUsuario(titulo){		
		
             var concluido = new Array();  
             var pendente = new Array();
             var cancelado = new Array();
             var usuarios = new Array();
             
             $.getJSON(url, function(data2) {
                            	 
            	 console.log(data2);		
            	 
            	 $.each(data2 , function(i, v) {					   
			        			         	
            		 concluido.push([ v.concluido]);		
            		 pendente.push([ v.pendente]);	
            		 cancelado.push([ v.cancelado]);	
            	 	 usuarios.push([v.nome]);	
			    });
            	 
            	 
            	 
            	 $('#graficoUsuarios').highcharts({
				        chart: {
				            type: 'bar'
				        },
				        title: {
				            text: 'HISTORICO DOS TECNICOS'
				        },
				        subtitle: {
				            text: titulo
				        },
				        xAxis: {
				            categories: usuarios,
				            title: {
				                text: null
				            }
				        },
				        yAxis: {
				            min: 0,
				            title: {
				                text: 'Quantidade',
				                align: 'high'
				            },
				            labels: {
				                overflow: 'justify'
				            }
				        },
				        tooltip: {
				            valueSuffix: ''
				        },
				        plotOptions: {
				            bar: {
				                dataLabels: {
				                    enabled: true
				                }
				            }
				        },
				        legend: {
				            layout: 'vertical',
				            align: 'right',
				            verticalAlign: 'top',
				            x: -40,
				            y: 80,
				            floating: true,
				            borderWidth: 1,
				            backgroundColor: ((Highcharts.theme && Highcharts.theme.legendBackgroundColor) || '#FFFFFF'),
				            shadow: true
				        },
				        credits: {
				            enabled: false
				        },
				        series: [{
				            name: 'Pendentes',
				            data: pendente
				        }, {
				            name: 'Canceladas',
				            data: cancelado
				        }, {
				            name: 'Concluidas',
				            data: concluido
				        }]
				    });
				   
	
	});
     
	};


		 
		
});
