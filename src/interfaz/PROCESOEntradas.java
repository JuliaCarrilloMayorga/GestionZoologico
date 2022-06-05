package interfaz;

import java.awt.Color;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
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
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.hibernate.exception.JDBCConnectionException;

import conexionBBDD.Persistencia;
import tablas.*;
import utilidades.Utilidades;

public class PROCESOEntradas  extends PROCESOPadre {

	//Padre y persistencia
	JFrame padre;
	Persistencia per;

	//Campos únicos de este proceso.
	private JComboBox comboBoxEvento;
	private JTextField textFieldCantidad;
	
	//Elementos empleados en este proceso
	protected Evento evento;
	
	protected ArrayList<Entrada> listaElesBorrar = new ArrayList<>();
	protected ArrayList<Entrada> listaElesGuardar = new ArrayList<>();
	
	/**
	 * Da contenido a nombreProceso y camposTabla, variables empleadas desde la clase padre PROCESOPadre. <br>
	 * nombreProceso: Nombre para los títulos
	 * camposTabla: campos con los que se configurará la tabla
	 */
	private void configurar() {
		nombreProceso="Compra de entradas";
		camposTabla = new String[] {"Fecha compra", "Evento", "id"}; //Campos de entrada
	}

	public PROCESOEntradas(JFrame padre, boolean modal, Persistencia per) {
		super(padre, modal, per);
		
		setResizable(false);
		this.padre = padre;
		this.per = per;

		configurar();
		
		setForeground(new Color(204, 255, 204));
		setTitle("Relación de "+nombreProceso);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 542, 417);

		labelTitulo.setText("Relación de "+nombreProceso);
		
		//El panel único de este proceso
		panelUnico.setSize(516, 87);
		panelFijo.setBounds(10, 152, 516, 222);
		setBounds(100, 100, 542, 419);
		
		JLabel lblId_3 = new JLabel("Evento:");
		lblId_3.setBounds(10, 15, 67, 24);
		panelUnico.add(lblId_3);
		
		comboBoxEvento = new JComboBox();
		
		comboBoxEvento.setBounds(87, 15, 284, 20);
		panelUnico.add(comboBoxEvento);
		
		JLabel lblId_3_1 = new JLabel("Cantidad:");
		lblId_3_1.setBounds(10, 52, 67, 24);
		panelUnico.add(lblId_3_1);
		
		textFieldCantidad = new JTextField();
		textFieldCantidad.setBounds(87, 52, 284, 20);
		panelUnico.add(textFieldCantidad);
		textFieldCantidad.setColumns(10);
		
		btnAniadir = new JButton("Añadir");
		btnAniadir.setEnabled(false);
		btnAniadir.setBounds(403, 52, 103, 23);
		panelUnico.add(btnAniadir);
		btnAniadir.setMnemonic('a');
		
		btnAniadir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aniadirProceso();
			}
		});
		
		setLocationRelativeTo(padre);
		
		mostrarEventos(comboBoxEvento);
		
		comboBoxEvento.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				configurarTabla(objetosTabla());
			}
		});
		
		configurarTabla(objetosTabla());
	}
	
	/**
	 * Devuelve el formulario a su estado inicial. <br>
	 * Reinicia los elementos únicos de este proceso y llama a {@link PROCESOPadre#cancelar} de su clase padre.
	 */
	protected void cancelar() {
		comboBoxEvento.setSelectedIndex(0);
		textFieldCantidad.setText("");
		
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
		String empS = String.valueOf(table.getModel().getValueAt(fila, 2));
	
		try {
			Entrada entrada = (Entrada)per.getObjetoMulti("Entrada", new String[] {"id", empS});
			
			if(entrada!=null) { //Si no es null, la entrada ya existe, tiene un id asignado
				listaElesBorrar.add(entrada);			
			} else { // Si es null es una de las que se han añadido sin guardar, se busca una coincidencia en el array de entradas nuevas y se elimina
				String fecha = String.valueOf(table.getModel().getValueAt(fila, 0));
				for (int i = 0; i < listaElesGuardar.size(); i++) {
					Entrada e = listaElesGuardar.get(i);
					
					if(Utilidades.getFechaHoraString(e.getFechaHoraVenta()).equals(fecha)) {
						listaElesGuardar.remove(i);
						break;
					}	
				}
			}
			
			if(listaElesBorrar.size()==0 && listaElesGuardar.size()==0) { //Si no hay nada que gusrdar o borrar se desactiva Guardar
				btnGuardar.setEnabled(false);
			} else {
				btnGuardar.setEnabled(true);
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
			for (int i = 0; i < cantidad; i++) {
				Entrada e = new Entrada(evento, new Date());
				listaElesGuardar.add(e);
				
				((DefaultTableModel)table.getModel()).insertRow(0, configurarVectorObjeto(e));
			}
			
			btnGuardar.setEnabled(true);
		}
		textFieldCantidad.setText("");
	}
	
	/**
	 * Guarda los elementos añadidos y borra los eliminados.
	 */
	protected void guardarCambios() {	
		String mensaje="Va a eliminar "+listaElesBorrar.size()+ " entrada" + (listaElesBorrar.size()==1?"":"s") + ".\n"
				+ " Va a comprar "+listaElesGuardar.size()+ " entrada" + (listaElesGuardar.size()==1?"":"s") + " por un total de "+evento.getPrecio()*listaElesGuardar.size()+" €."
						+ "\n¿Está de acuerdo?" ;
		
		int res = JOptionPane.showConfirmDialog(this, mensaje, "Guardar cambios", JOptionPane.YES_NO_OPTION);
		
		if(res==JOptionPane.YES_OPTION) {
			try {
				for (int i = 0; i < listaElesGuardar.size(); i++) {
					per.guardarObjeto("Entrada", listaElesGuardar.get(i));
				}
				
				for (int i = 0; i < listaElesBorrar.size(); i++) {
					per.eliminarObjeto("Entrada", listaElesBorrar.get(i));
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
	
	private boolean validarCampos(){
		String cantidad = textFieldCantidad.getText();
		
		if(!cantidad.matches("[1-9]+[0-9]*")) {
			JOptionPane.showMessageDialog(this, "Debe completar el campo de CANTIDAD con un número positivo.", "Campo incorrecto", JOptionPane.WARNING_MESSAGE);
			return false;
		}
		return true;
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
		if(evento!=null)
			per.actualizarSesion(evento);
		
		btnGuardar.setEnabled(false);
		
		listaElesBorrar = new ArrayList<>();
		listaElesGuardar = new ArrayList<>();

		configurarElementos();
		
		ArrayList<Entrada> entradas = new ArrayList<Entrada>();
		if(evento!=null) {//si se ha seleccionado evento, coge sus entradas y activa la opción de añadir nuevas
			entradas.addAll(evento.getEntradas(per));
			btnAniadir.setEnabled(true);
			textFieldCantidad.setEnabled(true);
		} else {
			btnAniadir.setEnabled(false);
			textFieldCantidad.setEnabled(false);
		}

		entradas.sort(null);

		Vector objs = new Vector();
		for (Entrada emp : entradas) { //{"Fecha compra", "Evento", "id"};
			Vector e =  configurarVectorObjeto(emp);
			objs.add(e);
		}
		
		return objs;
	}
	
	/**
	 * Busca mediante la persistencia los objetos seleccionados de este proceso. <br>
	 * Si no se ha seleccionado nada, lo setea a null directamente.
	 */
	private void configurarElementos(){
		String eventoS = (String)comboBoxEvento.getSelectedItem();
		if(eventoS.equals("")) {
			evento=null;
		} else {
			try {
				evento = (Evento)per.getObjetoMulti("Evento", new String[] {"descripcion", eventoS});
				
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
	}
	
	/**
	 * Configura el vector con los atributas del objeto recibido como parámetro.
	 * @return Vector de Strings de los atributos del objeto recibido.
	 */
	private Vector configurarVectorObjeto(Entrada emp){
		Vector e =  new Vector();
		
		Date d = emp.getFechaHoraVenta();			
		e.add(Utilidades.getFechaHoraString(d));
		
		e.add(emp.getEvento().getDescripcion());
		
		e.add(emp.getId());
		
		return e;
	}
	protected void configTamanioCols(){
		int tamanio1 = 80;
		
		TableColumn columna;
		columna=table.getColumnModel().getColumn(2);
		columna.setPreferredWidth(tamanio1);
		columna.setMaxWidth(tamanio1);
		columna.setMinWidth(5);
	}
	
}
