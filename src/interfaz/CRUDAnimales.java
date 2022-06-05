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

public class CRUDAnimales extends CRUDPadre {
	private JPanel contentPane;
	private JFrame padre;
	
	//Campos unicos del formulario
	private JTextField textFieldNombre;
	private JTextField textFieldFecha;

	private JComboBox comboBoxZona;
	private JComboBox comboBoxEspecie;
	private Animal animal;

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
		elementosForm.add(textFieldFecha);
		elementosForm.add(comboBoxZona);
		elementosForm.add(comboBoxEspecie);
		
		elementosFormBloq.add(comboBoxZona);
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
		nombreClase = "Animal";
		nombreClasePlur = "Animales";
		campos = new String[] {"id", "Nombre", "Especie", "Zona", "Fecha nacimiento"};
		masculino=true;
	}

	//CONSTRUCTOR
	public CRUDAnimales(JFrame padre, boolean modal, Persistencia per2) {
		super(padre, modal);
		
		configurarNombres();
		
		setTitle("Gestión de "+nombreClasePlur.toLowerCase() );
		
		setResizable(false);
		this.padre = padre;
		this.per = per2;
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 362, 363);
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
		
		JLabel lblId = new JLabel("Zona:");
		lblId.setBounds(20, 233, 67, 24);
		contentPane.add(lblId);
		
		textFieldId = new JTextField();
		textFieldId.setBounds(97, 85, 235, 20);
		textFieldId.setEnabled(false);
		contentPane.add(textFieldId);
		textFieldId.setColumns(10);
		
		textFieldNombre = new JTextField();
		textFieldNombre.setBounds(97, 122, 235, 20);
		contentPane.add(textFieldNombre);
		textFieldNombre.setColumns(10);
		
		comboBoxEspecie = new JComboBox();
		comboBoxEspecie.setBounds(97, 159, 235, 20);
		contentPane.add(comboBoxEspecie);
		
		textFieldFecha = new JTextField();
		textFieldFecha.setBounds(97, 196, 235, 20);
		contentPane.add(textFieldFecha);
		textFieldFecha.setEnabled(false);
		textFieldFecha.setColumns(10);
		
		textFieldFecha.setToolTipText("DD/MM/AAAA");
		
		comboBoxZona = new JComboBox();
		comboBoxZona.setBounds(97, 233, 235, 20);
		contentPane.add(comboBoxZona);
		comboBoxZona.setEnabled(false);
		
		JLabel lblId_1 = new JLabel("Id:");
		lblId_1.setBounds(20, 85, 40, 24);
		contentPane.add(lblId_1);
		
		JLabel lblId_2 = new JLabel("Nombre:");
		lblId_2.setBounds(20, 122, 67, 24);
		contentPane.add(lblId_2);
		
		JLabel lblId_3 = new JLabel("Especie:");
		lblId_3.setBounds(20, 159, 67, 24);
		contentPane.add(lblId_3);
		
		btnAccion = new JButton("Buscar");
		btnAccion.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				accion();
			}
		});
		btnAccion.setBounds(10, 293, 103, 23);
		contentPane.add(btnAccion);
		
		JLabel lblId_4 = new JLabel("Fecha nac.:");
		lblId_4.setBounds(20, 196, 67, 24);
		contentPane.add(lblId_4);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(10, 279, 336, 13);
		contentPane.add(separator);
		
		JButton btnReiniciar = new JButton("Reiniciar");
		btnReiniciar.setBounds(243, 293, 103, 23);
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
		panel.setBounds(10, 70, 336, 198);
		contentPane.add(panel);
		panel.setLayout(null);
		
		btnEliminar = new JButton("Eliminar");
		btnEliminar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				eliminar();
			}
		});
		btnEliminar.setVisible(false);
		btnEliminar.setBounds(126, 293, 103, 23);
		contentPane.add(btnEliminar);
		
		btnAccion.setMnemonic('b');
		btnReiniciar.setMnemonic('r');
		btnEliminar.setMnemonic('e');
		
		configurarElementos();
		mostrarZonas();
		mostrarEspecies();
	}

	/**
	 * Rellena el ComboBox comboBoxEspecie con todas las especies existentes en la BBDD.
	 */
	private void mostrarEspecies() {
		try {
			ArrayList<Especie> especies = (ArrayList<Especie>) per.getTodosObjetos("Especie");
			comboBoxEspecie.addItem("");
			for (Especie especie : especies) {
				comboBoxEspecie.addItem(especie.getDescripcion());
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	/**
	 * Rellena el ComboBox comboBoxZona con todas las zonas existentes en la BBDD.
	 */
	private void mostrarZonas() {
		try {
			ArrayList<Zona> zonas = (ArrayList<Zona>) per.getTodosObjetos("Zona");
			for (Zona zona : zonas) {
				comboBoxZona.addItem(zona.getDescripcion());
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
	 * Elimina el objeto seleccionado. <br>
	 * Comprueba que sus Sets con tengan elementos. Si tienen, lanza un mensaje de aviso y cancela la operación. <br>
	 * Si están vacíon, lanza un mensaje de confirmación. <br>
	 * Si se acepta, elimina el objeto mediante la persistencia y lanza un mensaje de confirmación.
	 */
	private void eliminar() {
		int id = Integer.valueOf(textFieldId.getText());
		per.actualizarSesion(animal);

		Set<Consume> consumes = animal.getConsumes(per);
		Set<Animaltratamiento> tratamientos = animal.getAnimaltratamientos(per);

		if(consumes.size()>0) {
			JOptionPane.showMessageDialog(this, "No puede eliminar el animal con id " + id + ", ya que tiene "+ consumes.size() +" alimento"+(consumes.size()==1?"":"s")+" relacionado"+(consumes.size()==1?"":"s")+".", "Animal no eliminado", JOptionPane.WARNING_MESSAGE);

		} else if(tratamientos.size()>0) {
			JOptionPane.showMessageDialog(this, "No puede eliminar el animal con id " + id + ", ya que tiene "+ tratamientos.size() +" tratamiento"+(consumes.size()==1?"":"s")+" relacionado"+(consumes.size()==1?"":"s")+".", "Animal no eliminado", JOptionPane.WARNING_MESSAGE);

		} else {
			try {
				
				int res = JOptionPane.showConfirmDialog(this, "¿Está seguro de que quiere eliminar el animal de id: "+id+"?", "Borrar animal", JOptionPane.YES_NO_OPTION);

				if(res==JOptionPane.YES_OPTION) {
					per.eliminarObjeto("Animal", animal);
					JOptionPane.showMessageDialog(this, "Se ha eliminado el animal de id " + id + ".", "Animal eliminado", JOptionPane.INFORMATION_MESSAGE);
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
		try {
			Animal animal = (Animal) elementoPadre; //Castea a un objeto del tipo de esta clase el elemento dado.
			
			animal.setNombre(textFieldNombre.getText().trim().toUpperCase());
			String especieS = (String)comboBoxEspecie.getSelectedItem();		
			Especie esp = (Especie)per.getObjetoMulti("Especie", new String[] {"descripcion", especieS});
			animal.setEspecie(esp);
			
			String fecha = textFieldFecha.getText().trim();
			
			try {
				animal.setFechaNac(Utilidades.getFechaDate(fecha));
			} catch (ParseException e) {
				Utilidades.notificaError(padre, "Error interno", e, "Hay un error en el código del programa.");
			}
			
			String zonaS = (String)comboBoxZona.getSelectedItem();
			animal.setZona((Zona)per.getObjetoMulti("Zona", new String[] {"descripcion", zonaS}));
			
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
	 * Valida los campos del formulario antes de guardar el objeto. <br>
	 * Animal: Validar facha (Correcta o vacía) y Nombre y Especie con contenido y únicos.
	 * @param nuevo Si es nuevo se comprueba que no exista en la BBDD otro elemento con los mismos campos que deben ser únicos,<br>
	 * si no es nuevo es un elemento a modificar y se comprueba que no coincidan con otro distinto al modificado.
	 */
	protected boolean validarCampos(boolean nuevo) {
		String nombre = textFieldNombre.getText().trim().toUpperCase();
		String especieS = (String)comboBoxEspecie.getSelectedItem();
		if(nombre.equals("") || especieS.equals("")) { // Comprobar Nombre y Especie con contenido
			JOptionPane.showMessageDialog(this, "Debe completar los campos de NOMBRE y ESPECIE.", "Campos incompletos", JOptionPane.WARNING_MESSAGE);
			return false;
		} 
		
		String fecha = textFieldFecha.getText().trim();
		if(!fecha.equals("") && !Utilidades.isFechaValida(fecha)){ //Comprobar que Fecha es valida o vacía
			JOptionPane.showMessageDialog(this, "La fecha debe tener formato DD/MM/AAAA o estar en blanco.", "Fecha inválida", JOptionPane.WARNING_MESSAGE);
			return false;
		} 
		
		try {
			Especie especie = (Especie)per.getObjetoMulti("Especie", new String[] {"descripcion", especieS});
			Animal a = (Animal)per.getObjetoMulti("Animal", new String[] {"nombre", nombre, "idEspecie", String.valueOf(especie.getId())});
			
			if(nuevo) { //Si nuevo:
				if(a!=null) { //El animal no debe existir
					JOptionPane.showMessageDialog(this, "Ya existe un animal con nombre " + nombre + " de la especie "+ especieS +".", "Animal ya existente", JOptionPane.WARNING_MESSAGE);
					return false;
				} 
			} else { // Si es modificación:
				int id = Integer.valueOf(textFieldId.getText());
				if(a!=null && (a.getId()!=id)) { //El animal no debe coincidir con otro distinto al modificado
					JOptionPane.showMessageDialog(this, "Ya existe un animal distinto (ID: "+ a.getId() +") con nombre " + nombre + " de la especie "+ especieS +".", "Animal ya existente", JOptionPane.WARNING_MESSAGE);
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
		try {
			String nombre = textFieldNombre.getText().trim().toUpperCase();
			String especie = (String)comboBoxEspecie.getSelectedItem();
			textFieldNombre.setText(nombre);
			
			Especie esp = (Especie)per.getObjetoMulti("Especie", new String[] {"descripcion", especie});
			Vector resultados = resultadosBusqueda(nombre, esp);
			String[][] parametros = {{"Nombre", nombre}, {"Especie", especie}};
			
			CRUDTablaGeneral ventanaAnimales = new CRUDTablaGeneral(this, null, true, resultados, parametros);
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

	/**
	 * Realiza una búsqueda por los parámetros permitidos para este objeto mediante el método {@link Persistencia#getObetosFiltradoMulti} <br>
	 * Crea un vector de vectores de strings con los datos de cada elemento, que es lo que se necesito para alimentar el JTable de datos.
	 * @param nombre String con uno de los parámetros de búsqueda
	 * @param especie String con uno de los parámetros de búsqueda
	 * @return Vector con los objetos resultantes de la búsqueda
	 */
	private Vector resultadosBusqueda(String nombre, Especie especie) {
		//{"id", "Nombre", "Especie", "Zona", "Fecha nacimiento"};
		try {
			String[] camposIgual= {};
			if(especie!=null) { // Si se ha seleccionado especie se pasa para filtrar también por ella
				camposIgual= new String[]{"idEspecie", String.valueOf(especie.getId())};		
			} 
			ArrayList<Animal> animales = (ArrayList<Animal>) per.getObetosFiltradoMulti("Animal", new String[]{"nombre", nombre}, camposIgual);
			
			Vector animalesV= new Vector();
			
			for (Animal a : animales) {
				Vector animal = new Vector();
				animal.add(a.getId());
				animal.add(a.getNombre());
				animal.add(a.getEspecie().getDescripcion());
				
				animal.add(a.getZona().getDescripcion());
				
				Date d = a.getFechaNac();
				animal.add(d!=null?Utilidades.getFechaString(d):"");
				
				animalesV.add(animal);
			}
			
			return animalesV;
			
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
		return null;
	}

	/**
	 * Configura el formulario para modificar un elemento. <br>
	 * Desbloquea y limpia los campos llamando al super {@link CRUDPadre#prepararModificar} <br>
	 * Busca con {@link Persistencia#getObjetoMulti} el objeto seleccionado en la tabla y setea los campos del formulario con sus datos.
	 * @param parametros Los atributos necesarios para buscar el elemento seleccionado en la tabla en la BBDD.
	 */
	protected void prepararModificar(String[] parametros) {
		super.prepararModificar(parametros);

		try {
			String nombre = parametros[0];
			String especie = parametros[1];		
			
			Especie esp = (Especie)per.getObjetoMulti("Especie", new String[] {"descripcion", especie});
			animal = (Animal)per.getObjetoMulti("Animal", new String[] {"nombre", nombre, "idEspecie", String.valueOf(esp.getId())});
			
			textFieldId.setText(String.valueOf(animal.getId()));
			comboBoxEspecie.setSelectedItem(animal.getEspecie().getDescripcion());
			
			Date d = animal.getFechaNac();
			textFieldFecha.setText(d!=null?Utilidades.getFechaString(d):"");
			
			textFieldNombre.setText(animal.getNombre());
			comboBoxZona.setSelectedItem(animal.getZona().getDescripcion());
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
