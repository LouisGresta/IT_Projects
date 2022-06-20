package serveur;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import utilisateur.Utilisateur;

public class MainServeur {
	private static List<ServerThread> threadsClient = new ArrayList<>();
	private static List<Utilisateur> connectedUsers = new ArrayList<>();
	
	public static void main(String[] args) {
		
		//initialisation parametres de connection base de donnee
		Properties prop = new Properties();
		try(FileInputStream propertyFile = new FileInputStream("conf.properties")) {
			prop.load(propertyFile);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		String driver = prop.getProperty("jdbc.driver");
		String url = prop.getProperty("jdbc.url");
		String login = prop.getProperty("jdbc.login");
		String password = prop.getProperty("jdbc.password");
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		System.out.println("Properties chargé");
		
		//pour le look
		try{
			UIManager.setLookAndFeel(new NimbusLookAndFeel());
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		//creation fenetre principale
		InformationUtilisateur mywindow = new InformationUtilisateur(url, login, password);
		mywindow.setVisible(true);
		
		try(ServerSocket serverSocket = new ServerSocket(5000)) {
			while(true) {
				Socket client = serverSocket.accept();
				ServerThread clientThread = new ServerThread(client, url, login, password);
				clientThread.start();
				threadsClient.add(clientThread);
			}
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		System.out.println("\n\nFermeture Serveur");
	}
}
