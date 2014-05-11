package controllers;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.List;

import models.Patient;
import play.data.Form;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;
import views.html.patient;

public class PatientController extends Controller {

	@Transactional
	public static Result save() {
		Form<Patient> patientForm = Form.form(Patient.class);
		Patient patient = patientForm.bindFromRequest().get();
		JPA.em().persist(patient);
		return ok(index.render("user saved!"));
	}
	
	@Transactional
	public static Result search(String phrase) {
		String parts[] = phrase.split(" ");
		if (parts.length > 0) {
			String conditions = "";
			for (int i = 0; i < parts.length; i++) {
				String part = parts[i];
				if (i > 0) {
					conditions += " AND";
				}
				conditions += " forename LIKE '" + part + "%' OR surname LIKE '" + part + "%'";// OR pesel LIKE '" + part + "%'" ;
			}
			List<Patient> patients = JPA.em().createQuery("FROM Patient WHERE" + conditions, Patient.class).getResultList();
			return ok(Json.toJson(patients));
		} else {
			return ok(Json.toJson(new ArrayList<Patient>()));
		}
	}
	
	@Transactional
	public static Result edit() {
		return ok(patient.render(form(Patient.class)));
	}
	
	@Transactional
	public static Result list() {
		//TODO: ebean...
		Patient patient = JPA.em().find(Patient.class, Long.valueOf(1));
		
		return ok(index.render(patient.getForename() + " " + patient.getSurname()));
	}

}
