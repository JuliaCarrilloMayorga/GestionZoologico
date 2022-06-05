package interfaz;

import java.awt.BorderLayout;


import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.hibernate.exception.JDBCConnectionException;

import conexionBBDD.*;
import tablas.*;
import utilidades.*;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.SwingConstants;
import java.awt.Color;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JSeparator;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

public class CRUDEmpleados extends CRUDPadre {
	private JPanel contentPane;
	private JFrame padre;
	
	//Campos unicos del formulario
	private JTextField textFieldNombre;
	private JTextField textFieldFecha;
	private JTextField textFieldDireccion;
	
	private Empleado empleado;

	/**
	 * Configura las variables elementosForm y elementosFormBloq, empleadas desde métodos de la clase padre 
	 * <ul>
	 * <li>elementosForm: ArrayLsit con todos los elementos del formulario (JTextField y JComboBox)
	 * <li>elementosFormBloq: ArrayLsit con los elementos bloqueados para el proceso de búsqueda
	 * </ul>
	 */
	private void configurarElementos() {
		elementosForm = new ArrayList();
		elementosFormBloq = new ArrayList();
		
		elementosForm.add(textFieldId);
		elementosForm.add(textFieldNombre);
		elementosForm.add(textFieldDireccion);
		elementosForm.add(textFieldFecha);
		
		elementosFormBloq.add(textFieldFecha);
	}

	/**
	 * Configura variables empleadas desde métodos de la clase CRUDTablaGeneral
	 * <ul>
	 * <li>nombreClase: Nombre de la clase/objeto, con la primera letra en mayúscula
	 * <li>nombreClasePlur: Nombre en plural de la clase/objeto, con la primera letra en mayúscula
	 * <li>campos: Array de String con los títulos de las columnas de la tabla de búsqueda
	 * <li>masculino: Género del nombre de la clase
	 * </ul>
	 */
	private void configurarNombres() {
		nombreClase = "Empleado";
		nombreClasePlur = "Empleados";
		campos = new String[] {"id", "Nombre", "Dirección", "Fecha nacimiento"};
		masculino=true;
	}

	//CONSTRUCTOR
	public CRUDEmpleados(JFrame padre, boolean modal, Persistencia per2) {
		super(padre, modal);
		
		configurarNombres();
		
		setTitle("Gestión de "+nombreClasePlur.toLowerCase() );
		
		setResizable(false);
		this.padre = padre;
		this.per = per2;
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 362, 326);
		setLocationRelativeTo(padre);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(255, 255, 255));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblTitulo = new JLabel(nombreClasePlur);
		lblTitulo.setBounds(10, 11, 336, 32);
		lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
		lblTitulo.setFont(new Font("Tahoma", Font.PLAIN, 20));
		contentPane.add(lblTitulo);
		
		textFieldId = new JTextField();
		textFieldId.setBounds(97, 85, 235, 20);
		textFieldId.setEnabled(false);
		contentPane.add(textFieldId);
		textFieldId.setColumns(10);
		
		textFieldNombre = new JTextField();
		textFieldNombre.setBounds(97, 122, 235, 20);
		contentPane.add(textFieldNombre);
		textFieldNombre.setColumns(10);
		
		textFieldDireccion = new JTextField();
		textFieldDireccion.setBounds(97, 159, 235, 20);
		contentPane.add(textFieldDireccion);
		textFieldDireccion.setColumns(10);
		
		textFieldFecha = new JTextField();
		textFieldFecha.setBounds(97, 196, 235, 20);
		contentPane.add(textFieldFecha);
		textFieldFecha.setEnabled(false);
		textFieldFecha.setColumns(10);
		
		textFieldFecha.setToolTipText("DD/MM/AAAA");
		
		JLabel lblId_1 = new JLabel("Id:");
		lblId_1.setBounds(20, 85, 40, 24);
		contentPane.add(lblId_1);
		
		JLabel lblId_2 = new JLabel("Nombre:");
		lblId_2.setBounds(20, 122, 67, 24);
		contentPane.add(lblId_2);
		
		JLabel lblId_3 = new JLabel("Dirección:");
		lblId_3.setBounds(20, 159, 67, 24);
		contentPane.add(lblId_3);
		
		btnAccion = new JButton("Buscar");
		btnAccion.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				accion();
			}
		});
		btnAccion.setBounds(10, 256, 103, 23);
		contentPane.add(btnAccion);
		
		JLabel lblId_4 = new JLabel("Fecha nac.:");
		lblId_4.setBounds(20, 196, 67, 24);
		contentPane.add(lblId_4);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(10, 242, 336, 13);
		contentPane.add(separator);
		
		JButton btnReiniciar = new JButton("Reiniciar");
		btnReiniciar.setBounds(243, 256, 103, 23);
		btnReiniciar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reiniciar();
			}
		});
		contentPane.add(btnReiniciar);
		
		lblSubtitulo = new JLabel("Buscar "+nombreClase.toLowerCase());
		lblSubtitulo.setBounds(10, 43, 336, 20);
		lblSubtitulo.setHorizontalAlignment(SwingConstants.CENTER);
		lblSubtitulo.setFont(new Font("Tahoma", Font.PLAIN, 14));
		contentPane.add(lblSubtitulo);
		
		JPanel panel = new JPanel();
		panel.setBackground(new Color(204, 255, 204));
		panel.setBounds(10, 70, 336, 161);
		contentPane.add(panel);
		panel.setLayout(null);
		
		btnEliminar = new JButton("Eliminar");
		btnEliminar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				eliminar();
			}
		});
		btnEliminar.setVisible(false);
		btnEliminar.setBounds(126, 256, 103, 23);
		contentPane.add(btnEliminar);
		
		btnAccion.setMnemonic('b');
		btnReiniciar.setMnemonic('r');
		btnEliminar.setMnemonic('e');
		
		configurarElementos();
	}

	/**
	 * Elimina el objeto seleccionado. <br>
	 * Comprueba que sus Sets con tengan elementos. Si tienen, lanza un mensaje de aviso y cancela la operación. <br>
	 * Si están vacíon, lanza un mensaje de confirmación. <br>
	 * Si se acepta, elimina el objeto mediante la persistencia y lanza un mensaje de confirmación.
	 */
	private void eliminar() {
		int id = Integer.valueOf(textFieldId.getText());
		per.actualizarSesion(empleado);

		Set<Nomina> nominas = empleado.getNominas(per);
		Set<Animaltratamiento> aniTratamientos = empleado.getAnimaltratamientos(per);
		Set<Tratamiento> tratamientos = empleado.getTratamientos(per);
		Set<Zona> zonas = empleado.getZonas(per);

		if(nominas.size()>0) {
			JOptionPane.showMessageDialog(this, "No puede eliminar el empleado con id " + id + ", ya que tiene "+ nominas.size() +" nomina"+(nominas.size()==1?"":"s")+" relacionada"+(nominas.size()==1?"":"s")+".", "Empleado no eliminado", JOptionPane.WARNING_MESSAGE);

		} else if(tratamientos.size()>0) {
			JOptionPane.showMessageDialog(this, "No puede eliminar el empleado con id " + id + ", ya que tiene "+ tratamientos.size() +" tratamiento"+(tratamientos.size()==1?"":"s")+" relacionado"+(tratamientos.size()==1?"":"s")+".", "Empleado no eliminado", JOptionPane.WARNING_MESSAGE);

		} else if(aniTratamientos.size()>0) {
			JOptionPane.showMessageDialog(this, "No puede eliminar el empleado con id " + id + ", ya que tiene "+ aniTratamientos.size() +" tratamiento"+(aniTratamientos.size()==1?"":"s")+" de animales relacionado"+(aniTratamientos.size()==1?"":"s")+".", "Empleado no eliminado", JOptionPane.WARNING_MESSAGE);

		}else if(zonas.size()>0) {
			JOptionPane.showMessageDialog(this, "No puede eliminar el empleado con id " + id + ", ya que tiene "+ zonas.size() +" zona"+(zonas.size()==1?"":"s")+" relacionada"+(zonas.size()==1?"":"s")+".", "Empleado no eliminado", JOptionPane.WARNING_MESSAGE);

		} else {
			try {
				
				int res = JOptionPane.showConfirmDialog(this, "Está seguro de que quiere eliminar el empleado de id: "+id+"?", "Borrar empleado", JOptionPane.YES_NO_OPTION);

				if(res==JOptionPane.YES_OPTION) {
					per.eliminarObjeto("Empleado", empleado);
					JOptionPane.showMessageDialog(this, "Se ha eliminado el empleado de id " + id + ".", "Empleado eliminado", JOptionPane.INFORMATION_MESSAGE);
				}
				reiniciar();
				
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
	 * Configura el objeto dado con los datos de los campos del formulario. <br>
	 * Castea a un objeto del tipo de esta clase el elemento dado.<br>
	 * Setea cada uno de sus atributos con los valores de los campos.
	 * @param elementoPadre Elemento de la clase ElementoPadre a configurar
	 */
	protected void configurar(ElementoPadre elementoPadre) {
		Empleado empl = (Empleado) elementoPadre;
		
		empl.setNombre(textFieldNombre.getText().trim().toUpperCase());
		empl.setDireccion(textFieldDireccion.getText().trim());
		
		String fecha = textFieldFecha.getText().trim();
		try {
			empl.setFechaNac(Utilidades.getFechaDate(fecha));
		} catch (ParseException e) {
			Utilidades.notificaError(null, "Fecha errónea", e, "El formato de la fecha es erróneo.");
		}
		
	}

	/**
	 * Valida los campos del formulario antes de guardar el objeto. <br>
	 * Animal: Validar facha (Correcta o vacía) y Nombre y Especie con contenido y únicos.
	 * @param nuevo Si es nuevo se comprueba que no exista en la BBDD otro elemento con los mismos campos que deben ser únicos,<br>
	 * si no es nuevo es un elemento a modificar y se comprueba que no coincidan con otro distinto al modificado.
	 */
	protected boolean validarCampos(boolean nuevo) {
		String nombre = textFieldNombre.getText().trim().toUpperCase();
		String dir = textFieldDireccion.getText().trim();
		if(nombre.equals("") || dir.equals("")) { // Comprobar Nombre y Dirección con contenido
			JOptionPane.showMessageDialog(this, "Debe completar los campos de NOMBRE y DIRECCIÓN.", "Campos incompletos", JOptionPane.WARNING_MESSAGE);
			return false;
		} 
		
		String fecha = textFieldFecha.getText().trim();
		if(!Utilidades.isFechaValida(fecha)){ //Comprobar que Fecha es valida
			JOptionPane.showMessageDialog(this, "La fecha debe tener formato DD/MM/AAAA.", "Fecha inválida", JOptionPane.WARNING_MESSAGE);
			return false;
		} 
		return true;
	}

	/**
	 * Lanza una ventana CRUDTablaGeneral que muestra los resultados de la busqueda filtrada por parámetros escritos.
	 * <ul>
		 * <li>	Guarda en un Vector resultados los objetos resultantes de una búsqueda con {@link Persistencia#getObjetoMulti}
		 * <li> Se crea un Array de Strings con el nombre de los campos por los que se ha realizado la búsqueda, para configurar los subtítulos de la tabla.
		 * <li> Lanza una ventana CRUDTablaGeneral 
	 * </ul>
	 */
	protected void buscar() {
		String nombre = textFieldNombre.getText().trim().toUpperCase();
		String dir = textFieldDireccion.getText().trim();
		textFieldNombre.setText(nombre);
		
		Vector resultados = resultadosBusqueda(nombre, dir);
		String[][] parametros = {{"Nombre", nombre}, {"Dirección", dir}};
		
		CRUDTablaGeneral ventanaAnimales = new CRUDTablaGeneral(this, null, true, resultados, parametros);
		ventanaAnimales.setVisible(true);
	}

	/**
	 * Realiza una búsqueda por los parámetros permitidos para este objeto mediante el método {@link Persistencia#getObetosFiltradoMulti} <br>
	 * Crea un vector de vectores de strings con los datos de cada elemento, que es lo que se necesito para alimentar el JTable de datos.
	 * @param nombre String con uno de los parámetros de búsqueda
	 * @param dir String con uno de los parámetros de búsqueda
	 * @return Vector con los objetos resultantes de la búsqueda
	 */
	private Vector resultadosBusqueda(String nombre, String dir) {
		// {"id", "Nombre", "Disercción", "Fecha nacimiento"};
		Vector empleadosV= new Vector();
		try {
			ArrayList<Empleado> empleados = (ArrayList<Empleado>) per.getObetosFiltradoMulti("Empleado", new String[]{"nombre", nombre, "direccion", dir}, new String[]{});
			
			for (Empleado a : empleados) {
				Vector em = new Vector();
				em.add(a.getId());
				em.add(a.getNombre());
				em.add(a.getDireccion());
				
				Date d = a.getFechaNac();
				em.add(Utilidades.getFechaString(d));
				
				empleadosV.add(em);
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
		return empleadosV;
	}

	/**
	 * Configura el formulario para modificar un elemento. <br>
	 * Desbloquea y limpia los campos llamando al super {@link CRUDPadre#prepararModificar} <br>
	 * Busca con {@link Persistencia#getObjetoMulti} el objeto seleccionado en la tabla y setea los campos del formulario con sus datos.
	 * @param parametros Los atributos necesarios para buscar el elemento seleccionado en la tabla en la BBDD.
	 */
	protected void prepararModificar(String[] parametros) {
		super.prepararModificar(parametros);

		String nombre = parametros[0];
		String dir = parametros[1];		
		
		try {
			empleado = (Empleado)per.getObjetoMulti("Empleado", new String[] {"nombre", nombre, "direccion", dir});
			
			textFieldId.setText(String.valueOf(empleado.getId()));
			
			Date d = empleado.getFechaNac();
			textFieldFecha.setText(Utilidades.getFechaString(d));
			
			textFieldNombre.setText(empleado.getNombre());
			textFieldDireccion.setText(empleado.getDireccion());
			
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
