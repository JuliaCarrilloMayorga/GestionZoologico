package tablas;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.exception.JDBCConnectionException;

import conexionBBDD.Persistencia;
import interfaz.VentanaInicial;
import utilidades.Utilidades;

//Lo heredan todas las clases de las tablas
public class ElementoPadre {
	protected Integer id;

	//Devuelve el id. Es como getId pero solo para las entidades, no lo implementan las clases Consume ni Animaltratamiento, cuyos ids son objetos
	public Integer obtenerId() {return this.id;}
	
	public String[] getCampos() throws ParseException {return null;}
	public void configurar(String[] valores, Persistencia per) throws ParseException, SQLException, Exception {}
	public String[] getNombreCampos(){return null;}
	
	//Método llamado desde los getters de SETs en JDBC, devuelve el set de X para un objeto
	protected HashSet getGenericSet(Persistencia per, String set, String clase, int id) {
		try {
			//Coge todos los objetos de la tabla "set" filtrados por el id del objeto que lo llama
			ArrayList al = (ArrayList) per.getObetosFiltradoMulti(set, new String[] {}, new String[] {"id"+clase, String.valueOf(id)});
			return new HashSet(al);
			
		} catch (SQLException | JDBCConnectionException e) {
			Utilidades.notificaError(null, "Error de conexión", e, "Probablemente se ha perdido la conexión con MySql. \n"
					+ "Error fatal, el programa se cerrará a continuación");
			e.printStackTrace();
			System.exit(0);
		} catch (Exception e) {
			Utilidades.notificaError(null, "Error interno", e, "Hay un error en el código del programa.\n"
					+ "Error fatal, el programa se cerrará a continuación");
			e.printStackTrace();
			System.exit(0);
		} 
		return null;
	}
	
	//Método llamado desde los getters de SETs en JDBC, para los casos es nos que hay una tabla de relación intermedia que no están en forma de objeto java (escapaz y trabaja)
	protected HashSet getGenericSetTablaInter(Persistencia per, String claseSet, String tablaIntermedia,
			String claseBase, Integer id) {
		try {
			ArrayList al = (ArrayList) per.getObetosSetMNSimple(claseSet, tablaIntermedia, claseBase, id);
			return new HashSet(al);
		} catch (SQLException | JDBCConnectionException e) {
			Utilidades.notificaError(null, "Error de conexión", e, "Probablemente se ha perdido la conexión con MySql. \n"
					+ "Error fatal, el programa se cerrará a continuación");
			e.printStackTrace();
			System.exit(0);
		} catch (Exception e) {
			Utilidades.notificaError(null, "Error interno", e, "Hay un error en el código del programa.\n"
					+ "Error fatal, el programa se cerrará a continuación");
			e.printStackTrace();
			System.exit(0);
		} 
		return null;
	}

	
	

}
