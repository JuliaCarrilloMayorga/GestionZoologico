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
import java.util.Set;
import java.util.Vector;

public class CRUDZonas extends CRUDPadre {
	private JPanel contentPane;
	private JFrame padre;

	private JTextField textFieldDescripcion;
	private Zona zona;

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
		elementosForm.add(textFieldDescripcion);
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
		nombreClase = "Zona";
		nombreClasePlur = "Zonas";
		campos = new String[] {"id", "Descripción"};
		masculino=false;
	}
	
	//CONSTRUCTOR
	public CRUDZonas(JFrame padre, boolean modal, Persistencia per2) {
		super(padre, modal);
		
		configurarNombres();
		
		setTitle("Gestión de "+nombreClasePlur.toLowerCase() );
		
		setResizable(false);
		this.padre = padre;
		this.per = per2;
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 362, 252);
		setLocationRelativeTo(padre);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(255, 255, 255));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblAnimales = new JLabel(nombreClasePlur);
		lblAnimales.setBounds(10, 11, 336, 32);
		lblAnimales.setHorizontalAlignment(SwingConstants.CENTER);
		lblAnimales.setFont(new Font("Tahoma", Font.PLAIN, 20));
		contentPane.add(lblAnimales);
		
		textFieldId = new JTextField();
		textFieldId.setBounds(97, 85, 235, 20);
		textFieldId.setEnabled(false);
		contentPane.add(textFieldId);
		textFieldId.setColumns(10);
		
		textFieldDescripcion = new JTextField();
		textFieldDescripcion.setBounds(97, 122, 235, 20);
		textFieldDescripcion.setColumns(10);
		contentPane.add(textFieldDescripcion);
		
		JLabel lblId_1 = new JLabel("Id:");
		lblId_1.setBounds(20, 85, 40, 24);
		contentPane.add(lblId_1);
		
		JLabel lblId_2 = new JLabel("Descripc.:");
		lblId_2.setBounds(20, 122, 93, 24);
		contentPane.add(lblId_2);
		
		btnAccion = new JButton("Buscar");
		btnAccion.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				accion();
			}
		});
		btnAccion.setBounds(10, 182, 103, 23);
		contentPane.add(btnAccion);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(10, 168, 336, 13);
		contentPane.add(separator);
		
		JButton btnReiniciar = new JButton("Reiniciar");
		btnReiniciar.setBounds(243, 182, 103, 23);
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
		panel.setBounds(10, 70, 336, 87);
		contentPane.add(panel);
		
		btnEliminar = new JButton("Eliminar");
		btnEliminar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				eliminar();
			}
		});
		btnEliminar.setVisible(false);
		btnEliminar.setBounds(126, 182, 103, 23);
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
		per.actualizarSesion(zona);

		Set<Animal> animales = zona.getAnimals(per);
		Set<Empleado> empleados = zona.getEmpleados(per);

		if(animales.size()>0) {
			JOptionPane.showMessageDialog(this, "No puede eliminar la zona con id " + id + ", ya que tiene "+ animales.size() +" animal"+ (animales.size()==1?"":"es")+" relacionado"+(animales.size()==1?"":"s")+".", "Zona no eliminada", JOptionPane.WARNING_MESSAGE);

		} else if(empleados.size()>0) {
			JOptionPane.showMessageDialog(this, "No puede eliminar la zona con id " + id + ", ya que tiene "+ empleados.size() +" empleado"+ (empleados.size()==1?"":"s")+" relacionado"+(empleados.size()==1?"":"s")+".", "Zona no eliminada", JOptionPane.WARNING_MESSAGE);

		} else {
			try {
				int res = JOptionPane.showConfirmDialog(this, "Está seguro de que quiere eliminar la zona de id: "+id+"?", "Borrar zona", JOptionPane.YES_NO_OPTION);
				if(res==JOptionPane.YES_OPTION) {
					
					per.eliminarObjeto("Zona",zona);
					JOptionPane.showMessageDialog(this, "Se ha eliminado la zona de id " + id + ".", "Zona eliminada", JOptionPane.INFORMATION_MESSAGE);

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
		Zona zona = (Zona) elementoPadre;
		zona.setDescripcion(textFieldDescripcion.getText().trim().toUpperCase());
	}

	/**
	 * Valida los campos del formulario antes de guardar el objeto. <br>
	 * Animal: Validar facha (Correcta o vacía) y Nombre y Especie con contenido y únicos.
	 * @param nuevo Si es nuevo se comprueba que no exista en la BBDD otro elemento con los mismos campos que deben ser únicos,<br>
	 * si no es nuevo es un elemento a modificar y se comprueba que no coincidan con otro distinto al modificado.
	 */
	protected boolean validarCampos(boolean nuevo) {
		String descripcion = textFieldDescripcion.getText().trim().toUpperCase();
		
		if(descripcion.equals("")) { // Comprobar descripcion con contenido
			JOptionPane.showMessageDialog(this, "Debe completar el campo de DESCRIPCIÓN.", "Campo incompleto", JOptionPane.WARNING_MESSAGE);
			return false;
		} 
		
		try {
			Zona z = (Zona)per.getObjetoMulti("Zona", new String[] {"descripcion", descripcion});
			
			if(nuevo) { //Si nuevo:
				if(z!=null) { //La zona no debe existir
					JOptionPane.showMessageDialog(this, "Ya existe una zona con descripcion " + descripcion +".", "Zona ya existente", JOptionPane.WARNING_MESSAGE);
					return false;
				}
			} else { // Si es modificación:
				int id = Integer.valueOf(textFieldId.getText());
				
				if(z!=null && (z.getId()!=id)) { //La zona no debe coincidir con otro distinto al modificado
					JOptionPane.showMessageDialog(this, "Ya existe una zona distinto (ID: "+ z.getId() +") con descripcion " + descripcion, "Zona ya existente", JOptionPane.WARNING_MESSAGE);
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
		
		return false;
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
		String descripcion = textFieldDescripcion.getText().trim().toUpperCase();
		textFieldDescripcion.setText(descripcion);
		
		Vector resultados = resultadosBusqueda(descripcion);
		String[][] parametros = {{"Descripción", descripcion}};
		
		CRUDTablaGeneral ventanaZonas = new CRUDTablaGeneral(this, null, true, resultados, parametros);
		ventanaZonas.setVisible(true);
	}
	
	//{"id", "Descripcion"};

	/**
	 * Realiza una búsqueda por los parámetros permitidos para este objeto mediante el método {@link Persistencia#getObetosFiltradoMulti} <br>
	 * Crea un vector de vectores de strings con los datos de cada elemento, que es lo que se necesito para alimentar el JTable de datos.
	 * @param descripcion String con uno de los parámetros de búsqueda
	 * @return Vector con los objetos resultantes de la búsqueda
	 */
	private Vector resultadosBusqueda(String descripcion) {
		Vector zonasV= new Vector();
		try {
			ArrayList<Zona> zonas = (ArrayList<Zona>) per.getObetosFiltradoMulti("Zona", new String[]{"descripcion", descripcion}, new String[]{});
			
			for (Zona a : zonas) {
				Vector zona = new Vector();
				zona.add(a.getId());
				zona.add(a.getDescripcion());
				
				zonasV.add(zona);
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
		return zonasV;
	}


	/**
	 * Configura el formulario para modificar un elemento. <br>
	 * Desbloquea y limpia los campos llamando al super {@link CRUDPadre#prepararModificar} <br>
	 * Busca con {@link Persistencia#getObjetoMulti} el objeto seleccionado en la tabla y setea los campos del formulario con sus datos.
	 * @param parametros Los atributos necesarios para buscar el elemento seleccionado en la tabla en la BBDD.
	 */
	protected void prepararModificar(String[] parametros) {
		super.prepararModificar(parametros);
		String descripcion = parametros[0];
		
		try {
			
			zona = (Zona)per.getObjetoMulti("Zona", new String[] {"descripcion", descripcion});
			
			textFieldId.setText(String.valueOf(zona.getId()));
			textFieldDescripcion.setText(zona.getDescripcion());
			
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
