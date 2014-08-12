package pl.edu.agh.mkulpa.dentaise.mobile.rest;

import java.io.Serializable;

public class Work implements Serializable {
	private static final long serialVersionUID = 7641751046131195216L;

	private long id;
	private Area area;
	private Diagnosis diagnosis;
	private Treatment treatment;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}

	public Diagnosis getDiagnosis() {
		return diagnosis;
	}

	public void setDiagnosis(Diagnosis diagnosis) {
		this.diagnosis = diagnosis;
	}

	public Treatment getTreatment() {
		return treatment;
	}

	public void setTreatment(Treatment treatment) {
		this.treatment = treatment;
	}

}
