package serveur;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

public class AjoutUtilisateur extends JFrame {
		
		/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel entrez_id = new JLabel("entrez l'identifiant identifiant :");
	public JTextArea identifiant = new JTextArea();
	private JLabel entrez_mdp = new JLabel("entrez le mot de passe :");
	public JTextArea motDePasse = new JTextArea();
	private JLabel entrez_nom = new JLabel("entrez le nom :");
	public JTextArea le_nom = new JTextArea();
	private JLabel entrez_prenom = new JLabel("entrez le prenom :");
	public JTextArea prenom = new JTextArea();
	private JLabel entrez_type = new JLabel("entrez le type :");
	public JTextArea type = new JTextArea();
	
	
	
	public JButton ajouter = new JButton("Ajouter");
	
	
	
	public AjoutUtilisateur() {
		super("application test");
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setSize(600,400);
		this.setLocationRelativeTo(null);
		
		JLabel infos = new JLabel("Entrez les informations du nouvel utilisateur :" );
		
		
		JPanel contentpane = (JPanel) this.getContentPane();
		contentpane.setLayout(new GridLayout(12,1,5,5));
		contentpane.add(infos);
		contentpane.add(entrez_id);
		contentpane.add(identifiant);
		contentpane.add(entrez_mdp);
		contentpane.add(motDePasse);
		contentpane.add(entrez_nom);
		contentpane.add(le_nom);
		contentpane.add(entrez_prenom);
		contentpane.add(prenom);
		contentpane.add(entrez_type);
		contentpane.add(type);
		contentpane.add(ajouter);
		
		
		
	}
	
	

	

	
	public static void main(String[] test) {
		//pour le look
		try{
			UIManager.setLookAndFeel(new NimbusLookAndFeel());
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		
		
		
		AjoutUtilisateur mywindow = new AjoutUtilisateur();
		mywindow.setVisible(true);
		
		
	}
		
}

		
		
	
	
	

