package edu.miami.bte324.finalproject2.alejandrosanchez_matthewspahr;

import java.io.Serializable;

/**
 * @author alejandrosanchez
 * @author matthewspahr
 *
 */
public class Message implements Serializable {
	private static final long serialVersionUID = 2082136983160842391L;

	static 	final 	int LOGIN=0, LOGOUT = 1, 
						WHO = 2, WHO_L = 3, WHO_ALL = 4, 
						INVITE = 5, INVITE_R = 6, 
						HIT = 7, 
						MESSAGE = 8,
						SPECTATE = 9;
	
	private int 	type;
	private String 	message;
	private Login 	login;
	private int 	successCode;
	
	public Message(int type, String message) {
		this.type = type;
		this.message = message;
	}
	
	public Message(int type, String message,  Login login, int successCode){
		this.type = type;
		this.message = message;
		this.login = login; 
		this.successCode = successCode;
	}
	
	public Message(int type, String message, int successCode){
		this.type = type;
		this.message = message;
		this.successCode = successCode;
	}
	
	int getType() {
		return this.type;
	}
	String getMessage() {
		return this.message;
	}
	
	public Login getLogin() {
		return this.login;
	}
	public int getSuccessCode() {
		return this.successCode;
	}
	public void setSuccessCode(int successCode) {
		this.successCode = successCode;
	}
}