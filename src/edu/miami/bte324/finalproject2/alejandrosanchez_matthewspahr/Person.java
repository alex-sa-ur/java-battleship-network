package edu.miami.bte324.finalproject2.alejandrosanchez_matthewspahr;

import java.io.Serializable;

/**
 * @author alejandrosanchez
 * @author matthewspahr
 *
 */
public abstract class Person implements Serializable {
	private static final long serialVersionUID = 501753849941455702L;
	
	private String firstName, lastName;
	
	public Person(String firstName, String lastName) {
		super();
		this.firstName 	= firstName;
		this.lastName 	= lastName;
	}
	
	public String getFirstName() {
		return this.firstName;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getLastName() {
		return this.lastName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
}
