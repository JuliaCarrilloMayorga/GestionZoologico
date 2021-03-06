package tablas;
// Generated 14-feb-2022 13:11:52 by Hibernate Tools 4.0.1.Final

import java.sql.SQLException;
import java.text.ParseException;

import conexionBBDD.Persistencia;
import utilidades.Utilidades;

/**
 * Consume generated by hbm2java
 */
public class Consume extends ElementoPadre implements java.io.Serializable {

	private ConsumeId id;
	private Animal animal;
	private Alimento alimento;
	private int cantidadDia;

	//___MÉTODOS PARA LA PERSISTENCIA JDBC
	
	public String[] getNombreCampos(){
		return new String[] {"idAnimal", "idAlimento", "cantidadDia"};
	}
	
	public String[] getCampos() throws ParseException {
		return new String[] {String.valueOf(animal.getId()), String.valueOf(alimento.getId()), String.valueOf(cantidadDia)};
	}
	
	public void configurar(String[] valores, Persistencia per) throws SQLException, Exception {
		String idAnimal=valores[0];
		String idAlimento=valores[1];
		id = new ConsumeId(Integer.valueOf(idAnimal), Integer.valueOf(idAlimento));
		
		animal = (Animal) per.getObjetoMulti("Animal", new String[] {"id", idAnimal});
		alimento = (Alimento) per.getObjetoMulti("Alimento", new String[] {"id", idAlimento});
		cantidadDia = Integer.valueOf(valores[2]);
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
		Consume other = (Consume) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	//___MÉTODOS AUTOGENERADOS
	
	public Consume() {
	}

	public Consume(ConsumeId id, Animal animal, Alimento alimento, int cantidadDia) {
		this.id = id;
		this.animal = animal;
		this.alimento = alimento;
		this.cantidadDia = cantidadDia;
	}

	public ConsumeId getId() {
		return this.id;
	}
	
	public void setId(ConsumeId id) {
		this.id = id;
	}

	public Animal getAnimal() {
		return this.animal;
	}

	public void setAnimal(Animal animal) {
		this.animal = animal;
	}

	public Alimento getAlimento() {
		return this.alimento;
	}

	public void setAlimento(Alimento alimento) {
		this.alimento = alimento;
	}

	public int getCantidadDia() {
		return this.cantidadDia;
	}

	public void setCantidadDia(int cantidadDia) {
		this.cantidadDia = cantidadDia;
	}

}
