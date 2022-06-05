package interfaz;

import java.awt.Color;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.hibernate.exception.JDBCConnectionException;

import conexionBBDD.Persistencia;
import tablas.*;
import utilidades.Utilidades;

public class PROCESOTratamientosAnimal  extends PROCESOPadre { //TODO:Hacer que esto funcione

	//Padre y persistencia
	JFrame padre;
	Persistencia per;
	
	//Elementos empleados en este proceso
	protected Animal animal;
	protected Empleado empleado;
	protected Tratamiento tratamiento;

	protected ArrayList<Animaltratamiento> listaElesBorrar = new ArrayList<>();
	protected ArrayList<Animaltratamiento> listaElesGuardar = new ArrayList<>();
	
	//Animal
	private JComboBox comboBoxEspecie;
	private JTextField textFieldId;
	private JTextField textFieldNombre;
	
	private JComboBox comboBoxEmpleado;
	private JComboBox comboBoxTratamiento;
	private JButton btnReiniciar;
	private JButton btnBuscar;
	private JTextField textFieldFechayHora;
	
	/**
	 * Da contenido a nombreProceso y camposTabla, variables empleadas desde la clase padre PROCESOPadre. <br>
	 * nombreProceso: Nombre para los títulos
	 * camposTabla: campos con los que se configurará la tabla
	 */
	private void configurar() {
		nombreProceso="Tratamientos de los Animales";
		camposTabla = new String[] {"Nombre animal", "Especie animal", "Tratamiento", "Empleado", "Fecha"}; //Campos de la tabla
	}
	
	/**
	 * Devuelve el elemento principal de este proceso, el que será guardado por hibernate.
	 */

	public PROCESOTratamientosAnimal(JFrame padre, boolean modal, Persistencia per) {
		super(padre, modal, per);
		
		setResizable(false);
		this.padre = padre;
		this.per = per;

		configurar();
		
		setForeground(new Color(204, 255, 204));
		setTitle("Relación de "+nombreProceso);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 552, 409);

		labelTitulo.setText("Relación de "+nombreProceso);
		
		btnAniadir = new JButton("Añadir");
		btnAniadir.setEnabled(false);
		btnAniadir.setBounds(417, 208, 89, 23);
		panelUnico.add(btnAniadir);
		btnAniadir.setMnemonic('a');
		
		btnAniadir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aniadirProceso();
			}
		});
		
		//___________________________COSAS DEL PANEL ÚNICO
		
		setBounds(100, 100, 544, 574);
		panelUnico.setBounds(10, 54, 516, 242);
		panelFijo.setBounds(10, 307, 516, 221);
		btnAniadir.setBounds(417, 208, 89, 23);
		
		JLabel labelId = new JLabel("Id:");
		labelId.setBounds(318, 42, 67, 24);
		panelUnico.add(labelId);
		
		textFieldId = new JTextField();
		textFieldId.setEnabled(false);
		textFieldId.setColumns(10);
		textFieldId.setBounds(365, 42, 141, 20);
		panelUnico.add(textFieldId);
		
		JLabel lblNombre = new JLabel("Nombre:");
		lblNombre.setBounds(10, 42, 67, 24);
		panelUnico.add(lblNombre);
		
		textFieldNombre = new JTextField();
		textFieldNombre.setColumns(10);
		textFieldNombre.setBounds(72, 42, 214, 20);
		panelUnico.add(textFieldNombre);
		
		JLabel labelEspecie = new JLabel("Especie:");
		labelEspecie.setBounds(10, 79, 67, 24);
		panelUnico.add(labelEspecie);
		
		comboBoxEspecie = new JComboBox();
		comboBoxEspecie.setBounds(72, 79, 214, 20);
		panelUnico.add(comboBoxEspecie);
		
		btnBuscar = new JButton("Buscar");
		btnBuscar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buscarAnimal();
			}
		});
		btnBuscar.setMnemonic('b');
		btnBuscar.setBounds(318, 79, 89, 23);
		panelUnico.add(btnBuscar);
		
		btnReiniciar = new JButton("Reiniciar");
		btnReiniciar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reiniciarAnimal();
			}
		});
		btnReiniciar.setMnemonic('r');
		btnReiniciar.setBounds(417, 79, 89, 23);
		panelUnico.add(btnReiniciar);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(10, 116, 496, 11);
		panelUnico.add(separator);
		
		JLabel lblAnimal = new JLabel("        ANIMAL");
		lblAnimal.setBounds(10, 10, 496, 24);
		panelUnico.add(lblAnimal);
		
		//_A
		JLabel lblTratamiento = new JLabel("Tratamiento:");
		lblTratamiento.setBounds(10, 133, 95, 24);
		panelUnico.add(lblTratamiento);
		
		comboBoxTratamiento = new JComboBox();
		comboBoxTratamiento.setBounds(101, 132, 284, 20);
		panelUnico.add(comboBoxTratamiento);
		
		JLabel lblEmpleado = new JLabel("Empleado:");
		lblEmpleado.setBounds(10, 170, 95, 24);
		panelUnico.add(lblEmpleado);
		
		comboBoxEmpleado = new JComboBox();
		comboBoxEmpleado.setBounds(101, 170, 284, 20);
		panelUnico.add(comboBoxEmpleado);
		
		JLabel lblFechaYHora = new JLabel("Fecha y hora:");
		lblFechaYHora.setBounds(10, 207, 95, 24);
		panelUnico.add(lblFechaYHora);
		
		textFieldFechayHora = new JTextField();
		textFieldFechayHora.setEnabled(false);
		textFieldFechayHora.setColumns(10);
		textFieldFechayHora.setBounds(101, 207, 284, 20);
		
		textFieldFechayHora.setToolTipText("DD/MM/AAAA HH:MM");
		
		panelUnico.add(textFieldFechayHora);

		//___FIN COSAS PANEL ÚNICO
		
		mostrarTratamientos(comboBoxTratamiento);
		mostrarEmpleados(comboBoxEmpleado);
		mostrarEspecies(comboBoxEspecie);
		
		comboBoxTratamiento.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				configurarTabla(objetosTabla());
			}
		});
		
		comboBoxEmpleado.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				configurarTabla(objetosTabla());
			}
		});
		setLocationRelativeTo(padre);
		
		configurarTabla(objetosTabla());
	}
	
	
	//COSAS DEL ANIMAL 
	
	private void buscarAnimal() {
		String nombre = textFieldNombre.getText().trim().toUpperCase();
		String especie = (String)comboBoxEspecie.getSelectedItem();
		textFieldNombre.setText(nombre);
		
		try {
			Especie esp = (Especie)per.getObjetoMulti("Especie", new String[] {"descripcion", especie});
			Vector resultados = resultadosBusqueda(nombre, esp);
			String[][] parametros = {{"Nombre", nombre}, {"Especie", especie}};
			
			CRUDAnimales animalesPadre = new CRUDAnimales(null, true, per);
			
			CRUDTablaGeneral ventanaAnimales = new CRUDTablaGeneral(animalesPadre, this, true, resultados, parametros);
			ventanaAnimales.getBtnCrear().setVisible(false);
			ventanaAnimales.setVisible(true);
			
		} catch (SQLException | JDBCConnectionException e) {
			Utilidades.notificaError(padre, "Error de conexión", e, "Probablemente se ha perdido la conexión con MySql. \n"
					+ "Error fatal, el programa se cerrará a continuación");
			e.printStackTrace();
			System.exit(0);
		} catch (Exception e) {
			Utilidades.notificaError(padre, "Error interno", e, "Hay un error en el código del programa.\n"
					+ "Error fatal, el programa se cerrará a continuación");
			e.printStackTrace();
			System.exit(0);
		}
	}

	public void mostrarAnimal(String[] paramsDevolver) {
		String nombre = paramsDevolver[0];
		String especie = paramsDevolver[1];		
		
		try {
			Especie esp = (Especie)per.getObjetoMulti("Especie", new String[] {"descripcion", especie});
			Animal animal = (Animal)per.getObjetoMulti("Animal", new String[] {"nombre", nombre, "idEspecie", String.valueOf(esp.getId())});
			
			textFieldId.setText(String.valueOf(animal.getId()));
			textFieldNombre.setText(String.valueOf(animal.getNombre()));
			comboBoxEspecie.setSelectedItem(animal.getEspecie().getDescripcion());
			
			textFieldNombre.setEnabled(false);
			comboBoxEspecie.setEnabled(false);
			btnBuscar.setEnabled(false);
			
			configurarTabla(objetosTabla());		
			
		} catch (SQLException | JDBCConnectionException e) {
			Utilidades.notificaError(padre, "Error de conexión", e, "Probablemente se ha perdido la conexión con MySql. \n"
					+ "Error fatal, el programa se cerrará a continuación");
			e.printStackTrace();
			System.exit(0);
		} catch (Exception e) {
			Utilidades.notificaError(padre, "Error interno", e, "Hay un error en el código del programa.\n"
					+ "Error fatal, el programa se cerrará a continuación");
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public void reiniciarAnimal(){
		comboBoxEspecie.setSelectedIndex(0);
		textFieldId.setText("");
		textFieldNombre.setText("");
		textFieldFechayHora.setText("");
		
		textFieldNombre.setEnabled(true);
		comboBoxEspecie.setEnabled(true);
		btnBuscar.setEnabled(true);
		
		btnAniadir.setEnabled(false);
		textFieldFechayHora.setEnabled(false);
		
		configurarTabla(objetosTabla());
	}
	
	//__FIN COSAS DEL ANIMAL
	
	/**
	 * Devuelve el formulario a su estado inicial. <br>
	 * Reinicia los elementos únicos de este proceso y llama a {@link PROCESOPadre#cancelar} de su clase padre.
	 */
	protected void cancelar() {
		comboBoxEmpleado.setSelectedIndex(0);
		comboBoxTratamiento.setSelectedIndex(0);
		
		reiniciarAnimal();
		
		super.cancelar();
	}
	
	/**
	 * Elimina un registro de proceso. <br>
	 * Selecciona la fila seleccionada en la tabla y busca el objeto que le corresponde. <br>
	 * Añade el elemento a borrar al ArrayList listaElesBorrar. <br>
	 * Quita la línea de la tabla y activa/desactiva el botón de guardar.
	 */
	protected void eliminarProceso() {
		int fila = super.table.getSelectedRow();
		
		try {
			// {"Nombre animal", "Especie animal", "Tratamiento", "Empleado", "Fecha"};
			String animalNombreS = String.valueOf(table.getModel().getValueAt(fila, 0));
			String animalEspS = String.valueOf(table.getModel().getValueAt(fila, 1));
			Especie especie = (Especie)per.getObjetoMulti("Especie", new String[] {"descripcion", animalEspS});
			Animal a = (Animal)per.getObjetoMulti("Animal", new String[] {"nombre", animalNombreS, "idEspecie", String.valueOf(especie.getId())});
			
			String tratS = String.valueOf(table.getModel().getValueAt(fila, 2));
			Tratamiento t  = (Tratamiento)per.getObjetoMulti("Tratamiento", new String[] {"descripcion", tratS});
			
			String empS = String.valueOf(table.getModel().getValueAt(fila, 3));
			Empleado e = (Empleado)per.getObjetoMulti("Empleado", new String[] {"nombre", empS});
			
			String fecha=String.valueOf(table.getModel().getValueAt(fila, 4));
			try {
				fecha = Utilidades.getFechaHoraStringMysql(fecha);
			} catch (ParseException e1) {
				Utilidades.notificaError(padre, "Error interno", e1, "Hay un error en el código del programa.");
			}
			
			Animaltratamiento aniTratamiento = (Animaltratamiento)per.getObjetoMulti("Animaltratamiento", new String[] 
					{"idAnimal", String.valueOf(a.getId()), 
							"idEmpleado", String.valueOf(e.getId()),
							"idTratamiento", String.valueOf(t.getId()),
							"fechaHora", fecha
					});
			
			if(aniTratamiento!=null) { //Si no es null, la entrada ya existe, tiene un id asignado
				listaElesBorrar.add(aniTratamiento);
				btnGuardar.setEnabled(true);
			} else { // Si es null es uno de los que se han añadido sin guardar, se busca una coincidencia en el array de aniTrats nuevos y se elimina
				fecha=String.valueOf(table.getModel().getValueAt(fila, 4));
				
				for (int i = 0; i < listaElesGuardar.size(); i++) {
					Animaltratamiento posible = listaElesGuardar.get(i);
					
					if(Utilidades.getFechaHoraString(posible.getFechaHora()).equals(fecha)) {
						listaElesGuardar.remove(i);
						
						btnGuardar.setEnabled(false);
						btnAniadir.setEnabled(true);
						textFieldFechayHora.setEnabled(true);
						
						break;
					}	
				}
			}
			
			((DefaultTableModel)table.getModel()).removeRow(fila);
			
		} catch (SQLException | JDBCConnectionException e) {
			Utilidades.notificaError(padre, "Error de conexión", e, "Probablemente se ha perdido la conexión con MySql. \n"
					+ "Error fatal, el programa se cerrará a continuación");
			e.printStackTrace();
			System.exit(0);
		} catch (Exception e) {
			Utilidades.notificaError(padre, "Error interno", e, "Hay un error en el código del programa.\n"
					+ "Error fatal, el programa se cerrará a continuación");
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	
	/**
	 * Añade un registro e proceso.
	 * Añade el elemento a borrar al ArrayList listaElesBorrar. <br>
	 * Activa el botón de Guardar y desactiva el de añadir.
	 */
	protected void aniadirProceso() {
		if(validarCampos()) {
			Date d=null;
			try {
				d = Utilidades.getFechaHoraDate(textFieldFechayHora.getText().trim());
			} catch (ParseException e) {
				Utilidades.notificaError(padre, "Error interno", e, "Hay un error en el código del programa.");
			}
			
			AnimaltratamientoId aniTratamientoId = new AnimaltratamientoId(animal.getId(), empleado.getId(), tratamiento.getId(), d);
			Animaltratamiento aniTratamiento = new Animaltratamiento(aniTratamientoId, animal, empleado, tratamiento);
	
			listaElesGuardar.add(aniTratamiento);
			
			((DefaultTableModel)table.getModel()).addRow(configurarVectorObjeto(aniTratamiento));
			btnGuardar.setEnabled(true);
		}
	}
	
	private boolean validarCampos(){
		String fecha = textFieldFechayHora.getText().trim();
		
		if(!Utilidades.isFechaHoraValida(fecha)){ //Comprobar que Fecha es valida o vacía
			JOptionPane.showMessageDialog(this, "La fecha debe tener formato DD/MM/AAAA HH:MM.", "Fecha inválida", JOptionPane.WARNING_MESSAGE);
			return false;
		}
		
		Set<Tratamiento> tratamientos = empleado.getTratamientos(per);
		if(!tratamientos.contains(tratamiento)) {
			JOptionPane.showMessageDialog(this, "El trabajador "+empleado.getNombre()+" no está capacitado para realizar el tratamiento "+tratamiento.getDescripcion(), "Trabajador-Tratamiento no válidos", JOptionPane.WARNING_MESSAGE);
			return false;
		}
		
		//Comprueba si existe ese animalTratamiento
		String fechaMysql="";
		try {
			fechaMysql = Utilidades.getFechaHoraStringMysql(fecha);
		} catch (ParseException e1) {
			Utilidades.notificaError(padre, "Error interno", e1, "Hay un error en el código del programa.");
		}
		
		try {
			Animaltratamiento aniTratamientoId = (Animaltratamiento)per.getObjetoMulti("Animaltratamiento", new String[] 
					{"idAnimal", String.valueOf(animal.getId()), 
							"idEmpleado", String.valueOf(empleado.getId()),
							"idTratamiento", String.valueOf(tratamiento.getId()),
							"fechaHora", fechaMysql
					});
			
			if(aniTratamientoId!=null) {
				JOptionPane.showMessageDialog(this, "Ya existe un tratamiento de animal con estos parámetros.", "Tratamiento de animal ya existente", JOptionPane.WARNING_MESSAGE);
				return false;
			}
			
			//Coprueba si ya se ha añadido un animal con esa fecha
			for (int i = 0; i < listaElesGuardar.size(); i++) {
				Animaltratamiento e = listaElesGuardar.get(i);
				
				if(Utilidades.getFechaHoraString(e.getId().getFechaHora()).equals(fecha)) {
					JOptionPane.showMessageDialog(this, "Ya ha registrado un tratamiento el animal con estos parámetros.", "Tratamiento de animal ya existente", JOptionPane.WARNING_MESSAGE);
					return false;	
				}	
			}
			return true;
		} catch (SQLException | JDBCConnectionException e) {
			Utilidades.notificaError(padre, "Error de conexión", e, "Probablemente se ha perdido la conexión con MySql. \n"
					+ "Error fatal, el programa se cerrará a continuación");
			e.printStackTrace();
			System.exit(0);
		} catch (Exception e) {
			Utilidades.notificaError(padre, "Error interno", e, "Hay un error en el código del programa.\n"
					+ "Error fatal, el programa se cerrará a continuación");
			e.printStackTrace();
			System.exit(0);
		}
		return true;
	}
	
	/**
	 * Guarda los elementos añadidos y borra los eliminados.
	 */
	protected void guardarCambios() {	
		String mensaje="Va a eliminar "+listaElesBorrar.size()+ " tratamiento" + (listaElesBorrar.size()==1?"":"es") + " de animales.\n"
				+ " Va a añadir "+listaElesGuardar.size()+ " tratamiento" + (listaElesGuardar.size()==1?"":"es") + " de animales."
						+ "\n¿Está de acuerdo?" ;
		
		int res = JOptionPane.showConfirmDialog(this, mensaje, "Guardar cambios", JOptionPane.YES_NO_OPTION);
		
		if(res==JOptionPane.YES_OPTION) {
			try {
				for (int i = 0; i < listaElesGuardar.size(); i++) {
					per.guardarObjetoSinId("Animaltratamiento", listaElesGuardar.get(i));
				}
				
				for (int i = 0; i < listaElesBorrar.size(); i++) {
					per.eliminarObjeto("Animaltratamiento", listaElesBorrar.get(i));
				}
				
				btnGuardar.setEnabled(false);
				
			} catch (SQLException | JDBCConnectionException e) {
				Utilidades.notificaError(padre, "Error de conexión", e, "Probablemente se ha perdido la conexión con MySql. \n"
						+ "Error fatal, el programa se cerrará a continuación");
				e.printStackTrace();
				System.exit(0);
			} catch (Exception e) {
				Utilidades.notificaError(padre, "Error interno", e, "Hay un error en el código del programa.\n"
						+ "Error fatal, el programa se cerrará a continuación");
				e.printStackTrace();
				System.exit(0);
			}
		}
		configurarTabla(objetosTabla());
	}
	
	/**
	 * Actualiza los objetos en estado Persistente. <br>
	 * Desactiva el botón de Guardar, busca los elementos seleccionados mediante {@link #configurarElementos}, <br>
	 * según se han seleccionado todos o no, rellena el array de objetos utilizado para completar la tabla. <br>
	 * Habilita el botón Añadir si se han completado todos los elementos necesarios.<br>
	 * Se llama a {@link #configurarVectorObjeto} para generar el vector de vectores.
	 * @return Devuelve un vector de vectores, cada uno correspondiente a una línea de la tabla, con sus datos en Strings.
	 */
	protected Vector objetosTabla(){
		if (animal!=null) {
			per.actualizarSesion(animal);
		}
		if (empleado!=null) {
			per.actualizarSesion(empleado);
		}
		if (tratamiento!=null) {
			per.actualizarSesion(tratamiento);
		}
		btnGuardar.setEnabled(false);
		
		configurarElementos();
		
		listaElesBorrar = new ArrayList<>();
		listaElesGuardar = new ArrayList<>();
		
		Set<Animaltratamiento> aniTrats=new HashSet(0);
		
		btnAniadir.setEnabled(false);
		textFieldFechayHora.setEnabled(false);
		textFieldFechayHora.setText("");
		
		if(animal!=null) {//si se ha seleccionado animal, coge sus aniTrats
			aniTrats.addAll(animal.getAnimaltratamientos(per));
			if(tratamiento!=null) {  //Si se ha seleccionado tratamiento, retiene sólo los comunes entre ambos conjuntos
				aniTrats.retainAll(tratamiento.getAnimaltratamientos(per));
			}
			if(empleado!=null) {//Si se ha seleccionado empleado, retiene sólo los comunes entre ambos conjuntos
				aniTrats.retainAll(empleado.getAnimaltratamientos(per));
				if(tratamiento!=null) { ////Si TAMBIÉN se ha seleccionado tratamiento, activa la opción de añadir uno nuevo
					btnAniadir.setEnabled(true);
					textFieldFechayHora.setEnabled(true);
				}
			}
		} else if (tratamiento!=null) {//Si el animal no está seleccionado y el tratamiento si, se coge su conjunto
			aniTrats.addAll(tratamiento.getAnimaltratamientos(per));
			if(empleado!=null) {//Si se ha seleccionado empleado, retiene sólo los comunes entre ambos conjuntos
				aniTrats.retainAll(empleado.getAnimaltratamientos(per));
			}
		} else if (empleado!=null) {//Si ni el animal ni el tratamiento están seleccionado y el empleado si, se coge su conjunto
			aniTrats.addAll(empleado.getAnimaltratamientos(per));
		} 
		
		Vector objs = new Vector();
		for (Animaltratamiento aniTrat : aniTrats) { //{"Nombre animal", "Especie animal", "Tratamiento", "Empleado", "Fecha y hora"};
			Vector e =  configurarVectorObjeto(aniTrat);
			objs.add(e);
		}
		
		return objs;
	}
	
	/**
	 * Busca mediante la persistencia los objetos seleccionados de este proceso. <br>
	 * Si no se ha seleccionado nada, lo setea a null directamente.
	 */
	private void configurarElementos(){
		String idS = textFieldId.getText().toUpperCase();
		
		try {
			if(idS.equals("")) {
				animal=null;
			} else {
				animal = (Animal)per.getObjetoMulti("Animal", new String[] {"id", idS});
			}
			
			String tratS = (String)comboBoxTratamiento.getSelectedItem();
			if(tratS.equals("")) {
				tratamiento=null;
			} else {
				tratamiento = (Tratamiento)per.getObjetoMulti("Tratamiento", new String[] {"descripcion", tratS});
			}
			
			String empS = (String)comboBoxEmpleado.getSelectedItem();
			if(empS.equals("")) {
				empleado=null;
			} else {
				empleado = (Empleado)per.getObjetoMulti("Empleado", new String[] {"nombre", empS});
			}
			
		} catch (SQLException | JDBCConnectionException e) {
			Utilidades.notificaError(padre, "Error de conexión", e, "Probablemente se ha perdido la conexión con MySql. \n"
					+ "Error fatal, el programa se cerrará a continuación");
			e.printStackTrace();
			System.exit(0);
		} catch (Exception e) {
			Utilidades.notificaError(padre, "Error interno", e, "Hay un error en el código del programa.\n"
					+ "Error fatal, el programa se cerrará a continuación");
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	/**
	 * Configura el vector con los atributos del objeto recibido como parámetro.
	 * @return Vector de Strings de los atributos del objeto recibido.
	 */
	private Vector configurarVectorObjeto(Animaltratamiento aniTrat){ //{"Nombre animal", "Especie animal", "Tratamiento", "Empleado", "Fecha y hora"};
		Vector e =  new Vector();

		e.add(aniTrat.getAnimal().getNombre());
		e.add(aniTrat.getAnimal().getEspecie().getDescripcion());
		
		e.add(aniTrat.getTratamiento().getDescripcion());
		
		e.add(aniTrat.getEmpleado().getNombre());
		
		Date d = aniTrat.getId().getFechaHora();			
		e.add(Utilidades.getFechaHoraString(d));
		
		return e;
	}
	
	protected void configTamanioCols(){
		int tamanio1 = 105;
		
		TableColumn columna;
		columna=table.getColumnModel().getColumn(4);
		columna.setPreferredWidth(tamanio1);
		columna.setMaxWidth(tamanio1);
		columna.setMinWidth(5);
	}
}
