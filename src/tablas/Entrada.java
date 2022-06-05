package tablas;
// Generated 14-feb-2022 13:11:52 by Hibernate Tools 4.0.1.Final

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;

import conexionBBDD.Persistencia;
import utilidades.Utilidades;

/**
 * Entrada generated by hbm2java
 */
public class Entrada extends ElementoPadre implements java.io.Serializable, Comparable<Entrada> {

	private Integer id;
	private Date fechaHoraVenta;
	private Evento evento;

	//___MÉTODOS PARA LA PERSISTENCIA JDBC
			public String[] getNombreCampos(){
				return new String[] {"id", "fechaHoraVenta", "idEvento"};
			}
		
			public String[] getCampos() throws ParseException {
				String fechaMysql = Utilidades.getFechaHoraStringMysql(fechaHoraVenta);
				return new String[] {String.valueOf(id), fechaMysql, String.valueOf(evento.getId())};
			}
			
			public void configurar(String[] valores, Persistencia per) throws SQLException, Exception {
				id=Integer.valueOf(valores[0]);
				fechaHoraVenta=Utilidades.getFechaHoraDateMysql(valores[1]);
				String idEvento=valores[2];		
				
				evento = (Evento) per.getObjetoMulti("Evento", new String[] {"id", idEvento});
			}
	//___MÉTODOS PARA LA PERSISTENCIA JDBC
			
	public Entrada() {
	}

	public Entrada(Evento evento, Date fechaHoraVenta) {
		this.evento = evento;
		this.fechaHoraVenta = fechaHoraVenta;
	}

	public Integer getId() {
		return this.id;
	}
	
	public Integer obtenerId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Evento getEvento() {
		return this.evento;
	}

	public void setEvento(Evento evento) {
		this.evento = evento;
	}

	public Date getFechaHoraVenta() {
		return this.fechaHoraVenta;
	}

	public void setFechaHoraVenta(Date fechaHoraVenta) {
		this.fechaHoraVenta = fechaHoraVenta;
	}

	public int compareTo(Entrada o) {
		return this.fechaHoraVenta.compareTo(o.fechaHoraVenta)*-1;
	}

}