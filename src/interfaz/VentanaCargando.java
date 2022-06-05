package interfaz;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import conexionBBDD.Persistencia;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;
import java.awt.Color;
import javax.swing.ImageIcon;

public class VentanaCargando extends JFrame {

	public JPanel contentPaneCargando;
	
	/**
	 * Create the frame.
	 */
	public static void main(String[] args) {
		VentanaCargando v = new VentanaCargando();
	}
	
	public VentanaCargando() {
		setEnabled(false);
		setResizable(false);
		setTitle("Cargando...");
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 281, 166);
		contentPaneCargando = new JPanel();
		contentPaneCargando.setBackground(new Color(255, 250, 250));
		contentPaneCargando.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPaneCargando);
		contentPaneCargando.setLayout(null);
		
		JLabel lblCargando = new JLabel("... Cargando ...");
		lblCargando.setHorizontalAlignment(SwingConstants.CENTER);
		lblCargando.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblCargando.setBounds(10, 24, 255, 29);
		contentPaneCargando.add(lblCargando);
		
		JLabel lblEsperePorFavor = new JLabel("Espere por favor.");
		lblEsperePorFavor.setHorizontalAlignment(SwingConstants.CENTER);
		lblEsperePorFavor.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblEsperePorFavor.setBounds(20, 59, 255, 17);
		contentPaneCargando.add(lblEsperePorFavor);
		
		JLabel lblNewLabel_1 = new JLabel("");
		lblNewLabel_1.setIcon(new ImageIcon(VentanaCargando.class.getResource("/utilidades/loading-cargandoSm.gif")));
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setBounds(10, 93, 255, 33);
		contentPaneCargando.add(lblNewLabel_1);
		
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setIcon(new ImageIcon(VentanaCargando.class.getResource("/utilidades/junglaFondo.jpg")));
		lblNewLabel.setBounds(0, 0, 275, 137);
		contentPaneCargando.add(lblNewLabel);
		
		setLocationRelativeTo(null);
		setVisible(true);
	}
}
