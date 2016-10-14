package br.com.jortec.util;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Jorliano on 10/02/2016.
 */
public class Formate {

	public static String dataParaString(Date data) {
		DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
		String dataFormatada = dateFormat.format(data);
		return dataFormatada;
	}

	public static String dataParaStringExtenso(Date data) {
		DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.FULL);
		String dataFormatada = dateFormat.format(data);
		return dataFormatada;
	}

	public static Date longParaDatastring(long dataLong) {
		
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(dataLong);		
		
		Date data = c.getTime();
		return data;
	}

	public static Date stringParaData(String data) {
		Date dataFormatada = null;
		// DateFormat format = new SimpleDateFormat("dd/MM/yyyy");

		try {
			DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
			dataFormatada = dateFormat.parse(data);

		} catch (ParseException e) {
			e.printStackTrace();
		}
		return dataFormatada;
	}
}