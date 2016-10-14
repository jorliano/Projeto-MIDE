package br.com.jortec.util;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Jorliano on 10/02/2016.
 */
public class Formate {


   

    public static String dataParaString(Date data){
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT);
        String dataFormatada = dateFormat.format(data);
        return  dataFormatada;
    }
    
    public static Date stringParaData(String dataString){
            	
       Date data = null;
       SimpleDateFormat formato;
			
       try {			
    	   
    	   String pattern = "EEE MMM dd HH:mm:ss zzzz yyyy";
    	                     //Thu Mar 17 23:42:47 GMT-03:00 2016    	                       
    	                      // Thu Mar 17 23:48:06 GMT-03:00 2016  Locale.US,
    	   
			formato = new SimpleDateFormat(pattern,Locale.US);
			data = formato.parse(dataString);			
			
			if(data == null){
				formato = new SimpleDateFormat(pattern);
				data = formato.parse(dataString);		
			}
			
			
				
		} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}

      
        return data;
    }
}