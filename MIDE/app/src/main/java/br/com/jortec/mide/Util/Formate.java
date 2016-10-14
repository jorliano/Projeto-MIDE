package br.com.jortec.mide.Util;

import android.util.Log;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
        try {

            Log.i("LOG "," String a ser formatada para data "+dataString);

            SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
            data = formato.parse(dataString);
            formato.format(data);

           //Calendar calendar = Calendar.getInstance();
           // calendar.setTime(data);

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.i("LOG "," data formatada "+data);
        return data;
    }
}