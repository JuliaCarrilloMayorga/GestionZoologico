package interfaz;

import java.awt.BorderLayout;


import java.awt.EventQueue;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import conexionBBDD.*;
import tablas.*;
import utilidades.Utilidades;

import java.awt.Color;
import javax.swing.JLabel;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.SwingConstants;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.JScrollPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.ListSelectionModel;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

public class CRUDTablaGeneral  extends JDialog {

	private JPanel contentPane;
	
	CRUDPadre padre;
	PROCESOPadre procesoPadre;
	String[][] parametros;
	
	Vector objetos;
	
	private JTable table;
	private JLabel lblNoEnc1;
	private JLabel lblNoEnc2;
	private JScrollPane scrollPane;
	private JButton btnSeleccionar;
	private JButton btnCrear;
	
	/**
	 * Ventana con la tabla con los resultados de las búsquedas realizadas desde las clases CRUDs
	 * @param padre Ventana CRUD padre. De ella se objetienen datos como el nombre de la clase o los nombres de las columnas necesarios.
	 * @param procesoPadre Ventana PROCESO padre. Solo existe si esta tabla se crea desde una ventana de proceso, para buscar nimales por ejemplo.
	 * @param modal Propio del JDialog. Bloquea la ventana anterior mientras esta esté en uso.
	 * @param objetos Vector de vectores de Strings con los datos de las entradas que debe tener la tabla
	 * @param parametros Parámetros de la búsqueda, empleados para ponerlos en los subtítulos. Cada línea es un parámetro, la primera columna es el nombre del atrubuto y la segunda el valor.
	 */
	public CRUDTablaGeneral(CRUDPadre padre, PROCESOPadre procesoPadre, boolean modal, Vector objetos, String[][] parametros) {
		super(padre, modal);
		setResizable(false);
		this.padre = padre;
		this.procesoPadre = procesoPadre;
		this.parametros = parametros;
		this.objetos = objetos;
		
		setForeground(new Color(204, 255, 204));
		setTitle("Seleccionar "+ padre.nombreClase);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 542, 356);
		
		contentPane = new JPanel();
		contentPane.setBackground(new Color(255, 255, 255));
		contentPane.setForeground(new Color(204, 255, 204));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel labelTitulo = new JLabel(padre.nombreClasePlur);
		labelTitulo.setHorizontalAlignment(SwingConstants.CENTER);
		labelTitulo.setFont(new Font("Tahoma", Font.PLAIN, 20));
		labelTitulo.setBounds(10, 11, 516, 32);
		contentPane.add(labelTitulo);
		
		JPanel panel = new JPanel();
		panel.setBackground(new Color(204, 255, 204));
		panel.setBounds(10, 85, 516, 221);
		contentPane.add(panel);
		panel.setLayout(null);
		
		//Parámetro 1
		JLabel lblCont1 = new JLabel("Con "+ parametros[0][0] +": '" + parametros[0][1] +"'"); //Ejemplo: Con nombre: 'MANOL'
		lblCont1.setHorizontalAlignment(SwingConstants.CENTER);
		lblCont1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblCont1.setBounds(10, 54, 516, 20);
		contentPane.add(lblCont1);
		
		
		//Parámetro 2 (A veces está)
		if(parametros.length==2) {
			JLabel lblCont2 = new JLabel("Con "+ parametros[1][0] +": '" + parametros[1][1] +"'");
			lblCont2.setHorizontalAlignment(SwingConstants.CENTER);
			lblCont2.setFont(new Font("Tahoma", Font.PLAIN, 14));
			lblCont2.setBounds(10, 77, 516, 20);
			contentPane.add(lblCont2);
			
			panel.setBounds(10, 109, 516, 221); //Si está hace la ventana un poco más grande y desplaza el panel de la tabla
			setBounds(100, 100, 542, 380);
		} 
		
		setLocationRelativeTo(padre);
		
		btnCrear = new JButton("Crear");
		btnCrear.setMnemonic('r');
		btnCrear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { //______CREAR
				padre.prepararCrear();
				dispose();
			}
		});
		btnCrear.setBounds(74, 187, 103, 23);
		panel.add(btnCrear);
		
		JButton button_1 = new JButton("Cancelar");
		button_1.setMnemonic('c');
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { //______CANCELAR
				
				if(procesoPadre!=null) { //Si se esta creando desde un PROCESO
					procesoPadre.reiniciarAnimal(); //Reinicia los campos del animal en el formulario del proceso
				}else {
					padre.reiniciar(); //Reinicia todo el formulario del CRUD básico
				}
				dispose();
			}
		});
		button_1.setBounds(339, 187, 103, 23);
		panel.add(button_1);
		
		if(procesoPadre==null) { //Si se está llamando desde un CRUD básico se añade la etiqueta que muestra "¿Desea crear uno nuevo?" si no hay entradas
			lblNoEnc2 = new JLabel("¿Desea crear uno nuevo?");
			lblNoEnc2.setVisible(false);
			lblNoEnc2.setBounds(10, 92, 496, 41);
			panel.add(lblNoEnc2);
			lblNoEnc2.setHorizontalAlignment(SwingConstants.CENTER);
			lblNoEnc2.setFont(new Font("Tahoma", Font.PLAIN, 14));
		}
		
		lblNoEnc1 = new JLabel("No se han encontrado "+ padre.nombreClasePlur +" coincidentes con este filtro.");
		lblNoEnc1.setVisible(false);
		lblNoEnc1.setBounds(10, 54, 496, 50);
		panel.add(lblNoEnc1);
		lblNoEnc1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNoEnc1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 11, 496, 159);
		panel.add(scrollPane);
		
		table = new JTable() {
			public boolean isCellEditable(int row, int column) {                
                return false;               
			};
		};
		
		
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane.setViewportView(table);
		table.setModel(new DefaultTableModel(
			new Object[][] {
			},
			padre.campos
		));
		
		btnSeleccionar = new JButton("Seleccionar");
		btnSeleccionar.setMnemonic('s');
		btnSeleccionar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {  //______SELECCIONAR
				devolverParametros();
				dispose();
			}
		});
		btnSeleccionar.setEnabled(false);
		btnSeleccionar.setBounds(207, 187, 103, 23);
		panel.add(btnSeleccionar);
		
		ListSelectionModel lsm = table.getSelectionModel();
		lsm.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) { // Si se seleccioan un elemento de la tabla se enablea el botón Seleccionar
				if(table.getSelectedRow()!=-1) {
					btnSeleccionar.setEnabled(true);
				} else {
					btnSeleccionar.setEnabled(false);
				}
			}

		});
		
		if(objetos.isEmpty()) { // Si la tabla está vacía se muestran las etiquetas que lo indican en lugar de la tabla
			table.setVisible(false);
			scrollPane.setVisible(false);
			lblNoEnc1.setVisible(true);
			if(lblNoEnc2 != null)
				lblNoEnc2.setVisible(true);
		} else {
			configurarTabla(objetos);
		}
	}
	
	/**
	 *  Método utilizado al crear este formulario desde un proceso para hacer invisible el botón crear.
	 * @return JButton Crear (btnCrear)
	 */
	public JButton getBtnCrear() {
		return btnCrear;
	}
	
	private void devolverParametros() {
		int fila = table.getSelectedRow();
		
		String[] paramsDevolver = new String[parametros.length];
		for (int i = 0; i < parametros.length; i++) {
			for (int j = 0; j < padre.campos.length; j++) {
				if(padre.campos[j].equals(parametros[i][0])) {
					paramsDevolver[i] = String.valueOf(table.getModel().getValueAt(fila, j));
				}
			}
		}
		
		if(procesoPadre!=null) { //Si se esta creando desde un PROCESO
			procesoPadre.mostrarAnimal(paramsDevolver);
		} else {
			padre.prepararModificar(paramsDevolver);
		}
	}
	
	/**
	 * Configura la tabla.
	 * <ul>
	 * 	<li> Crea el vector con los nombre de las columnas en base as Array de String campos del CRUDPadre
	 * 	<li> Utiliza el vector de objetos dado, traido por el constructor, para rellenar la tabla
	 * 	<li> Hace más pequeña la primera columna, pues corresponde al campo de Ids.
	 * </ul>
	 * @param objetos El vector de vectores de String con los datos de los elementos a mostrar
	 */
	private void configurarTabla(Vector objetos) {
		Vector vTitColum=new Vector();
		
		for (int i = 0; i < padre.campos.length; i++) {
			vTitColum.add(padre.campos[i]);
		}
        
        DefaultTableModel modeloTabla = (DefaultTableModel)table.getModel();
        modeloTabla.setDataVector(objetos, vTitColum);
        
        
        TableColumnModel columnModel = table.getColumnModel();
        int num = columnModel.getColumnCount();
        int tamanio1 = 60+((40/num-10)*2); //Si hay menos columnas hace la columna un poco más ancha
        
        TableColumn columna;
        columna=table.getColumnModel().getColumn(0);
        columna.setPreferredWidth(tamanio1);
        columna.setMaxWidth(tamanio1);
        columna.setMinWidth(5);

	}
}
