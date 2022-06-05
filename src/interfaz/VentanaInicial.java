package interfaz;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.hibernate.HibernateException;
import org.hibernate.exception.GenericJDBCException;
import org.hibernate.exception.JDBCConnectionException;

import com.mysql.jdbc.exceptions.jdbc4.MySQLNonTransientConnectionException;
import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;

import conexionBBDD.*;
import utilidades.Utilidades;

import java.awt.event.ActionListener;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Properties;
import java.awt.event.ActionEvent;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;

/**
 * 
 * @author Julia
 *
 * Lanza el programa
	Menú desde donde se lanza cada formulario
	Lee el archivo CFG.INI, inicia la persistencia y la pasa a los hijos
	Mientras se crea la persistencia lanza la VentanaCargando y luego la quita
 */
public class VentanaInicial extends JFrame {
	//Persistencia de todo el programa. Se pasa a lso hijos
	private static Persistencia per;
	private static JPanel contentPane;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			VentanaCargando ventana = new VentanaCargando();

			iniciarPersistencia();
			
			VentanaInicial frame = new VentanaInicial();
			ventana.setVisible(false);
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Create the frame.
	 */
	public VentanaInicial() {
		setTitle("Gestión de zoológico");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, 444, 21);
		contentPane.add(menuBar);
		
		JMenu mnCruds = new JMenu("CRUDs");
		mnCruds.setMnemonic('c');
		menuBar.add(mnCruds);
		
		JMenuItem mntmAnimales = new JMenuItem("Animales");
		mntmAnimales.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
		mntmAnimales.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ventanaAnimales();
			}
		});
		
		JMenuItem mntmAlimentos = new JMenuItem("Alimentos");
		mntmAlimentos.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK));
		mntmAlimentos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ventanaAlimentos();
			}
		});
		mnCruds.add(mntmAlimentos);
		mnCruds.add(mntmAnimales);
		
		JMenuItem mntmEspecies = new JMenuItem("Especies");
		mntmEspecies.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		mntmEspecies.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ventanaEspecies();
			}
		});
		
		JMenuItem mntmEmpleados = new JMenuItem("Empleados");
		mntmEmpleados.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.CTRL_MASK));
		mntmEmpleados.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ventanaEmpleados();
			}
		});
		mnCruds.add(mntmEmpleados);
		mnCruds.add(mntmEspecies);
		
		JMenuItem mntmEventos = new JMenuItem("Eventos");
		mntmEventos.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_MASK));
		mntmEventos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ventanaEventos();
			}
		});
		mnCruds.add(mntmEventos);
		
		JMenuItem mntmTratamientos = new JMenuItem("Tratamientos");
		mntmTratamientos.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_MASK));
		mntmTratamientos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ventanaTratamientos();
			}
		});
		mnCruds.add(mntmTratamientos);
		
		JMenuItem mntmZonas = new JMenuItem("Zonas");
		mntmZonas.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK));
		mntmZonas.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ventanaZonas();
			}
		});
		mnCruds.add(mntmZonas);
		
		JMenu mnGestinDeProcesos = new JMenu("Gestión de relaciones");
		mnGestinDeProcesos.setMnemonic('g');
		menuBar.add(mnGestinDeProcesos);
		
		JMenuItem mntmCompraDeEntradas = new JMenuItem("Compra de entradas");
		mntmCompraDeEntradas.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
		mntmCompraDeEntradas.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ventanaEntradas();
			}
		});
		mnGestinDeProcesos.add(mntmCompraDeEntradas);
		
		JMenuItem mntmGestinDeNminas = new JMenuItem("Gestión de nóminas");
		mntmGestinDeNminas.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_MASK));
		mntmGestinDeNminas.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ventanaNominas();
			}
		});
		mnGestinDeProcesos.add(mntmGestinDeNminas);
		
		JMenuItem mntmCapacitacinDeLos = new JMenuItem("Capacitación de los Trabajadores");
		mntmCapacitacinDeLos.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
		mntmCapacitacinDeLos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ventanaCapaz();
			}
		});
		
		JSeparator separator = new JSeparator();
		mnGestinDeProcesos.add(separator);
		
		JMenuItem mntmConsumoDeLos = new JMenuItem("Alimentos de los Animales");
		mntmConsumoDeLos.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
		mntmConsumoDeLos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ventanaConsumo();
			}
		});
		mnGestinDeProcesos.add(mntmConsumoDeLos);
		mnGestinDeProcesos.add(mntmCapacitacinDeLos);
		
		JMenuItem mntmTratamientosDeAnimales = new JMenuItem("Tratamientos de Animales");
		mntmTratamientosDeAnimales.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
		mntmTratamientosDeAnimales.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ventanaTratAnim();
			}
		});
		mnGestinDeProcesos.add(mntmTratamientosDeAnimales);
		
		JMenuItem mntmZonasDeLos = new JMenuItem("Zonas de los Trabajadores");
		mntmZonasDeLos.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
		mntmZonasDeLos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ventanaTrabZona();
			}
		});
		mnGestinDeProcesos.add(mntmZonasDeLos);
		
		JMenu mnNewMenu = new JMenu("Informes");
		mnNewMenu.setMnemonic('i');
		menuBar.add(mnNewMenu);
		
		JMenuItem mntmInformeDeTratamientos = new JMenuItem("Tratamientos de especies");
		mntmInformeDeTratamientos.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
		mntmInformeDeTratamientos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ventanaTratEspecies();
			}
		});
		mnNewMenu.add(mntmInformeDeTratamientos);
		
		JLabel lblGestinDeZoolgico = new JLabel("Gestión de Zoológico");
		lblGestinDeZoolgico.setHorizontalAlignment(SwingConstants.CENTER);
		lblGestinDeZoolgico.setFont(new Font("Tahoma", Font.PLAIN, 28));
		lblGestinDeZoolgico.setBounds(10, 207, 424, 42);
		contentPane.add(lblGestinDeZoolgico);
		
		JLabel label = new JLabel("");
		label.setIcon(new ImageIcon(VentanaInicial.class.getResource("/utilidades/portadaC.jpg")));
		label.setBounds(0, 0, 444, 271);
		contentPane.add(label);
		
		
	}

	//Métodos para lanzar los distintos formularios del programa. Se les llama desde las opciones del menú.
	protected void ventanaTratEspecies() {
		INFORMETratEspecie ventana = new INFORMETratEspecie(this, true, per);
		ventana.setVisible(true);
	}
	protected void ventanaConsumo() {
		PROCESOConsumoAnim ventana = new PROCESOConsumoAnim(this, true, per);
		ventana.setVisible(true);
	}
	protected void ventanaNominas() {
		PROCESONominas ventana = new PROCESONominas(this, true, per);
		ventana.setVisible(true);
	}
	protected void ventanaCapaz() {
		PROCESOTrabCapaz ventana = new PROCESOTrabCapaz(this, true, per);
		ventana.setVisible(true);
	}
	protected void ventanaEntradas() {
		PROCESOEntradas ventana = new PROCESOEntradas(this, true, per);
		ventana.setVisible(true);
	}
	protected void ventanaEmpleados() {
		CRUDEmpleados ventana = new CRUDEmpleados(this, true, per);
		ventana.setVisible(true);
	}
	protected void ventanaTratamientos() {
		CRUDTratamientos ventana = new CRUDTratamientos(this, true, per);
		ventana.setVisible(true);
	}
	protected void ventanaTratAnim() {
		PROCESOTratamientosAnimal ventana = new PROCESOTratamientosAnimal(this, true, per);
		ventana.setVisible(true);
	}
	protected void ventanaTrabZona() {
		PROCESOTrabZona ventana = new PROCESOTrabZona(this, true, per);
		ventana.setVisible(true);
	}
	protected void ventanaAlimentos() {
		CRUDAlimentos ventana = new CRUDAlimentos(this, true, per);
		ventana.setVisible(true);
	}
	protected void ventanaEventos() {
		CRUDEventos ventana = new CRUDEventos(this, true, per);
		ventana.setVisible(true);
	}
	protected void ventanaEspecies() {
		CRUDEspecies ventana = new CRUDEspecies(this, true, per);
		ventana.setVisible(true);
	}
	protected void ventanaAnimales() {
		CRUDAnimales ventana = new CRUDAnimales(this, true, per);
		ventana.setVisible(true);
	}
	protected void ventanaZonas() {
		CRUDZonas ventana = new CRUDZonas(this, true, per);
		ventana.setVisible(true);
	}
	
	/**
	 * 
		Lee el archivo CFG.INI, inicia la persistencia.<br>
		Excepciones controladas:<br>
		<ul>
			<li> Archivo CFG.INI no encontrado
			<li> Atributo que indica tipo de persistencia no encontrado
			<li> Tipo de persistencia no soportada (Hibernate, JDBC)
			<li> JDBC:
			<ul>
				<li> Atributos necesarios para la conexión no encontrados
				<li> DDBB no existente
				<li> Puerto incorrecto
				<li> Conexión a mysql fallida
				<li> Error de código escrito
			</ul>
			<li> Hibernate:
			<ul>
				<li> Atributo que indica archivo de config de Hibernate no encontrado
				<li> Archivo de config no encontrado
				<li> Archivo de config con parámetros mal
				<li> Conexión a mysql fallida
				<li> Error de código escrito
			</ul>
		</ul>
	 */
	private static void iniciarPersistencia() {
		Properties prop = new Properties(); 
		try {
			prop.load(new InputStreamReader(VentanaInicial.class.getResourceAsStream("../CFG.INI")));  //Lee archivo CFG.INI
		} catch (Exception e) {  //Archivo CFG.INI no encontrado
			Utilidades.notificaError(null, "Error al cargar las propiedades", e, "Ha sucedido un error al cargar el archivo CFG.INI. \n"
					+ "Compruebe que existe un archivo CFG.INI en la raiz del proyecto. \n"
					+ "Error fatal, el programa se cerrará a continuación.");
			System.exit(0);
		} 

		String tipoPersistencia = prop.getProperty("tipoPersistencia");

		if(tipoPersistencia==null) { //Atributo que indica tipo de persistencia no encontrado
			Utilidades.notificaError(null, "Error de conexión", null, "Tipo de persistencia no indicada en el archivo CFG.INI (Atributo'tipoPersistencia' no encontrado).\n"
					+ "Modifique las opciones del archivo CFG.ini para solucionarlo.\n"
					+ "Error fatal, el programa se cerrará a continuación.");
			System.exit(0);
		}

		switch (tipoPersistencia) {
		case "mysqlJDBC": //________________________ JDBC _________
			try {
				String servidor = prop.getProperty("mysqlJDBC.servidor");
				String baseDatos = prop.getProperty("mysqlJDBC.basedatos");
				String puerto = prop.getProperty("mysqlJDBC.puerto");
				String usuario = prop.getProperty("mysqlJDBC.usuario");
				String password = prop.getProperty("mysqlJDBC.password");

				if(servidor==null || baseDatos==null || puerto==null || usuario==null || password==null) {  //Atributos necesarios para la conexión no encontrados
					Utilidades.notificaError(null, "Error de conexión", null, "Atrinutos de configuración de la conexión no indicados en el archivo CFG.INI \n"
							+ "Compruebe que están indicados los atributos 'mysqlJDBC.servidor', 'mysqlJDBC.basedatos', 'mysqlJDBC.puerto', 'mysqlJDBC.usuario' y 'mysqlJDBC.password'.\n"
							+ "Modifique las opciones del archivo CFG.ini para solucionarlo.\n"
							+ "Error fatal, el programa se cerrará a continuación.");
					System.exit(0);
				}

				per = new PersistenciaMysql(servidor, puerto, baseDatos, usuario, password);

			} catch (MySQLSyntaxErrorException em) { //Error al conectar a mysql, DDBB no existente
				Utilidades.notificaError(null, "Error de conexión", em, "No se ha podido conectar con MySQL.\n"
						+ "Compruebe que la base de datos indicada en el archivo CFG.INI es correcta.\n"
						+ "Error fatal, el programa se cerrará a continuación.");
				System.exit(0);

			} catch (MySQLNonTransientConnectionException em) { //Error al conectar a mysql, Puerto incorrecto
				Utilidades.notificaError(null, "Error de conexión", em, "No se ha podido conectar con MySQL.\n"
						+ "Compruebe que el puerto indicado en el archivo CFG.INI es correcto.\n"
						+ "Error fatal, el programa se cerrará a continuación.");
				System.exit(0);

			} catch (SQLException em) { //Error al conectar a mysql, si el servicio está desactivado o el driver JDBC mal enlazado
				Utilidades.notificaError(null, "Error de conexión", em, "No se ha podido conectar con MySQL.\n"
						+ "Compruebe que el servicio de MySQL está activado o revise la configuración indicada en CFG.INI.\n"
						+ "Error fatal, el programa se cerrará a continuación.");
				System.exit(0);

			} catch (Exception e) {
				Utilidades.notificaError(null, "Error interno", e, "Hay un error en el código del programa.");
				System.exit(0);
			} 
			break;

		case "hibernate": //________________________ HIBERNATE _________
			try {
				String archivoCFG = prop.getProperty("hibernate.archivoCFG");  

				if(archivoCFG==null) {  //Atributo que indica archivo de config de Hibernate no encontrado
					Utilidades.notificaError(null, "Error de conexión", null, "Archivo de configuración no indicado en el archivo CFG.INI (Atributo 'hibernate.archivoCFG' no encontrado).\n"
							+ "Modifique las opciones del archivo CFG.ini para solucionarlo.\n"
							+ "Error fatal, el programa se cerrará a continuación.");
					System.exit(0);
				}

				per = new PersistenciaHiber(archivoCFG, false); //Crea la persistencia
				per.getObjetoMulti("Animal", new String[] {"id", "1"});  //Realiza un select para comprobar que hay conexión a la BBDD

			} catch (JDBCConnectionException em) { //Error al conectar a mysql, si el servicio está desactivado o el driver JDBC mal enlazado
				Utilidades.notificaError(null, "Error de conexión", em, "No se ha podido conectar con MySQL.\n"
						+ "Compruebe que el servicio de MySQL está activado o revise la configuración de Hibernate.\n"
						+ "Error fatal, el programa se cerrará a continuación.");
				System.exit(0);

			} catch (GenericJDBCException el) { //Archivo de config con parámetros mal
				Utilidades.notificaError(null, "Error de conexión", el, "Archivo de configuración de Hibernate con datos incorrectos.\n"
						+ "Compruebe la configuración descrita en el archivo" + prop.getProperty("hibernate.archivoCFG") + ".\n"
						+ "Error fatal, el programa se cerrará a continuación.");
				System.exit(0);

			} catch (HibernateException e) { //Archivo de config no encontrado 
				Utilidades.notificaError(null, "Error de conexión", e, "Archivo de configuración de Hibernate '"
						+ prop.getProperty("hibernate.archivoCFG") +"' no encontrado.\n"
						+ "Compruebe que el archivo XML existe y que coincide con la configuración de CFG.INI.\n"
						+ "Error fatal, el programa se cerrará a continuación.");
				System.exit(0);
			}  catch (SQLException e) {
				Utilidades.notificaError(null, "Error interno", e, "Hay un error en el código del programa, en PersistenciaMysql.");
				System.exit(0);
			} catch (Exception e) {
				Utilidades.notificaError(null, "Error interno", e, "Hay un error en el código del programa.");
				System.exit(0);
			} 

			break;
		default: //Tipo de persistencia no soportada (Hibernate, JDBC)
			Utilidades.notificaError(null, "Error de conexión", null, "Tipo de persistencia no soportada: "+tipoPersistencia+".\n"
					+ "Modifique las opciones del archivo CFG.ini para solucionarlo.\n"
					+ "Error fatal, el programa se cerrará a continuación.");
			System.exit(0);
		}		
	}
}
