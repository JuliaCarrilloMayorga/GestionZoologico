package conexionBBDD;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import tablas.*;
import utilidades.Utilidades;

public class PersistenciaMysql implements Persistencia{

	Connection con;
	
	public PersistenciaMysql(String servidor, String puerto, String baseDatos, String usuario, String password) throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		
		String cadenaConexion = "jdbc:mysql://" + servidor + ":"+ puerto + "/" + baseDatos;
		con = DriverManager.getConnection(cadenaConexion, usuario, password);
		
		con.setAutoCommit(false);
	}
	
	private void commitear() throws SQLException {
		con.commit();
	}
	public void cancelarCambios() throws SQLException {
		con.rollback();
	}
	
	//_______________________________   CRUD BÁSICOS   _______________________________________________________________________
		 /**
		  * Guarda un objeto con INSERT O UPDATE segun exista o no
		  *  @param tabla Nombre de la tabla/objeto, con la primera letra en mayúsculas.
		  *  @param obj objeto a guardar, debe ser heredero de ElementoPadre
		  *  @return El id del objeto guardado
		  */
	public int guardarObjeto(String tabla, ElementoPadre obj) throws Exception {
		Integer id = obj.obtenerId();

		if(id==null) { //Si el id del objeto es null, el objeto no existe, se hace INSERT
			String sql = "INSERT into "+tabla.toLowerCase()+" VALUES (null,"; //Inicio de la sentencia INSERT, establece null para el id autoincrementado
			
			String[] camposEle = obj.getCampos(); // Array de strings con los valores del objeto
			for (int i = 1; i < camposEle.length; i++) { //Recorre el array y los añade a la sentencia
				if(camposEle[i]!=null) {
					sql += "'"+camposEle[i]+"'";
				} else { //Si alguno es null añade la palabra null sin las comillas
					sql += "null";
				}
				
				if(i != camposEle.length-1) { //Si no es la última vuelta, añade un ','
					sql += ",";
				}
			}
			sql += ")";
			
			Statement st = con.createStatement();
			st.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
			
			ResultSet rs = st.getGeneratedKeys();
			rs.next();
			
			commitear();
			return rs.getInt(1);
			
		} else { //Si el id del objeto es null, el objeto no existe, se hace UPDATE
			String[] camposNombre = obj.getNombreCampos(); //Array de strings con el nombre de los acampos (id, descripcion, nombre...)
			String[] camposValores = obj.getCampos(); //Array de strings con los valored de los campos (1, sabana, Manolo)
			
			int numCampos = camposNombre.length; //Número de campos que tiene esta clase
			
			String sql = "UPDATE "+tabla.toLowerCase()+" SET ";
			
			 for (int i =1; i < numCampos; i++) { //Por cada elemento recorre el bucle y le añade una linea con un 'campo=valor' a la sentencia
				
				 sql += camposNombre[i] + "=";
				 if(camposValores[i]!=null) {
					 sql += "'"+camposValores[i]+"'";
				 } else {
					 sql += "null";
				 }

				 if(i != numCampos-1) { //Si no es la última vuelta, añade un ','
					 sql += " , ";
				 }
			 }
			
			 sql += " WHERE id = "+camposValores[0]; // Señala el id del animal a modificar
			 
			Statement st = con.createStatement();
			st.executeUpdate(sql);
			
			commitear();
			return Integer.valueOf(camposValores[0]);
		}
	}

	/**
	  * Elimina un objeto con DELETE
	  * @param tabla Nombre de la tabla/objeto, con la primera letra en mayúsculas.
	  * @param obj objeto a eliminar, debe ser heredero de ElementoPadre
	  */
	public void eliminarObjeto(String tabla, ElementoPadre obj) throws SQLException, ParseException {
		Integer id = obj.obtenerId(); //El método obtenerId lo tienen todos los hijos de ElementoPadre con id tipo Integer, es decir todos menos Consume y Animaltratamiento (Objetos de Relación)
		if(id!=null) { // si se ha obtenido un Id, es un objeto de una tabla Entidad
			String sql = "DELETE FROM "+tabla+" WHERE id=?";
			
			PreparedStatement st = con.prepareStatement(sql);
			st.setInt(1, obj.obtenerId());			
			st.executeUpdate();		
		} else { //Si no se ha obtenido el Id, es un objeto de una tabla de Relación
			String sql = "DELETE FROM "+tabla+" WHERE "; //En el where se incluián todos los campos que forman parte de la PK
			
			String[] nombreCampos = obj.getNombreCampos(); //Array con los campos incluidos en el PK
			String[] valoresCampos = obj.getCampos(); //Array con los valores de dichos campos
			
			for (int i = 0; i < nombreCampos.length; i++) {
					 sql += nombreCampos[i] + " = " + "'"+valoresCampos[i]+"'";

					 if(i != nombreCampos.length-1) { //Si no es la última vuelta, añade un AND
						 sql += " AND ";
					 }
			}
			Statement st = con.createStatement();
			st.executeUpdate(sql);
		}	
		commitear();
	}

	/**
	  * Devuelve una lista de objetos de una clase determinada obtenidos con la sentencia SQL dada. <br>
	  * Utilizadada desde todos los métodos tipo SELECT
	  * @param tabla Nombre de la clase
	  * @param sql sentencia SQL a ejecutar
	  */
	private List listaObjetos(String tabla, String sql) throws SQLException, Exception {
		Statement st = con.createStatement();
		ResultSet rs = st.executeQuery(sql); //Ejecuta el SQL
		
		List lista = new ArrayList();
		
		while (rs.next()) {
			Class<?>clazz = Class.forName("tablas."+tabla); //Se crea un objeto Class segun el nombre de la clase (tabla)
			ElementoPadre ele = (ElementoPadre) clazz.newInstance();  //Se guarda en un ElementoPadre una instancia de la clase del CRUD
			
			int numCampos = ele.getNombreCampos().length;
			String[] valores = new String[numCampos]; // Se crea un array de Strings que contendrá los valores de los atributos del objeto
			for (int i = 0; i < numCampos; i++) {
				valores[i] = rs.getString(i+1);
			}
			
			ele.configurar(valores, this); //Se llama al método configurar de la clase que toca, lo cual emplea setters para configurar el elemento
			lista.add(ele); //Se añade a la lista
		}
		return lista;
	}
	
	 /**
	  * Devuelve una lista con todos los objetos de una tabla. <br>
	  * Crea la sentencia SQL y se la pasa a {@link #listaObjetos}, que devuelve la lista a retornar
	  * @param tabla Nombre de la tabla/objeto, con la primera letra en mayúsculas.
	  */
	public List getTodosObjetos(String tabla) throws SQLException, Exception {
		String sql ="SELECT * FROM " + tabla.toLowerCase();
		
		return listaObjetos(tabla, sql);
	}

	/**
	  * Devuelve una lista con los objetos de una tabla filtrados por algunos de sus campos mediante LIKE y otros de sus campos mediante '='. <br>
	  * Crea la sentencia SQL y se la pasa a {@link #listaObjetos}, que devuelve la lista a retornar
	  * @param tabla Nombre de la tabla/objeto, con la primera letra en mayúsculas.
	  * @param camposLike Array de Strings, ordenados en pares. De cada par, el primero marca el nombre del campo y el segundo el valor. Estos campos serán condición tipo LIKE.
	  * @param camposIgual Array de Strings, ordenados en pares. De cada par, el primero marca el nombre del campo y el segundo el valor. Estos campos serán condición tipo '='.
	  */
	public List getObetosFiltradoMulti(String tabla, String[] camposLike, String[] camposIgual) throws SQLException, Exception {
		String sql="SELECT * FROM " + tabla.toLowerCase() + " WHERE ";

		 for (int i = 0; i < camposLike.length; i++) { //Por cada pareja de elementos de camposLike recorre el bucle y le añade una linea con un LIKE a la sentencia
			 sql += camposLike[i] + " LIKE " + "'%"+camposLike[++i]+"%'";

			 if(i != camposLike.length-1) { //Si no es la última vuelta, añade un AND
				 sql += " AND ";
			 }
		 }
		 if(camposLike.length>0 && camposIgual.length>0) { //Si hay elementos de camposLike y también de camposIgual, añade un AND para juntar las condiciones generadas
			 sql += " AND ";
		 }
		 for (int i = 0; i < camposIgual.length; i++) { //Por cada pareja de elementos de camposIgual recorre el bucle y le añade una linea con un '=' a la sentencia
			 sql += camposIgual[i] + " = " + "'"+camposIgual[++i]+"'";

			 if(i != camposIgual.length-1) { //Si no es la última vuelta, añade un AND
				 sql += " AND ";
			 }
		 }

		return listaObjetos(tabla, sql);
	}

	/**
	 * Devuelve una lista de los objetos contenidos en un SET de una relación m...n sin atributos extra (Relaciones trabaja y escapaz)<br>
	 * Solo se utiliza en JDBC. Se llama desde la clase ElementoPadre.
	 * @param claseSet Clase de los elementos de la lista devuelta, los del Set (Tratamiento, Empleado, Zona).
	 * @param tablaIntermedia Nombre de la tabla de relación empleada (escapaz, trabaja).
	 * @param claseBase Nombre de la clase desde la que se llama al método.
	 * @param id Id del objeto que llama al método, se usa para filtrar los resultados.
	 */
	public List getObetosSetMNSimple(String claseSet, String tablaIntermedia, String claseBase, Integer id) throws SQLException, Exception {
		
		String sql="SELECT * FROM " + tablaIntermedia +" t "
				+ " INNER JOIN "+claseSet.toLowerCase()+" e on(t.id"+claseSet+" = e.id)"
				+ " WHERE id"+claseBase+"="+id;

		Statement st = con.createStatement();
		ResultSet rs = st.executeQuery(sql);

		List lista = new ArrayList();

		while (rs.next()) {
			Class<?>clazz = Class.forName("tablas."+claseSet); //Se crea un objeto Class segun el nombre de la clase
			ElementoPadre ele = (ElementoPadre) clazz.newInstance();  //Se guarda en un ElementoPadre una instancia de la clase del CRUD

			int numCampos = ele.getNombreCampos().length;
			
			String[] valores = new String[numCampos];
			for (int i = 0; i < numCampos; i++) {
				valores[i] = rs.getString(i+3);
			}

			ele.configurar(valores, this);
			lista.add(ele);
		}
		return lista;
	}
	
	/**
	  * Devuelve UN objeto filtrado por algunos de sus campos mediante mediante '='.
	  * @param tabla Nombre de la tabla/objeto, con la primera letra en mayúsculas.
	  * @param campos Array de Strings, ordenados en pares. De cada par, el primero marca el nombre del campo y el segundo el valor. Estos campos serán condición tipo LIKE.
	  */
	public Object getObjetoMulti(String tabla, String[] campos) throws SQLException, Exception {
		String sql = "SELECT * FROM " + tabla.toLowerCase() + " WHERE ";

		 for (int i = 0; i < campos.length; i++) { //Por cada pareja de elementos de campos recorre el bucle y le añade una linea con un '=' a la sentencia
			 sql += campos[i] + " = " + "'"+campos[++i]+"'";

			 if(i != campos.length-1) {
				 sql += " AND "; //Si no es la última vuelta, añade un AND
			 }
		 }

		Statement st = con.createStatement();
		ResultSet rs = st.executeQuery(sql);
			
		if(rs.next()) {
			Class<?>clazz = Class.forName("tablas."+tabla); //Se crea un objeto Class segun el nombre de la clase
			ElementoPadre ele = (ElementoPadre) clazz.newInstance();  //Se guarda en un ElementoPadre una instancia de la clase del CRUD
			
			int numCampos = ele.getNombreCampos().length;
			String[] valores = new String[numCampos]; // Se crea un array de Strings que contendrá los valores de los atributos del objeto
			for (int i = 0; i < numCampos; i++) {
				valores[i] = rs.getString(i+1);
			}
			
			ele.configurar(valores, this); //Se llama al método configurar de la clase que toca, lo cual emplea setters para configurar el elemento
			return ele;
		} else {
			return null; //Esto no debería ejecutarse, solo si hay un error
		}
	}

	
	 //_______________________________   CRUD RELACIONES   _______________________________________________________________________
	
	/**
	  * Borra una relación m...n <br>
	  * SQL: Hace un DELETE de la tabla indicata filtrando por lso campos dados.
	  *  @param tabla Nombre de la tabla que guarda la relación, en minúsculas.
	  *  @param elementos Objetos a desrelacionar (Solo útil para Hibernate)
	  *  @param campos Campos para determinar mediante WHERE los objetos a desrelacionar (Solo útil para JDBC)
	  */
	public void borrarProceso(String tabla, ElementoPadre[] elementos, String[] campos) throws SQLException {
		String sql = "DELETE FROM "+tabla+" WHERE ";
		
		for (int i = 0; i < campos.length; i++) { //Por cada pareja de elementos de campos recorre el bucle y le añade una linea con un '=' al query
			 sql += campos[i] + " = " + "'"+campos[++i]+"'";

			 if(i != campos.length-1) { //Si no es la última vuelta, añade un AND
				 sql += " AND ";
			 }
		 }
		
		Statement st = con.createStatement();
		st.executeUpdate(sql);
	}

	/**
	  * Guarda una relación m...n <br>
	  * SQL: Hace un INSERT de la tabla indicata filtrando por lso campos dados.
	  *  @param tabla Nombre de la tabla que guarda la relación, en minúsculas.
	  *  @param elementos Objetos a relacionar (Solo útil para Hibernate)
	  *  @param campos Campos para determinar mediante WHERE los objetos a relacionar (Solo útil para JDBC)
	  */
	public void aniadirProceso(String tabla, ElementoPadre[] elementos, String[] campos) throws SQLException {
		String sql = "INSERT INTO "+tabla+" VALUES (";
		
		for (int i = 0; i < campos.length; i++) {
			if(campos[i]!=null) { //Recorre el array de valores dados y lños añade. En este caso los valores serán iDs FK y atributos
				sql += "'"+campos[i]+"'"; 
			} else {
				sql += "null";
			}
			
			 if(i != campos.length-1) { //Si no es la última vuelta, añade un ','
				 sql += ",";
			 }
		}
		sql += ")";
		
		Statement st = con.createStatement();
		st.executeUpdate(sql);
	}

	 /**
	  * Guarda un objeto con INSERT sin devolver id <br>
	  * Pensado para objetos de tipo Relación, cuyo id es una clave compuesta. Al no permitir modificaciones no hay parte de UPDATE.
	  *  @param tabla Nombre de la tabla/objeto, con la primera letra en mayúsculas.
	  *  @param obj objeto a guardar, debe ser heredero de ElementoPadre
	  */
	public void guardarObjetoSinId(String tabla, ElementoPadre obj) throws ParseException, SQLException {
		String sql = "INSERT into "+tabla.toLowerCase()+" VALUES (";
		
		String[] camposEle = obj.getCampos(); // Array de strings con los valores del objeto
		for (int i = 0; i < camposEle.length; i++) { //Recorre el array y los añade a la sentencia
			if(camposEle[i]!=null) {
				sql += "'"+camposEle[i]+"'";
			} else { //Si alguno es null añade la palabra null sin las comillas
				sql += "null";
			}
			
			 if(i != camposEle.length-1) { //Si no es la última vuelta, añade un ','
				 sql += ",";
			 }
		}
		sql += ")";

		Statement st = con.createStatement();
		st.executeUpdate(sql);
		commitear();
	}

	/**
	  * Devuelve una lista ordenada de Animaltratamientos filtrados por especie, desde una fecha hasta otra. <br>
	  *  @param DescrEspecie String. Descripción de la especia
	  *  @param fechaDesde String. Fecha inicial. Formato de msql 'yyyy-MM-dd HH:mm:ss'
	  *  @param fechaHasta String. Fecha final. Formato de msql 'yyyy-MM-dd HH:mm:ss'
	  */
	public ArrayList<Animaltratamiento> getAniTratsFechas(String descrEspecie, String fechaDesde, String fechaHasta) throws Exception {
		String sql = "SELECT * FROM animaltratamiento a INNER JOIN animal n on(a.idAnimal = n.id)"
				+ " INNER JOIN especie e  on(n.idEspecie = e.id)"
				+ " WHERE e.descripcion=? AND a.fechaHora BETWEEN ? AND ? "
				+ "ORDER BY a.fechaHora DESC, n.nombre ASC";
		
		PreparedStatement st = con.prepareStatement(sql);
		st.setString(1, descrEspecie);
		st.setString(2, fechaDesde+" 00:00");	
		st.setString(3, fechaHasta+" 23:59");	
		
		ResultSet rs = st.executeQuery();
		List lista = new ArrayList();
		
		String[] valores = new String[4];
		while (rs.next()) {
			Animaltratamiento ani = new Animaltratamiento();
			
			valores[0]= rs.getString(1);
			valores[1]= rs.getString(2);
			valores[2]= rs.getString(3);
			valores[3]= rs.getString(4);
			
			ani.configurar(valores, this);
			lista.add(ani);
		}
		return (ArrayList<Animaltratamiento>) lista;
	}

	//Solo para Hibernate
	public void actualizarSesion(ElementoPadre obj) {}
}
