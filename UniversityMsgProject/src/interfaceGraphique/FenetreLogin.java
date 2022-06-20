package interfaceGraphique;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import serveur.Request;
import utilisateur.Utilisateur;

public class FenetreLogin extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField userLogin;
	private JTextField psswLogin;
	private JButton login;
	ObjectInputStream input;
	ObjectOutputStream output;
	
	
	public FenetreLogin() {
		Socket socketclient;
		try {
			socketclient = new Socket("127.0.0.1", 5000);
			OutputStream out = socketclient.getOutputStream();
			output = new ObjectOutputStream(out);
			InputStream in = socketclient.getInputStream();
			input = new ObjectInputStream(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		setTitle("login");
		
		setSize(300, 200);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		JLabel logo = new JLabel();
		JPanel pLogin = new JPanel();
		
		JLabel affichage = new JLabel("entrez votre login puis votre password");

		
		userLogin = new JTextField(20);
		psswLogin = new JTextField(20);
		login = new JButton("login");
	
				
		pLogin.add(affichage);
		pLogin.add(userLogin);
		pLogin.add(psswLogin);
		pLogin.add(login);
		
		
		
		this.setLayout(new BorderLayout());
		this.add(logo, BorderLayout.NORTH);
		
		this.add(pLogin, BorderLayout.CENTER);
		
		login.addActionListener(new loginButtonListener());		
	}
	

	
	class loginButtonListener implements ActionListener {
		
		JFrame erreurUtilisateur;
		
		public void actionPerformed(ActionEvent event) {
			if((userLogin.getText().length() > 0) && (psswLogin.getText().length() > 0 )) {
				//si nom entrez + mdp entrez bon setVisible true page principale
				//TODO connection
				//recuperer toutes les donnees et implementer les classes pour le faire
				//TODO
				
				boolean reponse = true;
				Utilisateur utilisateur = new Utilisateur("rien", "rien", "rien", "rien", "rien");
				
				try {
					output.writeObject(Request.CONNECTION);
					output.writeObject(userLogin.getText());
					output.writeObject(psswLogin.getText());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				try {
					reponse = (boolean) input.readObject();
				} catch (ClassNotFoundException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}					
				
				if(reponse) {					
					try {
						utilisateur = (Utilisateur) input.readObject();
					} catch (ClassNotFoundException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					FenetrePrincipale user = new FenetrePrincipale(utilisateur, input, output);
					user.setVisible(true);
					dispose();
				}
				
				else {
					erreurUtilisateur = new JFrame();
					JLabel messageErreur = new JLabel("Login ou password incorrect !");
					JButton buttonOk = new JButton("ok");
					JPanel pMessageErreur = new JPanel();
					JPanel pButtonOk = new JPanel();
					
					
					//parametrage de la fenetre newTicket
					
					erreurUtilisateur.setTitle("Connexion impossible");
					erreurUtilisateur.setSize(200, 100);
					erreurUtilisateur.setLocationRelativeTo(null);
					erreurUtilisateur.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
					
					//ajout des composant a chaque JPanel
					
					pMessageErreur.add(messageErreur);
					pButtonOk.add(buttonOk);					
					
					JPanel contentPaneNewTicket = (JPanel)erreurUtilisateur.getContentPane();
				
					//acces au contentPane de newTicket pour structurer la fenetre
					
					BorderLayout layout = new BorderLayout();
					contentPaneNewTicket.setLayout(layout);
					
					contentPaneNewTicket.add(pMessageErreur, BorderLayout.CENTER);
					contentPaneNewTicket.add(pButtonOk, BorderLayout.SOUTH);

					
					erreurUtilisateur.setAlwaysOnTop(true);
					erreurUtilisateur.setVisible(true);
					
					buttonOk.addActionListener(new ButtonOkListener());
				}
						
					}
					
				}
		
		class ButtonOkListener implements ActionListener{
			public void actionPerformed(ActionEvent e) {
				erreurUtilisateur.dispose();	
			}
	    }
	}	
}
