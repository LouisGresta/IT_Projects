package utilisateur;

import java.io.Serializable;
import java.util.TreeSet;

public class Ticket implements Comparable<Ticket>, Serializable {
	private static final long serialVersionUID = 7390228097644013246L;
	
	private Utilisateur createur;
    private String titre;
    private Groupe destinataire;
    private TreeSet<Message> listeMessages = new TreeSet<>();
    private Message premierMessage;
    private Message dernierMessage;
    int nbMessNonLu;
    
    public Ticket(Utilisateur createur, Groupe destinataire, Message premierMessage, String titre) {
        this.createur=createur;
        this.destinataire=destinataire;
        this.premierMessage = premierMessage;
        listeMessages.add(premierMessage);
        this.dernierMessage = premierMessage;
        this.titre = titre;
        this.nbMessNonLu = 0;
        for(Message mess:listeMessages)
            if(mess.getStatus() == 0)
                nbMessNonLu++;
    }
    

    public void ajouterMessage(Message msg) {
        listeMessages.add(msg);
        dernierMessage = msg;
    }

    public void updatenbMessNonLu() {
        nbMessNonLu = 0;
        for(Message mess:listeMessages)
            if(mess.getStatus()==0)
                nbMessNonLu++;
    }
    
    
    public Utilisateur getCreateur() {
        return createur;
    }


    public void setCreateur(Utilisateur createur) {
        this.createur = createur;
    }


    public String getTitre() {
        return titre;
    }


    public void setTitre(String titre) {
        this.titre = titre;
    }


    public Groupe getDestinataire() {
        return destinataire;
    }


    public void setDestinataire(Groupe destinataire) {
        this.destinataire = destinataire;
    }


    public TreeSet<Message> getListeMessages() {
        return listeMessages;
    }


    public void setListeMessages(TreeSet<Message> listeMessages) {
        this.listeMessages = listeMessages;
    }

    public Message getPremierMessage() {
        return premierMessage;
    }
    
    public Message getDernierMessage() {
        return dernierMessage;
    }


    public void setDernierMessage(Message dernierMessage) {
        this.dernierMessage = dernierMessage;
    }


    public int getNbMessNonLu() {
        return nbMessNonLu;
    }


    public void setNbMessNonLu(int nbMessNonLu) {
        this.nbMessNonLu = nbMessNonLu;
    }


    public int compareTo(Ticket ticket) {
        if(ticket.getNbMessNonLu() < this.getNbMessNonLu())
            return 1;
        else if(ticket.getNbMessNonLu() > this.getNbMessNonLu())
            return -1;
        else {
            return this.getTitre().compareTo(ticket.getTitre());
        }

    }
    
    public boolean equals(Ticket ticket) {
        return super.equals(ticket);
    }
    
    public int hashCode() {
        return super.hashCode();
    }
}