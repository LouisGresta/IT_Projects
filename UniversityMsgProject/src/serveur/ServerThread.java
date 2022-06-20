package serveur;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.TreeMap;
import java.util.TreeSet;

import utilisateur.Groupe;
import utilisateur.Message;
import utilisateur.Ticket;
import utilisateur.Utilisateur;

public class ServerThread extends Thread {
	private Utilisateur connectedUser = null;
	private ObjectInputStream input;
	private ObjectOutputStream output;
	//parametres liés à la base de données
	private String url;
	private String login;
	private String password;
	
	public ServerThread(Socket client, String url, String login, String password) {
		try {
			OutputStream out = client.getOutputStream();
			output = new ObjectOutputStream(out);
			InputStream in = client.getInputStream();
			input = new ObjectInputStream(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		//initialisation parametres de connection base de donnee
		this.url = url;
		this.login = login;
		this.password = password;
	}
	
	/**
	 * @return the connectedUser
	 */
	public Utilisateur getConnectedUser() {
		return connectedUser;
	}

	@Override
	public void run() {
		try {
			Request request = Request.NONE;
			do {
				try {
					request = (Request) input.readObject();
				} catch (java.io.EOFException e) {
					continue;
				}
				switch (request) {
				case CONNECTION:
					connection();
					break;
				case CREATETICKET:
					creationTicket();
					break;
				case USERUPDATE:
					utilisateurInterfaceInformations();
					break;
				case SENDMESSAGE:
					envoyerMessage();
					break;
				case SENDNUMMESSAGE:
					int numM = 0;
					try(Connection connection = DriverManager.getConnection(url, login, password)){
						String cmdSql = "SELECT numm FROM T_Message";
						try(Statement statement = connection.createStatement();
								ResultSet result = statement.executeQuery(cmdSql)){
							while(result.next()) {
								if(numM < result.getInt(1))
									numM = result.getInt(1);
							}
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
					output.writeObject(numM);
					break;
				default:
					break;
				}
				
			} while(!request.equals(Request.QUITAPPLICATION));
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		
	}

	private void envoyerMessage() {
		try {
			int numM = 0;
			try(Connection connection = DriverManager.getConnection(url, login, password)){
				String cmdSql = "SELECT numm FROM T_Message";
				try(Statement statement = connection.createStatement();
						ResultSet result = statement.executeQuery(cmdSql)){
					while(result.next()) {
						if(numM < result.getInt(1))
							numM = result.getInt(1);
					}
				}
				numM += 1;
				output.writeObject(numM);
				Ticket destinataire = (Ticket) input.readObject();
				Message msg = destinataire.getDernierMessage();
				cmdSql = "INSERT INTO T_Message VALUES(?,?,?,?)";
				try(PreparedStatement statement = connection.prepareStatement(cmdSql)){
					statement.setInt(1, numM);
					statement.setString(2, msg.getMsg());
					statement.setInt(3, msg.getStatus());
					statement.setString(4, msg.getEnvoyeur().getIdentifiant());
					statement.executeUpdate();
				}
				
				cmdSql = "INSERT INTO T_Toget VALUES(?,?,?,?)";
				try(PreparedStatement statement = connection.prepareStatement(cmdSql)){
					statement.setString(1, destinataire.getTitre());
					statement.setInt(2, destinataire.getDestinataire().getNum());
					statement.setInt(3, destinataire.getPremierMessage().getNum());
					statement.setInt(4, msg.getNum());
					statement.executeUpdate();
				}
			} catch (SQLException e1) {
				System.out.println("Connexion à la base de donnée échouée ou erreur requete sql");
				e1.printStackTrace();
			}
			
			System.out.println("utilisateur ajouté à la base de donnée");
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}

	private void creationTicket() {
		try {
			Ticket ticket = (Ticket) input.readObject();
			String cmdSql = "INSERT INTO T_Ticket VALUES(?,?,?)";
			try(Connection connection = DriverManager.getConnection(url, login, password)){
				try(PreparedStatement statement = connection.prepareStatement(cmdSql)){
					statement.setString(1, ticket.getTitre());
					statement.setInt(2, ticket.getDestinataire().getNum());
					statement.setInt(3, ticket.getPremierMessage().getNum());
					statement.executeUpdate();
				}
				
				cmdSql = "INSERT INTO T_Toaccess VALUES(?,?,?,?)";
				try(PreparedStatement statement = connection.prepareStatement(cmdSql)){
					statement.setString(1, ticket.getCreateur().getIdentifiant());
					statement.setString(2, ticket.getTitre());
					statement.setInt(3, ticket.getDestinataire().getNum());
					statement.setInt(4, ticket.getPremierMessage().getNum());
					statement.executeUpdate();
				}
			} catch (SQLException e1) {
				System.out.println("Connexion à la base de donnée échouée ou erreur requete sql");
				e1.printStackTrace();
			}
				
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void connection() {
		try {
			String loginUser = (String) input.readObject();
			String passwordUser = (String) input.readObject();
			try(Connection connection = DriverManager.getConnection(url, login, password)){
				String cmdSql = "SELECT * FROM T_User WHERE loginu=?";
				try(PreparedStatement statement = connection.prepareStatement(cmdSql)){
					statement.setString(1, loginUser);
					ResultSet result = statement.executeQuery();
					
					if(!result.next()) // login incorrect
						output.writeObject(false);
					else {
						if(!passwordUser.equals(result.getString("Password"))) // mot de passe incorrect
							output.writeObject(false);
						else {
							//utilisateur connecté
							output.writeObject(true);
							connectedUser = new Utilisateur(result.getString(1), result.getString(2),
									result.getString(3), result.getString(4), result.getString(5));
							//chargement des données liées à cet utilisateur
							utilisateurInterfaceInformations();
						}
					}
				}
			} catch (SQLException e1) {
				System.out.println("Connexion à la base de donnée échouée ou erreur requete sql");
				e1.printStackTrace();
			}
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void utilisateurInterfaceInformations() {
		TreeMap<Groupe, TreeSet<Ticket>> GroupTicketMap = new TreeMap<>();
		try(Connection connection = DriverManager.getConnection(url, login, password)){
			String cmdSql = "SELECT DISTINCT t_groupe.numg, t_groupe.nameg "
					+ "FROM T_Groupe, T_Toaccess "
					+ "WHERE T_Toaccess.loginu=? and T_Groupe.numg=T_Toaccess.numg";
			try(PreparedStatement statement = connection.prepareStatement(cmdSql)){
				statement.setString(1, connectedUser.getIdentifiant());
				ResultSet result = statement.executeQuery();
				while(result.next()) {
					Groupe newGroupe = new Groupe(result.getInt(1), result.getString(2));
					TreeSet<Ticket> ticketTree = new TreeSet<>();
					cmdSql = "SELECT DISTINCT titlet, numg, numfirstmsg "
							+ "FROM T_Toaccess "
							+ "WHERE T_Toaccess.loginu=?";
					try(PreparedStatement statement1 = connection.prepareStatement(cmdSql)){
						statement1.setString(1, connectedUser.getIdentifiant());
						ResultSet result1 = statement1.executeQuery();
						
						while(result1.next()) {
							String titlet = result1.getString(1);
							int numg = result1.getInt(2);
							int numfirstmsg = result1.getInt(3);
							//recuperer le premier msg
							cmdSql = "SELECT DISTINCT t_message.NumM, t_message.Msg, t_message.StatusM, "
									+ "t_user.LoginU, t_user.Password, t_user.NameU, t_user.Firstname, t_user.TypeU "
									+ "FROM T_Message, T_User "
									+ "WHERE T_Message.numM=? and t_message.loginu=t_user.LoginU";
							Message firstMessage = null;
							try(PreparedStatement statement2 = connection.prepareStatement(cmdSql)){
								statement2.setInt(1, numfirstmsg);
								ResultSet result2 = statement2.executeQuery();
								if (result2.next()) {
									Utilisateur createurTicket = new Utilisateur(result2.getString("LoginU"), result2.getString("Password"),
											result2.getString("NameU"), result2.getString("Firstname"), result2.getString("TypeU"));
									firstMessage = new Message(result2.getInt(1), result2.getString(2), createurTicket);
								}
							}
							Ticket newTicket = new Ticket(connectedUser, newGroupe, firstMessage, titlet);
							//tout les messages du ticket de titre titlet
							cmdSql = "SELECT DISTINCT t_message.NumM, t_message.Msg, t_message.StatusM, "
									+ "t_user.LoginU, t_user.Password, t_user.NameU, t_user.Firstname, t_user.TypeU "
									+ "FROM t_message, t_user, t_toget, t_ticket "
									+ "WHERE t_message.loginu=t_user.LoginU and t_ticket.Titlet=? and t_toget.Titlet=? and "
									+ "t_message.numM=t_toget.numM";
							try(PreparedStatement statement2 = connection.prepareStatement(cmdSql)){
								statement2.setString(1, titlet);
								statement2.setString(2, titlet);
								ResultSet result2 = statement2.executeQuery();
								while(result2.next()) {
									Message newMessage = new Message(result2.getInt(1), result2.getString(2),
											new Utilisateur(result2.getString("LoginU"), result2.getString("Password"),
													result2.getString("NameU"), result2.getString("Firstname"), result2.getString("TypeU")));
									newTicket.ajouterMessage(newMessage);
								}
							}
							ticketTree.add(newTicket);
						}
					}
					GroupTicketMap.put(newGroupe, ticketTree);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		connectedUser.setListeTickets(GroupTicketMap);
		//envoi de l'utilisateur initialisé au client
		try {
			output.writeObject(connectedUser);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
