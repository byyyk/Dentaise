package pl.edu.agh.mkulpa.dentaise.server.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PatientDto {
	
	private long id;
	private String forename;
	private String surname;
	
	public PatientDto() {
		
	}
	
	public PatientDto(String forename, String surname) {
		this.setForename(forename);
		this.setSurname(surname);
	}
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}

	public String getForename() {
		return forename;
	}

	public void setForename(String forename) {
		this.forename = forename;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

}
