package interfaz;

import java.awt.Color;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.SQLException;
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
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.hibernate.exception.JDBCConnectionException;

import conexionBBDD.Persistencia;
import tablas.*;
import utilidades.Utilidades;

public class PROCESOTrabCapaz  extends PROCESOPadre {

	//Padre y persistencia
	JFrame padre;
	Persistencia per;

	//Campos únicos de este proceso.
	private JComboBox comboBoxTratamiento;
	private JComboBox comboBoxEmpleado;
	
	//Elementos empleados en este proceso
	protected Tratamiento tratamiento;
	protected Empleado empleado;
	ArrayList<Empleado> todosEmp;
	
	//Elementos pasa lanzar mensajes de confirmación
		int elemEliminados=0;
		boolean guardado=false;
	
	/**
	 * Da contenido a nombreProceso y camposTabla, variables empleadas desde la clase padre PROCESOPadre. <br>
	 * nombreProceso: Nombre para los títulos
	 * camposTabla: campos con los que se configurará la tabla
	 */
	private void configurar() {
		nombreProceso="Capacitación de los Trabajadores";
		camposTabla = new String[] {"id", "Nombre", "Fecha nacimiento", "Dirección"}; //Campos de empleado
		
		try {
			todosEmp = (ArrayList<Empleado>)per.getTodosObjetos("Empleado");
			
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
	 * Devuelve el elemento principal de este proceso, el que será guardado por hibernate.
	 */

	public PROCESOTrabCapaz(JFrame padre, boolean modal, Persistencia per) {
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
		
		//El panel único de este proceso
		panelFijo.setBounds(10, 152, 516, 222);
		setBounds(100, 100, 542, 419);
		panelUnico.setSize(516, 87);
		
		JLabel lblId_3 = new JLabel("Tratamien.:");
		lblId_3.setBounds(10, 15, 86, 24);
		panelUnico.add(lblId_3);
		
		comboBoxTratamiento = new JComboBox();
		
		comboBoxTratamiento.setBounds(87, 15, 284, 20);
		panelUnico.add(comboBoxTratamiento);
		
		JLabel lblId_3_1 = new JLabel("Empleados:");
		lblId_3_1.setBounds(10, 52, 67, 24);
		panelUnico.add(lblId_3_1);
		
		comboBoxEmpleado = new JComboBox();
		comboBoxEmpleado.setEnabled(false);
		
		comboBoxEmpleado.setBounds(87, 52, 284, 20);
		panelUnico.add(comboBoxEmpleado);
		
		btnAniadir = new JButton("Añadir");
		btnAniadir.setEnabled(false);
		panelUnico.add(btnAniadir);
		btnAniadir.setMnemonic('a');
		btnAniadir.setBounds(403, 52, 103, 23);
		
		btnAniadir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aniadirProceso();
			}
		});

		setLocationRelativeTo(padre);
		
		mostrarTratamientos(comboBoxTratamiento);
		mostrarEmpleados(comboBoxEmpleado);
		
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
		
		configurarTabla(objetosTabla());
	}
	
	/**
	 * Devuelve el formulario a su estado inicial. <br>
	 * Reinicia los elementos únicos de este proceso y llama a {@link PROCESOPadre#cancelar} de su clase padre.
	 */
	protected void cancelar() {
		comboBoxTratamiento.setSelectedIndex(0);
		comboBoxEmpleado.setSelectedIndex(0);
		
		elemEliminados=0;
		guardado=false;
		
		super.cancelar();
	}
	
	/**
	 * Guarda los elementos añadidos y borra los eliminados.
	 */
	protected void guardarCambios() {
		String mensaje="";

		if(guardado) {
			mensaje="Va a guardar la relación de "+ tratamiento.getDescripcion() +" y " + empleado.getNombre() + ".\n"
					+ "\n¿Está de acuerdo?" ;
		} else if(elemEliminados>0) {
			mensaje="Va a eliminar "+elemEliminados+ " relacion" + (elemEliminados==1?"":"es") + " de "+ tratamiento.getDescripcion() +".\n"
					+ "\n¿Está de acuerdo?" ;
		}
		int res = JOptionPane.showConfirmDialog(this, mensaje, "Guardar cambios", JOptionPane.YES_NO_OPTION);
		
		if(res==JOptionPane.YES_OPTION) {
			try {
				per.guardarObjeto("Tratamiento", tratamiento); //Guarda el objeto de inverse true (Hibernate) o hace commit (JDBC)
				
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
	 * Elimina un registro de proceso. <br>
	 * Selecciona la fila seleccionada en la tabla y busca el objeto que le corresponde. <br>
	 * Añade el elemento a borrar al ArrayList listaElesBorrar. <br>
	 * Quita la línea de la tabla y activa/desactiva el botón de guardar.
	 */
	protected void eliminarProceso() {
		int fila = super.table.getSelectedRow();
		
		String empS = String.valueOf(table.getModel().getValueAt(fila, 0));
		
		try {
			Empleado empleado = (Empleado)per.getObjetoMulti("Empleado", new String[] {"id", empS});
			
			int rel=0;
			Set<Animaltratamiento> aniTratamientos = empleado.getAnimaltratamientos(per);
			for (Animaltratamiento animaltratamiento : aniTratamientos) {
				if(animaltratamiento.getTratamiento().equals(tratamiento)) {
					rel++;
				}
			}
			
			if(rel>0) {
				JOptionPane.showMessageDialog(this, "No se puede eliminar la relación del trabajador "+empleado.getNombre()+" con el tratamiento "+ tratamiento.getDescripcion() +", ya que tiene "+ rel +" un tratamiento"+(rel==1?"":"s")+" de animal relacionado"+(rel==1?"":"s")+". \n"
						+ "Elimine primero los tratamientos de animales dependientes.", "Relación de tratamiento no eliminada", JOptionPane.WARNING_MESSAGE);
			}else {
				per.borrarProceso("escapaz", new ElementoPadre[] {tratamiento, empleado}, new String[] {"idEmpleado", String.valueOf(empleado.getId()), "idTratamiento", String.valueOf(tratamiento.getId())});
				
				if(!guardado) { //Guardado marca si se habia añadido la relacion. Si es false se esta eliminando una relacion antigua
					elemEliminados++;
					btnGuardar.setEnabled(true);
					if(comboBoxEmpleado.getSelectedIndex()!=0) btnAniadir.setEnabled(true); //Si se tiene seleccionado un empleado se puede guardar la relación de nuevo
				} else { //Si no, se ha eliminado una relación que se acaba de añadir
					btnAniadir.setEnabled(true);
					btnGuardar.setEnabled(false);
					guardado=false;
					
				}
				((DefaultTableModel)table.getModel()).removeRow(fila);
				
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
	 * Añade un registro e proceso.
	 * Genera la relación y marga la variable 'guardado' a true. <br>
	 * Activa el botón de Guardar y desactiva el de añadir.
	 */
	protected void aniadirProceso() {
		try {
			per.aniadirProceso("escapaz", new ElementoPadre[] {tratamiento, empleado}, new String[] {String.valueOf(empleado.getId()), String.valueOf(tratamiento.getId())});
			
			guardado=true;
			
			((DefaultTableModel)table.getModel()).addRow(configurarVectorObjeto(empleado));
			btnAniadir.setEnabled(false);
			btnGuardar.setEnabled(true);
			
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
	 * Actualiza los objetos en estado Persistente. <br>
	 * Desactiva el botón de Guardar, busca los elementos seleccionados mediante {@link #configurarElementos}, <br>
	 * según se han seleccionado todos o no, rellena el array de objetos utilizado para completar la tabla. <br>
	 * Habilita el botón Añadir si se han completado todos los elementos necesarios y la tabla queda vacía.<br>
	 * Se llama a {@link #configurarVectorObjeto} para generar el vector de vectores.
	 * @return Devuelve un vector de vectores, cada uno correspondiente a una línea de la tabla, con sus datos en Strings.
	 */
	protected Vector objetosTabla(){
		if(tratamiento!=null)
			per.actualizarSesion(tratamiento);
		
		for (Empleado empleado : todosEmp) {
			per.actualizarSesion(empleado);
		}

		try {
			per.cancelarCambios();
		} catch (SQLException | JDBCConnectionException e) {
			Utilidades.notificaError(padre, "Error de conexión", e, "Probablemente se ha perdido la conexión con MySql. \n"
					+ "Error fatal, el programa se cerrará a continuación");
			e.printStackTrace();
			System.exit(0);
		}
		
		btnGuardar.setEnabled(false);
		btnAniadir.setEnabled(false);
		
		Set<Empleado> empleados=new HashSet(0);
		
		elemEliminados=0;
		guardado=false;
		
		configurarElementos();
		
		if(tratamiento != null) {//si se ha seleccionado tratamiento, activa el comboBox de empleado
			comboBoxEmpleado.setEnabled(true);
			if(empleado==null) { //Si NO se ha seleccionado empleado, muestra todos los de ese tratameinto
				empleados = tratamiento.getEmpleados(per);
			} else { //Si se ha seleccionado y están relacionados, muestra la relación, si no activa el Añadir
				if(tratamiento.getEmpleados(per).contains(empleado)) {
					empleados.add(empleado);
				} else {
					btnAniadir.setEnabled(true);
				}
			}			
		} else {
			comboBoxEmpleado.setEnabled(false);
		}
		
		Vector objs = new Vector();
		for (Empleado emp : empleados) { //{"id", "Nombre", "Fecha nacimiento", "Dirección"};
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
		try {
			String zonaS = (String)comboBoxTratamiento.getSelectedItem();
			if(zonaS.equals("")) {
				tratamiento=null;
			} else {
				tratamiento = (Tratamiento)per.getObjetoMulti("Tratamiento", new String[] {"descripcion", zonaS});			
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
	 * Configura el vector con los atributas del objeto recibido como parámetro.
	 * @return Vector de Strings de los atributos del objeto recibido.
	 */
	private Vector configurarVectorObjeto(Empleado emp){
		Vector e =  new Vector();
		
		e.add(emp.getId());
		e.add(emp.getNombre());
		
		Date d = emp.getFechaNac();			
		e.add(Utilidades.getFechaString(d));
		
		e.add(emp.getDireccion());
		
		return e;
	}
	
	protected void configTamanioCols(){
		int tamanio1 = 70;
		
		TableColumn columna;
		columna=table.getColumnModel().getColumn(0);
		columna.setPreferredWidth(tamanio1);
		columna.setMaxWidth(tamanio1);
		columna.setMinWidth(5);
	}
}
