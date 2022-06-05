package utilidades;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Utilidades {
	
	public static void notificaError(JFrame padre, String titulo, Exception e, String mensaje) {
		String contenido = "";
		if (mensaje != null)
			contenido += mensaje+"\n";
		//Muestra la excepción y su causas(excepciones anidadas)
		while (e != null) {
			contenido += "\n["+e.getClass().getName() + "] " + e.getMessage(); // Tipo y mensaje de la excepción
			e=(Exception)e.getCause();
		}
		JOptionPane.showMessageDialog(padre, contenido, titulo, JOptionPane.ERROR_MESSAGE);
	}
	
	
	 //FECHAS Y HORAS
	static SimpleDateFormat sdfFechas = new SimpleDateFormat("dd/MM/yyyy");
	static SimpleDateFormat sdfHoras = new SimpleDateFormat("HH:mm");
	static SimpleDateFormat sdfFechasHoras = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	
	static SimpleDateFormat sdfFechasMysql = new SimpleDateFormat("yyyy-MM-dd");
	static SimpleDateFormat sdfHorasMysql = new SimpleDateFormat("HH:mm:ss");
	static SimpleDateFormat sdfFechasHorasMysql = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	//Fechas
	public static String getFechaString(Date fecha) {
		String fechaS = sdfFechas.format(fecha);

		return fechaS;
	}
	
	public static Date getFechaDate(String fecha) throws ParseException {	
		if(!fecha.equals("")) {
			return sdfFechas.parse(fecha);
		}

		return null;
	}

	public static boolean isFechaValida(String fecha) {
		sdfFechas.setLenient(false);
		try {
			sdfFechas.parse(fecha);
			return true;
		} catch (ParseException e) {
			return false;
		}
	}
	
	//Horas
	public static Date getHoraDate(String hora) throws ParseException {	
		if(!hora.equals("")) {
			return sdfHoras.parse(hora);
		}

		return null;
	}
	
	public static boolean isHoraValida(String hora) {
		sdfHoras.setLenient(false);
		try {
			sdfHoras.parse(hora);
			return true;
		} catch (ParseException e) {
			return false;
		}
	}
	
	public static String getHoraString(Date hora) {
		String fechaS = sdfHoras.format(hora);

		return fechaS;
	}
	
	//Fecha + hora
	public static Date getFechaHoraDate(String fechaHora) throws ParseException {
		if(!fechaHora.equals("")) {
			return sdfFechasHoras.parse(fechaHora);
		}

		return null;
	}
	
	public static boolean isFechaHoraValida(String fechaHora) {
		sdfFechasHoras.setLenient(false);
		try {
			sdfFechasHoras.parse(fechaHora);
			return true;
		} catch (ParseException e) {
			return false;
		}
	}
	
	public static String getFechaHoraString(Date fechaHora) {
		if(fechaHora!=null)
			return sdfFechasHoras.format(fechaHora);

		return null;
	}
	
	public static String getFechaStringMysql(Date fecha) throws ParseException {
		if(fecha!=null)
			return sdfFechasMysql.format(fecha);
		
		return null;
	}
	public static String getHoraStringMysql(Date hora) throws ParseException {
		if(hora!=null)
			return sdfHorasMysql.format(hora);
		
		return null;
	}
	public static String getFechaHoraStringMysql(Date fechaHora) throws ParseException {
		if(fechaHora!=null)
			return sdfFechasHorasMysql.format(fechaHora);
		
		return null;
	}
	
	public static String getFechaHoraStringMysql(String fechaHora) throws ParseException {
		Date d = getFechaHoraDate(fechaHora);
		String fechaS = sdfFechasHorasMysql.format(d);
		
		return fechaS;
	}
	public static String getFechaStringMysql(String fecha) throws ParseException {
		Date d = getFechaDate(fecha);
		String fechaS = sdfFechasMysql.format(d);
		
		return fechaS;
	}

	public static Date getHoraDateMysql(String fecha) throws ParseException {
		if(fecha!=null) {
			return sdfHorasMysql.parse(fecha);
		}
		return null;
	}
	public static Date getFechaDateMysql(String fecha) throws ParseException {
		if(fecha!=null) {
			return sdfFechasMysql.parse(fecha);
		}
		return null;
	}
	public static Date getFechaHoraDateMysql(String fechaHora) throws ParseException {
		if(fechaHora!=null) {
			return sdfFechasHorasMysql.parse(fechaHora);
		}
		return null;
	}
	
}
