package utilisateur;

import java.io.Serializable;
import java.util.TreeMap;
import java.util.TreeSet;

public class Utilisateur implements Serializable {
	private static final long serialVersionUID = -3392918015047265994L;
	
	private TreeMap<Groupe, TreeSet<Ticket>> listeTickets = new TreeMap<Groupe, TreeSet<Ticket>>();
    private String identifiant; 
    private String motDePasse; 
    private String nom; 
    private String prenom; 
    private String type;
    
    public Utilisateur(String identifiant, String motDePasse, String nom, String prenom, String type) {
        this.identifiant=identifiant;
        this.motDePasse=motDePasse;
        this.nom=nom;
        this.prenom=prenom;
        this.type=type;
    }

    public TreeMap<Groupe, TreeSet<Ticket>> getListeTickets() {
        return listeTickets;
    }

    public void setListeTickets(TreeMap<Groupe, TreeSet<Ticket>> listeTickets) {
        this.listeTickets = listeTickets;
    }

    public String getIdentifiant() {
        return identifiant;
    }

    public void setIdentifiant(String identifiant) {
        this.identifiant = identifiant;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }



}