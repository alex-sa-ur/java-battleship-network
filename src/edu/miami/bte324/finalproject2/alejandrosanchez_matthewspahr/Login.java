package edu.miami.bte324.finalproject2.alejandrosanchez_matthewspahr;

import java.io.Serializable;

/**
 * @author alejandrosanchez
 * @author matthewspahr
 *
 */
public class Login implements Serializable {
	private static final long serialVersionUID = 2356755453667477272L;
	
	private String username, password;
	
	public Login(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public boolean confirmLogin(String username, String password) {
		return (this.username.equals(username) && this.password.equals(password));
	}
}