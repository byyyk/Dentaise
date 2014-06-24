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
import play.mvc.Security;
import views.html.index;
import views.html.patient;
import views.html.patients;

@Security.Authenticated(Secured.class)
public class PatientController extends Controller {

	//TODO: check if works
	@Transactional
	public static Result save() {
		Form<Patient> patientForm = Form.form(Patient.class);
		Patient patient = patientForm.bindFromRequest().get();
		JPA.em().merge(patient); 
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
	public static Result get(long id) {
		Patient patient = JPA.em().find(Patient.class, id);
		
		return ok(index.render(patient.getForename() + " " + patient.getSurname()));
	}
	
	@Transactional
	public static Result list() {
		return ok(patients.render());
	}

}
