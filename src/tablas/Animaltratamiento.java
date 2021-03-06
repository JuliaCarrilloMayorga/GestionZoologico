package tablas;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;

import conexionBBDD.Persistencia;
import utilidades.Utilidades;

// Generated 14-feb-2022 13:11:52 by Hibernate Tools 4.0.1.Final

/**
 * Animaltratamiento generated by hbm2java
 */
public class Animaltratamiento extends ElementoPadre implements java.io.Serializable {

	private AnimaltratamientoId id;
	private Animal animal;
	private Empleado empleado;
	private Tratamiento tratamiento;
	
	@Override
	public String toString() {
		return "Animaltratamiento [id=" + id + ", animal=" + animal + ", empleado=" + empleado + ", tratamiento="
				+ tratamiento + "]";
	}
	
	//___MÉTODOS PARA LA PERSISTENCIA JDBC
	
	public String[] getNombreCampos(){
		return new String[] {"idAnimal", "idEmpleado", "idTratamiento", "fechaHora"};
	}
	
	public String[] getCampos() throws ParseException {
		String fechaMysql = Utilidades.getFechaHoraStringMysql(getFechaHora());
		return new String[] {String.valueOf(animal.getId()), String.valueOf(empleado.getId()), String.valueOf(tratamiento.getId()), fechaMysql};
	}
	
	public void configurar(String[] valores, Persistencia per) throws SQLException, Exception {
		String idAnimal=valores[0];
		String idEmpleado=valores[1];
		String idTratamiento=valores[2];
		id = new AnimaltratamientoId(Integer.valueOf(idAnimal), Integer.valueOf(idEmpleado), Integer.valueOf(idTratamiento), Utilidades.getFechaHoraDateMysql(valores[3]));
		
		animal = (Animal) per.getObjetoMulti("Animal", new String[] {"id", idAnimal});
		empleado = (Empleado) per.getObjetoMulti("Empleado", new String[] {"id", idEmpleado});
		tratamiento = (Tratamiento) per.getObjetoMulti("Tratamiento", new String[] {"id", idTratamiento});
	}
	
	//___EQUALS

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Animaltratamiento other = (Animaltratamiento) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	//___MÉTODOS AUTOGENERADOS
	
	public Date getFechaHora() {
		return id.getFechaHora();
	}
	
	public Animaltratamiento() {
	}

	public Animaltratamiento(AnimaltratamientoId id, Animal animal, Empleado empleado, Tratamiento tratamiento) {
		this.id = id;
		this.animal = animal;
		this.empleado = empleado;
		this.tratamiento = tratamiento;
	}
	
	public AnimaltratamientoId getId() {
		return this.id;
	}
	
	public void setId(AnimaltratamientoId id) {
		this.id = id;
	}

	public Animal getAnimal() {
		return this.animal;
	}

	public void setAnimal(Animal animal) {
		this.animal = animal;
	}

	public Empleado getEmpleado() {
		return this.empleado;
	}

	public void setEmpleado(Empleado empleado) {
		this.empleado = empleado;
	}

	public Tratamiento getTratamiento() {
		return this.tratamiento;
	}

	public void setTratamiento(Tratamiento tratamiento) {
		this.tratamiento = tratamiento;
	}

}
