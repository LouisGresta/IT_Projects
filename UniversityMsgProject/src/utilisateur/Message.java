package utilisateur;

import java.io.Serializable;

public class Message implements Serializable, Comparable<Message> {
	private static final long serialVersionUID = -5684858929275625418L;
	
	private int num;
	private String msg;
	private int status = 0;
	private Utilisateur envoyeur;
	
	public Message(int num, String msg, Utilisateur envoyeur) {
		this.num=num;
		this.msg=msg;
		this.envoyeur = envoyeur;
	}
	
	/**
	 * @return the envoyeur
	 */
	public Utilisateur getEnvoyeur() {
		return envoyeur;
	}
	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}
	/**
	 * @return the num
	 */
	public int getNum() {
		return num;
	}
	/**
	 * @return the msg
	 */
	public String getMsg() {
		return msg;
	}
	
	@Override
	public int compareTo(Message message) {
		if(message.getNum() < this.getNum())
			return 1;
		else if(message.getNum() > this.getNum())
			return -1;
		else
			return 0;
	}
	
	public boolean equals(Message message) {
		return super.equals(message);
	}
	
	public int hashCode(){
		return super.hashCode();
	}

}
