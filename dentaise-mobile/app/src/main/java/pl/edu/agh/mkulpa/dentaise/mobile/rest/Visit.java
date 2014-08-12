package pl.edu.agh.mkulpa.dentaise.mobile.rest;

import java.util.Date;
import java.util.List;

public class Visit {
	private static final long serialVersionUID = 6496243611914464953L;
	
	private long id;
	private Date date;
	private Patient patient;
	private String notes;
    private List<Work> workList;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

    public List<Work> getWorkList() {
        return workList;
    }

    public void setWorkList(List<Work> workList) {
        this.workList = workList;
    }
}
