package conexionBBDD;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import tablas.*;

public class PersistenciaHiber implements Persistencia{
	Session sesion;

	//Crea la persistencia
	 public PersistenciaHiber(String fichCfg, boolean mostrarSQL) {
		    Configuration configuration = new Configuration();
		    configuration.configure(fichCfg);
		    if (mostrarSQL)
		    	configuration.setProperty("hibernate.show_sql", "true");
		    
		    ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
		    SessionFactory sessionFactory = configuration.buildSessionFactory(serviceRegistry);
		    sesion=sessionFactory.openSession();
		}
	 
	//_______________________________   CRUD BÁSICOS   _______________________________________________________________________
	 /**
	  * Guarda un objeto con SAVE
	  *  @param tabla Nombre de la tabla/objeto, con la primera letra en mayúsculas.
	  *  @param obj objeto a guardar, debe ser heredero de ElementoPadre
	  *  @return El id del objeto guardado
	  */
	 public int guardarObjeto(String tabla, ElementoPadre obj) {
		 sesion.beginTransaction();
		 sesion.save(obj);
		 sesion.getTransaction().commit();

		 return obj.obtenerId();
	 }

	 /**
	  * Elimina un objeto con DELETE
	  * @param tabla Nombre de la tabla/objeto, con la primera letra en mayúsculas.
	  * @param obj objeto a eliminar, debe ser heredero de ElementoPadre
	  */
	 public void eliminarObjeto(String tabla, ElementoPadre obj) {
		 sesion.beginTransaction();
		 sesion.delete(obj);
		 sesion.getTransaction().commit();		
	 }

	 /**
	  * Devuelve una lista con todos los objetos de una tabla
	  * @param tabla Nombre de la tabla/objeto, con la primera letra en mayúsculas.
	  */
	 public List getTodosObjetos(String tabla) {
		 Query query = sesion.createQuery("SELECT e FROM " + tabla + " e");

		 List res = query.list();
		 return res;
	 }

	 /**
	  * Devuelve una lista con los objetos de una tabla filtrados por algunos de sus campos mediante LIKE y otros de sus campos mediante '='.
	  * @param tabla Nombre de la tabla/objeto, con la primera letra en mayúsculas.
	  * @param camposLike Array de Strings, ordenados en pares. De cada par, el primero marca el nombre del campo y el segundo el valor. Estos campos serán condición tipo LIKE.
	  * @param camposIgual Array de Strings, ordenados en pares. De cada par, el primero marca el nombre del campo y el segundo el valor. Estos campos serán condición tipo '='.
	  */
	 public List getObetosFiltradoMulti(String tabla, String[] camposLike, String[] camposIgual) { //campo, contenido, campo, contenido...
		 String contenidoQuery="SELECT z FROM " + tabla + " z WHERE ";

		 for (int i = 0; i < camposLike.length; i++) { //Por cada pareja de elementos de camposLike recorre el bucle y le añade una linea con un LIKE al query
			 contenidoQuery += camposLike[i] + " LIKE " + "'%"+camposLike[++i]+"%'";

			 if(i != camposLike.length-1) { //Si no es la última vuelta, añade un AND
				 contenidoQuery += " AND ";
			 }
		 }

		 if(camposLike.length>0 && camposIgual.length>0) { //Si hay elementos de camposLike y también de camposIgual, añade un AND para juntar las condiciones generadas
			 contenidoQuery += " AND ";
		 }
 
		 for (int i = 0; i < camposIgual.length; i++) { //Por cada pareja de elementos de camposIgual recorre el bucle y le añade una linea con un '=' al query
			 contenidoQuery += camposIgual[i] + " = " + "'"+camposIgual[++i]+"'";

			 if(i != camposIgual.length-1) { //Si no es la última vuelta, añade un AND
				 contenidoQuery += " AND ";
			 }
		 }

		 Query query = sesion.createQuery(contenidoQuery);
		 List res = query.list();
		 return res;
	 }

	 /**
	  * Devuelve UN objeto filtrado por algunos de sus campos mediante mediante '='.
	  * @param tabla Nombre de la tabla/objeto, con la primera letra en mayúsculas.
	  * @param campos Array de Strings, ordenados en pares. De cada par, el primero marca el nombre del campo y el segundo el valor. Estos campos serán condición tipo LIKE.
	  */
	 public Object getObjetoMulti(String tabla, String[] campos) { //campo, contenido, campo, contenido...
		 String contenidoQuery = "SELECT e FROM " + tabla + " e WHERE ";

		 for (int i = 0; i < campos.length; i++) { //Por cada pareja de elementos de campos recorre el bucle y le añade una linea con un '=' al query
			 contenidoQuery += campos[i] + " = " + "'"+campos[++i]+"'";

			 if(i != campos.length-1) {
				 contenidoQuery += " AND "; //Si no es la última vuelta, añade un AND
			 }
		 }

		 Query query = sesion.createQuery(contenidoQuery);
		 Object obj= query.uniqueResult();
		 return obj;
	 }


	 //_______________________________   CRUD RELACIONES   _______________________________________________________________________
	 
	 /**
	  * Refresca un objeto persistente. Sólo para Hibernate.
	  * @param obj Objeto heredero de ElementoPadre a refrescar.
	  */
	 public void actualizarSesion( ElementoPadre obj) {
		sesion.refresh(obj);
	 }

	 /**
	  * Guarda un objeto con SAVE <br>
	  * No devuelve nada. Para objetos cuyo id no es un int, sino otro objeto.
	  *  @param tabla Nombre de la tabla/objeto, con la primera letra en mayúsculas.
	  *  @param obj objeto a guardar, debe ser heredero de ElementoPadre
	  */
	 public void guardarObjetoSinId(String tabla, ElementoPadre obj) {
		 sesion.beginTransaction();
		 sesion.save(obj);
		 sesion.getTransaction().commit();
	 }

	 /**
	  * Borra una relación m...n <br>
	  * Hibernate: Elimina del set de un objeto el otro objeto, y viceversa.
	  *  @param tabla Nombre de la tabla que guarda la relación, en minúsculas.
	  *  @param elementos Objetos a desrelacionar (Solo útil para Hibernate)
	  *  @param campos Campos para determinar mediante WHERE los objetos a desrelacionar (Solo útil para JDBC)
	  */
	 public void borrarProceso(String tabla, ElementoPadre[] elementos, String[] campos) {
		 switch (tabla) {
		 case "trabaja": //Elimina relaciones de Empleados con Zonas
		 {
			 Zona zona = (Zona) elementos[0];
			 Empleado empleado = (Empleado) elementos[1];

			 zona.getEmpleados().remove(empleado);
			 empleado.getZonas().remove(zona);
			 break;
		 }
		 case "escapaz": //Elimina relaciones de Empleados con Tratamientos
		 {
			 Tratamiento tratamiento = (Tratamiento) elementos[0];
			 Empleado empleado = (Empleado) elementos[1];

			 tratamiento.getEmpleados().remove(empleado);
			 empleado.getTratamientos().remove(tratamiento);
			 break;
		 }
		 default:
			 break;
		 }
	 }

	 /**
	  * Guarda una relación m...n <br>
	  * Hibernate: Añade al set de un objeto el otro objeto, y viceversa.
	  *  @param tabla Nombre de la tabla que guarda la relación, en minúsculas.
	  *  @param elementos Objetos a relacionar (Solo útil para Hibernate)
	  *  @param campos Campos para determinar mediante WHERE los objetos a relacionar (Solo útil para JDBC)
	  */
	 public void aniadirProceso(String tabla, ElementoPadre[] elementos, String[] campos) {
		 switch (tabla) {
		 case "trabaja": //Guarda relaciones de Empleados con Zonas
		 {
			 Zona zona = (Zona) elementos[0];
			 Empleado empleado = (Empleado) elementos[1];

			 zona.getEmpleados().add(empleado);
			 empleado.getZonas().add(zona);
			 break;
		 }
		 case "escapaz"://Guarda relaciones de Empleados con Tratamientos
		 {
			 Tratamiento tratamiento = (Tratamiento) elementos[0];
			 Empleado empleado = (Empleado) elementos[1];

			 tratamiento.getEmpleados().add(empleado);
			 empleado.getTratamientos().add(tratamiento);
			 break;
		 }
		 default:
			 break;
		 }			
	 }

	 /**
	  * Devuelve una lista ordenada de Animaltratamientos filtrados por especie, desde una fecha hasta otra. <br>
	  *  @param DescrEspecie String. Descripción de la especia
	  *  @param fechaDesde String. Fecha inicial. Formato de msql 'yyyy-MM-dd HH:mm:ss'
	  *  @param fechaHasta String. Fecha final. Formato de msql 'yyyy-MM-dd HH:mm:ss'
	  */
	 public ArrayList<Animaltratamiento> getAniTratsFechas(String DescrEspecie, String fechaDesde, String fechaHasta) {
		Query query = sesion.createQuery("SELECT a FROM Animaltratamiento a INNER JOIN a.animal n INNER JOIN n.especie e"
				+ " WHERE e.descripcion=? AND a.id.fechaHora BETWEEN ? AND ? "
				+ "ORDER BY a.id.fechaHora DESC, n.nombre ASC");
		
		query.setString(0, DescrEspecie);
		query.setString(1, fechaDesde+" 00:00");	
		query.setString(2, fechaHasta+" 23:59");	
		
		ArrayList<Animaltratamiento> res = (ArrayList<Animaltratamiento>) query.list();
		
		return res;
	}

	 
	//Sólo Mysql
	public List getObetosSetMNSimple(String claseSet, String tablaIntermedia, String claseBase, Integer id)
			throws SQLException, Exception {return null;}

	public void cancelarCambios() throws SQLException {}

}
