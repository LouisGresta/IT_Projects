package interfaceGraphique;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import utilisateur.Groupe;
import utilisateur.Message;
import utilisateur.Ticket;
import utilisateur.Utilisateur;

public class MainClient {

	public static void main(String[] args) {
		try{
            UIManager.setLookAndFeel(new NimbusLookAndFeel());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }	
        FenetreLogin login = new FenetreLogin();
		login.setVisible(true);	

//		Utilisateur utilisateur = new Utilisateur("FRE3259D", "1962-fzra", "Fabien", "REBU", "Etudiant");
//		
//		TreeSet<Ticket> tickets1 = new TreeSet<>();
//		TreeSet<Ticket> tickets2 = new TreeSet<>();
//		TreeSet<Ticket> tickets3 = new TreeSet<>();
//		
//		Groupe groupe1 = new Groupe(1, "TDA3");
//		Groupe groupe2 = new Groupe(2, "TDB5");
//		Groupe groupe3 = new Groupe(3, "TDA21");
//		
//		TreeMap<Groupe,TreeSet<Ticket>> listeTickets = new TreeMap<Groupe, TreeSet<Ticket>>();
//
//		tickets1.add(new Ticket(utilisateur, groupe1 , new Message(1, "salut la bleusaille", utilisateur), "Plomberie"));
//		tickets1.first().ajouterMessage( new Message(1, "est-ce que vous etes chaud ?", utilisateur));
//		tickets1.add(new Ticket(utilisateur, groupe1, new Message(2, "bonjour gars", utilisateur),"Ecole"));
//		tickets1.add(new Ticket(utilisateur, groupe1, new Message(3, "Comment allez vous ?",  utilisateur),"Citerne"));
//		
//		
//		tickets2.add(new Ticket(utilisateur, groupe2, new Message(4, "Hey bro",  utilisateur),"Oui"));
//		tickets2.add(new Ticket(utilisateur, groupe2, new Message(5, "La forme mec ?", utilisateur),"Trois fois quatre"));
//		tickets2.add(new Ticket(utilisateur, groupe2, new Message(6, "Grave mon gars", utilisateur),"Les chaussettes"));
//
//		
//		tickets3.add(new Ticket(utilisateur, groupe3, new Message(7, "Il faut reinstaller le tout", utilisateur),"La prof de francais"));
//		tickets3.add(new Ticket(utilisateur, groupe3, new Message(8, "Merci pour l'aide", utilisateur),"Les lacs du connemara"));
//		tickets3.add(new Ticket(utilisateur, groupe3, new Message(9, "Il n'y a pas de quoi", utilisateur),"Des nuages"));
//		
//		
//		listeTickets.put(groupe1, tickets1);
//		listeTickets.put(groupe2, tickets2);
//		listeTickets.put(groupe3, tickets3);
//		
//		utilisateur.setListeTickets(listeTickets);
//		
//		
//		ObjectInputStream input = null;
//		ObjectOutputStream output = null;
//		FenetrePrincipale principale = new FenetrePrincipale(utilisateur, input , output );
//		principale.setVisible(true);

	}

}
