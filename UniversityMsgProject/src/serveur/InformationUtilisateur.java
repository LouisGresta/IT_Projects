package serveur;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import utilisateur.Groupe;
import utilisateur.Utilisateur;

public class InformationUtilisateur extends JFrame  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AjoutUtilisateur mywindow2 = new AjoutUtilisateur();
	private JTextArea nom = new JTextArea();
	
	private DefaultMutableTreeNode racine = new DefaultMutableTreeNode("liste des groupes");
	private JTree arbre = new JTree(racine);
	
	private DefaultTableModel modelUtilisateurs;
	private DefaultTableModel modelUtilisateursGroupe;
	private JTable tabUtilisateurs;
	private JTable tabUtilisateursGroupe;
	
	//parametres liés à la base de données
	private String url;
	private String login;
	private String password;
	//listes d'utilisateurs et de groupes
	private List<Utilisateur> userList = new ArrayList<>();
	private List<Groupe> groupList = new ArrayList<>();
	
	
	
	
	
	
	public InformationUtilisateur(String url, String login, String password) {
		super("application test");
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setSize(600,400);
		this.setLocationRelativeTo(null);
		//initialisation parametres de connection base de donnee
		this.url = url;
		this.login = login;
		this.password = password;
		
		
		// vecteurs pour le tableau des utilisateurs 
		Vector<Vector<String>> userVector = new Vector<>();
		Vector<Vector<String>> userVectorGroup = new Vector<>();
		Vector<String> userColumnNames = new Vector<>();
		
		userColumnNames.add("Login");
		userColumnNames.add("Mot_De_Passe");
		userColumnNames.add("Nom");
		userColumnNames.add("Prénom");
		userColumnNames.add("Type");
		try(Connection connection = DriverManager.getConnection(url, login, password)){
			String cmdSql = "SELECT * FROM T_User";
			try(Statement statement = connection.createStatement(); 
					ResultSet resultSet = statement.executeQuery(cmdSql)){ //executeUpdate(cmdSql) pour un update de la bdd (insert/update)
				while(resultSet.next()) {
					Utilisateur user = new Utilisateur(resultSet.getString(1), resultSet.getString(2),
							resultSet.getString(3), resultSet.getString(4), resultSet.getString(5));
					userList.add(user);
					Vector<String> newUserVector = new Vector<>();
					newUserVector.add(resultSet.getString(1)); //loginu
					newUserVector.add(resultSet.getString(2)); //password
					newUserVector.add(resultSet.getString(3)); //nameu
					newUserVector.add(resultSet.getString(4)); //firstname
					newUserVector.add(resultSet.getString(5)); //typeu
					userVector.add(newUserVector);
					userVectorGroup.add(newUserVector);
				}
			}
			System.out.println(userVector);
			System.out.println("Utilisateurs chargés");
			cmdSql = "SELECT * FROM T_Groupe";
			try(Statement statement = connection.createStatement(); 
					ResultSet resultSet = statement.executeQuery(cmdSql)){ 
				while(resultSet.next()) {
					Groupe group = new Groupe(resultSet.getInt("numg"), resultSet.getString("nameg"));
					groupList.add(group);
				}
			}
			System.out.println("Groupes chargés");
			cmdSql = "SELECT * FROM T_Compose";
			try(Statement statement = connection.createStatement(); 
					ResultSet resultSet = statement.executeQuery(cmdSql)){ 
				while(resultSet.next()) {
					for(Utilisateur user : userList) {
						if(user.getIdentifiant().equals(resultSet.getString("loginu"))) {
							for(Groupe group : groupList) {
								if(group.getNum() == resultSet.getInt("numg"))
									group.ajouterUtilisateur(user);
							}
						}
					}
				}
			}
			System.out.println("Utilisateurs ajoutés aux groupes");
		} catch (SQLException e) {
			System.out.println("Connexion à la base de donnée échouée ou erreur requete sql");
			e.printStackTrace();
		}
		
		////creation tableau utilisateurs dans l'onglet groupe
		modelUtilisateurs = new DefaultTableModel(userVector,userColumnNames) {
			private static final long serialVersionUID = -1300775064522222233L;

			@Override
			public void setValueAt(Object aValue, int row, int column) {
				String columnName = null;
				switch (column) {
					case 0:
						columnName = "loginu";
						break;
					case 1:
						columnName = "password";
						break;
					case 2:
						columnName = "nameu";
						break;
					case 3:
						columnName = "firstname";
						break;
					case 4:
						columnName = "typeu";
						break;
					default:
						System.out.println("Numéro de colonne impossible");
						break;
				}
				// Connexion et ajout de l'utilisateur renseigné dans la base de donnée
				try(Connection connection = DriverManager.getConnection(url, login, password)){
					String cmdSql = "UPDATE T_User SET " + columnName + "=? WHERE loginu=?";
					try(PreparedStatement statement = connection.prepareStatement(cmdSql)){
						statement.setString(1, (String) aValue);
						statement.setString(2, (String) this.getValueAt(row, 0));
						statement.executeUpdate();
					}
				} catch (SQLException e1) {
					System.out.println("Connexion à la base de donnée échouée");
					e1.printStackTrace();
				}
				
				System.out.println("utilisateur mis à jour dans la base de donnée");
				userVector.get(row).set(column, (String) aValue);
			}
		};
		tabUtilisateurs = new JTable(modelUtilisateurs);
		tabUtilisateurs.setShowGrid(true);
		//creation tableau utilisateurs dans l'onglet groupe
		modelUtilisateursGroupe = new DefaultTableModel(userVectorGroup,userColumnNames) {
			private static final long serialVersionUID = 1L;

			@Override
            public boolean isCellEditable(int row,int column) {
                return false;
            }
        };
		tabUtilisateursGroupe = new JTable(modelUtilisateursGroupe);
		tabUtilisateursGroupe.setShowGrid(true);
        
		JButton supprimer_element_selectionne = new JButton("Supprimer l'élément sélectionné");
		JButton ajouter_utilisateur_groupe = new JButton("Ajouter au groupe sélectionné");
		JButton ajouter_utilisateur = new JButton("Ajouter");
		JButton supprimer_utilisateur = new JButton("Supprimer");
		JButton ajouter_groupe = new JButton("Ajouter");
		
		JLabel entrez_nom = new JLabel("entrez le nom du nouveau groupe :");
		
		
		//creation arbre des groupes
		for(Groupe groupe : groupList) {
			DefaultMutableTreeNode noeuds = new DefaultMutableTreeNode(groupe.getNom());
			for(Utilisateur user : groupe.getUtilisateurs()) {
				DefaultMutableTreeNode feuille = new DefaultMutableTreeNode(user.getIdentifiant());
				noeuds.add(feuille);
			}
			racine.add(noeuds);
		}
		
		DefaultTreeModel model = (DefaultTreeModel) arbre.getModel();
		model.reload(racine);
		
		JPanel fenetre_ajouter_groupe = new JPanel();
		fenetre_ajouter_groupe.setLayout(new GridLayout(3,1,20,20));
		fenetre_ajouter_groupe.add(supprimer_element_selectionne);
		fenetre_ajouter_groupe.add(entrez_nom);
		fenetre_ajouter_groupe.add(nom);
		fenetre_ajouter_groupe.add(ajouter_groupe);
		
		ajouter_groupe.addActionListener(new AjouterGroupe());
		ajouter_utilisateur_groupe.addActionListener(new AjouterUtilisateurGroupe());
		supprimer_element_selectionne.addActionListener(new SupprimerElementSelectionne());
		ajouter_utilisateur.addActionListener(event -> mywindow2.setVisible(true));
		supprimer_utilisateur.addActionListener(new SupprimerUtilisateur());
		
		JScrollPane listeUtilisateurs = new JScrollPane(tabUtilisateurs);
		JSplitPane supprimerSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT,listeUtilisateurs, supprimer_utilisateur);
		supprimerSplit.setResizeWeight(0.90);
		JSplitPane ajouterSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT,supprimerSplit, ajouter_utilisateur);
		ajouterSplit.setResizeWeight(0.90);
		
		JScrollPane fenetre_modifier = new JScrollPane(tabUtilisateursGroupe);	

		
		JSplitPane tableau_bouton = new JSplitPane(JSplitPane.VERTICAL_SPLIT,fenetre_modifier,ajouter_utilisateur_groupe);
		tableau_bouton.setResizeWeight(0.90);
		
		JSplitPane supprimer_selectionne = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableau_bouton, supprimer_element_selectionne);
		supprimer_selectionne.setResizeWeight(0.90);
				
		
		JSplitPane groupe_modifier = new JSplitPane(JSplitPane.VERTICAL_SPLIT,supprimer_selectionne,fenetre_ajouter_groupe);
		groupe_modifier.setResizeWeight(0.90);
		
		JSplitPane groupe_ajouter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, arbre,groupe_modifier);
		
		groupe_ajouter.setResizeWeight(0.4);
		
		
		
		
		JTabbedPane onglets = new JTabbedPane();
		onglets.addTab("utilisateurs", ajouterSplit );
		onglets.setMnemonicAt(0,KeyEvent.VK_1);
		

	
		onglets.addTab("groupes", groupe_ajouter );
		onglets.setMnemonicAt(1,KeyEvent.VK_2);
		
		JPanel contentpane = (JPanel) this.getContentPane();
		
		mywindow2.ajouter.addActionListener(new AjouterUtilisateur());
		contentpane.add(onglets);
		
	}
		
	class SupprimerUtilisateur implements ActionListener {
			
		public void actionPerformed(ActionEvent e) {
			String id = (String) tabUtilisateurs.getValueAt(tabUtilisateurs.getSelectedRow(), 0);
			System.out.println(id);
			// Connexion et ajout de l'utilisateur renseigné dans la base de donnée
			try(Connection connection = DriverManager.getConnection(url, login, password)){
				String cmdSql = "DELETE FROM T_User WHERE loginu=?";
				try(PreparedStatement statement = connection.prepareStatement(cmdSql)){
					statement.setString(1, id);
					statement.executeUpdate();
				}
			} catch (SQLException e1) {
				System.out.println("Connexion à la base de donnée échouée ou erreur requete sql");
				e1.printStackTrace();
			}
			System.out.println("utilisateur supprimé de la base de donnée");
			//actualisation interface
			modelUtilisateursGroupe.removeRow(tabUtilisateurs.getSelectedRow());
			modelUtilisateurs.removeRow(tabUtilisateurs.getSelectedRow());
		}
		
			
			
	}
				
	public class AjouterUtilisateur implements ActionListener {
			
		public void actionPerformed(ActionEvent e) {
			String id = mywindow2.identifiant.getText();
			String mdp = mywindow2.motDePasse.getText();
			String nom = mywindow2.le_nom.getText();
			String prenom = mywindow2.prenom.getText();
			String type = mywindow2.type.getText();
			userList.add(new Utilisateur(id, mdp, nom, prenom, type));
			// Connexion et ajout de l'utilisateur renseigné dans la base de donnée
			try(Connection connection = DriverManager.getConnection(url, login, password)){
				String cmdSql = "INSERT INTO T_User VALUES(?,?,?,?,?)";
				try(PreparedStatement statement = connection.prepareStatement(cmdSql)){
					statement.setString(1, id);
					statement.setString(2, mdp);
					statement.setString(3, nom);
					statement.setString(4, prenom);
					statement.setString(5, type);
					statement.executeUpdate();
				}
			} catch (SQLException e1) {
				System.out.println("Connexion à la base de donnée échouée ou erreur requete sql");
				e1.printStackTrace();
			}
			
			System.out.println("utilisateur ajouté à la base de donnée");
			//actualisation interface
			modelUtilisateurs.addRow(new String[] {id,mdp,nom,prenom,type});
			modelUtilisateursGroupe.addRow(new String[] {id,mdp,nom,prenom,type});
			mywindow2.dispose();
			
			
			
		}	
	}
		
		
	public class AjouterGroupe implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			int numG = 0;
			// Connexion et ajout du groupe renseigné dans la base de donnée
			try(Connection connection = DriverManager.getConnection(url, login, password)){
				String cmdSql = "SELECT numg FROM T_Groupe";
				try(Statement statement = connection.createStatement();
						ResultSet result = statement.executeQuery(cmdSql)){
					while(result.next()) {
						if(numG < result.getInt(1))
							numG = result.getInt(1);
					}
				}
				cmdSql = "INSERT INTO T_Groupe VALUES(?,?)";
				try(PreparedStatement statement = connection.prepareStatement(cmdSql)){
					statement.setInt(1, numG);
					statement.setString(2, nom.getText());
					statement.executeUpdate(); 
				}
			} catch (SQLException e1) {
				System.out.println("Connexion à la base de donnée échouée ou erreur requete sql");
				e1.printStackTrace();
			}
			System.out.println(numG+1 + ", " + nom.getText());
			groupList.add(new Groupe(numG+1, nom.getText()));
			System.out.println("groupe ajouté à la base de donnée");
			//actualisation interface
			DefaultMutableTreeNode nouveau_noeuds = new DefaultMutableTreeNode(nom.getText());
			racine.add(nouveau_noeuds);
			DefaultTreeModel model = (DefaultTreeModel) arbre.getModel();
			model.reload(racine);
		}		
	}
		
		
		
	public class AjouterUtilisateurGroupe implements ActionListener {
				
		public void actionPerformed(ActionEvent e) {
			
			DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)arbre.getLastSelectedPathComponent();
			String loginU = (String) tabUtilisateursGroupe.getValueAt(tabUtilisateursGroupe.getSelectedRow(), 0);
			String nomG = (String) selectedNode.getUserObject(); 
			int numG = 0;
			for(Groupe groupe : groupList) {
				if(groupe.getNom().equals(nomG)) {
					numG = groupe.getNum();
					for(Utilisateur user : userList) {
						if(user.getIdentifiant().equals(loginU))
							groupe.ajouterUtilisateur(user);
					}
				}
				
			}
			
			// Connexion et ajout de l'utilisateur au groupe dans la base de donnée
			try(Connection connection = DriverManager.getConnection(url, login, password)){
				String cmdSql = "INSERT INTO T_Compose VALUES(?, ?)";
				try(PreparedStatement statement = connection.prepareStatement(cmdSql)){
					statement.setString(1, loginU);
					statement.setInt(2, numG);
					statement.executeUpdate(); 
				}
			} catch (SQLException e1) {
				System.out.println("Connexion à la base de donnée échouée ou erreur requete sql");
				e1.printStackTrace();
			}
			
			System.out.println("utilisateur ajouté au groupe dans la base de donnée");
			//actualisation interface
			DefaultMutableTreeNode nouveau_noeuds =	new DefaultMutableTreeNode(loginU);
			selectedNode.add(nouveau_noeuds);
			DefaultTreeModel model = (DefaultTreeModel) arbre.getModel();
			model.reload(racine);
		}
	}
			
	
			
	public class SupprimerElementSelectionne implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
			
			DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) arbre.getLastSelectedPathComponent();
			DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) selectedNode.getParent();
			String selectedNodeName = (String) selectedNode.getUserObject();
			boolean isGroup = false;
			int numG = 0;
			Groupe groupToRemove = null;
			for(Groupe groupe : groupList) {
                if (selectedNodeName.equals(groupe.getNom())) {
                	isGroup = true;
                	numG = groupe.getNum();
                    groupToRemove = groupe;
                }
            }
			if(isGroup) { //groupe
				groupList.remove(groupToRemove);
				// Connexion 
				try(Connection connection = DriverManager.getConnection(url, login, password)){
					String cmdSql = "DELETE FROM T_Compose WHERE numg=?";
					try(PreparedStatement statement = connection.prepareStatement(cmdSql)){
						statement.setInt(1, numG);
						statement.executeUpdate(); 
					}
					cmdSql = "DELETE FROM T_Groupe WHERE numg=?";
					try(PreparedStatement statement = connection.prepareStatement(cmdSql)){
						statement.setInt(1, numG);
						statement.executeUpdate(); 
					}
				} catch (SQLException e1) {
					System.out.println("Connexion à la base de donnée échouée ou erreur requete sql");
					e1.printStackTrace();
				}
				System.out.println("groupe supprimé d la base de donnée");
				
			}
			else { //utilisateur
				String loginU = selectedNodeName; 
				String nomG = (String) parentNode.getUserObject(); 
				for(Groupe groupe : groupList) {
					if(groupe.getNom().equals(nomG)) {
						numG = groupe.getNum();
						for(Utilisateur user : userList) {
							if(user.getIdentifiant().equals(loginU))
								groupe.supprimerUtilisateur(user);
						}
					}
				}
				
				// Connexion 
				try(Connection connection = DriverManager.getConnection(url, login, password)){
					String cmdSql = "DELETE FROM T_Compose WHERE loginu=? and numg=?";
					try(PreparedStatement statement = connection.prepareStatement(cmdSql)){
						statement.setString(1, loginU);
						statement.setInt(2, numG);
						statement.executeUpdate(); 
					}
				} catch (SQLException e1) {
					System.out.println("Connexion à la base de donnée échouée ou erreur requete sql");
					e1.printStackTrace();
				}
				System.out.println("utilisateur supprimé au groupe dans la base de donnée");
			}
				//actualisation interface
				DefaultTreeModel model = (DefaultTreeModel) arbre.getModel();
				model.removeNodeFromParent((DefaultMutableTreeNode)arbre.getLastSelectedPathComponent());
				model.reload(racine);
		}
	}
		
}


		
		
	
	
	

