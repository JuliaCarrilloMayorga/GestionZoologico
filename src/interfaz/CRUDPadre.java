package interfaz;

import java.awt.BorderLayout;

import java.awt.EventQueue;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.hibernate.exception.JDBCConnectionException;

import conexionBBDD.Persistencia;
import tablas.*;
import utilidades.Utilidades;
/**
 * Clase padre de todos los CRUDs básicos.
 */
public class CRUDPadre extends JDialog {

	private JPanel contentPane;
	private JFrame padre;
	protected Persistencia per;

	//Atributos overwrited en cada clase particular para setear los nombres
	protected String nombreClase;
	protected String nombreClasePlur;
	protected boolean masculino;
		//Campos que aparecen en la tabla al buscar un elemento
	protected String[] campos;

	//Array de los elementos del formulario, empleado para reiniciarlos desde esta clase.
	protected ArrayList<JComponent> elementosForm;
	//Array de los campos que al reiniciar el formulario deben ser inaccesibles (Se enablean al crear o modificar), empleado para deshabilitarlos desde esta clase.
	protected ArrayList<JComponent> elementosFormBloq;
	
	//Elementos comunes a todos los cRUDs manejados desde esta clase
	protected JButton btnAccion;
	protected JTextField textFieldId;
	protected JLabel lblSubtitulo;
	protected JButton btnEliminar;
	
	//CONSTRUCTOR
	public CRUDPadre(JFrame padre, boolean modal) {
		super(padre, modal);
		this.padre = padre;
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
	}

	//_____Métodos overwrited en las clases hijas de forma particular, pero que son llamados desde esta clase.
	
	/**
	 *  Método declarado pero descrito en cada clase hija. <br>
	 * Lanza una ventana CRUDTablaGeneral que muestra los resultados de la busqueda filtraada por parámetros escritos.
	 * <ul>
	 * <li>	Guarda en un Vector resultados los objetos resultantes de una búsqueda con {@link per#getObjetoMulti}
	 * <li> Se crea un Array de Strings con el nombre de los campos por los que se ha realizado la búsqueda, para configurar los subtítulos de la tabla.
	 * <li> Lanza una ventana CRUDTablaGeneral 
	 * </ul>
	 */
	protected void buscar() {};
	
	/**
	 * Método declarado pero descrito en cada clase hija. <br>
	 * Valida los campos del formulario antes de guardar el objeto. <br>
	 * @param nuevo Si es nuevo se comprueba que no exista en la BBDD otro elemento con los mismos campos que deben ser únicos,<br>
	 * si no es nuevo es un elemento a modificar y se comprueba que no coincidan con otro distinto al modificado.
	 */
	protected boolean validarCampos(boolean bool) {return true;};
	
	/**
	 * Método declarado pero descrito en cada clase hija. <br>
	 * Configura el objeto dado con los datos de los campos del formulario. <br>
	 * Castea a un objeto del tipo de esta clase el elemento dado.<br>
	 * Setea cada uno de sus atributos con los valores de los campos.
	 * @param elementoPadre Elemento de la clase ElementoPadre a configurar
	 */
	protected void configurar(ElementoPadre elementoPadre) {};
	
	//_____Metodos generalizados para cualquier clase hija.
	
	/**
	 * Método llamado al interactuar con btnAccion, que según el momento es Buscar, Crear o Modificar. <br>
	 * Según el nombre del botón se llama al método que corresponde: {@link #buscar}, {@link #crear} o {@link #modificar}
	 */
	protected void accion() {
		if(btnAccion.getText().equals("Buscar")) {
			buscar();
		} else if (btnAccion.getText().equals("Crear")){
			crear();
		} else {
			modificar();
		}
	}
	
	/**
	 * Método llamado al interactuar con btnAccion en modo Modificar. <br>
	 * Llama a {@link #validarCampos}. Si son correctos, coge el objeto seleccionado por su id, lo setea con los nuevos valores y lo guarda.
	 */
	protected void modificar() {
		if (validarCampos(false)) {
			try {
				String id = textFieldId.getText(); //id del objeto cogido del TextField 'textFieldId'
				ElementoPadre ele = (ElementoPadre)per.getObjetoMulti(nombreClase, new String[] {"id", id}); //Obtiene el ElementoPadre del CRUD que sea mediente su id
				configurar(ele); //Configura el elemento obtenido con los valores nuevos de los campos del formulario (Se ejecuta la versión de la clase hija)
				per.guardarObjeto(nombreClase, ele); //Guarda el objeto modificado
				
				//Confirma los cambios y reinicia
				JOptionPane.showMessageDialog(this, "Se ha modificado "+(masculino?"el":"la")+" "+nombreClase.toLowerCase()+" con id " + id + ".", nombreClase+ " modificad"+ (masculino?"o":"a"), JOptionPane.INFORMATION_MESSAGE);
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
	 * Método llamado al interactuar con btnAccion en modo Crear. <br>
	 * Llama a {@link #validarCampos}. Si son correctos, coge el objeto seleccionado por su id, lo setea con los nuevos valores y lo guarda.
	 */
	protected void crear() {
		if (validarCampos(true)) {
			
			//Crea una instancia nueva correspondiente al string 'nombreClase' del CRUD desde el que se llama
			Class<?> clazz;
			ElementoPadre ele=null;
			try {
				clazz = Class.forName("tablas."+nombreClase); //Se crea un objeto Class segun el nombre de la clase
				ele = (ElementoPadre) clazz.newInstance();  //Se guarda en un ElementoPadre una instancia de la clase del CRUD
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				Utilidades.notificaError(padre, "Error interno", e, "Hay un error en el código del programa.");
			}
			
			configurar(ele); //Configura el elemento obtenido con los valores nuevos de los campos del formulario (Se ejecuta la versión de la clase hija)
			try {
				//Guarda el nuevo objeto 
				int id = per.guardarObjeto(nombreClase, ele);
				//Confirma los cambios y reinicia
				JOptionPane.showMessageDialog(this, "Se ha guardado "+(masculino?"el":"la")+" "+nombreClase.toLowerCase()+" con id " + id + ".", nombreClase+ " guardado", JOptionPane.INFORMATION_MESSAGE);
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
	 * Recorre el array con los elementos que deben estar bloqueados definido en la clase hija y los enablea/desenablea.
	 * @param desbloquear Indica si se deben enablear o desenablear.
	 */
	private void desbloquearCampos(boolean desbloquear) {
		for (int i = 0; i < elementosFormBloq.size(); i++) {
			if(elementosFormBloq.get(i) instanceof JTextField) {
				JTextField t = (JTextField) elementosFormBloq.get(i);
				t.setEnabled(desbloquear);
			} else if(elementosFormBloq.get(i) instanceof JComboBox) {
				JComboBox c = (JComboBox) elementosFormBloq.get(i);
				c.setEnabled(desbloquear);
			}
		}
	}
	
	/**
	 * Método llamado al interactuar con btnReiniciar. <br>
	 * Configura el formulario como en su estado inicial.
	 */
	protected void reiniciar() {
		//Recorre el array con los elementos del formulario definido en la clase hija
		for (int i = 0; i < elementosForm.size(); i++) { 
			if(elementosForm.get(i) instanceof JTextField) { //Si es un JTextField lo deja en blanco
				JTextField t = (JTextField) elementosForm.get(i);
				t.setText("");
			} else if(elementosForm.get(i) instanceof JComboBox) { //Si es un JComboBox selecciona la primera opción
				JComboBox c = (JComboBox) elementosForm.get(i);
				c.setSelectedIndex(0);
			}
		}
		
		//Recorre el array con los elementos que deben estar bloqueados definido en la clase hija y los desenablea.
		desbloquearCampos(false);
		
		//Pone el botón btnAccion y el subtítulo en modo Buscar y pone el botón de Eliminar en invisible.
		btnAccion.setText("Buscar");
		btnAccion.setMnemonic('b');
		lblSubtitulo.setText("Buscar " + nombreClase.toLowerCase());
		btnEliminar.setVisible(false);
	}
	
	/**
	 * Método llamado desde CRUDTablaGeneral al darle a Crear. <br>
	 * Configura el formulario para crear un nuevo elemento, con todos sus campos limpios y desbloqueados.
	 */
	protected void prepararCrear() {
		reiniciar(); //Reinicia el formulario
		
		//Recorre el array con los elementos que deben estar bloqueados definido en la clase hija y los desenablea.
		desbloquearCampos(true);

		//Pone el botón btnAccion y el subtítulo en modo Crear.
		btnAccion.setText("Crear");
		btnAccion.setMnemonic('c');
		lblSubtitulo.setText("Nuevo "+ nombreClase.toLowerCase());
	}
	
	/**
	 * Método llamado desde CRUDTablaGeneral al darle a Modificar. <br>
	 * Configura el formulario para modificar un elemento seleccionado externamente, con todos sus campos rellenos con los datos del elemento y desbloqueados.
	 * Aquí solo se configuran los campos como en crear, desde la clase hija este método se completa configurando los datos de los campos.
	 * @param parametros Los atributos necesarios para buscar el elemento seleccionado en la tabla en la BBDD, utilizados desde el overwrite del hijo.
	 */
	protected void prepararModificar(String[] parametros) {
		reiniciar();//Reinicia el formulario
		
		//Recorre el array con los elementos que deben estar bloqueados definido en la clase hija y los desenablea.
		desbloquearCampos(true);

		//Pone el botón btnAccion y el subtítulo en modo Modificar, activa el botón de Eliminar.
		btnEliminar.setVisible(true);
		btnAccion.setText("Modificar");
		btnAccion.setMnemonic('m');
		lblSubtitulo.setText("Modificar/Eliminar "+ nombreClase.toLowerCase());		
	}

}
