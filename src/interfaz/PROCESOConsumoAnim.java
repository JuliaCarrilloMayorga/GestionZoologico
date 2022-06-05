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

public class PROCESOConsumoAnim  extends PROCESOPadre { //TODO:Hacer que esto funcione

	//Padre y persistencia
	JFrame padre;
	Persistencia per;
	
	//Elementos empleados en este proceso
	protected Animal animal;
	protected Alimento alimento;

	protected ArrayList<Consume> listaElesBorrar = new ArrayList<>();
	protected ArrayList<Consume> listaElesGuardar = new ArrayList<>();
	
	//Animal
	private JComboBox comboBoxEspecie;
	private JTextField textFieldId;
	private JTextField textFieldNombre;
	
	private JComboBox comboBoxAlimento;
	private JTextField textFieldCantidad;
	
	private JButton btnReiniciar;
	private JButton btnBuscar;
	
	/**
	 * Da contenido a nombreProceso y camposTabla, variables empleadas desde la clase padre PROCESOPadre. <br>
	 * nombreProceso: Nombre para los títulos
	 * camposTabla: campos con los que se configurará la tabla
	 */
	private void configurar() {
		nombreProceso="Alimentos de los animales";
		camposTabla = new String[] {"Nombre animal", "Especie animal", "Alimento", "Cantidad"}; //Campos de la tabla
	}
	
	/**
	 * Devuelve el elemento principal de este proceso, el que será guardado por hibernate.
	 */

	public PROCESOConsumoAnim(JFrame padre, boolean modal, Persistencia per) {
		super(padre, modal, per);
		
		setResizable(false);
		this.padre = padre;
		this.per = per;

		configurar();
		
		setForeground(new Color(204, 255, 204));
		setTitle("Relación de "+nombreProceso);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 544, 541);

		labelTitulo.setText("Relación de "+nombreProceso);
		
		btnAniadir = new JButton("Añadir");
		btnAniadir.setEnabled(false);
		btnAniadir.setMnemonic('a');
		panelUnico.add(btnAniadir);
		
		btnAniadir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aniadirProceso();
			}
		});
		
		//___________________________COSAS DEL PANEL ÚNICO
		
		setBounds(100, 100, 542, 536);
		panelUnico.setBounds(10, 54, 516, 204);
		panelFijo.setBounds(10, 269, 516, 221);
		btnAniadir.setBounds(417, 170, 89, 23);
		
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
		
		
		//__A
		JLabel lblAlimento = new JLabel("Alimento:");
		lblAlimento.setBounds(10, 133, 67, 24);
		panelUnico.add(lblAlimento);
		
		comboBoxAlimento = new JComboBox();
		comboBoxAlimento.setBounds(101, 133, 284, 20);
		panelUnico.add(comboBoxAlimento);
		
		JLabel lblFechaYHora = new JLabel("Cantidad:");
		lblFechaYHora.setBounds(10, 170, 67, 24);
		panelUnico.add(lblFechaYHora);
		
		textFieldCantidad = new JTextField();
		textFieldCantidad.setColumns(10);
		textFieldCantidad.setBounds(101, 170, 284, 20);
		
		panelUnico.add(textFieldCantidad);

		//___FIN COSAS PANEL ÚNICO
		
		mostrarAlimentos(comboBoxAlimento);
		mostrarEspecies(comboBoxEspecie);
		
		comboBoxAlimento.addActionListener(new ActionListener() {
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
		textFieldCantidad.setText("");
		
		textFieldNombre.setEnabled(true);
		comboBoxEspecie.setEnabled(true);
		btnBuscar.setEnabled(true);
		
		btnAniadir.setEnabled(false);
		textFieldCantidad.setEnabled(false);
		
		configurarTabla(objetosTabla());
	}
	
	//__FIN COSAS DEL ANIMAL
	
	/**
	 * Devuelve el formulario a su estado inicial. <br>
	 * Reinicia los elementos únicos de este proceso y llama a {@link PROCESOPadre#cancelar} de su clase padre.
	 */
	protected void cancelar() {
		comboBoxAlimento.setSelectedIndex(0);
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
			// {"Nombre animal", "Especie animal", "Alimento", "Cantidad"};
			String animalNombreS = String.valueOf(table.getModel().getValueAt(fila, 0));
			String animalEspS = String.valueOf(table.getModel().getValueAt(fila, 1));
			Especie especie = (Especie)per.getObjetoMulti("Especie", new String[] {"descripcion", animalEspS});
			Animal a = (Animal)per.getObjetoMulti("Animal", new String[] {"nombre", animalNombreS, "idEspecie", String.valueOf(especie.getId())});
			
			String aliS = String.valueOf(table.getModel().getValueAt(fila, 2));
			Alimento t  = (Alimento)per.getObjetoMulti("Alimento", new String[] {"descripcion", aliS});
			
			Consume consume = (Consume)per.getObjetoMulti("Consume", new String[] 
					{"idAnimal", String.valueOf(a.getId()), 
							"idAlimento", String.valueOf(t.getId())
					});
			
			if(consume!=null) { //Si no es null, la entrada ya existe, tiene un id asignado
				listaElesBorrar.add(consume);	
				btnGuardar.setEnabled(true); 
			} else { // Si es null es una que se acaba de añadir y se va a rectificar, se limpia listaElesGuardar y se acviva Añadir
				listaElesGuardar.clear();
				btnGuardar.setEnabled(false);
				btnAniadir.setEnabled(true);
				textFieldCantidad.setEnabled(true);
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
			int cantidad = Integer.valueOf(textFieldCantidad.getText());
			
			ConsumeId consumeId = new ConsumeId(animal.getId(), alimento.getId());
			Consume consume = new Consume(consumeId, animal, alimento, cantidad);
	
			listaElesGuardar.add(consume);
			
			((DefaultTableModel)table.getModel()).addRow(configurarVectorObjeto(consume));
			btnGuardar.setEnabled(true);
			btnAniadir.setEnabled(false);
			textFieldCantidad.setEnabled(false);
			textFieldCantidad.setText("");
		}
	}
	
	private boolean validarCampos(){
		String cantidad = textFieldCantidad.getText();
		
		if(!cantidad.matches("[1-9][0-9]*")) {
			JOptionPane.showMessageDialog(this, "La CANTIDAD debe ser un número entero positivo.", "Campo incorrecto", JOptionPane.WARNING_MESSAGE);
			return false;
		}
		return true;
	}
	
	/**
	 * Guarda los elementos añadidos y borra los eliminados.
	 */
	protected void guardarCambios() {	
		String mensaje="Va a eliminar "+listaElesBorrar.size()+ " alimento" + (listaElesBorrar.size()==1?"":"s") + " de animales.\n"
				+ " Va a añadir "+listaElesGuardar.size()+ " alimento"+ (listaElesGuardar.size()==1?"":"s") +" de animales."
						+ "\n¿Está de acuerdo?" ;
		
		int res = JOptionPane.showConfirmDialog(this, mensaje, "Guardar cambios", JOptionPane.YES_NO_OPTION);
		
		if(res==JOptionPane.YES_OPTION) {
			try {
				for (int i = 0; i < listaElesBorrar.size(); i++) {
					per.eliminarObjeto("Consume", listaElesBorrar.get(i));
				}
				
				for (int i = 0; i < listaElesGuardar.size(); i++) {
					per.guardarObjetoSinId("Consume", listaElesGuardar.get(i));
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
	 * Habilita el botón Añadir si se han completado todos los elementos necesarios y la tabla queda vacía.<br>
	 * Se llama a {@link #configurarVectorObjeto} para generar el vector de vectores.
	 * @return Devuelve un vector de vectores, cada uno correspondiente a una línea de la tabla, con sus datos en Strings.
	 */
	protected Vector objetosTabla(){
		if (animal!=null) {
			per.actualizarSesion(animal);
		}
		if (alimento!=null) {
			per.actualizarSesion(alimento);
		}
		
		configurarElementos();
		
		listaElesBorrar = new ArrayList<>();
		listaElesGuardar = new ArrayList<>();
		
		btnGuardar.setEnabled(false);
		btnAniadir.setEnabled(false);
		textFieldCantidad.setEnabled(false);
		textFieldCantidad.setText("");
		
		Set<Consume> consumes=new HashSet(0);
		
		if(animal!=null) { //si se ha seleccionado animal, coge sus consumes
			consumes.addAll(animal.getConsumes(per));
			if(alimento!=null) { //Si TAMBIÉN se ha seleccionado alimento, retiene sólo los comunes entre ambos conjuntos
				consumes.retainAll(alimento.getConsumes(per));
				if(consumes.size()==0) { //Si se queda vacío, se activa la posibilidad de añadir uno
					textFieldCantidad.setEnabled(true);
					btnAniadir.setEnabled(true);
				}
			}
		} else if (alimento!=null) { //Si el animal no está seleccionado y el alimento si, se coge su conjunto
			consumes.addAll(alimento.getConsumes(per));
		}
		
		Vector objs = new Vector();
		for (Consume con : consumes) { //{"Nombre animal", "Especie animal", "Tratamiento", "Empleado", "Fecha y hora"};
			Vector e =  configurarVectorObjeto(con);
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
			
			String aliS = (String)comboBoxAlimento.getSelectedItem();
			if(aliS.equals("")) {
				alimento=null;
			} else {
				alimento = (Alimento)per.getObjetoMulti("Alimento", new String[] {"descripcion", aliS});
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
	private Vector configurarVectorObjeto(Consume con){ //{"Nombre animal", "Especie animal", "Alimento", "Cantidad"};
		Vector e =  new Vector();

		e.add(con.getAnimal().getNombre());
		e.add(con.getAnimal().getEspecie().getDescripcion());
		
		e.add(con.getAlimento().getDescripcion());
				
		e.add(con.getCantidadDia());
		
		return e;
	}
	
	protected void configTamanioCols(){
		int tamanio1 = 80;
		
		TableColumn columna;
		columna=table.getColumnModel().getColumn(3);
		columna.setPreferredWidth(tamanio1);
		columna.setMaxWidth(tamanio1);
		columna.setMinWidth(5);
	}
}
