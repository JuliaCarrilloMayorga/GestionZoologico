package interfaz;

import java.awt.Color;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
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
import javax.swing.JSeparator;

public class INFORMETratEspecie  extends JDialog {
	JFrame padre;
	Persistencia per;

	private JTable table;
	private JScrollPane scrollPane;
	private JPanel contentPane;
	private JLabel labelTitulo;
	private JButton btnBuscar;
	private JComboBox comboBoxEspecie;
	private JTextField textFieldFechaDesde;
	private JTextField textFieldFechaHasta;
	
	//Vector con las columnas de la tabla
	private Vector vTitColum;
	

	private void iniciar() {
		vTitColum=new Vector();
		String[] camposTabla = new String[] {"Fecha trat.", "Nombre animal", "Descripción trat.", "Nombre empleado"};
		
		for (int i = 0; i < camposTabla.length; i++) {
			vTitColum.add(camposTabla[i]);
		}
	}
	
	public INFORMETratEspecie(JFrame padre, boolean modal, Persistencia per) {
		super(padre, modal);
		setResizable(false);
		this.padre = padre;
		this.per = per;
		
		iniciar();
		
		setForeground(new Color(204, 255, 204));
		setTitle("Informe de Tratamientos por especie");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 542, 472);
		
		contentPane = new JPanel();
		contentPane.setBackground(new Color(255, 255, 255));
		contentPane.setForeground(new Color(204, 255, 204));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		labelTitulo = new JLabel("Informe de Tratamientos por especie");
		labelTitulo.setHorizontalAlignment(SwingConstants.CENTER);
		labelTitulo.setFont(new Font("Tahoma", Font.PLAIN, 20));
		labelTitulo.setBounds(10, 11, 516, 32);
		contentPane.add(labelTitulo);
		
		JPanel panelUnico = new JPanel();
		panelUnico.setBackground(new Color(204, 255, 204));
		panelUnico.setBounds(10, 54, 516, 140);
		contentPane.add(panelUnico);
		panelUnico.setLayout(null);
		
		JLabel lblIdEmpleado = new JLabel("Especie:");
		lblIdEmpleado.setBounds(10, 15, 67, 24);
		panelUnico.add(lblIdEmpleado);
		
		comboBoxEspecie = new JComboBox();
		comboBoxEspecie.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cambioSeleccion();
			}
		});
		
		comboBoxEspecie.setBounds(87, 15, 284, 20);
		panelUnico.add(comboBoxEspecie);
		
		JLabel lblIdFecha = new JLabel("Fec. desde:");
		lblIdFecha.setBounds(10, 69, 91, 24);
		panelUnico.add(lblIdFecha);
		
		textFieldFechaDesde = new JTextField();
		textFieldFechaDesde.setEnabled(false);
		textFieldFechaDesde.setBounds(87, 69, 284, 20);
		panelUnico.add(textFieldFechaDesde);
		textFieldFechaDesde.setColumns(10);
		textFieldFechaDesde.setToolTipText("DD/MM/AAAA");
		
		btnBuscar = new JButton("Buscar");
		btnBuscar.setEnabled(false);
		btnBuscar.setBounds(403, 106, 103, 23);
		panelUnico.add(btnBuscar);
		btnBuscar.setMnemonic('b');
		
		JSeparator separator = new JSeparator();
		separator.setBounds(10, 52, 496, 13);
		panelUnico.add(separator);
		
		JLabel lblIdImporte = new JLabel("Fec. hasta:");
		lblIdImporte.setBounds(10, 106, 91, 24);
		panelUnico.add(lblIdImporte);
		
		textFieldFechaHasta = new JTextField();
		textFieldFechaHasta.setEnabled(false);
		textFieldFechaHasta.setColumns(10);
		textFieldFechaHasta.setBounds(87, 106, 284, 20);
		panelUnico.add(textFieldFechaHasta);
		textFieldFechaHasta.setToolTipText("DD/MM/AAAA");
		
		btnBuscar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buscar();
			}
		});

		setLocationRelativeTo(padre);
		
		JPanel panelFijo = new JPanel();
		panelFijo.setBackground(new Color(255, 245, 238));
		panelFijo.setBounds(10, 205, 516, 221);
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
		table.setRowSelectionAllowed(false);
		
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
		buttonCancelar.setBounds(403, 187, 103, 23);
		panelFijo.add(buttonCancelar);
		
		mostrarEspecies();
	}
	
	/**
	 * Rellena el comboBox de especies
	 */
	protected void mostrarEspecies() {
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

	//CONFIGURAR ELEMENTOS
	
	private void cambioSeleccion() {
		textFieldFechaDesde.setText("");
		textFieldFechaHasta.setText("");
		vaciarTabla();
		
		if(comboBoxEspecie.getSelectedIndex()!=0) {
			activarBusqueda(true);
		} else {
			activarBusqueda(false);
		}
	}
	private void activarBusqueda(boolean activar) {
		textFieldFechaDesde.setEnabled(activar);
		textFieldFechaHasta.setEnabled(activar);
		btnBuscar.setEnabled(activar);
	}
	private void cancelar() {
		comboBoxEspecie.setSelectedIndex(0);
		textFieldFechaDesde.setText("");
		textFieldFechaHasta.setText("");
		activarBusqueda(false);
		vaciarTabla();
	}
	
	//BUSCAR
	
	private void vaciarTabla() {
		DefaultTableModel modeloTabla = (DefaultTableModel)table.getModel();
		modeloTabla.setDataVector(null, vTitColum);
	}
	
	private void buscar() {
		if(validarCampos()) {
			activarBusqueda(false);
			
			Vector objetos = objetosTabla();
			
		    DefaultTableModel modeloTabla = (DefaultTableModel)table.getModel();
		    modeloTabla.setDataVector(objetos, vTitColum);
		}
	}
	
	private boolean validarCampos() {
		String fechaDesde = textFieldFechaDesde.getText().trim();
		String fechaHasta = textFieldFechaHasta.getText().trim();
		
		if(!Utilidades.isFechaValida(fechaDesde) || !Utilidades.isFechaValida(fechaHasta)){ //Comprobar que Fecha es valida
			JOptionPane.showMessageDialog(this, "Ambas fechas deben tener formato DD/MM/AAAA.", "Fecha inválida", JOptionPane.WARNING_MESSAGE);
			return false;
		}
		
		Date desde=null;
		Date hasta=null;
		try {
			 desde = Utilidades.getFechaDate(fechaDesde);
			 hasta = Utilidades.getFechaDate(fechaHasta);
		} catch (ParseException e) {
			Utilidades.notificaError(padre, "Error interno", e, "Hay un error en el código del programa.");
		}
		
		if(desde.after(hasta)){ //Comprobar que fechaDesde es anterior o igual a fechaHasta
			JOptionPane.showMessageDialog(this, "La fecha DESDE debe ser anterior o igual a la fecha HASTA.", "Fechas inválidas", JOptionPane.WARNING_MESSAGE);
			return false;
		}
		
		return true;
	}

	
	protected Vector objetosTabla(){ 
		String descEspecie = String.valueOf(comboBoxEspecie.getSelectedItem());

		String fechaDesde="";
		String fechaHasta="";
		try {
			fechaDesde = Utilidades.getFechaStringMysql(textFieldFechaDesde.getText().trim());
			fechaHasta = Utilidades.getFechaStringMysql(textFieldFechaHasta.getText().trim());
		} catch (ParseException e1) {
			Utilidades.notificaError(padre, "Error interno", e1, "Hay un error en el código del programa.");
		}
		
		Vector resultados = new Vector();
		try {
			ArrayList<Animaltratamiento> aniTrats=per.getAniTratsFechas(descEspecie, fechaDesde, fechaHasta);
			
			for (Animaltratamiento aniTrat : aniTrats) {
				Vector e =  configurarVectorObjeto(aniTrat);
				resultados.add(e);
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
		return resultados;
	};

	private Vector configurarVectorObjeto(Animaltratamiento aniTrat){  //{"Fecha trat.", "Nombre animal", "Descripción trat.", "Nombre empleado"};
		Vector e =  new Vector();

		Date d = aniTrat.getId().getFechaHora();			
		e.add(Utilidades.getFechaHoraString(d));
		
		e.add(aniTrat.getAnimal().getNombre());		
		e.add(aniTrat.getTratamiento().getDescripcion());
		e.add(aniTrat.getEmpleado().getNombre());
		
		return e;
	}

	
}
