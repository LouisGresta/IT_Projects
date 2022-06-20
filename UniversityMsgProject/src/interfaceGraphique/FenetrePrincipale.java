package interfaceGraphique;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import serveur.Request;
import utilisateur.Groupe;
import utilisateur.Message;
import utilisateur.Ticket;
import utilisateur.Utilisateur;


public class FenetrePrincipale extends JFrame{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Utilisateur utilisateur;
	private JPanel contentPane;
	private JTextField saisiMessage;
	private JScrollPane pTree;
	private JButton envoyer;
	private JTree tree;
	private HashMap<String, JTextArea> allTextArea;
	protected HashMap<Integer, DefaultMutableTreeNode> mapNode;
	private JScrollPane pChatArea;
	protected String chatActif;
	private DefaultMutableTreeNode root;
	private JSplitPane splitPane;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private TreeChangeListener TreeSelection;

	
	
	
	public FenetrePrincipale(Utilisateur utilisateur, ObjectInputStream input, ObjectOutputStream output) {
		this.utilisateur = utilisateur;	
		this.input = input;
		this.output = output;
		
		setTitle("Utilisateur");

		setSize(800, 600);

		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.addWindowListener(new CloseWindowListener());
	    
	    
		root = new DefaultMutableTreeNode(utilisateur.getPrenom()+" "+utilisateur.getNom());
		
		allTextArea = new HashMap<String, JTextArea>(); 
		mapNode = new HashMap<Integer, DefaultMutableTreeNode>();
		
		int i = 0;
		
		for(Groupe groupe:utilisateur.getListeTickets().keySet())
		{
			//creation du noeud groupeX
			DefaultMutableTreeNode groupeNode = new DefaultMutableTreeNode(groupe.getNom());
			
			mapNode.put(i, groupeNode);
			
			
			for(Ticket ticket:utilisateur.getListeTickets().get(groupe))
			{
			//creation de la feuilleY lie au noeudX
				groupeNode.add(new DefaultMutableTreeNode(ticket.getTitre()));
				
			//remplissage des differents chat avec les messages sauvegardes
				JTextArea zoneDeTexte = new JTextArea();
				zoneDeTexte.setEditable(false);
				for(Message message:ticket.getListeMessages()) {
					zoneDeTexte.append(message.getEnvoyeur().getPrenom() +" "+ message.getEnvoyeur().getNom() + "\n | " + message.getMsg() + " |\n");
				}	
				allTextArea.put(ticket.getTitre(), zoneDeTexte);
			}
			root.add(groupeNode);
			i++;
		}		
		
		
		//initialisation/declaration de touts les composants de la fenetre principale
		
		pChatArea = new JScrollPane(new JTextArea());
        tree = new JTree(root);
        pTree = new JScrollPane(tree);
        saisiMessage = new JTextField(50);
	    envoyer = new JButton("envoyer");
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pTree, pChatArea);
	   
		JPanel pBarreMessage = new JPanel(new FlowLayout());
        JButton ajouterTicket = new JButton("Ajouter un ticket");
        JPanel pAjouterTicket = new JPanel();  
	    
        
      
	    pAjouterTicket.add(ajouterTicket);
	    pBarreMessage.add(saisiMessage);
	    pBarreMessage.add(envoyer);
		
	    
	    //acces au content pane et au layout pour structurer la fenetre
	    
       	contentPane = (JPanel) this.getContentPane();
       	
		BorderLayout layout = new BorderLayout();
		contentPane.setLayout(layout);
		contentPane.add(splitPane, BorderLayout.CENTER);
		contentPane.add(pBarreMessage,BorderLayout.PAGE_END);
		contentPane.add(pAjouterTicket, BorderLayout.BEFORE_FIRST_LINE);

		//ajout des listenner pour les boutons ou l'arbre
		
		ajouterTicket.addActionListener(new AjouterTicketButtonListener());
		envoyer.addActionListener(new sendMessageButtonListener());
		
		TreeSelection = new TreeChangeListener();
		tree.addTreeSelectionListener(TreeSelection);
		
	}
	
	
	class CloseWindowListener implements WindowListener{
		@Override
		public void windowClosing(WindowEvent e) {
			try {
				output.writeObject(Request.QUITAPPLICATION);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
		}

		@Override
		public void windowIconified(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowDeiconified(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowActivated(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowDeactivated(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowOpened(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowClosed(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}
	}
	/*Listener sur l'arbre qui gere le changement de chat pour chaque ticket*/
	
	class TreeChangeListener implements TreeSelectionListener{
		public void valueChanged(TreeSelectionEvent e) {
			DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
			String current = (String) currentNode.getUserObject();
			
			if(currentNode.isLeaf()) {
				splitPane.remove(pChatArea);
		
				pChatArea = new JScrollPane(allTextArea.get(current));
				splitPane.add(pChatArea);
				setContentPane(contentPane);
				chatActif = current;
			}
		}	
	}
	
	/*Listener sur le bouton envoyer qui gere l'ajoue du message au chat concerne et l'ajoute a la base de donnees via le serveur*/
	
	class sendMessageButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
	        if (saisiMessage.getText().length() > 0 ) {
	        	
	        	int numMess = 0;
	        	
	        	try {
					output.writeObject(Request.SENDMESSAGE);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
	        	
	        	try {
					numMess = (int) input.readObject();
				} catch (ClassNotFoundException | IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
	        	
	        	//ecriture du message ecrit dans le chat
	        	
	        	allTextArea.get(chatActif).append(utilisateur.getPrenom()+ " "+ utilisateur.getNom() + "\n | " + saisiMessage.getText() + " |\n");
	        	saisiMessage.setText("");
	        	setContentPane(contentPane);
	        	
	        	
	        	DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
	            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) selectedNode.getParent();
	            String nomTicket = (String) selectedNode.getUserObject(); 
	            String groupe = (String) parentNode.getUserObject();
	            

	            Message newMessage = new Message(numMess, saisiMessage.getText(), utilisateur);
	            Ticket sendTicket = null;
	            Groupe sendGroupe = null;
	            
	            //on cherche le bon ticket pour l'envoyer au serveur
	            
	            for(Groupe groupeName:utilisateur.getListeTickets().keySet()) {
	            	if(groupeName.getNom().equals(groupe))
	            	{
	            		sendGroupe = groupeName;
	            		break;
	            	}
	            }
	            for(Ticket ticket:utilisateur.getListeTickets().get(sendGroupe)) {
	            	if(ticket.getTitre().equals(nomTicket))
	            	{
	            		sendTicket = ticket;
	            		break;
	            	}
	            }
	            
	            sendTicket.ajouterMessage(newMessage);
	            
	            try {
					output.writeObject(sendTicket);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					
				}      	
	        }
	    }
	}
	
	
	/*Listener sur la creation d'un nouveau ticket*/
	class AjouterTicketButtonListener implements ActionListener{
		
		//declaration des composants 
		
		JFrame newTicket = new JFrame();
		JButton creerTicket;
		JTextField titreTicket;
		JTextField destinataire;
		JTextField probleme;
		
		public void actionPerformed(ActionEvent e) {
			
			//initialisation/declaration des composants
			
			newTicket = new JFrame();
			JPanel pText = new JPanel();
			JPanel pCreer = new JPanel();
			titreTicket = new JTextField(20);
			destinataire = new JTextField(20);
			probleme = new JTextField(20);
			JButton creerTicket = new JButton("créer");
			
			JLabel lTitre = new JLabel("Titre :");
			JLabel lDestinataire = new JLabel("Destinataire :");
			JLabel lProbleme = new JLabel("Probleme :");

			//parametrage de la fenetre newTicket
			
			newTicket.setTitle("Nouveau Ticket");
			newTicket.setSize(350, 200);
			newTicket.setLocationRelativeTo(null);
			newTicket.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			
			//ajout des composant a chaque JPanel
			
			pText.add(lTitre);
			pText.add(titreTicket);
			pText.add(lDestinataire);
			pText.add(destinataire);
			pText.add(lProbleme);
			pText.add(probleme);
			pCreer.add(creerTicket);
			
			JPanel contentPaneNewTicket = (JPanel)newTicket.getContentPane();
		
			//acces au contentPane de newTicket pour structurer la fenetre
			
			BorderLayout layout = new BorderLayout();
			contentPaneNewTicket.setLayout(layout);
			
			contentPaneNewTicket.add(pText, BorderLayout.CENTER);
			contentPaneNewTicket.add(pCreer, BorderLayout.SOUTH);

			
			newTicket.setAlwaysOnTop(true);
			newTicket.setVisible(true);
			
			creerTicket.addActionListener(new CreerTicketActionListener());
		}
		
		/*Listener interne au bouton creerTicket quoi gere la creation d'un ticket*/
		
		class CreerTicketActionListener implements ActionListener{

			public void actionPerformed(ActionEvent e) {
				
				//pour eviter les conflit avec le listener de l'arbre
				
				tree.removeTreeSelectionListener(TreeSelection);
				
				if((titreTicket.getText().length() > 0) && (destinataire.getText().length() > 0) && (probleme.getText().length() > 0)) {

					//2 cas soit on l'ajoute a un groupe existant sois on creer un nouveau groupe
					
					int verif = -1;
					int i = 0;
					int numMess = 0;
					
					try {
						output.writeObject(Request.SENDNUMMESSAGE);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					try {
						numMess = (int) input.readObject();
					} catch (ClassNotFoundException | IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					for(Groupe groupe:utilisateur.getListeTickets().keySet()) {
						if(destinataire.getText().equals(groupe.getNom())) {
							verif = 0;

							Message newMess = new Message(numMess, probleme.getText(), utilisateur);
							Ticket newTicket = new Ticket(utilisateur, groupe, newMess, titreTicket.getText()); 
							utilisateur.getListeTickets().get(groupe).add(newTicket);
							
							mapNode.get(i).add(new DefaultMutableTreeNode(newTicket.getTitre()));
							
							DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
							model.reload(root);
							
							JTextArea newJTextArea = new JTextArea((utilisateur.getPrenom()+ " "+ utilisateur.getNom() + "\n | " + probleme.getText() + " |\n"));
							newJTextArea.setEditable(false);
							allTextArea.put(newTicket.getTitre(), newJTextArea);
							break;
						}
					i++;
					}
					if(verif==-1) {
						System.out.println(destinataire.getText());
						Groupe newGroupe = new Groupe(-1, destinataire.getText());
						Message newMess = new Message(numMess, probleme.getText(), utilisateur);
						Ticket newTicket = new Ticket(utilisateur, newGroupe, newMess, titreTicket.getText()); 
						TreeSet<Ticket> listNewTicket = new TreeSet<>();
						listNewTicket.add(newTicket);
						utilisateur.getListeTickets().put(newGroupe, listNewTicket);
						
						
						DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(newTicket.getDestinataire().getNom());
						newNode.add(new DefaultMutableTreeNode(newTicket.getTitre()));
						mapNode.put(i+1, newNode);
						root.add(newNode);
						
						DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
						model.reload(root);
						
						JTextArea newJTextArea = new JTextArea();
						newJTextArea.append(utilisateur.getPrenom()+ " "+ utilisateur.getNom() + "\n | " + probleme.getText() + " |\n");
						newJTextArea.setEditable(false);
						allTextArea.put(newTicket.getTitre(), newJTextArea);
						

					}
				}
				
				//on reactive le listener
				
				tree.addTreeSelectionListener(TreeSelection);
				
				newTicket.dispose();
			}
			
		}
		
	}
	

	class TicketTreeModel implements TreeModel{
		
		protected Groupe root;
		
		public TicketTreeModel(Groupe root) {
			this.root = root;
		}
		
		public Object getRoot() {
			return this.root;
		}

		
		public Object getChild(Object parent, int index) {
			return utilisateur.getListeTickets().get(parent);

		}

		public int getChildCount(Object parent) {
			return utilisateur.getListeTickets().get(parent).size();
		}
		
		public boolean isLeaf(Object node) {
			return this.getChildCount(node)==0;
		}

		@Override
		public void valueForPathChanged(TreePath path, Object newValue) {
			// TODO Auto-generated method stub
			
		}

		public int getIndexOfChild(Object parent, Object child) {
			int i = 0;
			for(Ticket ticket:utilisateur.getListeTickets().get(parent)) {
				if(ticket == child)
					break;
				i++;
			}
			return i;
		}

		@Override
		public void addTreeModelListener(TreeModelListener l) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void removeTreeModelListener(TreeModelListener l) {
			// TODO Auto-generated method stub
			
		}	
	}
	
	public JTree getTree() {
		return tree;
	}


	public void setTree(JTree tree) {
		this.tree = tree;
	}
}