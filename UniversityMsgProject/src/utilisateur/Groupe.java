package utilisateur;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Groupe implements Serializable, Comparable<Groupe> {
	private static final long serialVersionUID = 3208363539148857635L;
	
	private int num;
	private String nom;
	private List<Utilisateur> utilisateurs = new ArrayList<>();
	
	
	public Groupe(int num, String nom, Utilisateur... utilisateurs) {
		this.num=num;
		this.nom=nom;
		for(Utilisateur user : utilisateurs)
			this.utilisateurs.add(user);
	}
	
	
	/**
	 * @return the num
	 */
	public int getNum() {
		return num;
	}
	/**
	 * @return the nom
	 */
	public String getNom() {
		return nom;
	}
	/**
	 * @return the utilisateurs
	 */
	public List<Utilisateur> getUtilisateurs() {
		return utilisateurs;
	}
	
	public void ajouterUtilisateur(Utilisateur utilisateur) {
		this.utilisateurs.add(utilisateur);
	}
	
	public void supprimerUtilisateur(Utilisateur utilisateur) {
		this.utilisateurs.remove(utilisateur);
	}
	
	@Override
	public int compareTo(Groupe groupe) {
		return this.getNom().compareTo(groupe.getNom());
	}

	public boolean equals(Groupe groupe) {
		return super.equals(groupe);
	}
	
	public int hashCode() {
		return super.hashCode();
	}

}
