package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Visit implements Serializable {
	private static final long serialVersionUID = 6496243611914464953L;
	
	private long id;
	private Date date;
	private Doctor doctor;
	private Patient patient;
	private String notes;
	private List<Work> workList;
	
	@Id
	@GeneratedValue
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Column
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	public Doctor getDoctor() {
		return doctor;
	}

	public void setDoctor(Doctor doctor) {
		this.doctor = doctor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	@Column
	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "visit", cascade = {CascadeType.PERSIST,CascadeType.MERGE})
	public List<Work> getWorkList() {
		return workList;
	}

	public void addWork(Work work) {
		if (!workList.contains(work)) {
			workList.add(work);
			work.setVisit(this);			
		}
	}
	
	public void removeWork(Work work) {
		if (workList.contains(work)) {
			workList.remove(work);
			work.setVisit(null);
		}
	}
	
	public void setWorkList(List<Work> workList) {
		this.workList = workList;
	}
}
