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
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.hibernate.exception.JDBCConnectionException;

import conexionBBDD.Persistencia;
import tablas.*;
import utilidades.Utilidades;

public class PROCESONominas  extends PROCESOPadre {

	//Padre y persistencia
	JFrame padre;
	Persistencia per;

	//Campos únicos de este proceso.
	private JComboBox comboBoxEmpleado;
	
	private JTextField textFieldFecha;
	private JTextField textFieldImporte;
	private JTextField textFieldIRPF;
	private JTextField textFieldSS;
	
	//Elementos empleados en este proceso
	protected Empleado empleado;
	
	protected ArrayList<Nomina> listaElesBorrar = new ArrayList<>();
	protected ArrayList<Nomina> listaElesGuardar = new ArrayList<>();
	
	/**
	 * Da contenido a nombreProceso y camposTabla, variables empleadas desde la clase padre PROCESOPadre. <br>
	 * nombreProceso: Nombre para los títulos
	 * camposTabla: campos con los que se configurará la tabla
	 */
	private void configurar() {
		nombreProceso="Gestión de nóminas";
		camposTabla = new String[] {"Fecha emisión", "Empleado", "Importe bruto", "IRPF", "SS", "id"}; //Campos de nómina
	}

	public PROCESONominas(JFrame padre, boolean modal, Persistencia per) {
		super(padre, modal, per);
		
		setResizable(false);
		this.padre = padre;
		this.per = per;

		configurar();
		
		setForeground(new Color(204, 255, 204));
		setTitle("Relación de "+nombreProceso);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		labelTitulo.setText("Relación de "+nombreProceso);
		
		//_____________UNICO
		
		setBounds(100, 100, 542, 533);
		panelUnico.setBounds(10, 54, 516, 201);
		panelFijo.setBounds(10, 266, 516, 221);
		
		
				JLabel lblIdEmpleado = new JLabel("Empleado:");
				lblIdEmpleado.setBounds(10, 11, 67, 24);
				panelUnico.add(lblIdEmpleado);
				
				comboBoxEmpleado = new JComboBox();
				
				comboBoxEmpleado.setBounds(87, 13, 284, 20);
				panelUnico.add(comboBoxEmpleado);
				
				JLabel lblIdFecha = new JLabel("Fecha:");
				lblIdFecha.setBounds(10, 61, 91, 24);
				panelUnico.add(lblIdFecha);
				
				textFieldFecha = new JTextField();
				textFieldFecha.setEnabled(false);
				textFieldFecha.setBounds(87, 63, 284, 20);
				panelUnico.add(textFieldFecha);
				textFieldFecha.setColumns(10);
				textFieldFecha.setToolTipText("DD/MM/AAAA");
				
				btnAniadir = new JButton("Añadir");
				btnAniadir.setEnabled(false);
				btnAniadir.setBounds(403, 167, 103, 23);
				panelUnico.add(btnAniadir);
				btnAniadir.setMnemonic('a');
				
				JSeparator separator = new JSeparator();
				separator.setBounds(10, 46, 496, 13);
				panelUnico.add(separator);
				
				JLabel lblIdImporte = new JLabel("Imp. bruto:");
				lblIdImporte.setBounds(10, 96, 91, 24);
				panelUnico.add(lblIdImporte);
				
				textFieldImporte = new JTextField();
				textFieldImporte.setEnabled(false);
				textFieldImporte.setColumns(10);
				textFieldImporte.setBounds(87, 98, 284, 20);
				panelUnico.add(textFieldImporte);
				
				JLabel lblIdIRPF = new JLabel("% IRPF:");
				lblIdIRPF.setBounds(10, 131, 91, 24);
				panelUnico.add(lblIdIRPF);
				
				textFieldIRPF = new JTextField();
				textFieldIRPF.setEnabled(false);
				textFieldIRPF.setColumns(10);
				textFieldIRPF.setBounds(87, 133, 284, 20);
				panelUnico.add(textFieldIRPF);
				
				textFieldSS = new JTextField();
				textFieldSS.setEnabled(false);
				textFieldSS.setColumns(10);
				textFieldSS.setBounds(87, 168, 284, 20);
				panelUnico.add(textFieldSS);
				
				JLabel lblIdSS = new JLabel("% SS:");
				lblIdSS.setBounds(10, 166, 91, 24);
				panelUnico.add(lblIdSS);
				
				
				//________________________UNICO
		
		btnAniadir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aniadirProceso();
			}
		});
		
		setLocationRelativeTo(padre);
		
		mostrarEmpleados(comboBoxEmpleado);
		
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
		comboBoxEmpleado.setSelectedIndex(0);
		reiniciarCamposNuevoEle();
		
		super.cancelar();
	}
	
	/**
	 * Elimina un registro de proceso. <br>
	 * Selecciona la fila seleccionada en la tabla y busca el objeto que le corresponde. <br>
	 * Añade el elemento a borrar al ArrayList listaElesBorrar. <br>
	 * Quita la línea de la tabla y activa/desactiva el botón de guardar.
	 */
	protected void eliminarProceso() { //{"Fecha emisión", "Empleado", "Importe bruto", "IRPF", "SS", "id"};
		int fila = super.table.getSelectedRow();
		String empS = String.valueOf(table.getModel().getValueAt(fila, 5));
		
		try {
			Nomina nomina = (Nomina)per.getObjetoMulti("Nomina", new String[] {"id", empS});
			
			if(nomina!=null) { //Si no es null, la entrada ya existe, tiene un id asignado
				listaElesBorrar.add(nomina);			
			} else { // Si es null es una de las que se han añadido sin guardar, se busca una coincidencia en el array de entradas nuevas y se elimina
				String fecha = String.valueOf(table.getModel().getValueAt(fila, 0));
				Double importe = Double.valueOf(String.valueOf(table.getModel().getValueAt(fila, 2)));
				Double irpf =  Double.valueOf(String.valueOf(table.getModel().getValueAt(fila, 3)));
				Double ss =  Double.valueOf(String.valueOf(table.getModel().getValueAt(fila, 4)));
				
				for (int i = 0; i < listaElesGuardar.size(); i++) {
					Nomina e = listaElesGuardar.get(i);
					
					if(Utilidades.getFechaString(e.getFechaEmision()).equals(fecha) &&
							e.getImporteBruto()==importe && e.getIrpf()==irpf && e.getSegSocial()==ss) {
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
			String fecha = String.valueOf(textFieldFecha.getText());
			Date d=null;
			try {
				d = Utilidades.getFechaDate(fecha);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			Double importe = Double.valueOf(textFieldImporte.getText());
			Double irpf = Double.valueOf(textFieldIRPF.getText());
			Double ss = Double.valueOf(textFieldSS.getText());
			
			Nomina n = new Nomina(empleado, d, importe, irpf, ss);
			listaElesGuardar.add(n);
			
			((DefaultTableModel)table.getModel()).insertRow(0, configurarVectorObjeto(n));
			btnGuardar.setEnabled(true);
			
			reiniciarCamposNuevoEle();
		}
	}
	
	private void reiniciarCamposNuevoEle() {
		textFieldFecha.setText("");
		textFieldImporte.setText("");
		textFieldIRPF.setText("");
		textFieldSS.setText("");
	}
	
	/**
	 * Guarda los elementos añadidos y borra los eliminados.
	 */
	protected void guardarCambios() {	
		String mensaje="Va a eliminar "+listaElesBorrar.size()+ " nómina" + (listaElesBorrar.size()==1?"":"s") + ".\n"
				+ " Va a generar "+listaElesGuardar.size()+ " nómina" + (listaElesGuardar.size()==1?"":"s") + "."
						+ "\n¿Está de acuerdo?" ;
		
		int res = JOptionPane.showConfirmDialog(this, mensaje, "Guardar cambios", JOptionPane.YES_NO_OPTION);
		
		if(res==JOptionPane.YES_OPTION) {
			try {
				for (int i = 0; i < listaElesGuardar.size(); i++) {
					per.guardarObjeto("Nomina", listaElesGuardar.get(i));
				}
				
				for (int i = 0; i < listaElesBorrar.size(); i++) {
					per.eliminarObjeto("Nomina", listaElesBorrar.get(i));
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
		String fecha = String.valueOf(textFieldFecha.getText());
		
		String importe = String.valueOf(textFieldImporte.getText());
		String irpf = String.valueOf(textFieldIRPF.getText());
		String ss = String.valueOf(textFieldSS.getText());
		
		if(!Utilidades.isFechaValida(fecha)) {
			JOptionPane.showMessageDialog(this, "La FECHA debe tener formato DD/MM/AAAA.", "Campo incorrecto", JOptionPane.WARNING_MESSAGE);
			return false;
		}
		if(!importe.matches("[1-9][0-9]*(\\.[0-9]+)?")) {
			JOptionPane.showMessageDialog(this, "El IMPORTE debe ser un número positivo.", "Campo incorrecto", JOptionPane.WARNING_MESSAGE);
			return false;
		}
		if(!irpf.matches("[0-9]+(\\.[0-9]+)?") || Double.valueOf(irpf)<=0 || Double.valueOf(irpf)>=100) {
			JOptionPane.showMessageDialog(this, "El porcentaje de IRPF debe ser un número comprendido entre 0 y 100 .", "Campo incorrecto", JOptionPane.WARNING_MESSAGE);
			return false;
		}
		if(!ss.matches("[0-9]+(\\.[0-9]+)?") || Double.valueOf(ss)<=0 || Double.valueOf(ss)>=100) {
			JOptionPane.showMessageDialog(this, "El porcentaje de SEGURIDAD SOCIAL debe ser un número comprendido entre 0 y 100.", "Campo incorrecto", JOptionPane.WARNING_MESSAGE);
			return false;
		}
		if(Double.valueOf(ss)+Double.valueOf(irpf)>100) {
			JOptionPane.showMessageDialog(this, "La suma del porcentaje de IRPF y SEGURIDAD SOCIAL no debe ser superior a 100.", "Campo incorrecto", JOptionPane.WARNING_MESSAGE);
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
		if(empleado!=null)
			per.actualizarSesion(empleado);
		
		btnGuardar.setEnabled(false);
		
		listaElesBorrar = new ArrayList<>();
		listaElesGuardar = new ArrayList<>();

		configurarElementos();
		
		ArrayList<Nomina> nominas = new ArrayList<Nomina>();
		if(empleado!=null) { //si se ha seleccionado empleado, coge sus nóminas y activa la opción de añadir nuevas
			nominas.addAll(empleado.getNominas(per));
			
			activarElesNuevo(true);
		} else {
			activarElesNuevo(false);
		}
		nominas.sort(null);

		Vector objs = new Vector();
		for (Nomina nom : nominas) {
			Vector e =  configurarVectorObjeto(nom);
			objs.add(e);
		}
		
		return objs;
	}
	
	private void activarElesNuevo(boolean activar) {
		btnAniadir.setEnabled(activar);
		textFieldFecha.setEnabled(activar);
		textFieldImporte.setEnabled(activar);
		textFieldIRPF.setEnabled(activar);
		textFieldSS.setEnabled(activar);
	}
	
	/**
	 * Busca mediante la persistencia los objetos seleccionados de este proceso. <br>
	 * Si no se ha seleccionado nada, lo setea a null directamente.
	 */
	private void configurarElementos(){
		String emplS = (String)comboBoxEmpleado.getSelectedItem();
		if(emplS.equals("")) {
			empleado=null;
		} else {
			try {
				empleado = (Empleado)per.getObjetoMulti("Empleado", new String[] {"nombre", emplS});
				
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
	private Vector configurarVectorObjeto(Nomina nom){ //{"Fecha emisión", "Empleado", "Importe bruto", "IRPF", "SS", "id"};
		Vector e =  new Vector();
		
		Date d = nom.getFechaEmision();			
		e.add(Utilidades.getFechaString(d));
		
		e.add(nom.getEmpleado().getNombre());
		e.add(nom.getImporteBruto());
		e.add(nom.getIrpf());
		e.add(nom.getSegSocial());
		e.add(nom.getId());
		
		return e;
	}
	
	protected void configTamanioCols(){
		int tamanio1 = 60;
		
		TableColumn columna;
		columna=table.getColumnModel().getColumn(3);
		columna.setPreferredWidth(tamanio1);
		columna.setMaxWidth(tamanio1);
		columna.setMinWidth(5);
		
		columna=table.getColumnModel().getColumn(4);
		columna.setPreferredWidth(tamanio1);
		columna.setMaxWidth(tamanio1);
		columna.setMinWidth(5);
		
		columna=table.getColumnModel().getColumn(5);
		columna.setPreferredWidth(tamanio1);
		columna.setMaxWidth(tamanio1);
		columna.setMinWidth(5);
	}
	
}
