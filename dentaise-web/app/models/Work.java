package models;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Work implements Serializable {
	private static final long serialVersionUID = 7641751046131195216L;

	private long id;
	private Area area;
	private Diagnosis diagnosis;
	private Treatment treatment;
	private Visit visit;

	@Id
	@GeneratedValue
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public Diagnosis getDiagnosis() {
		return diagnosis;
	}

	public void setDiagnosis(Diagnosis diagnosis) {
		this.diagnosis = diagnosis;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public Treatment getTreatment() {
		return treatment;
	}

	public void setTreatment(Treatment treatment) {
		this.treatment = treatment;
	}

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	public Visit getVisit() {
		return visit;
	}

	public void setVisit(Visit visit) {
		if (!sameAsOld(visit)) {
			Visit oldVisit = this.visit;
			this.visit = visit;
			if (oldVisit != null) {
				oldVisit.removeWork(this);
			}
			if (visit != null) {
				visit.addWork(this);
			}
		}
	}

	private boolean sameAsOld(Visit newVisit) {
		return visit == null ? newVisit == null : visit.equals(newVisit);
	}
}
