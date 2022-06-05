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

public class CRUDAlimentos extends CRUDPadre {
	private JPanel contentPane;
	private JFrame padre;

	private JTextField textFieldDescripcion;
	private JTextField textFieldCoste;
	
	private Alimento alimento;

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
		elementosForm.add(textFieldCoste);
		
		elementosFormBloq.add(textFieldCoste);
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
		nombreClase = "Alimento";
		nombreClasePlur = "Alimentos";
		campos = new String[] {"id", "Descripción", "Coste"};
		masculino=true;
	}
	
	//CONSTRUCTOR
	public CRUDAlimentos(JFrame padre, boolean modal, Persistencia per2) {
		super(padre, modal);
		
		configurarNombres();
		
		setTitle("Gestión de "+nombreClasePlur.toLowerCase() );
		
		setResizable(false);
		this.padre = padre;
		this.per = per2;
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 362, 289);
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
		btnAccion.setBounds(10, 219, 103, 23);
		
		contentPane.add(btnAccion);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(10, 205, 336, 13);
		contentPane.add(separator);
		
		JButton btnReiniciar = new JButton("Reiniciar");
		btnReiniciar.setBounds(243, 219, 103, 23);
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
		
		btnEliminar = new JButton("Eliminar");
		btnEliminar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				eliminar();
			}
		});
		btnEliminar.setVisible(false);
		btnEliminar.setBounds(126, 219, 103, 23);
		contentPane.add(btnEliminar);
		
		JLabel lblCoste = new JLabel("Coste:");
		lblCoste.setBounds(20, 159, 40, 24);
		contentPane.add(lblCoste);
		
		textFieldCoste = new JTextField();
		textFieldCoste.setEnabled(false);
		textFieldCoste.setColumns(10);
		textFieldCoste.setBounds(97, 159, 235, 20);
		contentPane.add(textFieldCoste);
		
		JPanel panel = new JPanel();
		panel.setBackground(new Color(204, 255, 204));
		panel.setBounds(10, 70, 336, 124);
		contentPane.add(panel);
		
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
		try {
			per.actualizarSesion(alimento);

			Set<Consume> consumes = alimento.getConsumes(per);

			if(consumes.size()>0) {
				JOptionPane.showMessageDialog(this, "No puede eliminar el alimento con id " + id + ", ya que tiene "+ consumes.size() +" animal"+ (consumes.size()==1?"":"es")+" relacionado"+(consumes.size()==1?"":"s")+".", "Alimento no eliminado", JOptionPane.WARNING_MESSAGE);
			} else {
				int res = JOptionPane.showConfirmDialog(this, "Está seguro de que quiere eliminar el alimento de id: "+id+"?", "Borrar alimento", JOptionPane.YES_NO_OPTION);

				if(res==JOptionPane.YES_OPTION) {
					per.eliminarObjeto("Alimento",alimento);
					JOptionPane.showMessageDialog(this, "Se ha eliminado el alimento de id " + id + ".", "Alimento eliminado", JOptionPane.INFORMATION_MESSAGE);
				}
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

	/**
	 * Configura el objeto dado con los datos de los campos del formulario. <br>
	 * Castea a un objeto del tipo de esta clase el elemento dado.<br>
	 * Setea cada uno de sus atributos con los valores de los campos.
	 * @param elementoPadre Elemento de la clase ElementoPadre a configurar
	 */
	protected void configurar(ElementoPadre elementoPadre) {
		Alimento ali = (Alimento) elementoPadre;
		
		ali.setDescripcion(textFieldDescripcion.getText().trim().toUpperCase());
		ali.setCoste(Double.valueOf(textFieldCoste.getText().trim()));
	}

	/**
	 * Valida los campos del formulario antes de guardar el objeto. <br>
	 * Animal: Validar facha (Correcta o vacía) y Nombre y Especie con contenido y únicos.
	 * @param nuevo Si es nuevo se comprueba que no exista en la BBDD otro elemento con los mismos campos que deben ser únicos,<br>
	 * si no es nuevo es un elemento a modificar y se comprueba que no coincidan con otro distinto al modificado.
	 */
	protected boolean validarCampos(boolean nuevo) {
		String descripcion = textFieldDescripcion.getText().trim().toUpperCase();
		String coste = textFieldCoste.getText().trim();
		
		if(descripcion.equals("") || coste.equals("")) { // Comprobar descripcion con contenido
			JOptionPane.showMessageDialog(this, "Debe completar los campos de DESCRIPCIÓN y COSTE.", "Campo incompleto", JOptionPane.WARNING_MESSAGE);	
			return false;
		} 
		
		if(!coste.matches("[0-9]+(\\.[0-9]+)?") || Double.valueOf(coste)==0) {
			JOptionPane.showMessageDialog(this, "Debe completar el campo de COSTE con un número mayor que 0.", "Campo incorrecto", JOptionPane.WARNING_MESSAGE);
			return false;
		}
		try {
			Alimento ali = (Alimento)per.getObjetoMulti("Alimento", new String[] {"descripcion", descripcion});
			if(nuevo) {
				if(ali!=null) { //La zona no debe existir
					JOptionPane.showMessageDialog(this, "Ya existe un alimento con descripción " + descripcion +".", "Alimento ya existente", JOptionPane.WARNING_MESSAGE);
					return false;
				}
			} else {
				int id = Integer.valueOf(textFieldId.getText());
				
				if(ali!=null && (ali.getId()!=id)) { //La zona no debe coincidir con otro distinto al modificado
					JOptionPane.showMessageDialog(this, "Ya existe un alimento distinto (ID: "+ ali.getId() +") con descripcion " + descripcion, "Alimento ya existente", JOptionPane.WARNING_MESSAGE);
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

	/**
	 * Realiza una búsqueda por los parámetros permitidos para este objeto mediante el método {@link Persistencia#getObetosFiltradoMulti} <br>
	 * Crea un vector de vectores de strings con los datos de cada elemento, que es lo que se necesito para alimentar el JTable de datos.
	 * @param descripcion String con uno de los parámetros de búsqueda
	 * @return Vector con los objetos resultantes de la búsqueda
	 */
	private Vector resultadosBusqueda(String descripcion) {
		//{"id", "Descripcion", "Coste"};
		Vector objV= new Vector();
		try {
			ArrayList<Alimento> alimento = (ArrayList<Alimento>) per.getObetosFiltradoMulti("Alimento", new String[]{"descripcion", descripcion}, new String[]{});
			
			for (Alimento a : alimento) {
				Vector o = new Vector();
				o.add(a.getId());
				o.add(a.getDescripcion());
				o.add(a.getCoste());
				
				objV.add(o);
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
		return objV;
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
			alimento = (Alimento)per.getObjetoMulti("Alimento", new String[] {"descripcion", descripcion});
			
			textFieldId.setText(String.valueOf(alimento.getId()));
			textFieldDescripcion.setText(alimento.getDescripcion());
			textFieldCoste.setText(String.valueOf(alimento.getCoste()));
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
