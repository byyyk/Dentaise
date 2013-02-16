package pl.edu.agh.mkulpa.dentaise.persistence.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Patient implements Serializable {

	private static final long serialVersionUID = -2036408534891065777L;

	private int id;
	private String forename;
	private String surname;
	
	public Patient() {
		
	}
	
	public Patient(String forename, String surname) {
		this.setForename(forename);
		this.setSurname(surname);
	}
	
	@Id
	@GeneratedValue
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	@Column
	public String getForename() {
		return forename;
	}

	public void setForename(String forename) {
		this.forename = forename;
	}

	@Column
	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}
	
}
