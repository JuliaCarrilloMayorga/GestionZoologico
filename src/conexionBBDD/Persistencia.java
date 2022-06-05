package conexionBBDD;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;


import java.util.List;
import java.util.TreeSet;

import org.hibernate.Query;

import tablas.*; 

public interface Persistencia {
	
	//__CRUD BÁSICO__
	
	 /**
	  * Guarda un objeto
	  *  @param tabla Nombre de la tabla/objeto, con la priemra letra en mayúsculas.
	  *  @param obj objeto a guardar, debe ser heredero de ElementoPadre
	  *  @return El id del objeto guardado
	 * @throws ParseException Si hay una conversión de un Date a un String
	 * @throws SQLException 
	 * @throws Exception 
	 * @see PersistenciaHiber#guardarObjeto
	 * @see PersistenciaMysql#guardarObjeto
	  */
	int guardarObjeto(String tabla, ElementoPadre obj) throws ParseException, SQLException, Exception;

	/**
	  * Elimina un objeto con DELETE
	  * @param tabla Nombre de la tabla/objeto, con la priemra letra en mayúsculas.
	  * @param obj objeto a eliminar, debe ser heredero de ElementoPadre
	 * @throws SQLException 
	 * @throws ParseException 
	 * @see PersistenciaHiber#eliminarObjeto
	 * @see PersistenciaMysql#eliminarObjeto
	  */
	void eliminarObjeto(String tabla, ElementoPadre obj) throws SQLException, ParseException;
	
	/**
	  * Devuelve una lista con todos los objetos de una tabla
	  * @param tabla Nombre de la tabla/objeto, con la primera letra en mayúsculas.
	 * @throws SQLException 
	 * @throws Exception 
	 * @see PersistenciaHiber#getTodosObjetos
	 * @see PersistenciaMysql#getTodosObjetos
	  */
	List getTodosObjetos(String tabla) throws SQLException, Exception;
	
	/**
	  * Devuelve una lista con los objetos de una tabla filtrados por algunos de sus campos mediante LIKE y otros de sus campos mediante '='.
	  * @param tabla Nombre de la tabla/objeto, con la primera letra en mayúsculas.
	  * @param camposLike Array de Strings, ordenados en pares. De cada par, el primero marca el nombre del campo y el segundo el valor. Estos campos serán condición tipo LIKE.
	  * @param camposIgual Array de Strings, ordenados en pares. De cada par, el primero marca el nombre del campo y el segundo el valor.Estos campos serán condición tipo '='.
	 * @throws Exception 
	 * @throws SQLException 
	 * @see PersistenciaHiber#getObetosFiltradoMulti
	 * @see PersistenciaMysql#getObetosFiltradoMulti
	  */
	List getObetosFiltradoMulti(String tabla, String[] camposLike, String[] camposIgual) throws SQLException, Exception;
	
	/**
	  * Devuelve UN objeto filtrado por algunos de sus campos mediante mediante '='.
	  * @param tabla Nombre de la tabla/objeto, con la primera letra en mayúsculas.
	  * @param campos Array de Strings, ordenados en pares. De cada par, el primero marca el nombre del campo y el segundo el valor. Estos campos serán condición tipo LIKE.
	 * @throws Exception 
	 * @throws SQLException 
	 * @see PersistenciaHiber#getObjetoMulti
	 * @see PersistenciaMysql#getObjetoMulti
	  */
	Object getObjetoMulti(String tabla, String[] campos) throws SQLException, Exception;
	
	/**
	 * Devuelve una lista de los objetos contenidos en un SET de una relación m...n sin atributos extra (Relaciones trabaja y escapaz)<br>
	 * Solo se utiliza en JDBC. Se llama desde la clase ElementoPadre.
	 * @param claseSet Clase de los elementos de la lista devuelta, los del Set (Tratamiento, Empleado, Zona).
	 * @param tablaIntermedia Nombre de la tabla de relación empleada (escapaz, trabaja).
	 * @param claseBase Nombre de la clase desde la que se llama al método.
	 * @param id Id del objeto que llama al método, se usa para filtrar los resultados.
	 * @throws Exception 
	 * @throws SQLException 
	 */
	List getObetosSetMNSimple(String claseSet, String tablaIntermedia, String claseBase, Integer id) throws SQLException, Exception;
	
	//__CRUD RELACIONES__

	/**
	  * Refresca un objeto persistente. Sólo para Hibernate.
	  * @param obj Objeto heredero de ElementoPadre a refrescar.
	  */
	void actualizarSesion(ElementoPadre obj);

	/**
	 * Borra una relación m...n 
	 *  @param tabla Nombre de la tabla que guarda la relación, en minúsculas.
	 *  @param elementos Objetos a desrelacionar (Solo útil para Hibernate)
	 *  @param campos Campos para determinar mediante WHERE los objetos a desrelacionar (Solo útil para JDBC)
	 * @throws SQLException
	 * @see PersistenciaHiber#aniadirProceso
	 * @see PersistenciaMysql#aniadirProceso 
	 */
	void borrarProceso(String tabla, ElementoPadre[] elementos, String[] campos) throws SQLException;

	/**
	 * Guarda un objeto con SAVE <br>
	 * No devuelve nada. Para objetos cuyo id no es un int, sino otro objeto.
	 *  @param tabla Nombre de la tabla/objeto, con la primera letra en mayúsculas.
	 *  @param obj objeto a guardar, debe ser heredero de ElementoPadre
	 * @throws SQLException 
	 * @see PersistenciaHiber#borrarProceso
	 * @see PersistenciaMysql#borrarProceso
	 */
	void aniadirProceso(String tabla, ElementoPadre[] elementos, String[] campos) throws SQLException;
	
	/**
	  * Guarda una relación m...n 
	  *  @param tabla Nombre de la tabla que guarda la relación, en minúsculas.
	  *  @param elementos Objetos a relacionar (Solo útil para Hibernate)
	  *  @param campos Campos para determinar mediante WHERE los objetos a relacionar (Solo útil para JDBC)
	 * @throws ParseException 
	 * @throws SQLException 
	 * @see PersistenciaHiber#guardarObjetoSinId
	 * @see PersistenciaMysql#guardarObjetoSinId
	  */
	void guardarObjetoSinId(String tabla, ElementoPadre obj) throws ParseException, SQLException;

	//__CRUD INFORMES__
	
	/**
	  * Devuelve una lista ordenada de Animaltratamientos filtrados por especie, desde una fecha hasta otra. <br>
	  *  @param DescrEspecie String. Descripción de la especia
	  *  @param fechaDesde String. Fecha inicial. Formato de msql 'yyyy-MM-dd HH:mm:ss'
	  *  @param fechaHasta String. Fecha final. Formato de msql 'yyyy-MM-dd HH:mm:ss'
	  *  @see PersistenciaHiber#getAniTratsFechas
	 * @see PersistenciaMysql#getAniTratsFechas
	  */
	ArrayList<Animaltratamiento> getAniTratsFechas(String DescrEspecie, String fechaDesde, String fechaHasta) throws SQLException, Exception;

	void cancelarCambios() throws SQLException;
}
