package interfaz;

import java.awt.Color;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.hibernate.exception.JDBCConnectionException;

import conexionBBDD.Persistencia;
import tablas.*;
import utilidades.Utilidades;

public class PROCESOPadre  extends JDialog {
	JFrame padre;
	Persistencia per;

	protected JTable table;
	private JScrollPane scrollPane;
	
	protected JButton btnGuardar;
	protected JPanel contentPane;
	protected JPanel panelUnico;
	protected JPanel panelFijo;
	protected JLabel labelTitulo;
	protected JButton btnEliminar;
	protected JButton btnAniadir;
	
	protected String nombreProceso;
	protected String[] camposTabla;
	
	
	public PROCESOPadre(JFrame padre, boolean modal, Persistencia per) {
		
		super(padre, modal);
		setResizable(false);
		this.padre = padre;
		this.per = per;
		
		setForeground(new Color(204, 255, 204));
		setTitle("Proceso");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 542, 409);
		
		contentPane = new JPanel();
		contentPane.setBackground(new Color(255, 255, 255));
		contentPane.setForeground(new Color(204, 255, 204));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		labelTitulo = new JLabel("Proceso de ");
		labelTitulo.setHorizontalAlignment(SwingConstants.CENTER);
		labelTitulo.setFont(new Font("Tahoma", Font.PLAIN, 20));
		labelTitulo.setBounds(10, 11, 516, 32);
		contentPane.add(labelTitulo);
		
		panelUnico = new JPanel();
		panelUnico.setBackground(new Color(204, 255, 204));
		panelUnico.setBounds(10, 54, 516, 83);
		contentPane.add(panelUnico);
		panelUnico.setLayout(null);
		
		setLocationRelativeTo(padre);
		
		panelFijo = new JPanel();
		panelFijo.setBackground(new Color(255, 245, 238));
		panelFijo.setBounds(10, 148, 516, 221);
		contentPane.add(panelFijo);
		panelFijo.setLayout(null);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 11, 496, 159);
		panelFijo.add(scrollPane);
		
		table = new JTable() {
			public boolean isCellEditable(int row, int column) {                
                return false;               
			};
		};
		
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane.setViewportView(table);
		table.setModel(new DefaultTableModel(
				
		));
		
		JButton buttonCancelar = new JButton("Cancelar");
		buttonCancelar.setMnemonic('c');
		buttonCancelar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelar();
			}
		});
		buttonCancelar.setBounds(339, 187, 103, 23);
		panelFijo.add(buttonCancelar);
		
		btnGuardar = new JButton("Guardar");
		btnGuardar.setMnemonic('g');
		btnGuardar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				guardarCambios();
			}
		});
		btnGuardar.setBounds(207, 187, 103, 23);
		panelFijo.add(btnGuardar);
		
		btnGuardar.setEnabled(false);
		
		btnEliminar = new JButton("Eliminar");
		btnEliminar.setMnemonic('e');
		btnEliminar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				eliminarProceso();
			}
		});
		btnEliminar.setEnabled(false);
		btnEliminar.setBounds(69, 187, 103, 23);
		panelFijo.add(btnEliminar);
		
		ListSelectionModel lsm = table.getSelectionModel();
		
		lsm.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				if(table.getSelectedRow()!=-1) {
					btnEliminar.setEnabled(true);
				} else {
					btnEliminar.setEnabled(false);
				}
			}
		});
	}
	
	//___ BÚSQUEDA ANIMALES

	//Busca un animal por nombre y especie, es igual que el metodo de CRUDAnimal
	protected Vector resultadosBusqueda(String nombre, Especie especie) {
		//{"id", "Nombre", "Especie", "Zona", "Fecha nacimiento"};
		String[] camposIgual= {};
		if(especie!=null) {
			camposIgual= new String[]{"idEspecie", String.valueOf(especie.getId())};		
		} 
		Vector animalesV= new Vector();
		try {
			
			ArrayList<Animal> animales = (ArrayList<Animal>) per.getObetosFiltradoMulti("Animal", new String[]{"nombre", nombre}, camposIgual);
			
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

		return animalesV;
	}

	public void mostrarAnimal(String[] paramsDevolver) {}
	public void reiniciarAnimal(){}

	//____________ACCIONES
	
	protected void eliminarProceso() {}
	
	protected void guardarCambios() {}
	
	protected void cancelar() {
		btnGuardar.setEnabled(false);
		btnAniadir.setEnabled(false);
		btnEliminar.setEnabled(false);
		
		configurarTabla(objetosTabla());
	}

	
	//____________CONFIGURAR TABLA
	
	protected void configurarTabla(Vector objetos) {
		Vector vTitColum=new Vector();
		
		for (int i = 0; i < camposTabla.length; i++) {
			vTitColum.add(camposTabla[i]);
		}
        
        DefaultTableModel modeloTabla = (DefaultTableModel)table.getModel();
        modeloTabla.setDataVector(objetos, vTitColum);
        
        configTamanioCols();
	}
	
	protected Vector objetosTabla(){return new Vector();};
	protected void configTamanioCols(){};
	
	//____________RELLENAR COMBOBOXS
	
	protected void mostrarEspecies(JComboBox comboBoxEspecie) {
		try {
			ArrayList<Especie> especies = (ArrayList<Especie>) per.getTodosObjetos("Especie"); 
			
			comboBoxEspecie.addItem("");
			
			for (Especie especie : especies) {
				comboBoxEspecie.addItem(especie.getDescripcion());
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
	protected void mostrarZonas(JComboBox comboBoxZona) {
		try {
			ArrayList<Zona> zonas = (ArrayList<Zona>) per.getTodosObjetos("Zona"); 
			
			comboBoxZona.addItem("");
			
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
	protected void mostrarEmpleados(JComboBox comboBoxEmp) {
		try {
			ArrayList<Empleado> empleados = (ArrayList<Empleado>) per.getTodosObjetos("Empleado"); 
			
			comboBoxEmp.addItem("");
			
			for (Empleado empleado : empleados) {
				comboBoxEmp.addItem(empleado.getNombre());
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
	protected void mostrarTratamientos(JComboBox comboBoxTrat) {
		try {
			ArrayList<Tratamiento> tratamientos = (ArrayList<Tratamiento>) per.getTodosObjetos("Tratamiento"); 
			
			comboBoxTrat.addItem("");
			
			for (Tratamiento tratamiento : tratamientos) {
				comboBoxTrat.addItem(tratamiento.getDescripcion());
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
	protected void mostrarEventos(JComboBox comboBoxEvent) {
		try {
			ArrayList<Evento> evs = (ArrayList<Evento>) per.getTodosObjetos("Evento"); 
			
			comboBoxEvent.addItem("");
			
			for (Evento ev : evs) {
				comboBoxEvent.addItem(ev.getDescripcion());
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
	protected void mostrarAlimentos(JComboBox comboBoxAli) {
		try {
			ArrayList<Alimento> evs = (ArrayList<Alimento>) per.getTodosObjetos("Alimento"); 
			
			comboBoxAli.addItem("");
			
			for (Alimento ev : evs) {
				comboBoxAli.addItem(ev.getDescripcion());
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
	
}
